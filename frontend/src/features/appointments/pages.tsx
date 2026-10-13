import { useState } from 'react'
import { Link, useParams, useSearchParams } from 'react-router-dom'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { apiRequest } from '../../shared/api/client'
import { ApiError, type PageResponse } from '../../shared/api/types'
import { useAuth } from '../../shared/auth/AuthProvider'
import { Button } from '../../shared/components/Button'
import { Dialog } from '../../shared/components/Dialog'
import { Pagination } from '../../shared/components/Pagination'
import { StatusPanel } from '../../shared/components/StatusPanel'

interface Appointment {
  id: number
  doctorId: number
  doctorName: string
  doctorSpecialization?: string
  patientId: number
  patientName: string
  appointmentDate: string
  appointmentTime: string
  status: 'PENDING' | 'CONFIRMED' | 'COMPLETED' | 'CANCELLED'
  reasonForVisit?: string
  cancellationReason?: string
}
interface Profile { id: number }

const statusLabels: Record<Appointment['status'], string> = { PENDING: 'Chờ xác nhận', CONFIRMED: 'Đã xác nhận', COMPLETED: 'Đã hoàn thành', CANCELLED: 'Đã hủy' }
const statusOptions = Object.entries(statusLabels) as [Appointment['status'], string][]

function appointmentTime(appointment: Appointment) { return `${appointment.appointmentDate} · ${appointment.appointmentTime.slice(0, 5)}` }
function appointmentPath(id: number, from: string) { return `/appointments/${id}?from=${encodeURIComponent(from)}` }

export function AppointmentListPage() {
  const { hasRole } = useAuth()
  const isAdmin = hasRole(['ADMIN'])
  const isDoctor = hasRole(['DOCTOR'])
  const [searchParams, setSearchParams] = useSearchParams()
  const page = Math.max(0, Number.parseInt(searchParams.get('page') ?? '0', 10) || 0)
  const filterKey = searchParams.toString()
  const profile = useQuery({
    queryKey: ['appointment-profile', isDoctor ? 'doctor' : 'patient'],
    queryFn: () => apiRequest<Profile>(isDoctor ? '/api/doctors/me' : '/api/patients/me'),
    enabled: !isAdmin,
  })
  const requestUrl = isAdmin
    ? `/api/appointments/search?${new URLSearchParams({ page: String(page), size: '10', sort: 'appointmentDate,desc', ...(searchParams.get('status') ? { status: searchParams.get('status')! } : {}), ...(searchParams.get('fromDate') ? { fromDate: searchParams.get('fromDate')! } : {}), ...(searchParams.get('toDate') ? { toDate: searchParams.get('toDate')! } : {}) }).toString()}`
    : profile.data ? `/api/appointments/${isDoctor ? 'doctors' : 'patients'}/${profile.data.id}?page=${page}&size=10&sort=appointmentDate,desc` : ''
  const appointments = useQuery({
    queryKey: ['appointments', requestUrl, filterKey],
    queryFn: () => apiRequest<PageResponse<Appointment>>(requestUrl),
    enabled: Boolean(requestUrl),
  })

  function updateFilters(updates: Record<string, string>) {
    const next = new URLSearchParams(searchParams)
    for (const [key, value] of Object.entries(updates)) {
      if (value) next.set(key, value)
      else next.delete(key)
    }
    next.delete('page')
    setSearchParams(next)
  }

  return <section className="appointment-directory">
    <p className="eyebrow">Lịch hẹn</p><h1>{isAdmin ? 'Tra cứu mọi cuộc hẹn' : 'Cuộc hẹn của tôi'}</h1>
    {isAdmin && <form className="appointment-filter card" onSubmit={(event) => { event.preventDefault(); updateFilters({ status: (event.currentTarget.elements.namedItem('status') as HTMLSelectElement).value, fromDate: (event.currentTarget.elements.namedItem('fromDate') as HTMLInputElement).value, toDate: (event.currentTarget.elements.namedItem('toDate') as HTMLInputElement).value }) }}>
      <label className="field">Trạng thái<select name="status" defaultValue={searchParams.get('status') ?? ''}><option value="">Tất cả</option>{statusOptions.map(([value, label]) => <option key={value} value={value}>{label}</option>)}</select></label>
      <label className="field">Từ ngày<input name="fromDate" type="date" defaultValue={searchParams.get('fromDate') ?? ''} /></label>
      <label className="field">Đến ngày<input name="toDate" type="date" defaultValue={searchParams.get('toDate') ?? ''} /></label>
      <div className="dialog-actions"><Button type="submit">Lọc cuộc hẹn</Button><Button type="button" variant="secondary" onClick={() => setSearchParams({})}>Xóa lọc</Button></div>
    </form>}
    {profile.isError ? <StatusPanel kind="error" title="Không tải được hồ sơ tài khoản" description={profile.error.message} onRetry={() => void profile.refetch()} /> : null}
    {appointments.isPending ? <StatusPanel kind="loading" title="Đang tải cuộc hẹn" description="Danh sách cuộc hẹn đang được tải." /> : null}
    {appointments.isError ? <StatusPanel kind="error" title="Không tải được cuộc hẹn" description={appointments.error.message} onRetry={() => void appointments.refetch()} /> : null}
    {appointments.data && appointments.data.content.length === 0 ? <StatusPanel kind="empty" title="Không có cuộc hẹn" description="Các cuộc hẹn phù hợp sẽ xuất hiện tại đây." /> : null}
    {appointments.data && appointments.data.content.length > 0 && <>
      <div className="appointment-table-wrap"><table className="appointment-table"><thead><tr><th>Thời gian</th><th>Bác sĩ</th><th>Bệnh nhân</th><th>Trạng thái</th><th><span className="sr-only">Thao tác</span></th></tr></thead><tbody>{appointments.data.content.map((appointment) => <tr key={appointment.id}><td><Link to={appointmentPath(appointment.id, '/appointments')}><strong>{appointmentTime(appointment)}</strong></Link></td><td>{appointment.doctorName}</td><td>{appointment.patientName}</td><td><span className={`appointment-status appointment-status--${appointment.status.toLowerCase()}`}>{statusLabels[appointment.status]}</span></td><td><Link className="button button--secondary" to={appointmentPath(appointment.id, '/appointments')}>Chi tiết</Link></td></tr>)}</tbody></table></div>
      <Pagination page={appointments.data.pageNumber} totalPages={appointments.data.totalPages} onChange={(nextPage) => updateFilters({ page: String(nextPage) })} />
    </>}
  </section>
}

