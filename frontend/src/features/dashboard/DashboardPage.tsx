import { useQuery } from '@tanstack/react-query'
import { useState, type FormEvent, type ReactNode } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { apiRequest } from '../../shared/api/client'
import type { PageResponse } from '../../shared/api/types'
import { useAuth } from '../../shared/auth/AuthProvider'
import type { Role } from '../../shared/auth/types'
import { StatusPanel } from '../../shared/components/StatusPanel'

type Profile = { id: number; fullName: string }
type Appointment = {
  id: number
  doctorName: string
  patientName: string
  appointmentDate: string
  appointmentTime: string
  status: 'PENDING' | 'CONFIRMED' | 'COMPLETED' | 'CANCELLED'
  reasonForVisit?: string
}
type Doctor = { id: number; fullName: string; specialization: string }
type Patient = { id: number; fullName: string; email: string }

const statusLabels: Record<Appointment['status'], string> = {
  PENDING: 'Chờ xác nhận',
  CONFIRMED: 'Đã xác nhận',
  COMPLETED: 'Đã hoàn thành',
  CANCELLED: 'Đã hủy',
}

function formatAppointment(appointment: Appointment) {
  const date = new Date(`${appointment.appointmentDate}T${appointment.appointmentTime || '00:00:00'}`)
  return new Intl.DateTimeFormat('vi-VN', { dateStyle: 'medium', timeStyle: 'short' }).format(date)
}

function isUpcoming(appointment: Appointment) {
  return appointment.status !== 'CANCELLED' && appointment.status !== 'COMPLETED'
    && new Date(`${appointment.appointmentDate}T${appointment.appointmentTime || '00:00:00'}`).getTime() >= Date.now()
}

function Widget({
  id,
  title,
  loading,
  error,
  retry,
  empty,
  controls,
  children,
}: {
  id?: string
  title: string
  loading: boolean
  error: boolean
  retry: () => void
  empty?: { title: string; description: string; to: string; cta: string }
  controls?: ReactNode
  children: ReactNode
}) {
  return <section className="dashboard-widget" id={id} aria-labelledby={`${id ?? title}-title`}>
    <div className="widget-heading"><h2 id={`${id ?? title}-title`}>{title}</h2></div>
    {controls}
    {loading ? <StatusPanel kind="loading" title="Đang tải dữ liệu" />
      : error ? <StatusPanel kind="error" title="Không thể tải dữ liệu" description="Có thể thử lại riêng phần này." onRetry={retry} />
        : empty ? <div className="widget-empty"><StatusPanel kind="empty" title={empty.title} description={empty.description} />
          <Link className="button button--primary" to={empty.to}>{empty.cta}</Link>
        </div> : children}
  </section>
}

function AppointmentList({ appointments, role }: { appointments: Appointment[]; role: Role }) {
  if (!appointments.length) return <p className="muted">Không có cuộc hẹn trong danh sách gần đây.</p>
  return <ul className="dashboard-list">
    {appointments.map((appointment) => <li key={appointment.id}>
      <div>
        <strong>{role === 'PATIENT' ? appointment.doctorName : appointment.patientName}</strong>
        <span>{formatAppointment(appointment)}</span>
      </div>
      <span className={`appointment-status appointment-status--${appointment.status.toLowerCase()}`}>{statusLabels[appointment.status]}</span>
    </li>)}
  </ul>
}

function RoleShortcuts({ role }: { role: Role }) {
  const shortcuts = role === 'PATIENT'
    ? [
      { to: '/doctors', title: 'Tìm bác sĩ', text: 'Xem bác sĩ và lịch khám hàng tuần.' },
      { to: '/dashboard#appointments', title: 'Cuộc hẹn', text: 'Theo dõi lịch sắp tới và lịch sử.' },
      { to: '/dashboard#records', title: 'Hồ sơ khám', text: 'Mở khu vực hồ sơ khám của bạn.' },
    ]
    : role === 'DOCTOR'
      ? [
        { to: '/dashboard#appointments', title: 'Cuộc hẹn', text: 'Xem lịch và yêu cầu đang chờ.' },
        { to: '/doctor', title: 'Lịch làm việc', text: 'Mở không gian quản lý lịch.' },
        { to: '/dashboard#profile', title: 'Hồ sơ cá nhân', text: 'Xem thông tin tài khoản bác sĩ.' },
      ]
      : [
        { to: '/admin', title: 'Công cụ quản trị', text: 'Mở khu vực quản trị hệ thống.' },
        { to: '/dashboard#doctors', title: 'Bác sĩ', text: 'Xem danh sách hồ sơ bác sĩ.' },
        { to: '/dashboard#patients', title: 'Bệnh nhân', text: 'Xem danh sách hồ sơ bệnh nhân.' },
      ]

  return <nav className="shortcut-grid" aria-label="Lối tắt">
    {shortcuts.map((shortcut) => <Link className="shortcut-card" key={shortcut.title} to={shortcut.to}>
      <strong>{shortcut.title}</strong><span>{shortcut.text}</span>
    </Link>)}
  </nav>
}

