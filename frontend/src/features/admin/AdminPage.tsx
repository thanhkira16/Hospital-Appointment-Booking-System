import { useQuery } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { apiRequest } from '../../shared/api/client'
import type { PageResponse } from '../../shared/api/types'
import { StatusPanel } from '../../shared/components/StatusPanel'

interface SummaryItem { id: number }

export function AdminPage() {
  const doctors = useQuery({
    queryKey: ['admin-summary', 'doctors'],
    queryFn: () => apiRequest<PageResponse<SummaryItem>>('/api/doctors?page=0&size=1'),
  })
  const patients = useQuery({
    queryKey: ['admin-summary', 'patients'],
    queryFn: () => apiRequest<PageResponse<SummaryItem>>('/api/patients?page=0&size=1'),
  })
  const appointments = useQuery({
    queryKey: ['admin-summary', 'appointments'],
    queryFn: () => apiRequest<PageResponse<SummaryItem>>('/api/appointments/search?page=0&size=1'),
  })
  const hasError = doctors.isError || patients.isError || appointments.isError

  return <section className="admin-page">
    <p className="eyebrow">Điều hành hệ thống</p>
    <h1>Công cụ quản trị</h1>
    <p className="muted">Quản lý hồ sơ và tra cứu hoạt động khám bệnh từ một nơi. Các thao tác thay đổi dữ liệu vẫn yêu cầu xác nhận tại màn hình chi tiết.</p>
    {hasError && <StatusPanel kind="error" title="Không tải đủ số liệu tổng quan" description="Bạn vẫn có thể mở từng khu vực quản lý bên dưới." onRetry={() => { void doctors.refetch(); void patients.refetch(); void appointments.refetch() }} />}
    <div className="admin-metric-grid" aria-label="Tổng quan dữ liệu">
      <article className="admin-metric"><span>Bác sĩ</span><strong>{doctors.isPending ? '…' : (doctors.data?.totalElements ?? '—')}</strong><small>hồ sơ trong hệ thống</small></article>
      <article className="admin-metric"><span>Bệnh nhân</span><strong>{patients.isPending ? '…' : (patients.data?.totalElements ?? '—')}</strong><small>hồ sơ trong hệ thống</small></article>
      <article className="admin-metric"><span>Cuộc hẹn</span><strong>{appointments.isPending ? '…' : (appointments.data?.totalElements ?? '—')}</strong><small>tổng số cuộc hẹn</small></article>
    </div>
    <div className="admin-action-list">
      <article className="admin-action card"><div><h2>Quản lý bác sĩ</h2><p>Tìm kiếm theo chuyên khoa, mở hồ sơ để cập nhật hoặc xóa sau khi xác nhận.</p></div><Link className="button button--primary" to="/doctors">Mở danh sách bác sĩ</Link></article>
      <article className="admin-action card"><div><h2>Quản lý bệnh nhân</h2><p>Xem danh sách tối giản và truy cập thông tin chi tiết theo quyền quản trị.</p></div><Link className="button button--primary" to="/patients">Mở danh sách bệnh nhân</Link></article>
      <article className="admin-action card"><div><h2>Tra cứu cuộc hẹn</h2><p>Kết hợp trạng thái, khoảng ngày, mã bác sĩ và mã bệnh nhân để tìm chính xác.</p></div><Link className="button button--primary" to="/appointments">Tra cứu cuộc hẹn</Link></article>
      <article className="admin-action card"><div><h2>Lịch làm việc bác sĩ</h2><p>Chọn bác sĩ để xem và quản lý các khung giờ làm việc hàng tuần.</p></div><Link className="button button--secondary" to="/doctor">Quản lý lịch làm việc</Link></article>
    </div>
    <aside className="admin-notice" aria-label="Lưu ý quản trị"><strong>Lưu ý:</strong> Hệ thống hiện chưa hỗ trợ tạo tài khoản hoặc cấp quyền quản trị tại đây. Xóa hồ sơ là xóa vĩnh viễn và có thể bị từ chối nếu hồ sơ đang liên kết với lịch hẹn.</aside>
  </section>
}
