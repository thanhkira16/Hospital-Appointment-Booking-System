import { useState, type FormEvent } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { apiRequest } from '../../shared/api/client'
import { ApiError, type FieldErrors } from '../../shared/api/types'
import { useAuth } from '../../shared/auth/AuthProvider'
import { readFormDraft } from '../../shared/auth/drafts'
import type { AuthSession, Role } from '../../shared/auth/types'
import { useFormDraft } from '../../shared/auth/useFormDraft'
import { Button } from '../../shared/components/Button'
import { TextInput } from '../../shared/components/TextInput'

interface AuthResponse {
  userId: number
  fullName: string
  email: string
  role: Role
  token: string
  tokenType: string
}

interface SignInValues { email: string; password: string }
interface RegistrationValues extends SignInValues {
  fullName: string
  phoneNumber: string
  dateOfBirth: string
  gender: string
  address: string
  medicalHistory: string
}

const emptySignIn: SignInValues = { email: '', password: '' }
const emptyRegistration: RegistrationValues = {
  fullName: '', email: '', password: '', phoneNumber: '', dateOfBirth: '',
  gender: '', address: '', medicalHistory: '',
}

function roleHome(role: Role) {
  return role === 'ADMIN' ? '/admin' : role === 'DOCTOR' ? '/doctor' : '/patient'
}

function sessionFromResponse(response: AuthResponse): AuthSession {
  return {
    token: response.token,
    user: { id: response.userId, name: response.fullName, email: response.email, role: response.role },
  }
}

function isValidEmail(email: string) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)
}

function requestErrorMessage(error: unknown, fallback: string) {
  if (!(error instanceof ApiError)) return fallback
  if (error.status === 409) return 'Email này đã được đăng ký. Vui lòng đăng nhập hoặc dùng email khác.'
  if (error.status === 401) return 'Email hoặc mật khẩu không chính xác.'
  return error.message || fallback
}

export function SignInPage() {
  const { signIn } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [values, setValues] = useState<SignInValues>(() => readFormDraft<SignInValues>('sign-in') ?? emptySignIn)
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({})
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)
  const draft = useFormDraft('sign-in', values)

  const update = (field: keyof SignInValues, value: string) => {
    setValues((current) => ({ ...current, [field]: value }))
    setFieldErrors((current) => Object.fromEntries(Object.entries(current).filter(([name]) => name !== field)))
    setError('')
  }

  const onSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    const errors: FieldErrors = {}
    if (!isValidEmail(values.email.trim())) errors.email = 'Vui lòng nhập email hợp lệ.'
    if (!values.password) errors.password = 'Vui lòng nhập mật khẩu.'
    else if (values.password.length < 6) errors.password = 'Mật khẩu phải có ít nhất 6 ký tự.'
    setFieldErrors(errors)
    if (Object.keys(errors).length) return

    setBusy(true)
    setError('')
    try {
      const response = await apiRequest<AuthResponse>('/api/auth/login', {
        method: 'POST', body: JSON.stringify({ email: values.email.trim(), password: values.password }),
      })
      signIn(sessionFromResponse(response))
      draft.clear()
      const requestedPath = (location.state as { from?: string } | null)?.from
      navigate(requestedPath ?? roleHome(response.role), { replace: true })
    } catch (cause) {
      setError(requestErrorMessage(cause, 'Không thể đăng nhập lúc này. Vui lòng thử lại.'))
      if (cause instanceof ApiError && cause.fieldErrors) setFieldErrors(cause.fieldErrors)
    } finally {
      setBusy(false)
    }
  }

  return <section className="card form-card">
    <h1>Đăng nhập</h1>
    <p className="muted">Đăng nhập bằng tài khoản bệnh viện của bạn.</p>
    {error && <p className="form-error" role="alert">{error}</p>}
    <form onSubmit={onSubmit} noValidate>
      <TextInput label="Email" type="email" value={values.email} onChange={(event) => update('email', event.target.value)} error={fieldErrors.email} autoComplete="email" required />
      <TextInput label="Mật khẩu" type="password" value={values.password} onChange={(event) => update('password', event.target.value)} error={fieldErrors.password} autoComplete="current-password" required />
      <Button type="submit" disabled={busy}>{busy ? 'Đang đăng nhập…' : 'Đăng nhập'}</Button>
    </form>
    <p className="muted">Chưa có tài khoản? <Link to="/register">Đăng ký bệnh nhân</Link></p>
  </section>
}

