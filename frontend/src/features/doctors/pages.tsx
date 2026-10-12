import { useEffect, useMemo, useRef, useState } from 'react'
import { Link, useLocation, useParams, useSearchParams } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { apiRequest } from '../../shared/api/client'
import type { PageResponse } from '../../shared/api/types'
import { Button } from '../../shared/components/Button'
import { Pagination } from '../../shared/components/Pagination'
import { StatusPanel } from '../../shared/components/StatusPanel'

interface Doctor {
  id: number
  fullName: string
  email: string
  specialization: string
  qualification: string
  yearsOfExperience: number
  phoneNumber: string
  consultationFee: number
}

interface DoctorSchedule {
  id: number
  doctorId: number
  dayOfWeek: string
  startTime: string
  endTime: string
  slotDurationMinutes: number
  active: boolean
}

const pageSize = 10
const dayOrder = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY']
const dayNames: Record<string, string> = {
  MONDAY: 'Thứ Hai', TUESDAY: 'Thứ Ba', WEDNESDAY: 'Thứ Tư', THURSDAY: 'Thứ Năm',
  FRIDAY: 'Thứ Sáu', SATURDAY: 'Thứ Bảy', SUNDAY: 'Chủ Nhật',
}

function money(value: number) {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(value)
}

function doctorDetailPath(id: number, from: string) {
  return `/doctors/${id}?from=${encodeURIComponent(from)}`
}

function DoctorSummary({ doctor, from }: { doctor: Doctor; from: string }) {
  return <article className="doctor-card">
    <div className="doctor-card__identity">
      <span className="doctor-avatar" aria-hidden="true">{doctor.fullName.trim().charAt(0).toLocaleUpperCase('vi-VN')}</span>
      <div><h2><Link to={doctorDetailPath(doctor.id, from)}>{doctor.fullName}</Link></h2><p>{doctor.specialization || 'Chưa cập nhật chuyên khoa'}</p></div>
    </div>
    <dl className="doctor-facts">
      <div><dt>Trình độ</dt><dd>{doctor.qualification || 'Đang cập nhật'}</dd></div>
      <div><dt>Kinh nghiệm</dt><dd>{doctor.yearsOfExperience} năm</dd></div>
      <div><dt>Phí khám</dt><dd>{money(doctor.consultationFee)}</dd></div>
    </dl>
    <Link className="button button--secondary doctor-card__action" to={doctorDetailPath(doctor.id, from)}>Xem hồ sơ</Link>
  </article>
}

