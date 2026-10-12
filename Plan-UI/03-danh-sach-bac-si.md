# 03 — Danh sách, tìm kiếm và xem bác sĩ

## Màn hình

- Danh sách bác sĩ dạng bảng trên desktop, thẻ gọn trên mobile.
- Bộ lọc chuyên khoa, số năm kinh nghiệm tối thiểu, phí khám tối đa.
- Chi tiết bác sĩ với nút xem lịch/đặt khám.

## API

- `GET /api/doctors?page=0&size=10&sort=id`.
- `GET /api/doctors/search?specialization=...&minExperience=...&maxFee=...&page=0&size=10`.
- `GET /api/doctors/{id}`.
- Trang chi tiết có thể tải lịch bằng `GET /api/doctors/{doctorId}/schedules`.

Response phân trang nằm trong `data.content`; mỗi bác sĩ có `id`, `fullName`, `email`, `specialization`, `qualification`, `yearsOfExperience`, `phoneNumber`, `consultationFee`.

## Hành vi

- Giữ bộ lọc/phân trang trong query string; debounce ô chuyên khoa, có nút xóa lọc.
- Giá hiển thị VND; không hiển thị thông tin bệnh nhân.
- API hiện yêu cầu đăng nhập do rule `/api/doctors/**`; redirect về login rồi quay lại trang.

## Tiêu chí hoàn thành

- Lọc kết hợp và phân trang không mất điều kiện lọc.
- Có loading/error/empty state; trang chi tiết giữ được trạng thái quay lại.

