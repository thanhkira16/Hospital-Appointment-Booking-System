import { useState } from 'react'
import { compareSchedules } from '../../shared/scheduleOrder'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { apiRequest } from '../../shared/api/client'
import { ApiError, type FieldErrors, type PageResponse } from '../../shared/api/types'
import { useAuth } from '../../shared/auth/AuthProvider'
import { Button } from '../../shared/components/Button'
import { Dialog } from '../../shared/components/Dialog'
import { StatusPanel } from '../../shared/components/StatusPanel'

interface Doctor { id: number; fullName: string; specialization: string }
interface Schedule { id: number; doctorId: number; dayOfWeek: string; startTime: string; endTime: string; slotDurationMinutes: number; active: boolean }
interface ScheduleValues { dayOfWeek: string; startTime: string; endTime: string; slotDurationMinutes: string }

const days = [
  ['MONDAY', 'Thứ Hai'], ['TUESDAY', 'Thứ Ba'], ['WEDNESDAY', 'Thứ Tư'], ['THURSDAY', 'Thứ Năm'],
  ['FRIDAY', 'Thứ Sáu'], ['SATURDAY', 'Thứ Bảy'], ['SUNDAY', 'Chủ Nhật'],
]

const emptyValues: ScheduleValues = { dayOfWeek: 'MONDAY', startTime: '08:00', endTime: '17:00', slotDurationMinutes: '30' }

function toRequest(values: ScheduleValues) {
  return { ...values, startTime: `${values.startTime}:00`, endTime: `${values.endTime}:00`, slotDurationMinutes: Number(values.slotDurationMinutes) }
}

function formatTime(value: string) { return value.slice(0, 5) }