function PatientDashboard() {
  const profile = useQuery({
    queryKey: ['dashboard', 'patient-profile'],
    queryFn: () => apiRequest<Profile>('/api/patients/me'),
  })
  const appointments = useQuery({
    queryKey: ['dashboard', 'patient-appointments', profile.data?.id],
    queryFn: () => apiRequest<PageResponse<Appointment>>(`/api/appointments/patients/${profile.data!.id}?page=0&size=10&sort=appointmentDate,desc`),
    enabled: profile.data !== undefined,
  })
  const upcoming = appointments.data?.content.filter(isUpcoming) ?? []
  const history = appointments.data?.content.filter((appointment) => !isUpcoming(appointment)) ?? []
  return <>
    <Widget title="Hồ sơ bệnh nhân" id="profile" loading={profile.isPending} error={profile.isError} retry={() => void profile.refetch()}>
      <p className="profile-summary">Xin chào, <strong>{profile.data?.fullName}</strong>.</p>
    </Widget>
    <div className="dashboard-grid">
      <Widget id="appointments" title="Cuộc hẹn sắp tới" loading={profile.isPending || (profile.data !== undefined && appointments.isPending)} error={profile.isError || appointments.isError} retry={() => void (profile.isError ? profile.refetch() : appointments.refetch())}
        empty={upcoming.length ? undefined : { title: 'Chưa có cuộc hẹn sắp tới', description: 'Tìm bác sĩ để chọn lịch khám phù hợp.', to: '/patient', cta: 'Tìm bác sĩ' }}>
        <AppointmentList appointments={upcoming} role="PATIENT" />
      </Widget>
      <Widget title="Lịch sử gần đây" loading={profile.isPending || (profile.data !== undefined && appointments.isPending)} error={profile.isError || appointments.isError} retry={() => void (profile.isError ? profile.refetch() : appointments.refetch())}
        empty={history.length ? undefined : { title: 'Chưa có lịch sử cuộc hẹn', description: 'Các cuộc hẹn đã diễn ra sẽ xuất hiện tại đây.', to: '/patient', cta: 'Tìm bác sĩ' }}>
        <AppointmentList appointments={history} role="PATIENT" />
      </Widget>
      <Widget title="Hồ sơ khám" id="records" loading={false} error={false} retry={() => undefined}
        empty={{ title: 'Mở hồ sơ khám', description: 'Hồ sơ được tạo sau khi cuộc hẹn hoàn thành.', to: '/dashboard#appointments', cta: 'Xem cuộc hẹn' }}>
        <></>
      </Widget>
    </div>
  </>
}

function DoctorDashboard() {
  const profile = useQuery({
    queryKey: ['dashboard', 'doctor-profile'],
    queryFn: () => apiRequest<Profile>('/api/doctors/me'),
  })
  const appointments = useQuery({
    queryKey: ['dashboard', 'doctor-appointments', profile.data?.id],
    queryFn: () => apiRequest<PageResponse<Appointment>>(`/api/appointments/doctors/${profile.data!.id}?page=0&size=10&sort=appointmentDate,desc`),
    enabled: profile.data !== undefined,
  })
  const upcoming = appointments.data?.content.filter(isUpcoming) ?? []
  const pending = appointments.data?.content.filter((appointment) => appointment.status === 'PENDING') ?? []
  return <>
    <Widget title="Hồ sơ bác sĩ" id="profile" loading={profile.isPending} error={profile.isError} retry={() => void profile.refetch()}>
      <p className="profile-summary">Xin chào, <strong>{profile.data?.fullName}</strong>.</p>
    </Widget>
    <div className="dashboard-grid">
      <Widget title="Cuộc hẹn sắp tới" id="appointments" loading={profile.isPending || (profile.data !== undefined && appointments.isPending)} error={profile.isError || appointments.isError} retry={() => void (profile.isError ? profile.refetch() : appointments.refetch())}
        empty={upcoming.length ? undefined : { title: 'Chưa có cuộc hẹn sắp tới', description: 'Các lịch khám sắp tới sẽ xuất hiện tại đây.', to: '/doctor', cta: 'Xem lịch làm việc' }}>
        <AppointmentList appointments={upcoming} role="DOCTOR" />
      </Widget>
      <Widget title="Cuộc hẹn cần xử lý" loading={profile.isPending || (profile.data !== undefined && appointments.isPending)} error={profile.isError || appointments.isError} retry={() => void (profile.isError ? profile.refetch() : appointments.refetch())}
        empty={pending.length ? undefined : { title: 'Không có yêu cầu đang chờ', description: 'Yêu cầu đặt lịch mới sẽ được hiển thị tại đây.', to: '/doctor', cta: 'Xem lịch làm việc' }}>
        <AppointmentList appointments={pending} role="DOCTOR" />
      </Widget>
    </div>
  </>
}

