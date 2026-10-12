import { useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { useMutation, useQuery } from '@tanstack/react-query'
import { apiRequest } from '../../shared/api/client'
import { ApiError } from '../../shared/api/types'
import { Button } from '../../shared/components/Button'
import { StatusPanel } from '../../shared/components/StatusPanel'

interface Doctor { id: number; fullName: string; specialization: string; consultationFee: number }
interface Patient { id: number; fullName: string; email: string }
interface DaySlot { startTime: string; endTime: string; state: 'AVAILABLE' | 'BOOKED' | 'PAST' }
interface DayResponse { doctorId: number; date: string; slotDurationMinutes: number | null; slots: DaySlot[] }
interface Appointment { id: number; doctorName: string; appointmentDate: string; appointmentTime: string; status: string }

function money(value: number) {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(value)
}

function today() {
  const now = new Date()
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`
}

function timeLabel(value: string) {
  return value.slice(0, 5)
}

export function BookingPage() {
  const { doctorId: rawDoctorId } = useParams()
  const doctorId = Number(rawDoctorId)
  const [date, setDate] = useState(today)
  const [selectedSlot, setSelectedSlot] = useState('')
  const [reason, setReason] = useState('')
  const doctor = useQuery({
    queryKey: ['doctor', doctorId],
    queryFn: () => apiRequest<Doctor>(`/api/doctors/${doctorId}`),
    enabled: Number.isSafeInteger(doctorId) && doctorId > 0,
  })
  const patient = useQuery({ queryKey: ['patient', 'me'], queryFn: () => apiRequest<Patient>('/api/patients/me') })
  const slots = useQuery({
    queryKey: ['doctor', doctorId, 'slots', 'day', date],
    queryFn: () => apiRequest<DayResponse>(`/api/doctors/${doctorId}/slots/day?date=${date}`),
    enabled: Number.isSafeInteger(doctorId) && doctorId > 0 && Boolean(date),
  })
  const booking = useMutation({
    mutationFn: () => apiRequest<Appointment>(`/api/appointments/patients/${patient.data!.id}`, {
      method: 'POST',
      body: JSON.stringify({ doctorId, appointmentDate: date, appointmentTime: `${selectedSlot}:00`, reasonForVisit: reason.trim() || null }),
    }),
    onError: (error) => {
      if (error instanceof ApiError && error.status === 409) {
        setSelectedSlot('')
        void slots.refetch()
      }
    },
  })

  const visibleSlots = (slots.data?.slots ?? []).filter((slot) => slot.state !== 'PAST')
  const freeCount = visibleSlots.filter((slot) => slot.state === 'AVAILABLE').length

  function changeDate(value: string) {
    setDate(value)
    setSelectedSlot('')
    booking.reset()
  }

  if (!Number.isSafeInteger(doctorId) || doctorId <= 0) return <StatusPanel kind="error" title="Đường dẫn bác sĩ không hợp lệ" description="Hãy quay lại danh sách bác sĩ và thử lại." />
  if (booking.data) return <section className="booking-page">
    <StatusPanel kind="success" title="Đặt lịch thành công" description="Cuộc hẹn đã được lưu và đang chờ bác sĩ xác nhận. Bạn có thể theo dõi trong danh sách cuộc hẹn." />
    <article className="booking-confirmation card">
      <p className="eyebrow">Mã cuộc hẹn</p><h1>#{booking.data.id}</h1>
      <dl className="booking-summary"><div><dt>Bác sĩ</dt><dd>{booking.data.doctorName}</dd></div><div><dt>Thời gian</dt><dd>{booking.data.appointmentDate} · {timeLabel(booking.data.appointmentTime)}</dd></div><div><dt>Trạng thái</dt><dd>Chờ xác nhận</dd></div></dl>
      <Link className="button button--primary" to="/appointments">Xem cuộc hẹn của tôi</Link>
    </article>
  </section>

  return <section className="booking-page">
    <Link className="back-link" to={`/doctors/${doctorId}`}>← Quay lại hồ sơ bác sĩ</Link>
    <p className="eyebrow">Đặt lịch khám</p><h1>Chọn lịch với bác sĩ</h1>
    {doctor.isPending || patient.isPending ? <StatusPanel kind="loading" title="Đang tải thông tin đặt lịch" description="Thông tin bác sĩ và hồ sơ bệnh nhân đang được tải." /> : null}
    {doctor.isError || patient.isError ? <StatusPanel kind="error" title="Không thể chuẩn bị đặt lịch" description={patient.error?.message ?? doctor.error?.message} onRetry={() => { void doctor.refetch(); void patient.refetch() }} /> : null}
    {doctor.data && patient.data && <>
      <article className="booking-doctor card"><div><p className="eyebrow">Bác sĩ</p><h2>{doctor.data.fullName}</h2><p className="muted">{doctor.data.specialization}</p></div><strong>{money(doctor.data.consultationFee)}</strong></article>
      <div className="booking-layout">
        <div className="booking-controls card">
          <label className="field">Ngày khám
            <input type="date" min={today()} value={date} onChange={(event) => changeDate(event.target.value)} required />
          </label>
          {slots.isPending ? <StatusPanel kind="loading" title="Đang tải giờ khám còn trống" description="Đang kiểm tra lịch đã đặt." /> : null}
          {slots.isError ? <StatusPanel kind="error" title="Không tải được giờ khám" description={slots.error.message} onRetry={() => void slots.refetch()} /> : null}
          {slots.data && freeCount === 0 ? <StatusPanel kind="empty" title="Không còn giờ khám trống" description="Hãy chọn ngày khác để tìm lịch phù hợp." /> : null}
          {slots.data && visibleSlots.length > 0 && <fieldset className="slot-picker"><legend>Giờ khám</legend><div className="slot-grid">{visibleSlots.map((slot) => { const label = timeLabel(slot.startTime); const taken = slot.state !== 'AVAILABLE'; return <label className={`slot-option${selectedSlot === label ? ' slot-option--selected' : ''}${taken ? ' slot-option--booked' : ''}`} key={slot.startTime}><input type="radio" name="appointment-slot" value={label} disabled={taken} checked={selectedSlot === label} onChange={(event) => setSelectedSlot(event.target.value)} /><span>{label}{slot.state === 'BOOKED' ? ' · Đã đặt' : ''}</span></label> })}</div><p className="slot-legend">Khung giờ gạch mờ đã có bệnh nhân khác đặt và không thể chọn.</p></fieldset>}
          <label className="field">Lý do khám <span className="field-hint">Không bắt buộc, tối đa 500 ký tự</span>
            <textarea value={reason} maxLength={500} rows={4} onChange={(event) => setReason(event.target.value)} />
          </label>
        </div>
        <aside className="booking-review card"><p className="eyebrow">Xem lại</p><h2>Xác nhận lịch khám</h2><dl className="booking-summary"><div><dt>Bệnh nhân</dt><dd>{patient.data.fullName}</dd></div><div><dt>Ngày</dt><dd>{date}</dd></div><div><dt>Giờ</dt><dd>{selectedSlot ? timeLabel(selectedSlot) : 'Chưa chọn'}</dd></div><div><dt>Phí khám</dt><dd>{money(doctor.data.consultationFee)}</dd></div></dl>
          {booking.isError && <p className="form-error" role="alert">{booking.error instanceof ApiError && booking.error.status === 409 ? 'Khung giờ này vừa được đặt. Danh sách giờ khám sẽ được tải lại.' : booking.error.message}</p>}
          <Button type="button" disabled={!selectedSlot || booking.isPending} onClick={() => booking.mutate()}>{booking.isPending ? 'Đang gửi...' : 'Xác nhận đặt lịch'}</Button>
        </aside>
      </div>
    </>}
  </section>
}
