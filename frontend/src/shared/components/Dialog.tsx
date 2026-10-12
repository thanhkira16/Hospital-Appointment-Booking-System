import { useEffect, useRef, type PropsWithChildren } from 'react'
import { Button } from './Button'

interface DialogProps { title: string; open: boolean; onClose: () => void }

export function Dialog({ title, open, onClose, children }: PropsWithChildren<DialogProps>) {
  const closeButton = useRef<HTMLButtonElement>(null)
  useEffect(() => {
    if (!open) return
    closeButton.current?.focus()
    const onKeyDown = (event: KeyboardEvent) => { if (event.key === 'Escape') onClose() }
    window.addEventListener('keydown', onKeyDown)
    return () => window.removeEventListener('keydown', onKeyDown)
  }, [onClose, open])
  if (!open) return null
  return <div className="dialog-backdrop" role="presentation" onMouseDown={onClose}>
    <section className="dialog" role="dialog" aria-modal="true" aria-labelledby="dialog-title" onMouseDown={(event) => event.stopPropagation()}>
      <h2 id="dialog-title">{title}</h2>
      <div>{children}</div>
      <Button ref={closeButton} type="button" onClick={onClose}>Đóng</Button>
    </section>
  </div>
}
