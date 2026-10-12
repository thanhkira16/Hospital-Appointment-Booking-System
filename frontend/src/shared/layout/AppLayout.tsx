import { NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '../auth/AuthProvider'

const labels = { ADMIN: 'Quản trị viên', DOCTOR: 'Bác sĩ', PATIENT: 'Bệnh nhân' } as const

export function AppLayout() {
  const { session, signOut } = useAuth()
  const role = session?.user.role
  return <div className="app-shell">
    <header className="site-header">
      <NavLink className="brand" to="/">Đặt lịch khám</NavLink>
      <nav aria-label="Điều hướng chính">
        {session ? <>
          <NavLink to="/dashboard">Tổng quan</NavLink>
          {role === 'ADMIN' && <NavLink to="/admin">Quản trị</NavLink>}
          {role === 'DOCTOR' && <NavLink to="/doctor">Lịch làm việc</NavLink>}
          {role === 'PATIENT' && <NavLink to="/patient">Đặt lịch</NavLink>}
          <button className="nav-button" onClick={signOut}>Đăng xuất</button>
        </> : <>
          <NavLink to="/sign-in">Đăng nhập</NavLink>
          <NavLink to="/register">Đăng ký bệnh nhân</NavLink>
        </>}
      </nav>
    </header>
    {session && <aside className="role-banner" aria-label="Vai trò hiện tại">{labels[role!]}: {session.user.name}</aside>}
    <main className="main-content"><Outlet /></main>
  </div>
}
