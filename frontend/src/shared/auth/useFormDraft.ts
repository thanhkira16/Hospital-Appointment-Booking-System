import { useEffect } from 'react'
import { registerDraftWriter } from './drafts'

export function useFormDraft<T>(key: string, value: T) {
  useEffect(() => registerDraftWriter(() => {
    localStorage.setItem(`hospital.form-draft.${key}`, JSON.stringify(value))
  }), [key, value])

  return {
    restore: (): T | null => {
      const saved = localStorage.getItem(`hospital.form-draft.${key}`)
      return saved ? (JSON.parse(saved) as T) : null
    },
    clear: () => localStorage.removeItem(`hospital.form-draft.${key}`),
  }
}