export function DoctorListPage() {
  const location = useLocation()
  const [searchParams, setSearchParams] = useSearchParams()
  const specialization = searchParams.get('specialization') ?? ''
  const page = Math.max(0, Number.parseInt(searchParams.get('page') ?? '0', 10) || 0)
  const minExperience = searchParams.get('minExperience') ?? ''
  const maxFee = searchParams.get('maxFee') ?? ''
  const [specializationDraft, setSpecializationDraft] = useState(specialization)
  const [experienceDraft, setExperienceDraft] = useState(minExperience)
  const [feeDraft, setFeeDraft] = useState(maxFee)
  const debounceTimer = useRef<ReturnType<typeof setTimeout> | null>(null)

  useEffect(() => () => { if (debounceTimer.current) clearTimeout(debounceTimer.current) }, [])

  const currentPath = `${location.pathname}${location.search}`
  const requestUrl = useMemo(() => {
    const params = new URLSearchParams({ page: String(page), size: String(pageSize), sort: 'id,asc' })
    if (specialization) params.set('specialization', specialization)
    if (minExperience) params.set('minExperience', minExperience)
    if (maxFee) params.set('maxFee', maxFee)
    const hasFilters = Boolean(specialization || minExperience || maxFee)
    return `/api/doctors${hasFilters ? '/search' : ''}?${params.toString()}`
  }, [page, specialization, minExperience, maxFee])
  const doctors = useQuery({
    queryKey: ['doctors', requestUrl],
    queryFn: () => apiRequest<PageResponse<Doctor>>(requestUrl),
  })

  function updateParams(updates: Record<string, string>, resetPage = true) {
    const next = new URLSearchParams(searchParams)
    for (const [key, value] of Object.entries(updates)) {
      if (value) next.set(key, value)
      else next.delete(key)
    }
    if (resetPage) next.delete('page')
    setSearchParams(next)
  }

  function changeSpecialization(value: string) {
    setSpecializationDraft(value)
    if (debounceTimer.current) clearTimeout(debounceTimer.current)
    debounceTimer.current = setTimeout(() => updateParams({ specialization: value }), 350)
  }

  function submitFilters(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (debounceTimer.current) clearTimeout(debounceTimer.current)
    updateParams({ specialization: specializationDraft.trim(), minExperience: experienceDraft, maxFee: feeDraft })
  }

  function clearFilters() {
    if (debounceTimer.current) clearTimeout(debounceTimer.current)
    setSpecializationDraft('')
    setExperienceDraft('')
    setFeeDraft('')
    setSearchParams({})
  }

  return <section className="doctor-directory">
    <p className="eyebrow">Danh bạ chuyên môn</p>
    <h1>Tìm bác sĩ phù hợp</h1>
    <p className="muted">Lọc theo chuyên khoa, kinh nghiệm và mức phí khám. Mở hồ sơ để xem lịch làm việc hàng tuần.</p>

    <form className="doctor-filter card" onSubmit={submitFilters}>
      <label className="field">Chuyên khoa
        <input value={specializationDraft} onChange={(event) => changeSpecialization(event.target.value)} placeholder="Ví dụ: Tim mạch" />
      </label>
      <label className="field">Kinh nghiệm tối thiểu (năm)
        <input type="number" min="0" step="1" value={experienceDraft} onChange={(event) => setExperienceDraft(event.target.value)} placeholder="Tất cả" />
      </label>
      <label className="field">Phí khám tối đa (₫)
        <input type="number" min="0" step="10000" value={feeDraft} onChange={(event) => setFeeDraft(event.target.value)} placeholder="Tất cả" />
      </label>
      <div className="doctor-filter__actions">
        <Button type="submit">Áp dụng bộ lọc</Button>
        <Button type="button" variant="secondary" onClick={clearFilters}>Xóa lọc</Button>
      </div>
    </form>

    <div className="directory-heading">
      <h2>Danh sách bác sĩ</h2>
      {doctors.data && <p>{doctors.data.totalElements} kết quả</p>}
    </div>
    {doctors.isPending ? <StatusPanel kind="loading" title="Đang tải danh sách bác sĩ" description="Danh sách và bộ lọc của bạn đang được tải." /> : null}
    {doctors.isError ? <StatusPanel kind="error" title="Không tải được danh sách bác sĩ" description={doctors.error.message} onRetry={() => void doctors.refetch()} /> : null}
    {doctors.data && doctors.data.content.length === 0 ? <>
      <StatusPanel kind="empty" title="Không tìm thấy bác sĩ phù hợp" description="Thử đổi chuyên khoa hoặc nới rộng điều kiện kinh nghiệm và phí khám." />
      <Button variant="secondary" onClick={clearFilters}>Xóa bộ lọc</Button>
    </> : null}
    {doctors.data && doctors.data.content.length > 0 && <>
      <div className="doctor-table-wrap">
        <table className="doctor-table">
          <thead><tr><th>Bác sĩ</th><th>Trình độ</th><th>Kinh nghiệm</th><th>Phí khám</th><th><span className="sr-only">Thao tác</span></th></tr></thead>
          <tbody>{doctors.data.content.map((doctor) => <tr key={doctor.id}>
            <td><Link to={doctorDetailPath(doctor.id, currentPath)}><strong>{doctor.fullName}</strong></Link><span>{doctor.specialization}</span></td>
            <td>{doctor.qualification || 'Đang cập nhật'}</td><td>{doctor.yearsOfExperience} năm</td><td>{money(doctor.consultationFee)}</td>
            <td><Link className="button button--secondary" to={doctorDetailPath(doctor.id, currentPath)}>Xem hồ sơ</Link></td>
          </tr>)}</tbody>
        </table>
      </div>
      <div className="doctor-cards">{doctors.data.content.map((doctor) => <DoctorSummary key={doctor.id} doctor={doctor} from={currentPath} />)}</div>
      <Pagination page={doctors.data.pageNumber} totalPages={doctors.data.totalPages} onChange={(nextPage) => updateParams({ page: String(nextPage) }, false)} />
    </>}
  </section>
}

