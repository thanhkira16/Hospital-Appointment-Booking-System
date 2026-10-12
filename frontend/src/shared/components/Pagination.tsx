import { Button } from './Button'

interface PaginationProps { page: number; totalPages: number; onChange: (page: number) => void }

export function Pagination({ page, totalPages, onChange }: PaginationProps) {
  if (totalPages <= 1) return null
  return <nav className="pagination" aria-label="Phân trang">
    <Button variant="secondary" disabled={page <= 0} onClick={() => onChange(page - 1)}>Trang trước</Button>
    <span>Trang {page + 1} / {totalPages}</span>
    <Button variant="secondary" disabled={page >= totalPages - 1} onClick={() => onChange(page + 1)}>Trang sau</Button>
  </nav>
}
