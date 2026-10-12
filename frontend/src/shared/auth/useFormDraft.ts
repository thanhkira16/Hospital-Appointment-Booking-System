import { useEffect } from 'react'
import { readFormDraft, registerDraftWriter } from './drafts'

export function useFormDraft<T>(key: string, value: T) {
  useEffect(() => registerDraftWriter(() => {
    localStorage.setItem(`hospital.form-draft.${key}`, JSON.stringify(value))
  }), [key, value])

  return {
    restore: (): T | null => readFormDraft<T>(key),
    clear: () => localStorage.removeItem(`hospital.form-draft.${key}`),
  }
}
