import { createContext, useCallback, useContext, useEffect, useMemo, useState, type PropsWithChildren } from 'react'
import { configureApiClient } from '../api/client'
import { saveAllFormDrafts } from './drafts'
import type { AuthSession, Role } from './types'

const storageKey = 'hospital.auth.session'

interface AuthContextValue {
  session: AuthSession | null
  sessionExpired: boolean
  signIn: (session: AuthSession) => void
  signOut: () => void
  acknowledgeExpiredSession: () => void
  hasRole: (roles: Role[]) => boolean
}

const AuthContext = createContext<AuthContextValue | null>(null)

function readSession(): AuthSession | null {
  try {
    const value = localStorage.getItem(storageKey)
    return value ? (JSON.parse(value) as AuthSession) : null
  } catch {
    return null
  }
}

export function AuthProvider({ children }: PropsWithChildren) {
  const [session, setSession] = useState<AuthSession | null>(readSession)
  const [sessionExpired, setSessionExpired] = useState(false)

  const signOut = useCallback(() => {
    localStorage.removeItem(storageKey)
    setSession(null)
  }, [])

  const expireSession = useCallback(() => {
    saveAllFormDrafts()
    signOut()
    setSessionExpired(true)
  }, [signOut])

  useEffect(() => {
    configureApiClient({ token: () => session?.token ?? null, sessionExpired: expireSession })
  }, [expireSession, session])

  const value = useMemo<AuthContextValue>(() => ({
    session,
    sessionExpired,
    signIn: (nextSession) => {
      localStorage.setItem(storageKey, JSON.stringify(nextSession))
      setSession(nextSession)
      setSessionExpired(false)
    },
    signOut,
    acknowledgeExpiredSession: () => setSessionExpired(false),
    hasRole: (roles) => Boolean(session && roles.includes(session.user.role)),
  }), [session, sessionExpired, signOut])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) throw new Error('useAuth must be used inside AuthProvider')
  return context
}
