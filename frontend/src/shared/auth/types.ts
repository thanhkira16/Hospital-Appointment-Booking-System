export type Role = 'ADMIN' | 'DOCTOR' | 'PATIENT'

export interface AuthUser {
  id: number
  name: string
  email: string
  role: Role
}

export interface AuthSession {
  token: string
  user: AuthUser
}
