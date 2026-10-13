import { useEffect, useState, type FormEvent } from 'react'
import { Link, useNavigate, useParams, useSearchParams } from 'react-router-dom'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { apiRequest } from '../../shared/api/client'
import { ApiError, type FieldErrors, type PageResponse } from '../../shared/api/types'
import { useAuth } from '../../shared/auth/AuthProvider'
import { Button } from '../../shared/components/Button'
import { Dialog } from '../../shared/components/Dialog'
import { Pagination } from '../../shared/components/Pagination'
import { StatusPanel } from '../../shared/components/StatusPanel'

interface Patient {
  id: number
  fullName: string
  email: string
  dateOfBirth: string
  gender: string
  phoneNumber: string
  address: string
  medicalHistory?: string
}

interface PatientProfileValues {
  dateOfBirth: string
  gender: string
  phoneNumber: string
  address: string
  medicalHistory: string
}

const pageSize = 10
const genderLabels: Record<string, string> = { MALE: 'Nam', FEMALE: 'Nữ', OTHER: 'Khác' }

function patientDetailPath(id: number, from: string) {
  return `/patients/${id}?from=${encodeURIComponent(from)}`
}

export function PatientListPage() {
  const [page, setPage] = useState(0)
  const patients = useQuery({
    queryKey: ['patients', page],
    queryFn: () => apiRequest<PageResponse<Patient>>(`/api/patients?page=${page}&size=${pageSize}&sort=id,asc`),
  })

  return <section className="patient-directory">
    <p className="eyebrow">Hồ sơ bệnh nhân</p>
    <h1>Danh sách bệnh nhân</h1>
    <p className="muted">Chỉ thông tin cần thiết được hiển thị trong danh sách. Mở hồ sơ để xem chi tiết theo quyền truy cập.</p>
    {patients.isPending ? <StatusPanel kind="loading" title="Đang tải danh sách bệnh nhân" description="Danh sách bệnh nhân đang được tải." /> : null}
    {patients.isError ? <StatusPanel kind="error" title="Không tải được danh sách bệnh nhân" description={patients.error.message} onRetry={() => void patients.refetch()} /> : null}
    {patients.data && patients.data.content.length === 0 ? <StatusPanel kind="empty" title="Chưa có hồ sơ bệnh nhân" description="Danh sách bệnh nhân hiện chưa có dữ liệu." /> : null}
    {patients.data && patients.data.content.length > 0 && <>
      <div className="patient-table-wrap">
        <table className="patient-table">
          <thead><tr><th>Bệnh nhân</th><th>Email</th><th>Liên hệ</th><th><span className="sr-only">Thao tác</span></th></tr></thead>
          <tbody>{patients.data.content.map((patient) => <tr key={patient.id}>
            <td><Link to={patientDetailPath(patient.id, '/patients')}><strong>{patient.fullName}</strong></Link></td>
            <td>{patient.email}</td><td>{patient.phoneNumber}</td>
            <td><Link className="button button--secondary" to={patientDetailPath(patient.id, '/patients')}>Xem hồ sơ</Link></td>
          </tr>)}</tbody>
        </table>
      </div>
      <Pagination page={patients.data.pageNumber} totalPages={patients.data.totalPages} onChange={setPage} />
    </>}
  </section>
}

