import { Link, NavLink, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../auth/AuthProvider'
import type { Role } from '../auth/types'

const labels: Record<Role, string> = { ADMIN: 'Quản trị viên', DOCTOR: 'Bác sĩ', PATIENT: 'Bệnh nhân' }
const destinations: Record<Role, { to: string; label: string }[]> = {
  ADMIN: [{ to: '/dashboard', label: 'Tổng quan' }, { to: '/admin', label: 'Quản trị' }, { to: '/doctors', label: 'Bác sĩ' }, { to: '/patients', label: 'Bệnh nhân' }, { to: '/appointments', label: 'Cuộc hẹn' }, { to: '/doctor', label: 'Lịch làm việc' }],
  DOCTOR: [{ to: '/dashboard', label: 'Tổng quan' }, { to: '/doctor', label: 'Lịch làm việc' }, { to: '/patients', label: 'Bệnh nhân' }, { to: '/appointments', label: 'Cuộc hẹn' }],
  PATIENT: [{ to: '/dashboard', label: 'Tổng quan' }, { to: '/doctors', label: 'Tìm bác sĩ' }, { to: '/patients/me', label: 'Hồ sơ cá nhân' }, { to: '/appointments', label: 'Cuộc hẹn' }],
}

const routeLabels: Record<string, string> = {
  '/': 'Trang chủ',
  '/dashboard': 'Tổng quan',
  '/doctors': 'Danh sách bác sĩ',
  '/patients': 'Danh sách bệnh nhân',
  '/appointments': 'Cuộc hẹn',
  '/admin': 'Quản trị',
  '/doctor': 'Lịch làm việc',
  '/patient': 'Đặt lịch khám',
  '/sign-in': 'Đăng nhập',
  '/register': 'Đăng ký bệnh nhân',
}

export function AppLayout() {
  const { session, signOut } = useAuth()
  const location = useLocation()
  const role = session?.user.role
  const currentPage = routeLabels[location.pathname] ?? 'Không gian làm việc'

  return <div className={`app-shell${session ? ' app-shell--authenticated' : ''}`}>
    <header className="site-header">
      <Link className="brand" to="/">Đặt lịch khám</Link>
      {!session ? <nav className="guest-nav" aria-label="Điều hướng chính">
        <NavLink to="/sign-in">Đăng nhập</NavLink>
        <NavLink to="/register">Đăng ký bệnh nhân</NavLink>
      </nav> : <div className="account-tools">
        <div className="account-identity"><span>{labels[role!]}</span><strong>{session.user.name}</strong></div>
        <button className="nav-button" onClick={signOut}>Đăng xuất</button>
      </div>}
    </header>
    {session && role && <div className="workspace-shell">
      <aside className="app-sidebar">
        <p className="sidebar-label">Điều hướng</p>
        <nav aria-label="Điều hướng theo vai trò">
          {destinations[role].map((destination) => <NavLink key={destination.to} to={destination.to} end>
            {destination.label}
          </NavLink>)}
        </nav>
      </aside>
      <main className="workspace-main">
        <div className="breadcrumb" aria-label="Điều hướng vị trí">
          <Link to="/dashboard">Không gian làm việc</Link><span aria-hidden="true">/</span><span aria-current="page">{currentPage}</span>
        </div>
        <Outlet />
      </main>
    </div>}
    {!session && <main className="main-content"><Outlet /></main>}
  </div>
}
