import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { apiRequest } from '../../shared/api/client'
import type { PageResponse } from '../../shared/api/types'
import { Button } from '../../shared/components/Button'
import { StatusPanel } from '../../shared/components/StatusPanel'

interface Doctor { id: number; fullName: string; specialization: string }
type AppointmentStatus = 'PENDING' | 'CONFIRMED' | 'COMPLETED' | 'CANCELLED'
interface Booking { appointmentId: number; status: AppointmentStatus; patientId: number; patientName: string; reasonForVisit: string | null }
interface DaySlot { startTime: string; endTime: string; state: 'AVAILABLE' | 'BOOKED' | 'PAST'; booking: Booking | null }
interface DaySchedule { doctorId: number; date: string; slotDurationMinutes: number | null; slots: DaySlot[] }

const statusLabels: Record<AppointmentStatus, string> = { PENDING: 'Chờ xác nhận', CONFIRMED: 'Đã xác nhận', COMPLETED: 'Đã hoàn thành', CANCELLED: 'Đã hủy' }

function toIso(date: Date) {
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

function shiftDay(value: string, days: number) {
  const [year, month, day] = value.split('-').map(Number)
  return toIso(new Date(year, month - 1, day + days))
}

function weekdayLabel(value: string) {
  const [year, month, day] = value.split('-').map(Number)
  return new Intl.DateTimeFormat('vi-VN', { weekday: 'long', day: '2-digit', month: '2-digit', year: 'numeric' }).format(new Date(year, month - 1, day))
}

const hm = (value: string) => value.slice(0, 5)

export function DoctorAvailabilityPage() {
  const [doctorId, setDoctorId] = useState('')
  const [date, setDate] = useState(() => toIso(new Date()))
  const doctors = useQuery({
    queryKey: ['availability-doctors'],
    queryFn: () => apiRequest<PageResponse<Doctor>>('/api/doctors?page=0&size=100&sort=user.fullName,asc'),
  })
  const id = Number(doctorId)
  const day = useQuery({
    queryKey: ['admin', 'doctor', id, 'day-schedule', date],
    queryFn: () => apiRequest<DaySchedule>(`/api/admin/doctors/${id}/day-schedule?date=${date}`),
    enabled: Number.isSafeInteger(id) && id > 0 && Boolean(date),
  })
  const slots = day.data?.slots ?? []
  const booked = slots.filter((slot) => slot.state === 'BOOKED').length
  const available = slots.filter((slot) => slot.state === 'AVAILABLE').length

  return <section className="availability-page">
    <Link className="back-link" to="/admin">← Quay lại quản trị</Link>
    <p className="eyebrow">Điều hành hệ thống</p>
    <h1>Lịch rảnh và lịch khám của bác sĩ</h1>
    <p className="muted">Chọn bác sĩ và ngày để biết khung giờ nào còn trống, khung giờ nào đã có bệnh nhân đặt.</p>
    <div className="availability-controls card">
      <label className="field">Bác sĩ
        <select value={doctorId} onChange={(event) => setDoctorId(event.target.value)}>
          <option value="">Chọn bác sĩ</option>
          {doctors.data?.content.map((doctor) => <option key={doctor.id} value={doctor.id}>{doctor.fullName} — {doctor.specialization}</option>)}
        </select>
      </label>
      <div className="availability-date">
        <Button type="button" variant="secondary" aria-label="Ngày trước" onClick={() => setDate(shiftDay(date, -1))}>←</Button>
        <label className="field">Ngày<input type="date" value={date} onChange={(event) => event.target.value && setDate(event.target.value)} /></label>
        <Button type="button" variant="secondary" aria-label="Ngày sau" onClick={() => setDate(shiftDay(date, 1))}>→</Button>
        <Button type="button" variant="secondary" onClick={() => setDate(toIso(new Date()))}>Hôm nay</Button>
      </div>
    </div>
    {doctors.isError && <StatusPanel kind="error" title="Không tải được danh sách bác sĩ" description={doctors.error.message} onRetry={() => void doctors.refetch()} />}
    {!doctorId && <StatusPanel kind="empty" title="Chưa chọn bác sĩ" description="Hãy chọn một bác sĩ để xem lịch trong ngày." />}
    {doctorId && day.isPending && <StatusPanel kind="loading" title="Đang tải lịch trong ngày" description="Đang kiểm tra khung giờ và cuộc hẹn." />}
    {day.isError && <StatusPanel kind="error" title="Không tải được lịch" description={day.error.message} onRetry={() => void day.refetch()} />}
    {day.data && <>
      <h2 className="availability-day">{weekdayLabel(date)}</h2>
      <div className="availability-summary" aria-label="Tóm tắt trong ngày">
        <article><span>Còn trống</span><strong>{available}</strong></article>
        <article><span>Đã có lịch</span><strong>{booked}</strong></article>
        <article><span>Tổng khung giờ</span><strong>{slots.length}</strong></article>
      </div>
      {slots.length === 0
        ? <StatusPanel kind="empty" title="Bác sĩ không có lịch làm việc ngày này" description="Thêm khung giờ làm việc tại mục Lịch làm việc nếu cần mở lịch cho ngày này." />
        : <ol className="availability-list">{slots.map((slot) => <li key={slot.startTime} className={`availability-row availability-row--${slot.state.toLowerCase()}`}>
          <strong className="availability-time">{hm(slot.startTime)} – {hm(slot.endTime)}</strong>
          {slot.state === 'BOOKED' && slot.booking
            ? <div className="availability-detail"><span className="availability-badge availability-badge--booked">Đã có lịch</span><Link to={`/patients/${slot.booking.patientId}`}><strong>{slot.booking.patientName}</strong></Link><span className="muted">{slot.booking.reasonForVisit || 'Không ghi lý do khám'}</span><span className={`appointment-status appointment-status--${slot.booking.status.toLowerCase()}`}>{statusLabels[slot.booking.status]}</span><Link className="button button--secondary" to={`/appointments/${slot.booking.appointmentId}`}>Chi tiết</Link></div>
            : slot.state === 'PAST'
              ? <div className="availability-detail"><span className="availability-badge availability-badge--past">Đã qua giờ, không có lịch</span></div>
              : <div className="availability-detail"><span className="availability-badge availability-badge--free">Còn trống</span></div>}
        </li>)}</ol>}
    </>}
  </section>
}