export function PatientDetailPage() {
  const { patientId } = useParams()
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { hasRole } = useAuth()
  const isAdmin = hasRole(['ADMIN'])
  const isPatient = hasRole(['PATIENT'])
  const rawFrom = searchParams.get('from')
  const returnTo = rawFrom === '/patients' ? rawFrom : '/dashboard'
  const requestedId = Number(patientId)
  const patient = useQuery({
    queryKey: patientId === 'me' ? ['patient', 'me'] : ['patient', requestedId],
    queryFn: () => apiRequest<Patient>(patientId === 'me' ? '/api/patients/me' : `/api/patients/${requestedId}`),
    enabled: patientId === 'me' ? isPatient : Number.isSafeInteger(requestedId) && requestedId > 0,
  })
  const [formValues, setFormValues] = useState<PatientProfileValues>({ dateOfBirth: '', gender: '', phoneNumber: '', address: '', medicalHistory: '' })
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({})
  const [formMessage, setFormMessage] = useState('')
  const [dirty, setDirty] = useState(false)
  const [deleteOpen, setDeleteOpen] = useState(false)
  const [deleteConfirmation, setDeleteConfirmation] = useState('')
  const [deleteMessage, setDeleteMessage] = useState('')

  const profileId = patient.data?.id ?? requestedId
  const canEdit = isAdmin || (isPatient && patientId === 'me')
  const updatePatient = useMutation({
    mutationFn: () => apiRequest<Patient>(`/api/patients/${profileId}`, {
      method: 'PUT',
      body: JSON.stringify(formValues),
    }),
    onSuccess: (updated) => {
      queryClient.setQueryData(patientId === 'me' ? ['patient', 'me'] : ['patient', profileId], updated)
      void queryClient.invalidateQueries({ queryKey: ['patients'] })
      setFormMessage('Đã lưu thay đổi hồ sơ bệnh nhân.')
      setFieldErrors({})
      setDirty(false)
    },
    onError: (error) => {
      setFormMessage(error instanceof ApiError ? error.message : 'Không thể lưu hồ sơ bệnh nhân.')
      setFieldErrors(error instanceof ApiError ? error.fieldErrors ?? {} : {})
    },
  })
  const deletePatient = useMutation({
    mutationFn: () => apiRequest<unknown>(`/api/patients/${profileId}`, { method: 'DELETE' }),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['patients'] })
      setDeleteOpen(false)
      navigate(returnTo, { replace: true })
    },
    onError: (error) => setDeleteMessage(error instanceof ApiError ? error.message : 'Không thể xóa hồ sơ bệnh nhân.'),
  })

  useEffect(() => {
    if (!patient.data) return
    // Form state must be initialized from the asynchronously loaded profile.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setFormValues({
      dateOfBirth: patient.data.dateOfBirth,
      gender: patient.data.gender,
      phoneNumber: patient.data.phoneNumber,
      address: patient.data.address,
      medicalHistory: patient.data.medicalHistory ?? '',
    })
    setDirty(false)
  }, [patient.data])

  useEffect(() => {
    if (!dirty) return
    const warn = (event: BeforeUnloadEvent) => { event.preventDefault(); event.returnValue = '' }
    window.addEventListener('beforeunload', warn)
    return () => window.removeEventListener('beforeunload', warn)
  }, [dirty])

  function updateFormValue(name: keyof PatientProfileValues, value: string) {
    setFormValues((current) => ({ ...current, [name]: value }))
    setFieldErrors((current) => ({ ...current, [name]: '' }))
    setFormMessage('')
    setDirty(true)
  }

  function submitProfile(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setFormMessage('')
    updatePatient.mutate()
  }

  return <section className="patient-detail">
    <Link className="back-link" to={returnTo}>← Quay lại</Link>
    {!patientId || (patientId !== 'me' && (!Number.isSafeInteger(requestedId) || requestedId <= 0)) ? <StatusPanel kind="error" title="Đường dẫn hồ sơ không hợp lệ" description="Hãy quay lại và chọn một hồ sơ bệnh nhân." /> : null}
    {patient.isPending ? <StatusPanel kind="loading" title="Đang tải hồ sơ bệnh nhân" description="Thông tin bệnh nhân đang được tải." /> : null}
    {patient.isError ? <StatusPanel kind="error" title="Không tải được hồ sơ bệnh nhân" description={patient.error.message} onRetry={() => void patient.refetch()} /> : null}
    {patient.data && <>
      <article className="patient-profile card">
        <p className="eyebrow">Hồ sơ bệnh nhân</p>
        <h1>{patient.data.fullName}</h1>
        <p className="muted">{patient.data.email}</p>
        <dl className="patient-profile__facts">
          <div><dt>Ngày sinh</dt><dd>{patient.data.dateOfBirth}</dd></div>
          <div><dt>Giới tính</dt><dd>{genderLabels[patient.data.gender] ?? patient.data.gender}</dd></div>
          <div><dt>Số điện thoại</dt><dd>{patient.data.phoneNumber}</dd></div>
          <div><dt>Địa chỉ</dt><dd>{patient.data.address}</dd></div>
        </dl>
        <section className="medical-history" aria-labelledby="medical-history-title">
          <h2 id="medical-history-title">Tiền sử bệnh</h2>
          <p>{patient.data.medicalHistory || 'Chưa cập nhật'}</p>
        </section>
        {canEdit && <form className="patient-profile-form" onSubmit={submitProfile}>
          <div className="directory-heading"><div><p className="eyebrow">Quản lý hồ sơ</p><h2>Cập nhật thông tin</h2></div></div>
          {formMessage && <p className={updatePatient.isError ? 'form-error' : 'form-success'} role="status">{formMessage}</p>}
          <label className="field">Ngày sinh
            <input type="date" value={formValues.dateOfBirth} max={new Date().toISOString().slice(0, 10)} onChange={(event) => updateFormValue('dateOfBirth', event.target.value)} aria-invalid={Boolean(fieldErrors.dateOfBirth)} required />
            {fieldErrors.dateOfBirth && <small className="field__error">{fieldErrors.dateOfBirth}</small>}
          </label>
          <label className="field">Giới tính
            <select value={formValues.gender} onChange={(event) => updateFormValue('gender', event.target.value)} aria-invalid={Boolean(fieldErrors.gender)} required>
              <option value="">Chọn giới tính</option><option value="MALE">Nam</option><option value="FEMALE">Nữ</option><option value="OTHER">Khác</option>
            </select>
            {fieldErrors.gender && <small className="field__error">{fieldErrors.gender}</small>}
          </label>
          <label className="field">Số điện thoại
            <input type="tel" value={formValues.phoneNumber} onChange={(event) => updateFormValue('phoneNumber', event.target.value)} aria-invalid={Boolean(fieldErrors.phoneNumber)} required />
            {fieldErrors.phoneNumber && <small className="field__error">{fieldErrors.phoneNumber}</small>}
          </label>
          <label className="field">Địa chỉ
            <input value={formValues.address} onChange={(event) => updateFormValue('address', event.target.value)} aria-invalid={Boolean(fieldErrors.address)} required />
            {fieldErrors.address && <small className="field__error">{fieldErrors.address}</small>}
          </label>
          <label className="field">Tiền sử bệnh
            <textarea value={formValues.medicalHistory} onChange={(event) => updateFormValue('medicalHistory', event.target.value)} rows={4} />
          </label>
          <Button type="submit" disabled={updatePatient.isPending}>{updatePatient.isPending ? 'Đang lưu...' : 'Lưu hồ sơ'}</Button>
        </form>}
        {isAdmin && <div className="patient-danger-zone">
          <h2>Xóa hồ sơ bệnh nhân</h2>
          <p>Thao tác này xóa dữ liệu vĩnh viễn và không thể hoàn tác.</p>
          <Button type="button" variant="danger" onClick={() => { setDeleteConfirmation(''); setDeleteMessage(''); setDeleteOpen(true) }}>Xóa hồ sơ</Button>
        </div>}
      </article>
    </>}
    <Dialog title="Xác nhận xóa hồ sơ" open={deleteOpen} onClose={() => setDeleteOpen(false)}>
      <p>Nhập lại tên bệnh nhân để xác nhận: <strong>{patient.data?.fullName}</strong>.</p>
      {deleteMessage && <p className="form-error" role="alert">{deleteMessage}</p>}
      <label className="field">Tên bệnh nhân
        <input value={deleteConfirmation} onChange={(event) => setDeleteConfirmation(event.target.value)} autoComplete="off" />
      </label>
      <div className="dialog-actions">
        <Button type="button" variant="secondary" onClick={() => setDeleteOpen(false)}>Hủy</Button>
        <Button type="button" variant="danger" disabled={deletePatient.isPending || deleteConfirmation !== patient.data?.fullName} onClick={() => deletePatient.mutate()}>{deletePatient.isPending ? 'Đang xóa...' : 'Xác nhận xóa'}</Button>
      </div>
    </Dialog>
  </section>
}
