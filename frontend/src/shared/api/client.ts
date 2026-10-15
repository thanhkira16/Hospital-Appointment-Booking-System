import { ApiError, type ApiResponse, type FieldErrors } from './types'
import { vietnameseMessage } from './vietnamese'

const baseUrl = (import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8082').replace(/\/$/, '')
let getToken = (): string | null => {
  try {
    return JSON.parse(localStorage.getItem('hospital.auth.session') ?? 'null')?.token ?? null
  } catch { return null }
}
let onSessionExpired: () => void = () => undefined

export function configureApiClient(options: { token: () => string | null; sessionExpired: () => void }) {
  getToken = options.token
  onSessionExpired = options.sessionExpired
}

export async function apiRequest<T>(path: string, init: RequestInit = {}): Promise<T> {
  const headers = new Headers(init.headers)
  headers.set('Accept', 'application/json')
  if (init.body && !headers.has('Content-Type')) headers.set('Content-Type', 'application/json')

  const token = getToken()
  if (token) headers.set('Authorization', `Bearer ${token}`)

  let response: Response
  try {
    response = await fetch(`${baseUrl}${path}`, { ...init, headers })
  } catch {
    throw new ApiError('Không thể kết nối đến máy chủ. Vui lòng thử lại.', 0)
  }

  const envelope = await readEnvelope<T>(response)
  if (response.status === 401) {
    if (token && path !== '/api/auth/login') {
      onSessionExpired()
      throw new ApiError('Phiên làm việc đã hết hạn.', 401)
    }
    throw new ApiError(vietnameseMessage(envelope?.message, 401), 401)
  }
  if (!response.ok || !envelope?.success) {
    throw new ApiError(
      vietnameseMessage(envelope?.message, response.status),
      response.status,
      toFieldErrors(envelope?.data),
    )
  }
  return envelope.data
}

async function readEnvelope<T>(response: Response): Promise<ApiResponse<T> | null> {
  const text = await response.text()
  if (!text) return null
  try {
    return JSON.parse(text) as ApiResponse<T>
  } catch {
    return null
  }
}

function toFieldErrors(value: unknown): FieldErrors | undefined {
  if (!value || typeof value !== 'object' || Array.isArray(value)) return undefined
  return Object.fromEntries(
    Object.entries(value).filter((entry): entry is [string, string] => typeof entry[1] === 'string')
      .map(([field, message]) => [field, vietnameseMessage(message)]),
  )
}
