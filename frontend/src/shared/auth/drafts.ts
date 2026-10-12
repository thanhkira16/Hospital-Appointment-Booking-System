const writers = new Set<() => void>()

export function registerDraftWriter(writer: () => void) {
  writers.add(writer)
  return () => { writers.delete(writer) }
}

export function saveAllFormDrafts() {
  writers.forEach((writer) => writer())
}
