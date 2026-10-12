import { useId, type InputHTMLAttributes } from 'react'

interface TextInputProps extends InputHTMLAttributes<HTMLInputElement> { label: string; error?: string }

export function TextInput({ label, error, id, ...props }: TextInputProps) {
  const generatedId = useId()
  const inputId = id ?? generatedId
  return <label className="field" htmlFor={inputId}>
    <span>{label}</span>
    <input id={inputId} aria-invalid={Boolean(error)} aria-describedby={error ? `${inputId}-error` : undefined} {...props} />
    {error && <small id={`${inputId}-error`} className="field__error">{error}</small>}
  </label>
}
