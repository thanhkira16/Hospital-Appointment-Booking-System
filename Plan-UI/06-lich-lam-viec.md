# 06 — Lịch làm việc bác sĩ

## Màn hình

- Lịch tuần theo ngày, giờ bắt đầu/kết thúc và độ dài slot.
- Bác sĩ quản lý lịch của mình; ADMIN quản lý lịch bác sĩ được chọn.
- Form thêm/sửa và xác nhận xóa khung giờ.

## API

- `GET /api/doctors/{doctorId}/schedules`.
- `POST /api/doctors/{doctorId}/schedules`.
- `PUT /api/schedules/{id}`.
- `DELETE /api/schedules/{id}`.
- Body create/update: `{ dayOfWeek, startTime, endTime, slotDurationMinutes }`.

## Quy tắc

- `dayOfWeek`: `MONDAY` đến `SUNDAY`; giờ `HH:mm:ss`; `slotDurationMinutes` từ 5 đến 120 phút; giờ bắt đầu phải trước giờ kết thúc.
- Ghi/sửa/xóa chỉ bác sĩ sở hữu hoặc ADMIN; đọc lịch cần JWT.
- Backend không có API bật/tắt lịch. Tạm dùng xóa để gỡ khung giờ và xác nhận rõ; có thể bổ sung trạng thái active toggle.
- Hiển thị lỗi server khi khung giờ không hợp lệ; lịch này là lịch tuần, không phải lịch theo ngày cụ thể.
- Lịch làm việc là lịch tuần lặp (ví dụ: thứ Hai 08:00–12:00). Khi bệnh nhân chọn ngày cụ thể (07), backend tính toán và trả về các slot khả dụng dựa trên `DoctorSchedule` của ngày đó trong tuần; UI không tính toán slot thay backend.

## Tiêu chí hoàn thành

- Lịch sau lưu tải lại đúng; giờ hiển thị locale Việt Nam, không nhầm timezone.
- Có thể dùng lịch bằng bàn phím và dialog đóng/mở accessible.

