import { BrowserRouter, Route, Routes, useNavigate } from 'react-router-dom'
import type { ReactNode } from 'react'
import { AuthProvider, useAuth } from './shared/auth/AuthProvider'
import { Dialog } from './shared/components/Dialog'
import { AppLayout } from './shared/layout/AppLayout'
import { FeatureErrorBoundary } from './routes/FeatureErrorBoundary'
import { RequireAuth } from './routes/RequireAuth'
import { RolePlaceholderPage, WelcomePage } from './features/foundation/pages'
import { DashboardPage } from './features/dashboard/DashboardPage'
import { RegisterPage, SignInPage } from './features/auth/pages'
import { DoctorDetailPage, DoctorListPage } from './features/doctors/pages'
import { PatientDetailPage, PatientListPage } from './features/patients/pages'
import { ScheduleManagementPage } from './features/schedules/pages'
import { BookingPage } from './features/appointments/BookingPage'
import { AppointmentDetailPage, AppointmentListPage } from './features/appointments/pages'

const feature = (node: ReactNode) => <FeatureErrorBoundary>{node}</FeatureErrorBoundary>

function Application() {
  const { sessionExpired, acknowledgeExpiredSession } = useAuth()
  const navigate = useNavigate()
  const closeExpiredSession = () => {
    acknowledgeExpiredSession()
    navigate('/sign-in', { replace: true })
  }
  return <>
    <Routes>
      <Route element={<AppLayout />}>
        <Route index element={feature(<WelcomePage />)} />
        <Route path="sign-in" element={feature(<SignInPage />)} />
        <Route path="register" element={feature(<RegisterPage />)} />
        <Route element={<RequireAuth />}>
          <Route path="dashboard" element={feature(<DashboardPage />)} />
          <Route path="doctors" element={feature(<DoctorListPage />)} />
          <Route path="doctors/:doctorId" element={feature(<DoctorDetailPage />)} />
          <Route element={<RequireAuth roles={['PATIENT']} />}>
            <Route path="doctors/:doctorId/book" element={feature(<BookingPage />)} />
          </Route>
            <Route path="appointments" element={feature(<AppointmentListPage />)} />
            <Route path="appointments/:appointmentId" element={feature(<AppointmentDetailPage />)} />
          <Route path="patients/me" element={feature(<PatientDetailPage />)} />
          <Route element={<RequireAuth roles={['ADMIN', 'DOCTOR']} />}>
            <Route path="patients" element={feature(<PatientListPage />)} />
          </Route>
          <Route path="patients/:patientId" element={feature(<PatientDetailPage />)} />
          <Route element={<RequireAuth roles={['ADMIN']} />}>
            <Route path="admin" element={feature(<RolePlaceholderPage title="Quản trị hệ thống" description="Công cụ quản trị sẽ xuất hiện tại đây." />)} />
          </Route>
          <Route element={<RequireAuth roles={['ADMIN', 'DOCTOR']} />}>
            <Route path="doctor" element={feature(<ScheduleManagementPage />)} />
          </Route>
          <Route element={<RequireAuth roles={['PATIENT']} />}>
            <Route path="patient" element={feature(<RolePlaceholderPage title="Đặt lịch khám" description="Tìm bác sĩ và chọn khung giờ phù hợp." />)} />
          </Route>
        </Route>
        <Route path="*" element={feature(<WelcomePage />)} />
      </Route>
    </Routes>
    <Dialog title="Phiên làm việc đã hết hạn" open={sessionExpired} onClose={closeExpiredSession}>
      <p>Thông tin đang nhập đã được lưu trên thiết bị này. Vui lòng đăng nhập lại để tiếp tục.</p>
    </Dialog>
  </>
}

export function App() {
  return <BrowserRouter><AuthProvider><Application /></AuthProvider></BrowserRouter>
}
