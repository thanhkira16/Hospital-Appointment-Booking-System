interface ToastProps { message?: string; tone?: 'success' | 'error' }

export function Toast({ message, tone = 'success' }: ToastProps) {
  return message ? <div className={`toast toast--${tone}`} role="status">{message}</div> : null
}