export function RegisterPage() {
  const { signIn } = useAuth()
  const navigate = useNavigate()
  const [values, setValues] = useState<RegistrationValues>(() => readFormDraft<RegistrationValues>('register-patient') ?? emptyRegistration)
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({})
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)
  const draft = useFormDraft('register-patient', values)

  const update = (field: keyof RegistrationValues, value: string) => {
    setValues((current) => ({ ...current, [field]: value }))
    setFieldErrors((current) => Object.fromEntries(Object.entries(current).filter(([name]) => name !== field)))
    setError('')
  }

  const onSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    const errors: FieldErrors = {}
    if (!values.fullName.trim()) errors.fullName = 'Vui lòng nhập họ và tên.'
    if (!isValidEmail(values.email.trim())) errors.email = 'Vui lòng nhập email hợp lệ.'
    if (values.password.length < 6) errors.password = 'Mật khẩu phải có ít nhất 6 ký tự.'
    if (!values.phoneNumber.trim()) errors.phoneNumber = 'Vui lòng nhập số điện thoại.'
    if (!values.dateOfBirth) errors.dateOfBirth = 'Vui lòng chọn ngày sinh.'
    if (!values.gender) errors.gender = 'Vui lòng chọn giới tính.'
    if (!values.address.trim()) errors.address = 'Vui lòng nhập địa chỉ.'
    setFieldErrors(errors)
    if (Object.keys(errors).length) return

    setBusy(true)
    setError('')
    try {
      const response = await apiRequest<AuthResponse>('/api/auth/register', {
        method: 'POST',
        body: JSON.stringify({
          fullName: values.fullName.trim(), email: values.email.trim(), password: values.password,
          role: 'PATIENT', phoneNumber: values.phoneNumber.trim(), dateOfBirth: values.dateOfBirth,
          gender: values.gender, address: values.address.trim(),
          medicalHistory: values.medicalHistory.trim() || undefined,
        }),
      })
      signIn(sessionFromResponse(response))
      draft.clear()
      navigate(roleHome(response.role), { replace: true })
    } catch (cause) {
      setError(requestErrorMessage(cause, 'Không thể tạo tài khoản lúc này. Vui lòng thử lại.'))
      if (cause instanceof ApiError && cause.fieldErrors) setFieldErrors(cause.fieldErrors)
    } finally {
      setBusy(false)
    }
  }

  return <section className="card form-card">
    <h1>Đăng ký bệnh nhân</h1>
    <p className="muted">Tạo tài khoản để tìm bác sĩ và đặt lịch khám.</p>
    {error && <p className="form-error" role="alert">{error}</p>}
    <form onSubmit={onSubmit} noValidate>
      <TextInput label="Họ và tên" value={values.fullName} onChange={(event) => update('fullName', event.target.value)} error={fieldErrors.fullName} autoComplete="name" required />
      <TextInput label="Email" type="email" value={values.email} onChange={(event) => update('email', event.target.value)} error={fieldErrors.email} autoComplete="email" required />
      <TextInput label="Mật khẩu" type="password" value={values.password} onChange={(event) => update('password', event.target.value)} error={fieldErrors.password} autoComplete="new-password" minLength={6} required />
      <TextInput label="Số điện thoại" type="tel" value={values.phoneNumber} onChange={(event) => update('phoneNumber', event.target.value)} error={fieldErrors.phoneNumber} autoComplete="tel" required />
      <TextInput label="Ngày sinh" type="date" value={values.dateOfBirth} onChange={(event) => update('dateOfBirth', event.target.value)} error={fieldErrors.dateOfBirth} required />
      <label className="field" htmlFor="gender"><span>Giới tính</span>
        <select id="gender" value={values.gender} onChange={(event) => update('gender', event.target.value)} aria-invalid={Boolean(fieldErrors.gender)} aria-describedby={fieldErrors.gender ? 'gender-error' : undefined} required>
          <option value="">Chọn giới tính</option><option value="Nam">Nam</option><option value="Nữ">Nữ</option><option value="Khác">Khác</option>
        </select>
        {fieldErrors.gender && <small id="gender-error" className="field__error">{fieldErrors.gender}</small>}
      </label>
      <TextInput label="Địa chỉ" value={values.address} onChange={(event) => update('address', event.target.value)} error={fieldErrors.address} autoComplete="street-address" required />
      <label className="field" htmlFor="medical-history"><span>Tiền sử bệnh (không bắt buộc)</span>
        <textarea id="medical-history" rows={3} value={values.medicalHistory} onChange={(event) => update('medicalHistory', event.target.value)} />
      </label>
      <Button type="submit" disabled={busy}>{busy ? 'Đang tạo tài khoản…' : 'Tạo tài khoản'}</Button>
    </form>
    <p className="muted">Đã có tài khoản? <Link to="/sign-in">Đăng nhập</Link></p>
  </section>
}