function AppointmentSearchForm({
  initialStatus,
  initialFromDate,
  initialToDate,
  initialDoctorId,
  initialPatientId,
  onSearch,
}: {
  initialStatus: string
  initialFromDate: string
  initialToDate: string
  initialDoctorId: string
  initialPatientId: string
  onSearch: (params: URLSearchParams) => void
}) {
  const [status, setStatus] = useState(initialStatus)
  const [fromDate, setFromDate] = useState(initialFromDate)
  const [toDate, setToDate] = useState(initialToDate)
  const [doctorId, setDoctorId] = useState(initialDoctorId)
  const [patientId, setPatientId] = useState(initialPatientId)
  const [dateError, setDateError] = useState('')
  const submit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    if (fromDate && toDate && fromDate > toDate) {
      setDateError('Ngày kết thúc phải bằng hoặc sau ngày bắt đầu.')
      return
    }
    setDateError('')
    const next = new URLSearchParams()
    if (status) next.set('status', status)
    if (fromDate) next.set('fromDate', fromDate)
    if (toDate) next.set('toDate', toDate)
    if (doctorId) next.set('doctorId', doctorId)
    if (patientId) next.set('patientId', patientId)
    onSearch(next)
  }
  return <form className="appointment-search-form" onSubmit={submit}>
    <label className="field"><span>Trạng thái</span>
      <select value={status} onChange={(event) => setStatus(event.target.value)}>
        <option value="">Tất cả trạng thái</option>
        <option value="PENDING">Chờ xác nhận</option>
        <option value="CONFIRMED">Đã xác nhận</option>
        <option value="COMPLETED">Đã hoàn thành</option>
        <option value="CANCELLED">Đã hủy</option>
      </select>
    </label>
    <label className="field"><span>Từ ngày</span><input type="date" value={fromDate} max={toDate || undefined} onChange={(event) => setFromDate(event.target.value)} /></label>
    <label className="field"><span>Đến ngày</span><input type="date" value={toDate} min={fromDate || undefined} onChange={(event) => setToDate(event.target.value)} /></label>
    <label className="field"><span>Mã bác sĩ</span><input type="number" min="1" step="1" value={doctorId} onChange={(event) => setDoctorId(event.target.value)} placeholder="Tất cả" /></label>
    <label className="field"><span>Mã bệnh nhân</span><input type="number" min="1" step="1" value={patientId} onChange={(event) => setPatientId(event.target.value)} placeholder="Tất cả" /></label>
    {dateError && <p className="form-error" role="alert">{dateError}</p>}
    <button className="button button--primary" type="submit">Tìm cuộc hẹn</button>
  </form>
}

