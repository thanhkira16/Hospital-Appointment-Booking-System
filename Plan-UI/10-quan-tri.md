# 10 — Công cụ quản trị

## Màn hình

- Tổng quan quản trị.
- Quản lý bác sĩ: danh sách, tìm kiếm, xem/sửa/xóa.
- Quản lý bệnh nhân: danh sách, xem/sửa/xóa.
- Tra cứu cuộc hẹn toàn hệ thống.

## API

- Bác sĩ: `GET /api/doctors`, `GET /api/doctors/search`, `GET /api/doctors/{id}`, `PUT /api/doctors/{id}`, `DELETE /api/doctors/{id}`.
- Bệnh nhân: `GET /api/patients`, `GET /api/patients/{id}`, `PUT /api/patients/{id}`, `DELETE /api/patients/{id}`.
- Cuộc hẹn: `GET /api/appointments/search` với `status`, `fromDate`, `toDate`, `doctorId`, `patientId` và phân trang.
- Lịch bác sĩ: dùng các endpoint trong `06-lich-lam-viec.md`.

## Phân quyền và giới hạn hiện tại

- Tất cả trang admin cần role `ADMIN`; HTTP 403 thì hiện quyền truy cập bị từ chối.
- Không có endpoint admin riêng; dùng API chuyên biệt có method security.
- Không có API admin tạo tài khoản bác sĩ/bệnh nhân; đăng ký hiện là công khai. Không tạo nút cấp quyền admin đến khi backend có endpoint an toàn.
- Backend không có xóa mềm; xóa bác sĩ/bệnh nhân có thể vướng khóa ngoại với lịch hẹn. Xác nhận trước và hiển thị lỗi server nếu không thể xóa.
- Hồ sơ bệnh nhân chứa thông tin nhạy cảm; chỉ hiển thị cột cần thiết theo role.

## Tiêu chí hoàn thành

- Danh sách phân trang, filter/search URL state và empty state.
- Xóa luôn có dialog xác nhận; lỗi 409/500 không bị hiểu nhầm là thành công.
- Kết hợp được bộ lọc cuộc hẹn; ngày kết thúc không trước ngày bắt đầu.

