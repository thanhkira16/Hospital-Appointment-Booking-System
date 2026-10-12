import { useState, type FormEvent } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../../shared/auth/AuthProvider'
import type { Role } from '../../shared/auth/types'
import { Button } from '../../shared/components/Button'
import { StatusPanel } from '../../shared/components/StatusPanel'
import { TextInput } from '../../shared/components/TextInput'
import { useFormDraft } from '../../shared/auth/useFormDraft'

export function WelcomePage() {
  return <section className="hero">
    <p className="eyebrow">Chăm sóc sức khỏe, rõ ràng hơn</p>
    <h1>Đặt lịch khám phù hợp với bạn</h1>
    <p>Tìm bác sĩ, chọn lịch phù hợp và theo dõi cuộc hẹn trên mọi thiết bị.</p>
  </section>
}

export function SignInPage() {
  const { signIn } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [name, setName] = useState('')
  const [role, setRole] = useState<Role>('PATIENT')
  const [error, setError] = useState('')
  const draft = useFormDraft('sign-in', { name, role })

  const onSubmit = (event: FormEvent) => {
    event.preventDefault()
    if (!name.trim()) { setError('Vui lòng nhập tên hiển thị.'); return }
    signIn({ token: 'demo-token', user: { id: 0, name: name.trim(), role } })
    draft.clear()
    const destination = (location.state as { from?: string } | null)?.from ?? '/dashboard'
    navigate(destination, { replace: true })
  }

  return <section className="card form-card">
    <h1>Đăng nhập</h1>
    <p className="muted">Khung đăng nhập tạm thời để kiểm tra điều hướng theo vai trò. Luồng xác thực thật sẽ được triển khai ở hạng mục 01.</p>
    <form onSubmit={onSubmit} noValidate>
      <TextInput label="Tên hiển thị" value={name} onChange={(event) => { setName(event.target.value); setError('') }} error={error} autoComplete="name" />
      <label className="field" htmlFor="role"><span>Vai trò thử nghiệm</span>
        <select id="role" value={role} onChange={(event) => setRole(event.target.value as Role)}>
          <option value="PATIENT">Bệnh nhân</option>
          <option value="DOCTOR">Bác sĩ</option>
          <option value="ADMIN">Quản trị viên</option>
        </select>
      </label>
      <Button type="submit">Tiếp tục</Button>
    </form>
  </section>
}

export function DashboardPage() {
  const { session } = useAuth()
  return <section>
    <p className="eyebrow">Tổng quan</p>
    <h1>Chào {session?.user.name}</h1>
    <StatusPanel kind="empty" title="Chưa có dữ liệu hiển thị" description="Các màn hình theo vai trò sẽ được bổ sung ở những hạng mục tiếp theo." />
  </section>
}

export function RolePlaceholderPage({ title, description }: { title: string; description: string }) {
  return <section>
    <p className="eyebrow">Không gian làm việc</p>
    <h1>{title}</h1>
    <StatusPanel kind="loading" title="Đang chuẩn bị màn hình" description={description} />
  </section>
}
