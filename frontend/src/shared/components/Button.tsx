import { forwardRef, type ButtonHTMLAttributes, type PropsWithChildren } from 'react'

type Props = ButtonHTMLAttributes<HTMLButtonElement> & { variant?: 'primary' | 'secondary' | 'danger' }

export const Button = forwardRef<HTMLButtonElement, PropsWithChildren<Props>>(
  ({ children, className = '', variant = 'primary', ...props }, ref) => (
    <button ref={ref} className={`button button--${variant} ${className}`.trim()} {...props}>{children}</button>
  ),
)
