# 09 — Hồ sơ khám bệnh

## Màn hình

- Bệnh nhân xem bệnh án từ cuộc hẹn đã hoàn tất.
- Bác sĩ mở cuộc hẹn hoàn tất và ghi chẩn đoán, toa thuốc, ghi chú.
- Admin có thể xem/ghi theo quyền API.

## API

- `POST /api/medical-records` body `{ appointmentId, diagnosis, prescription, doctorNotes }`; chẩn đoán bắt buộc.
- `GET /api/medical-records/appointment/{appointmentId}`.

## Quy tắc

- Tạo hồ sơ chỉ cho cuộc hẹn `COMPLETED`, bởi bác sĩ phụ trách hoặc ADMIN; mỗi cuộc hẹn có tối đa một hồ sơ.
- Xem chỉ bệnh nhân của cuộc hẹn, bác sĩ phụ trách hoặc ADMIN.
- Backend chưa có endpoint list hồ sơ theo bệnh nhân; điều hướng đến hồ sơ qua: Chi tiết cuộc hẹn → nút "Xem hồ sơ khám" (gọi `GET /api/medical-records/appointment/{appointmentId}`). Dashboard bệnh nhân không hiển thị hồ sơ trực tiếp mà dẫn qua lịch sử cuộc hẹn.
- Dữ liệu y tế nhạy cảm: không đưa vào dashboard tổng quan, log client, URL hoặc cache chia sẻ giữa tài khoản.
- 409 khi đã có hồ sơ; 400 khi cuộc hẹn chưa hoàn tất; 404 nếu chưa có hồ sơ.

## Tiêu chí hoàn thành

- Form gắn đúng appointmentId, xác nhận trước lưu và chỉ mở với cuộc hẹn phù hợp.
- Chẩn đoán/toa/ghi chú trình bày rõ và đọc tốt trên màn hình nhỏ.

