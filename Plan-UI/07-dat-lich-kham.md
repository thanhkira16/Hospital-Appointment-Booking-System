# 07 — Chọn slot và đặt lịch khám

## Luồng bệnh nhân

0. Đảm bảo đã có `patientId` từ `GET /api/patients/me` (gọi khi login, lưu vào session state); nếu thiếu thì hiện lỗi và điều hướng về hồ sơ.
1. Chọn bác sĩ từ danh sách/chi tiết.
2. Chọn ngày hôm nay hoặc tương lai.
3. Tải giờ còn trống và chọn một slot.
4. Nhập lý do khám (tối đa 500 ký tự), xem lại rồi xác nhận.
5. Hiển thị mã cuộc hẹn và trạng thái `PENDING`; email do backend gửi bất đồng bộ.

## API

- `GET /api/doctors/{doctorId}/slots?date=YYYY-MM-DD` trả `doctorId`, `date`, `slotDurationMinutes`, `availableSlots`.
- `POST /api/appointments/patients/{patientId}` body `{ doctorId, appointmentDate, appointmentTime, reasonForVisit }`.
- `patientId` nằm trên path; backend xác nhận hồ sơ thuộc tài khoản hiện tại (trừ ADMIN).

## Quy tắc và lỗi

- `patientId` lấy từ `GET /api/patients/me` sau login, lưu vào session; không suy ra từ `userId`.
- Đổi ngày/bác sĩ thì bỏ slot cũ và tải lại.
- Slot quá khứ trong ngày hiện tại bị backend loại.
- HTTP 409: slot vừa được đặt, thông báo và tải lại danh sách.
- Không có slot/ngày nghỉ: empty state gợi ý ngày khác.
- Chặn gửi lặp trong lúc submit; thành công thì mở chi tiết hẹn.
- Dùng `isSubmitting` state để disable nút xác nhận trong khi đang gửi request, tránh double-submit.
- Sau đặt lịch thành công, hiện thông báo "Email xác nhận đã được gửi đến địa chỉ email của bạn" (email do backend gửi bất đồng bộ).

## Tiêu chí hoàn thành

- Trước khi gửi, người dùng xem được bác sĩ, ngày, giờ, phí và lý do.
- Server là nguồn xác nhận slot cuối cùng; UI xử lý xung đột đồng thời.

