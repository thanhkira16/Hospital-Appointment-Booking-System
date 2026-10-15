const messages: Record<string, string> = {
  'Invalid email or password': 'Email hoặc mật khẩu không chính xác.',
  'Bad credentials': 'Email hoặc mật khẩu không chính xác.',
  'Authentication required': 'Vui lòng đăng nhập để tiếp tục.',
  'Validation failed': 'Thông tin chưa hợp lệ. Vui lòng kiểm tra các trường bên dưới.',
  'You do not have permission to perform this action': 'Bạn không có quyền thực hiện thao tác này.',
  'Full name is required': 'Vui lòng nhập họ và tên.',
  'Email is required': 'Vui lòng nhập email.',
  'Email must be valid': 'Địa chỉ email không hợp lệ.',
  'Password is required': 'Vui lòng nhập mật khẩu.',
  'Password must be at least 6 characters': 'Mật khẩu phải có ít nhất 6 ký tự.',
  'Role is required': 'Vui lòng chọn vai trò.',
  'Phone number is required': 'Vui lòng nhập số điện thoại.',
  'Date of birth is required': 'Vui lòng chọn ngày sinh.',
  'Date of birth must be in the past': 'Ngày sinh phải trước ngày hôm nay.',
  'Gender is required': 'Vui lòng chọn giới tính.',
  'Address is required': 'Vui lòng nhập địa chỉ.',
  'Specialization is required': 'Vui lòng nhập chuyên khoa.',
  'Qualification is required': 'Vui lòng nhập trình độ chuyên môn.',
  'Years of experience is required': 'Vui lòng nhập số năm kinh nghiệm.',
  'Years of experience cannot be negative': 'Số năm kinh nghiệm không được âm.',
  'Consultation fee is required': 'Vui lòng nhập phí khám.',
  'Consultation fee must be positive': 'Phí khám phải lớn hơn 0.',
  'Day of week is required': 'Vui lòng chọn ngày trong tuần.',
  'Start time is required': 'Vui lòng chọn giờ bắt đầu.',
  'End time is required': 'Vui lòng chọn giờ kết thúc.',
  'Slot duration is required': 'Vui lòng nhập thời lượng mỗi lượt khám.',
  'Slot duration must be at least 5 minutes': 'Mỗi lượt khám phải kéo dài ít nhất 5 phút.',
  'Start time must be before end time': 'Giờ bắt đầu phải trước giờ kết thúc.',
  'Doctor ID is required': 'Vui lòng chọn bác sĩ.',
  'Appointment ID is required': 'Vui lòng chọn cuộc hẹn.',
  'Appointment date is required': 'Vui lòng chọn ngày khám.',
  'Appointment date cannot be in the past': 'Ngày khám không được ở quá khứ.',
  'Appointment time is required': 'Vui lòng chọn giờ khám.',
  'Reason must not exceed 500 characters': 'Lý do khám không được vượt quá 500 ký tự.',
  'Diagnosis is required': 'Vui lòng nhập chẩn đoán.',
  'Cancellation reason is required': 'Vui lòng nhập lý do hủy.',
  'Cancellation reason must not exceed 500 characters': 'Lý do hủy không được vượt quá 500 ký tự.',
  'Cannot fetch slots for a past date': 'Không thể xem giờ khám cho ngày đã qua.',
  'Slot duration must be positive': 'Thời lượng mỗi lượt khám phải lớn hơn 0.',
  'dateOfBirth must be in YYYY-MM-DD format': 'Ngày sinh phải có định dạng năm-tháng-ngày.',
}

export function vietnameseMessage(message: string | undefined, status = 400): string {
  if (message && messages[message]) return messages[message]
  if (message?.startsWith('Email is already registered:')) return 'Email này đã được đăng ký.'
  if (message?.startsWith('Cannot change appointment status')) return 'Không thể thực hiện thao tác với trạng thái hiện tại của cuộc hẹn. Vui lòng tải lại dữ liệu.'
  if (message?.startsWith('Cannot create a medical record')) return 'Chỉ có thể tạo hồ sơ khám khi cuộc hẹn đã hoàn thành.'
  if (message?.startsWith('A medical record already exists')) return 'Cuộc hẹn này đã có hồ sơ khám.'
  if (message?.startsWith('The selected time slot')) return 'Khung giờ này không còn trống. Vui lòng chọn giờ khám khác.'
  if (message?.includes('required for DOCTOR registration')) return 'Vui lòng nhập đủ chuyên khoa, trình độ, kinh nghiệm và phí khám.'
  if (message?.includes('required for PATIENT registration')) return 'Vui lòng nhập đủ ngày sinh, giới tính và địa chỉ.'
  if (message?.startsWith('Public registration')) return 'Không thể đăng ký tài khoản quản trị tại đây.'
  // Keep Vietnamese business messages, but never expose raw server exceptions.
  if (message && !message.startsWith('An unexpected error') && /[\u0102-\u01b0\u1ea0-\u1ef9]/u.test(message)) return message
  if (status === 401) return 'Email hoặc mật khẩu không chính xác.'
  if (status === 403) return 'Bạn không có quyền thực hiện thao tác này.'
  if (status === 404) return 'Không tìm thấy dữ liệu được yêu cầu.'
  if (status === 409) return 'Dữ liệu bị trùng hoặc đã thay đổi. Vui lòng kiểm tra và thử lại.'
  if (status >= 500) return 'Hệ thống đang gặp sự cố. Vui lòng thử lại sau.'
  return 'Thông tin chưa hợp lệ. Vui lòng kiểm tra và thử lại.'
}
