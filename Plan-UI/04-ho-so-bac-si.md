# 04 — Hồ sơ bác sĩ

## Màn hình

- Trang đọc hồ sơ cho bệnh nhân/bác sĩ/admin.
- Form cập nhật hồ sơ cho chính bác sĩ hoặc ADMIN.
- Quản trị có nút xóa với dialog xác nhận.

## API

- `GET /api/doctors/{id}`.
- `PUT /api/doctors/{id}` body `{ specialization, qualification, yearsOfExperience, phoneNumber, consultationFee }`.
- `DELETE /api/doctors/{id}` chỉ ADMIN.

## Quy tắc

- Bác sĩ chỉ sửa hồ sơ của mình; admin sửa được mọi bác sĩ.
- Kinh nghiệm không âm; phí lớn hơn 0; chuyên khoa, bằng cấp, điện thoại bắt buộc.
- Bác sĩ tìm ID của mình qua `GET /api/doctors/me` (trả `DoctorResponse` gồm `id`); sau đó dùng ID đó cho các thao tác cập nhật.
- Xóa bác sĩ là hành động phá hủy dữ liệu; chỉ admin, xác nhận tên bác sĩ trước khi gọi API.

## Tiêu chí hoàn thành

- Sau lưu cập nhật cache; lỗi 403/404/400 được xử lý dễ hiểu.
- Tiền tệ và số năm có validation và label truy cập được.