export function AppointmentDetailPage() {
  const { appointmentId } = useParams()
  const [searchParams] = useSearchParams()
  const queryClient = useQueryClient()
  const { hasRole } = useAuth()
  const isAdmin = hasRole(['ADMIN'])
  const isDoctor = hasRole(['DOCTOR'])
  const returnTo = searchParams.get('from') === '/appointments' ? '/appointments' : '/dashboard'
  const id = Number(appointmentId)
  const [cancelOpen, setCancelOpen] = useState(false)
  const [cancelReason, setCancelReason] = useState('')
  const appointment = useQuery({ queryKey: ['appointment', id], queryFn: () => apiRequest<Appointment>(`/api/appointments/${id}`), enabled: Number.isSafeInteger(id) && id > 0 })
  const changeStatus = useMutation({
    mutationFn: ({ action, reason }: { action: 'confirm' | 'complete' | 'cancel'; reason?: string }) => apiRequest<Appointment>(`/api/appointments/${id}/${action}`, { method: 'PATCH', ...(reason === undefined ? {} : { body: JSON.stringify({ cancellationReason: reason }) }) }),
    onSuccess: (updated) => { queryClient.setQueryData(['appointment', id], updated); void queryClient.invalidateQueries({ queryKey: ['appointments'] }); setCancelOpen(false); setCancelReason('') },
  })
  const data = appointment.data
  const canDoctorAct = Boolean(data && (isAdmin || (isDoctor && data.status !== 'CANCELLED' && data.status !== 'COMPLETED')))
  const canCancel = data?.status === 'PENDING' || data?.status === 'CONFIRMED'
  const errorMessage = changeStatus.error instanceof ApiError ? changeStatus.error.message : changeStatus.error?.message

  return <section className="appointment-detail">
    <Link className="back-link" to={returnTo}>← Quay lại danh sách cuộc hẹn</Link>
    {!Number.isSafeInteger(id) || id <= 0 ? <StatusPanel kind="error" title="Mã cuộc hẹn không hợp lệ" description="Hãy quay lại danh sách cuộc hẹn." /> : null}
    {appointment.isPending ? <StatusPanel kind="loading" title="Đang tải chi tiết cuộc hẹn" description="Thông tin cuộc hẹn đang được tải." /> : null}
    {appointment.isError ? <StatusPanel kind="error" title="Không tải được cuộc hẹn" description={appointment.error.message} onRetry={() => void appointment.refetch()} /> : null}
    {data && <article className="appointment-detail-card card"><div className="directory-heading"><div><p className="eyebrow">Chi tiết cuộc hẹn #{data.id}</p><h1>{appointmentTime(data)}</h1></div><span className={`appointment-status appointment-status--${data.status.toLowerCase()}`}>{statusLabels[data.status]}</span></div><dl className="appointment-detail-facts"><div><dt>Bác sĩ</dt><dd>{data.doctorName}</dd></div><div><dt>Bệnh nhân</dt><dd>{data.patientName}</dd></div><div><dt>Chuyên khoa</dt><dd>{data.doctorSpecialization || 'Đang cập nhật'}</dd></div><div><dt>Lý do khám</dt><dd>{data.reasonForVisit || 'Không cung cấp'}</dd></div>{data.cancellationReason && <div><dt>Lý do hủy</dt><dd>{data.cancellationReason}</dd></div>}</dl>{errorMessage && <p className="form-error" role="alert">{errorMessage}</p>}<div className="appointment-actions">{canDoctorAct && data.status === 'PENDING' && <Button onClick={() => changeStatus.mutate({ action: 'confirm' })} disabled={changeStatus.isPending}>Xác nhận lịch</Button>}{canDoctorAct && data.status === 'CONFIRMED' && <Button onClick={() => changeStatus.mutate({ action: 'complete' })} disabled={changeStatus.isPending}>Đánh dấu hoàn tất</Button>}{canCancel && <Button variant="danger" onClick={() => setCancelOpen(true)} disabled={changeStatus.isPending}>Hủy cuộc hẹn</Button>}</div></article>}
    <Dialog title="Hủy cuộc hẹn" open={cancelOpen} onClose={() => setCancelOpen(false)}><p>Vui lòng cho biết lý do hủy. Lý do này sẽ được gửi trong thông báo cuộc hẹn.</p><label className="field">Lý do hủy<textarea value={cancelReason} maxLength={500} rows={4} onChange={(event) => setCancelReason(event.target.value)} required /><span className="field-hint">Còn {500 - cancelReason.length} ký tự</span></label><div className="dialog-actions"><Button type="button" variant="secondary" onClick={() => setCancelOpen(false)}>Đóng</Button><Button type="button" variant="danger" disabled={!cancelReason.trim() || cancelReason.length > 500 || changeStatus.isPending} onClick={() => changeStatus.mutate({ action: 'cancel', reason: cancelReason.trim() })}>Xác nhận hủy</Button></div></Dialog>
  </section>
}
