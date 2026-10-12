import { StatusPanel } from '../../shared/components/StatusPanel'

export function WelcomePage() {
  return <section className="hero">
    <p className="eyebrow">Chăm sóc sức khỏe, rõ ràng hơn</p>
    <h1>Đặt lịch khám phù hợp với bạn</h1>
    <p>Tìm bác sĩ, chọn lịch phù hợp và theo dõi cuộc hẹn trên mọi thiết bị.</p>
  </section>
}

export function RolePlaceholderPage({ title, description }: { title: string; description: string }) {
  return <section>
    <p className="eyebrow">Không gian làm việc</p>
    <h1>{title}</h1>
    <StatusPanel kind="loading" title="Đang chuẩn bị màn hình" description={description} />
  </section>
}
