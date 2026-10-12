import { Button } from './Button'

interface StatusPanelProps {
  kind: 'loading' | 'error' | 'empty' | 'success'
  title: string
  description?: string
  onRetry?: () => void
}

export function StatusPanel({ kind, title, description, onRetry }: StatusPanelProps) {
  return <section className={`status-panel status-panel--${kind}`} aria-live="polite">
    {kind === 'loading' && <div className="skeleton" aria-hidden="true" />}
    <h2>{title}</h2>
    {description && <p>{description}</p>}
    {onRetry && <Button variant="secondary" onClick={onRetry}>Thử lại</Button>}
  </section>
}
