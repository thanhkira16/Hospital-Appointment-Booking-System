# 00 — Nền tảng giao diện

## Mục tiêu

Thiết lập nền tảng để các tính năng dùng chung API, xác thực, điều hướng và cách hiển thị trạng thái nhất quán. Chọn React + TypeScript hoặc stack FE mà nhóm thống nhất; backend hiện không kèm frontend.

## Cấu trúc đề xuất

- `routes/`: trang theo tính năng và route bảo vệ theo role.
- `features/<feature>/`: trang, form, hook lấy dữ liệu và kiểu dữ liệu theo tính năng.
- `shared/api/`: HTTP client, chuẩn hóa `ApiResponse`, lỗi và header JWT.
- `shared/components/`: nút, input, dialog xác nhận, bảng, phân trang, skeleton, thông báo.
- `shared/auth/`: user, token, kiểm soát route theo `ADMIN`, `DOCTOR`, `PATIENT`.

## Hợp đồng kỹ thuật

- Base URL lấy từ biến môi trường, mặc định local `http://localhost:8082`.
- HTTP client tự thêm Bearer token; khi nhận 401 (bao gồm JWT hết hạn), hiển thị dialog "Phiên làm việc đã hết hạn" thay vì redirect im lặng; nếu user đang điền form thì auto-save vào localStorage trước khi xóa phiên.
- Đọc dữ liệu từ `response.data`; UI không phụ thuộc vào toàn bộ envelope.
- Đưa bộ lọc và phân trang lên URL để refresh/chia sẻ trang vẫn giữ trạng thái.
- Dùng server state cache (ví dụ TanStack Query) cho API; local state cho dialog và form.

## Quy tắc UI dùng chung

- Thiết kế mobile-first; kiểm tra tối thiểu 320, 768, 1024 và 1440 px.
- Có loading skeleton, lỗi có nút thử lại, empty state và trạng thái lưu thành công.
- Form có label hiển thị, lỗi cạnh trường, focus rõ, thao tác bàn phím; dialog quản lý focus và đóng bằng Escape.
- Trạng thái cuộc hẹn luôn có chữ, không dùng màu làm dấu hiệu duy nhất.
- Ngày giờ hiển thị theo locale `vi-VN`; gửi API đúng `YYYY-MM-DD`/`HH:mm:ss`.

## Tiêu chí hoàn thành

- Có HTTP client, mô hình lỗi, auth guard, layout theo role và component trạng thái dùng chung.
- Token không ghi vào log; màn yêu cầu quyền ẩn hành động không phù hợp.
- Mọi route có tiêu đề, trạng thái tải/lỗi/rỗng và responsive cơ bản.
- Có React Error Boundary bọc mỗi feature route; khi component con crash thì chỉ hiện lỗi khu vực đó, không crash toàn trang.

