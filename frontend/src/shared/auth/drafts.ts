const writers = new Set<() => void>()

export function readFormDraft<T>(key: string): T | null {
  try {
    const saved = localStorage.getItem(`hospital.form-draft.${key}`)
    return saved ? (JSON.parse(saved) as T) : null
  } catch {
    return null
  }
}

export function registerDraftWriter(writer: () => void) {
  writers.add(writer)
  return () => { writers.delete(writer) }
}

export function saveAllFormDrafts() {
  writers.forEach((writer) => writer())
}
