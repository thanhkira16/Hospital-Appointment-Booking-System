# 02 — Dashboard và điều hướng theo vai trò

## Mục tiêu

Tạo shell dùng chung gồm header, sidebar/menu responsive, breadcrumb, nội dung và đăng xuất. Menu thay đổi theo role.

## Dashboard theo vai trò

- **PATIENT:** lối tắt tìm bác sĩ, cuộc hẹn sắp tới, lịch sử và hồ sơ khám.
- **DOCTOR:** cuộc hẹn sắp tới, lịch làm việc, hồ sơ cá nhân và cuộc hẹn cần xử lý.
- **ADMIN:** danh sách bác sĩ/bệnh nhân, tìm kiếm mọi cuộc hẹn và lối tắt quản trị.

## API dữ liệu

- Bệnh nhân: gọi `GET /api/patients/me` để lấy `patientId`, sau đó `GET /api/appointments/patients/{patientId}?page=0&size=10`.
- Bác sĩ: gọi `GET /api/doctors/me` để lấy `doctorId`, sau đó `GET /api/appointments/doctors/{doctorId}?page=0&size=10`.
- Admin: `GET /api/appointments/search` với bộ lọc; `GET /api/doctors`, `GET /api/patients`.

## Trạng thái và tiêu chí

- Hiển thị skeleton, lỗi riêng từng widget và empty state có CTA.
- Không gọi truy vấn phụ thuộc ID trước khi tải được ID hồ sơ.
- Backend có `GET /api/patients/me` và `GET /api/doctors/me`; không suy ID hồ sơ từ `userId`, không hiển thị thống kê giả vì chưa có endpoint thống kê tổng hợp.

