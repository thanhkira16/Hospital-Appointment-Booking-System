# Kế hoạch xây dựng Frontend

Các file trong thư mục này chia UI theo tính năng, dựa trên API hiện có của backend. Mỗi plan mô tả màn hình, luồng thao tác, endpoint, trạng thái giao diện và tiêu chí hoàn thành.

## Thứ tự triển khai đề xuất

1. [00 — Nền tảng giao diện](00-nen-tang-giao-dien.md)
2. [01 — Đăng ký, đăng nhập và phiên làm việc](01-xac-thuc.md)
3. [02 — Điều hướng và dashboard theo vai trò](02-dashboard-va-dieu-huong.md)
4. [03 — Tìm kiếm và xem bác sĩ](03-danh-sach-bac-si.md)
5. [04 — Hồ sơ bác sĩ](04-ho-so-bac-si.md)
6. [05 — Hồ sơ bệnh nhân](05-ho-so-benh-nhan.md)
7. [06 — Lịch làm việc của bác sĩ](06-lich-lam-viec.md)
8. [07 — Chọn slot và đặt lịch](07-dat-lich-kham.md)
9. [08 — Quản lý cuộc hẹn](08-quan-ly-cuoc-hen.md)
10. [09 — Hồ sơ khám bệnh](09-ho-so-kham-benh.md)
11. [10 — Công cụ quản trị](10-quan-tri.md)

## Các điểm backend cần xử lý trước khi hoàn thiện UI

- ✅ `GET /api/patients/me` và `GET /api/doctors/me` đã được thêm vào backend — dùng để lấy `patientId`/`doctorId` sau đăng nhập thay vì suy từ `userId`.
- Endpoint đăng ký công khai nhận `role`, hiện cho phép gửi `ADMIN`. Backend cần chặn tự đăng ký quyền ADMIN trước khi phát hành UI công khai; form không được cho chọn ADMIN.
- Backend chưa có đổi/quên mật khẩu, refresh token, logout server-side, dời cuộc hẹn hoặc API trung tâm thông báo. JWT hết hạn sau 24h; UI xử lý 401 bằng thông báo rõ ràng (xem 00, 01).
- Xóa hồ sơ bác sĩ/bệnh nhân hiện là xóa thật (không có soft-delete); UI quản trị cần xác nhận rõ trước khi gọi DELETE, và xử lý lỗi 409 nếu bản ghi có ràng buộc khóa ngoại.
- Tìm kiếm bác sĩ, lịch bác sĩ và slot hiện yêu cầu JWT theo `SecurityConfig`.
- `GET /api/appointments/search` chỉ dành cho ADMIN; không render truy cập endpoint này cho DOCTOR/PATIENT.
- CORS được cấu hình qua biến môi trường `CORS_ALLOWED_ORIGINS`; mặc định dev là `http://localhost:3000,http://localhost:5173`.

## Quy ước dữ liệu API

- Response nằm trong `{ success, message, data, timestamp }`. Danh sách phân trang có `data.content`, `pageNumber`, `pageSize`, `totalElements`, `totalPages`, `last`.
- Gửi JWT bằng header `Authorization: Bearer <token>`.
- Lỗi validation trả HTTP 400 với `data` là map tên trường sang thông báo. Chuyển lỗi 401/403/404/409 thành thông báo thao tác rõ ràng.
- Ngày dùng `YYYY-MM-DD`, giờ `HH:mm:ss`, ngày trong tuần dùng enum tiếng Anh viết hoa.