export function DoctorDetailPage() {
  const { doctorId } = useParams()
  const [searchParams] = useSearchParams()
  const id = Number(doctorId)
  const rawFrom = searchParams.get('from')
  const returnTo = rawFrom?.startsWith('/doctors?') || rawFrom === '/doctors' ? rawFrom : '/doctors'
  const doctor = useQuery({
    queryKey: ['doctor', id],
    queryFn: () => apiRequest<Doctor>(`/api/doctors/${id}`),
    enabled: Number.isSafeInteger(id) && id > 0,
  })
  const schedules = useQuery({
    queryKey: ['doctor', id, 'schedules'],
    queryFn: () => apiRequest<DoctorSchedule[]>(`/api/doctors/${id}/schedules`),
    enabled: doctor.isSuccess,
  })
  const activeSchedules = schedules.data?.filter((schedule) => schedule.active)
    .sort((a, b) => dayOrder.indexOf(a.dayOfWeek) - dayOrder.indexOf(b.dayOfWeek)) ?? []

  return <section className="doctor-detail">
    <Link className="back-link" to={returnTo}>← Quay lại danh sách bác sĩ</Link>
    {!Number.isSafeInteger(id) || id <= 0 ? <StatusPanel kind="error" title="Đường dẫn bác sĩ không hợp lệ" description="Hãy quay lại danh sách và chọn một hồ sơ bác sĩ." /> : null}
    {doctor.isPending && doctorId && Number.isSafeInteger(id) && id > 0 ? <StatusPanel kind="loading" title="Đang tải hồ sơ bác sĩ" description="Thông tin bác sĩ đang được tải." /> : null}
    {doctor.isError ? <StatusPanel kind="error" title="Không tải được hồ sơ bác sĩ" description={doctor.error.message} onRetry={() => void doctor.refetch()} /> : null}
    {doctor.data && <>
      <article className="doctor-profile card">
        <div className="doctor-profile__heading">
          <span className="doctor-avatar doctor-avatar--large" aria-hidden="true">{doctor.data.fullName.trim().charAt(0).toLocaleUpperCase('vi-VN')}</span>
          <div><p className="eyebrow">Hồ sơ bác sĩ</p><h1>{doctor.data.fullName}</h1><p className="doctor-specialty">{doctor.data.specialization}</p></div>
        </div>
        <dl className="doctor-profile__facts">
          <div><dt>Trình độ chuyên môn</dt><dd>{doctor.data.qualification || 'Đang cập nhật'}</dd></div>
          <div><dt>Kinh nghiệm</dt><dd>{doctor.data.yearsOfExperience} năm</dd></div>
          <div><dt>Phí khám</dt><dd>{money(doctor.data.consultationFee)}</dd></div>
          <div><dt>Liên hệ</dt><dd>{doctor.data.phoneNumber || doctor.data.email}</dd></div>
        </dl>
        <a className="button button--primary" href="#schedule">Xem lịch làm việc</a>
      </article>

      <section className="schedule-section" id="schedule" aria-labelledby="schedule-title">
        <div className="directory-heading"><div><p className="eyebrow">Lịch hàng tuần</p><h2 id="schedule-title">Lịch làm việc</h2></div></div>
        {schedules.isPending ? <StatusPanel kind="loading" title="Đang tải lịch làm việc" description="Lịch hàng tuần của bác sĩ đang được tải." /> : null}
        {schedules.isError ? <StatusPanel kind="error" title="Không tải được lịch làm việc" description={schedules.error.message} onRetry={() => void schedules.refetch()} /> : null}
        {schedules.data && activeSchedules.length === 0 ? <StatusPanel kind="empty" title="Chưa có lịch làm việc được công bố" description="Lịch khám của bác sĩ chưa được cập nhật." /> : null}
        {activeSchedules.length > 0 && <ul className="schedule-list">
          {activeSchedules.map((schedule) => <li key={schedule.id}>
            <strong>{dayNames[schedule.dayOfWeek] ?? schedule.dayOfWeek}</strong>
            <span>{schedule.startTime.slice(0, 5)}–{schedule.endTime.slice(0, 5)}</span>
            <small>Mỗi lượt {schedule.slotDurationMinutes} phút</small>
          </li>)}
        </ul>}
      </section>
    </>}
  </section>
}
