# 08 — Danh sách và vòng đời cuộc hẹn

## Màn hình

- Bệnh nhân: lịch sắp tới, lịch sử và chi tiết cuộc hẹn của mình.
- Bác sĩ: lịch của mình và thao tác xác nhận/hoàn tất/hủy.
- ADMIN: tra cứu mọi cuộc hẹn theo bộ lọc.
- Chi tiết hiển thị bác sĩ, bệnh nhân, ngày, giờ, lý do, trạng thái và lý do hủy.

## API

- `GET /api/appointments/patients/{patientId}?page=0&size=10`.
- `GET /api/appointments/doctors/{doctorId}?page=0&size=10`.
- `GET /api/appointments/{id}`.
- `PATCH /api/appointments/{id}/confirm` — bác sĩ liên quan hoặc ADMIN.
- `PATCH /api/appointments/{id}/complete` — bác sĩ liên quan hoặc ADMIN.
- `PATCH /api/appointments/{id}/cancel` body `{ cancellationReason }` — bệnh nhân/bác sĩ liên quan hoặc ADMIN.
- `GET /api/appointments/search?status=&fromDate=&toDate=&doctorId=&patientId=&page=0&size=10` — ADMIN.
  > ⚠️ `GET /api/appointments/search` chỉ dành cho ADMIN (`@PreAuthorize("hasRole('ADMIN')")`). Không render nút/link đến màn hình này cho DOCTOR hoặc PATIENT.

## Chuyển trạng thái hợp lệ

- `PENDING → CONFIRMED` hoặc `CANCELLED`.
- `CONFIRMED → COMPLETED` hoặc `CANCELLED`.
- `COMPLETED` và `CANCELLED` là kết thúc.
- Hủy bắt buộc có lý do. Không có API dời ngày/giờ; người dùng hủy rồi đặt lịch mới.
- Trường `cancellationReason` tối đa 500 ký tự; hiện đếm ký tự còn lại và disable nút gửi khi vượt giới hạn.

## Tiêu chí hoàn thành

- Chỉ hiện hành động đúng role/chủ thể; server vẫn xác thực và có thể trả 403.
- Bộ lọc trạng thái/ngày giữ trong URL; sau thao tác cập nhật dữ liệu và làm mới danh sách.
- Danh sách dùng phân trang, không tải toàn bộ cùng lúc.

