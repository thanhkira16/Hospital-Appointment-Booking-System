import { useEffect, useId, useRef, type PropsWithChildren } from 'react'
import { Button } from './Button'

interface DialogProps { title: string; open: boolean; onClose: () => void }

export function Dialog({ title, open, onClose, children }: PropsWithChildren<DialogProps>) {
  const closeButton = useRef<HTMLButtonElement>(null)
  const close = useRef(onClose)
  const titleId = useId()
  useEffect(() => { close.current = onClose }, [onClose])
  useEffect(() => {
    if (!open) return
    const previouslyFocused = document.activeElement as HTMLElement | null
    closeButton.current?.focus()
    const previousOverflow = document.body.style.overflow
    document.body.style.overflow = 'hidden'
    const onKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape') close.current()
      if (event.key === 'Tab') {
        const dialog = closeButton.current?.closest('[role="dialog"]')
        const controls = dialog?.querySelectorAll<HTMLElement>('button:not(:disabled), input:not(:disabled), textarea:not(:disabled), select:not(:disabled), a[href]')
        if (!controls?.length) return
        const first = controls[0], last = controls[controls.length - 1]
        if (event.shiftKey && document.activeElement === first) { event.preventDefault(); last.focus() }
        else if (!event.shiftKey && document.activeElement === last) { event.preventDefault(); first.focus() }
      }
    }
    window.addEventListener('keydown', onKeyDown)
    return () => {
      window.removeEventListener('keydown', onKeyDown)
      document.body.style.overflow = previousOverflow
      previouslyFocused?.focus()
    }
  }, [open])
  if (!open) return null
  return <div className="dialog-backdrop" role="presentation" onMouseDown={onClose}>
    <section className="dialog" role="dialog" aria-modal="true" aria-labelledby={titleId} onMouseDown={(event) => event.stopPropagation()}>
      <h2 id={titleId}>{title}</h2>
      <div>{children}</div>
      <Button ref={closeButton} type="button" onClick={onClose}>Đóng</Button>
    </section>
  </div>
}