export function ScheduleManagementPage() {
  const { hasRole } = useAuth()
  const isAdmin = hasRole(['ADMIN'])
  const queryClient = useQueryClient()
  const [selectedDoctorId, setSelectedDoctorId] = useState('')
  const [formValues, setFormValues] = useState<ScheduleValues>(emptyValues)
  const [editingId, setEditingId] = useState<number | null>(null)
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({})
  const [formMessage, setFormMessage] = useState('')
  const [deleteSchedule, setDeleteSchedule] = useState<Schedule | null>(null)

  const doctors = useQuery({
    queryKey: ['schedule-doctors'],
    queryFn: () => apiRequest<PageResponse<Doctor>>('/api/doctors?page=0&size=100&sort=user.fullName,asc'),
    enabled: isAdmin,
  })
  const currentDoctor = useQuery({
    queryKey: ['schedule-doctor-me'],
    queryFn: () => apiRequest<Doctor>('/api/doctors/me'),
    enabled: !isAdmin,
  })
  const doctorId = isAdmin ? Number(selectedDoctorId) : currentDoctor.data?.id
  const schedules = useQuery({
    queryKey: ['doctor', doctorId, 'schedules'],
    queryFn: () => apiRequest<Schedule[]>(`/api/doctors/${doctorId}/schedules`),
    enabled: doctorId !== undefined && Number.isSafeInteger(doctorId) && doctorId > 0,
  })
  const saveSchedule = useMutation({
    mutationFn: () => apiRequest<Schedule>(editingId ? `/api/schedules/${editingId}` : `/api/doctors/${doctorId}/schedules`, {
      method: editingId ? 'PUT' : 'POST',
      body: JSON.stringify(toRequest(formValues)),
    }),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['doctor', doctorId, 'schedules'] })
      setFormValues(emptyValues)
      setEditingId(null)
      setFieldErrors({})
      setFormMessage(editingId ? 'Đã cập nhật khung giờ.' : 'Đã thêm khung giờ.')
    },
    onError: (error) => {
      setFormMessage(error instanceof ApiError ? error.message : 'Không thể lưu khung giờ.')
      setFieldErrors(error instanceof ApiError ? error.fieldErrors ?? {} : {})
    },
  })
  const removeSchedule = useMutation({
    mutationFn: () => apiRequest<unknown>(`/api/schedules/${deleteSchedule!.id}`, { method: 'DELETE' }),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['doctor', doctorId, 'schedules'] })
      setDeleteSchedule(null)
    },
    onError: (error) => setFormMessage(error instanceof ApiError ? error.message : 'Không thể xóa khung giờ.'),
  })
  const activeSchedules = schedules.data?.filter((schedule) => schedule.active).sort(compareSchedules) ?? []

  function selectDoctor(value: string) {
    setSelectedDoctorId(value)
    setEditingId(null)
    setFormValues(emptyValues)
    setFormMessage('')
  }

  function updateValue(name: keyof ScheduleValues, value: string) {
    setFormValues((current) => ({ ...current, [name]: value }))
    setFieldErrors((current) => ({ ...current, [name]: '' }))
    setFormMessage('')
  }

  function editSchedule(schedule: Schedule) {
    setEditingId(schedule.id)
    setFormValues({ dayOfWeek: schedule.dayOfWeek, startTime: formatTime(schedule.startTime), endTime: formatTime(schedule.endTime), slotDurationMinutes: String(schedule.slotDurationMinutes) })
    setFieldErrors({})
    setFormMessage('')
  }

  return <section className="schedule-management">
    <p className="eyebrow">Lịch làm việc</p>
    <h1>Quản lý lịch hàng tuần</h1>
    <p className="muted">Lịch lặp theo từng ngày trong tuần. Hệ thống sẽ tính các giờ khám còn trống khi bệnh nhân đặt lịch.</p>
    {isAdmin && <label className="field schedule-doctor-picker">Bác sĩ
      <select value={selectedDoctorId} onChange={(event) => selectDoctor(event.target.value)}>
        <option value="">Chọn bác sĩ cần quản lý</option>
        {doctors.data?.content.map((doctor) => <option key={doctor.id} value={doctor.id}>{doctor.fullName} · {doctor.specialization}</option>)}
      </select>
    </label>}
    {doctors.isError || currentDoctor.isError ? <StatusPanel kind="error" title="Không tải được bác sĩ" description="Không thể xác định bác sĩ cần quản lý." onRetry={() => { void doctors.refetch(); void currentDoctor.refetch() }} /> : null}
    {doctorId && <>
      <form className="schedule-form card" onSubmit={(event) => {
        event.preventDefault()
        if (formValues.startTime >= formValues.endTime) {
          setFormMessage('Giờ bắt đầu phải trước giờ kết thúc.')
          setFieldErrors({ startTime: 'Giờ bắt đầu phải trước giờ kết thúc.' })
          return
        }
        const overlap = activeSchedules.some((schedule) => schedule.id !== editingId
          && schedule.dayOfWeek === formValues.dayOfWeek
          && formValues.startTime < formatTime(schedule.endTime)
          && formValues.endTime > formatTime(schedule.startTime))
        if (overlap) {
          setFormMessage('Lịch làm việc bị trùng hoặc chồng giờ trong cùng ngày. Vui lòng chọn khung giờ khác.')
          setFieldErrors({ startTime: 'Khung giờ này chồng lấn với lịch hiện có.' })
          return
        }
        setFormMessage('')
        saveSchedule.mutate()
      }}>
        <div className="directory-heading"><div><p className="eyebrow">{editingId ? 'Chỉnh sửa' : 'Thêm mới'}</p><h2>{editingId ? 'Cập nhật khung giờ' : 'Thêm khung giờ'}</h2></div></div>
        {formMessage && <p className={saveSchedule.isError || removeSchedule.isError || Object.values(fieldErrors).some(Boolean) ? 'form-error' : 'form-success'} role="status">{formMessage}</p>}
        <label className="field">Ngày trong tuần
          <select value={formValues.dayOfWeek} onChange={(event) => updateValue('dayOfWeek', event.target.value)}>{days.map(([value, label]) => <option key={value} value={value}>{label}</option>)}</select>
          {fieldErrors.dayOfWeek && <small className="field__error">{fieldErrors.dayOfWeek}</small>}
        </label>
        <label className="field">Giờ bắt đầu
          <input type="time" value={formValues.startTime} onChange={(event) => updateValue('startTime', event.target.value)} required />
          {fieldErrors.startTime && <small className="field__error">{fieldErrors.startTime}</small>}
        </label>
        <label className="field">Giờ kết thúc
          <input type="time" value={formValues.endTime} onChange={(event) => updateValue('endTime', event.target.value)} required />
          {fieldErrors.endTime && <small className="field__error">{fieldErrors.endTime}</small>}
        </label>
        <label className="field">Thời lượng mỗi lượt khám (phút)
          <input type="number" min="5" max="120" step="5" value={formValues.slotDurationMinutes} onChange={(event) => updateValue('slotDurationMinutes', event.target.value)} required />
          {fieldErrors.slotDurationMinutes && <small className="field__error">{fieldErrors.slotDurationMinutes}</small>}
        </label>
        <div className="dialog-actions"><Button type="submit" disabled={saveSchedule.isPending}>{saveSchedule.isPending ? 'Đang lưu...' : editingId ? 'Lưu thay đổi' : 'Thêm khung giờ'}</Button>{editingId && <Button type="button" variant="secondary" onClick={() => { setEditingId(null); setFormValues(emptyValues) }}>Hủy chỉnh sửa</Button>}</div>
      </form>
      <section className="schedule-management-list" aria-labelledby="managed-schedule-title">
        <div className="directory-heading"><div><p className="eyebrow">Lịch hiện tại</p><h2 id="managed-schedule-title">Các khung giờ đang mở</h2></div></div>
        {schedules.isPending ? <StatusPanel kind="loading" title="Đang tải lịch làm việc" description="Các khung giờ đang được tải." /> : null}
        {schedules.isError ? <StatusPanel kind="error" title="Không tải được lịch làm việc" description={schedules.error.message} onRetry={() => void schedules.refetch()} /> : null}
        {schedules.data && activeSchedules.length === 0 ? <StatusPanel kind="empty" title="Chưa có khung giờ" description="Thêm khung giờ đầu tiên cho bác sĩ này." /> : null}
        {activeSchedules.length > 0 && <ul className="managed-schedule-list">{activeSchedules.map((schedule) => <li key={schedule.id}><div><strong>{days.find(([value]) => value === schedule.dayOfWeek)?.[1] ?? schedule.dayOfWeek}</strong><span>{formatTime(schedule.startTime)}–{formatTime(schedule.endTime)} · mỗi {schedule.slotDurationMinutes} phút</span></div><div className="dialog-actions"><Button type="button" variant="secondary" onClick={() => editSchedule(schedule)}>Sửa</Button><Button type="button" variant="danger" onClick={() => setDeleteSchedule(schedule)}>Xóa</Button></div></li>)}</ul>}
      </section>
    </>}
    <Dialog title="Xóa khung giờ" open={deleteSchedule !== null} onClose={() => setDeleteSchedule(null)}>
      <p>Khung giờ <strong>{deleteSchedule && `${formatTime(deleteSchedule.startTime)}–${formatTime(deleteSchedule.endTime)}`}</strong> sẽ bị xóa khỏi lịch tuần.</p>
      <div className="dialog-actions"><Button type="button" variant="secondary" onClick={() => setDeleteSchedule(null)}>Hủy</Button><Button type="button" variant="danger" disabled={removeSchedule.isPending} onClick={() => removeSchedule.mutate()}>{removeSchedule.isPending ? 'Đang xóa...' : 'Xác nhận xóa'}</Button></div>
    </Dialog>
  </section>
}
