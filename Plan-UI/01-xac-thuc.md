# 01 — Đăng ký, đăng nhập và phiên làm việc

## Màn hình

- Đăng nhập bằng email và mật khẩu.
- Đăng ký bệnh nhân; có thể bật đăng ký bác sĩ theo chính sách vận hành.
- Trang truy cập bị từ chối / phiên hết hạn.

## API

- `POST /api/auth/login`: `{ email, password }`.
- `POST /api/auth/register`: `{ fullName, email, password, role, phoneNumber, ...profile }`.
- Response trả `userId`, `fullName`, `email`, `role`, `token`, `tokenType`.

## Luồng

1. Validate trường bắt buộc ở client; server vẫn là nguồn xác nhận cuối.
2. Đăng ký `PATIENT`: thêm `dateOfBirth` dạng `YYYY-MM-DD`, `gender`, `address`, tùy chọn `medicalHistory`.
3. Đăng ký `DOCTOR`: thêm `specialization`, `qualification`, `yearsOfExperience`, `consultationFee`.
4. Lưu phiên và điều hướng về dashboard theo role.
5. Đăng xuất bằng cách xóa token/user phía client; backend không có endpoint logout.

## Quy tắc và lỗi

- Không cho chọn `ADMIN` trên UI. Backend hiện nhận role từ request công khai nên cần chặn tự đăng ký ADMIN trước khi public.
- Email hợp lệ; mật khẩu tối thiểu 6 ký tự; số điện thoại bắt buộc.
- Hiển thị email đã tồn tại (409), thông tin sai (401) và lỗi từng trường (400) ngay tại form.
- Chưa có quên mật khẩu, xác minh email, đổi mật khẩu hoặc refresh token; không giả lập các luồng này. JWT hết hạn sau 24h; khi phát hiện 401, hiện thông báo "Phiên hết hạn" và điều hướng về login (xem 00).

## Tiêu chí hoàn thành

- Thành công lưu JWT; request cần quyền có Bearer token.
- Refresh trang khôi phục phiên; 401 xóa phiên và về login.
- Route phân quyền không cho vào dashboard role khác.

