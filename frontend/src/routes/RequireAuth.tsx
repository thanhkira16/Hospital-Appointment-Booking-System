import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../shared/auth/AuthProvider'
import type { Role } from '../shared/auth/types'
import { StatusPanel } from '../shared/components/StatusPanel'

export function RequireAuth({ roles }: { roles?: Role[] }) {
  const { session, hasRole } = useAuth()
  const location = useLocation()
  if (!session) return <Navigate to="/sign-in" replace state={{ from: location.pathname }} />
  if (roles && !hasRole(roles)) {
    return <StatusPanel kind="error" title="Bạn không có quyền truy cập" description="Hành động không phù hợp với vai trò của bạn đã được ẩn." />
  }
  return <Outlet />
}
