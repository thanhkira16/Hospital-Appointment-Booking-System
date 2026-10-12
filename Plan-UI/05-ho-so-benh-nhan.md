# 05 — Hồ sơ bệnh nhân

## Màn hình

- Bệnh nhân xem/cập nhật hồ sơ cá nhân.
- Bác sĩ/admin xem hồ sơ theo ID; bác sĩ/admin có danh sách phân trang.
- Admin có thể xóa hồ sơ sau xác nhận.

## API

- `GET /api/patients?page=0&size=10`: ADMIN hoặc DOCTOR.
- `GET /api/patients/{id}`: chủ hồ sơ, DOCTOR hoặc ADMIN.
- `PUT /api/patients/{id}`: chủ hồ sơ hoặc ADMIN, body `{ dateOfBirth, gender, phoneNumber, address, medicalHistory }`.
- `DELETE /api/patients/{id}`: ADMIN.

## Quy tắc và điểm cần bổ sung

- Ngày sinh phải ở quá khứ, ngày gửi dạng `YYYY-MM-DD`; ngày sinh/giới tính/điện thoại/địa chỉ bắt buộc.
- `medicalHistory` là dữ liệu nhạy cảm: chỉ hiển thị cho bệnh nhân, bác sĩ có quyền hoặc admin; không đưa vào log, analytics hay URL.
- Bệnh nhân dùng `GET /api/patients/me` để lấy `patientId` sau đăng nhập; kết quả nên được cache trong session state và dùng lại cho mọi thao tác tiếp theo.
- Xóa bệnh nhân là xóa thật; xác nhận trước khi gọi API.

## Tiêu chí hoàn thành

- Form nạp giá trị hiện tại, cảnh báo rời trang khi có thay đổi chưa lưu.
- Danh sách chỉ hiển thị thông tin cần thiết theo role.

