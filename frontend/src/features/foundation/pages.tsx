import { useAuth } from '../../shared/auth/AuthProvider'
import { StatusPanel } from '../../shared/components/StatusPanel'

export function WelcomePage() {
  return <section className="hero">
    <p className="eyebrow">Chăm sóc sức khỏe, rõ ràng hơn</p>
    <h1>Đặt lịch khám phù hợp với bạn</h1>
    <p>Tìm bác sĩ, chọn lịch phù hợp và theo dõi cuộc hẹn trên mọi thiết bị.</p>
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