function AdminDashboard() {
  const [searchParams, setSearchParams] = useSearchParams()
  const filterKey = searchParams.toString()
  const doctors = useQuery({ queryKey: ['dashboard', 'doctors'], queryFn: () => apiRequest<PageResponse<Doctor>>('/api/doctors?page=0&size=5') })
  const patients = useQuery({ queryKey: ['dashboard', 'patients'], queryFn: () => apiRequest<PageResponse<Patient>>('/api/patients?page=0&size=5') })
  const appointments = useQuery({
    queryKey: ['dashboard', 'all-appointments', filterKey],
    queryFn: () => {
      const params = new URLSearchParams({ page: '0', size: '5', sort: 'appointmentDate,desc' })
      for (const key of ['status', 'fromDate', 'toDate', 'doctorId', 'patientId']) {
        const value = searchParams.get(key)
        if (value) params.set(key, value)
      }
      return apiRequest<PageResponse<Appointment>>(`/api/appointments/search?${params.toString()}`)
    },
  })
  return <>
    <section className="admin-tools" aria-label="Khu vực quản lý">
      <Link className="admin-tool-card" to="/doctors"><span className="eyebrow">Danh bạ</span><strong>Quản lý bác sĩ</strong><span>Tìm kiếm, xem, sửa hoặc xóa hồ sơ.</span></Link>
      <Link className="admin-tool-card" to="/patients"><span className="eyebrow">Hồ sơ</span><strong>Quản lý bệnh nhân</strong><span>Xem danh sách và cập nhật hồ sơ theo quyền.</span></Link>
      <Link className="admin-tool-card" to="/appointments"><span className="eyebrow">Lịch khám</span><strong>Tra cứu cuộc hẹn</strong><span>Mở danh sách phân trang với bộ lọc chi tiết.</span></Link>
    </section>
    <div className="dashboard-grid">
    <Widget id="doctors" title="Bác sĩ" loading={doctors.isPending} error={doctors.isError} retry={() => void doctors.refetch()}
      empty={doctors.data?.content.length ? undefined : { title: 'Chưa có hồ sơ bác sĩ', description: 'Danh sách bác sĩ hiện chưa có dữ liệu.', to: '/admin', cta: 'Mở quản trị' }}>
      <p className="widget-total">{doctors.data?.totalElements} hồ sơ</p>
      <ul className="dashboard-list">{doctors.data?.content.map((doctor) => <li key={doctor.id}><div><strong>{doctor.fullName}</strong><span>{doctor.specialization}</span></div></li>)}</ul>
    </Widget>
    <Widget id="patients" title="Bệnh nhân" loading={patients.isPending} error={patients.isError} retry={() => void patients.refetch()}
      empty={patients.data?.content.length ? undefined : { title: 'Chưa có hồ sơ bệnh nhân', description: 'Danh sách bệnh nhân hiện chưa có dữ liệu.', to: '/admin', cta: 'Mở quản trị' }}>
      <p className="widget-total">{patients.data?.totalElements} hồ sơ</p>
      <ul className="dashboard-list">{patients.data?.content.map((patient) => <li key={patient.id}><div><strong>{patient.fullName}</strong><span>{patient.email}</span></div></li>)}</ul>
    </Widget>
    <Widget id="appointments" title="Tìm kiếm cuộc hẹn" loading={appointments.isPending} error={appointments.isError} retry={() => void appointments.refetch()}
      controls={<AppointmentSearchForm key={filterKey} initialStatus={searchParams.get('status') ?? ''} initialFromDate={searchParams.get('fromDate') ?? ''} initialToDate={searchParams.get('toDate') ?? ''} initialDoctorId={searchParams.get('doctorId') ?? ''} initialPatientId={searchParams.get('patientId') ?? ''} onSearch={setSearchParams} />}
      empty={appointments.data?.content.length ? undefined : { title: 'Chưa có cuộc hẹn', description: 'Các cuộc hẹn sẽ xuất hiện khi được tạo.', to: '/admin', cta: 'Mở quản trị' }}>
      <p className="widget-total">{appointments.data?.totalElements} cuộc hẹn</p>
      <AppointmentList appointments={appointments.data?.content ?? []} role="ADMIN" />
    </Widget>
    </div>
  </>
}

export function AdminManagementPage() {
  return <section className="dashboard-page admin-page">
    <p className="eyebrow">Khu vực quản trị</p>
    <h1>Công cụ quản trị</h1>
    <p className="muted">Tổng quan hệ thống và truy cập các danh sách quản lý theo phân quyền hiện có.</p>
    <AdminDashboard />
  </section>
}

export function DashboardPage() {
  const { session } = useAuth()
  const role = session?.user.role ?? 'PATIENT'
  const titles: Record<Role, string> = { PATIENT: 'Tổng quan bệnh nhân', DOCTOR: 'Tổng quan bác sĩ', ADMIN: 'Tổng quan quản trị' }
  return <section className="dashboard-page">
    <p className="eyebrow">Không gian làm việc</p>
    <h1>{titles[role]}</h1>
    <RoleShortcuts role={role} />
    {role === 'PATIENT' ? <PatientDashboard /> : role === 'DOCTOR' ? <DoctorDashboard /> : <AdminDashboard />}
  </section>
}
