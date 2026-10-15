import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { ApiError } from '../../shared/api/types'
import { apiRequest } from '../../shared/api/client'
import { Button } from '../../shared/components/Button'
import { Dialog } from '../../shared/components/Dialog'
import { StatusPanel } from '../../shared/components/StatusPanel'

interface MedicalRecord {
  id: number
  appointmentId: number
  diagnosis: string
  prescription?: string | null
  doctorNotes?: string | null
  createdAt: string
}

interface MedicalRecordDraft {
  diagnosis: string
  prescription: string
  doctorNotes: string
}

interface MedicalRecordPanelProps {
  appointmentId: number
  accountId: number
  canWrite: boolean
}

const emptyDraft: MedicalRecordDraft = { diagnosis: '', prescription: '', doctorNotes: '' }

function createErrorMessage(error: Error | null) {
  if (!(error instanceof ApiError)) return error?.message
  if (error.status === 400) return 'Cuộc hẹn chưa đủ điều kiện tạo bệnh án. Hãy tải lại trạng thái cuộc hẹn.'
  if (error.status === 403) return 'Tài khoản hiện tại không có quyền ghi bệnh án cho cuộc hẹn này.'
  if (error.status === 409) return 'Bệnh án đã được tạo. Hãy tải lại để xem nội dung mới nhất.'
  return error.message
}

export function MedicalRecordPanel({ appointmentId, accountId, canWrite }: MedicalRecordPanelProps) {
  const queryClient = useQueryClient()
  const queryKey = ['medical-record', accountId, appointmentId]
  const [isOpen, setIsOpen] = useState(false)
  const [confirmOpen, setConfirmOpen] = useState(false)
  const [draft, setDraft] = useState(emptyDraft)
  const record = useQuery({
    queryKey,
    queryFn: () => apiRequest<MedicalRecord>(`/api/medical-records/appointment/${appointmentId}`),
    enabled: isOpen,
    staleTime: 0,
    gcTime: 0,
    retry: false,
  })
  const createRecord = useMutation({
    mutationFn: () => apiRequest<MedicalRecord>('/api/medical-records', {
      method: 'POST',
      body: JSON.stringify({ appointmentId, ...draft }),
    }),
    onSuccess: (created) => {
      queryClient.setQueryData(queryKey, created)
      setConfirmOpen(false)
      setDraft(emptyDraft)
    },
    onError: (error) => {
      if (error instanceof ApiError && error.status === 409) void record.refetch()
    },
  })

  function openRecord() {
    setIsOpen(true)
    void record.refetch()
  }

  const hasRecord = record.data !== undefined
  const isMissing = record.error instanceof ApiError && record.error.status === 404
  const loadError = record.error && !isMissing ? record.error : null
  const writeError = createErrorMessage(createRecord.error)

  return <section className="medical-record-panel" aria-labelledby="medical-record-heading">
    <div className="directory-heading">
      <div><p className="eyebrow">Bệnh án</p><h2 id="medical-record-heading">Hồ sơ khám bệnh</h2></div>
      <Button type="button" variant="secondary" onClick={openRecord} disabled={record.isFetching}>
        {record.isFetching ? 'Đang tải…' : isOpen ? 'Tải lại bệnh án' : canWrite ? 'Mở / ghi bệnh án' : 'Xem hồ sơ khám'}
      </Button>
    </div>

    {isOpen && <div className="medical-record-content">
      {record.isFetching && <StatusPanel kind="loading" title="Đang tải bệnh án" description="Dữ liệu y tế được tải riêng cho tài khoản hiện tại." />}
      {loadError && <StatusPanel kind="error" title="Không tải được bệnh án" description={loadError.message} onRetry={() => void record.refetch()} />}
      {isMissing && !record.isFetching && <StatusPanel kind="empty" title="Chưa có bệnh án" description={canWrite ? 'Có thể tạo bệnh án cho cuộc hẹn đã hoàn tất này.' : 'Bác sĩ chưa tạo hồ sơ khám cho cuộc hẹn này.'} />}
      {hasRecord && <article className="medical-record-card" aria-label={`Bệnh án cuộc hẹn ${appointmentId}`}>
        <dl className="medical-record-facts">
          <div><dt>Chẩn đoán</dt><dd>{record.data.diagnosis}</dd></div>
          <div><dt>Đơn thuốc</dt><dd>{record.data.prescription?.trim() || 'Không có thông tin'}</dd></div>
          <div><dt>Ghi chú của bác sĩ</dt><dd>{record.data.doctorNotes?.trim() || 'Không có thông tin'}</dd></div>
          <div><dt>Ngày lập</dt><dd>{new Date(record.data.createdAt).toLocaleString('vi-VN')}</dd></div>
        </dl>
      </article>}

      {canWrite && !hasRecord && !record.isFetching && !loadError && <form className="medical-record-form" onSubmit={(event) => { event.preventDefault(); setConfirmOpen(true) }}>
        <p className="muted">Bệnh án chỉ được tạo một lần và gắn với cuộc hẹn #{appointmentId}.</p>
        <label className="field" htmlFor={`diagnosis-${appointmentId}`}>Chẩn đoán
          <textarea id={`diagnosis-${appointmentId}`} value={draft.diagnosis} required rows={3} onChange={(event) => setDraft({ ...draft, diagnosis: event.target.value })} />
        </label>
        <label className="field" htmlFor={`prescription-${appointmentId}`}>Đơn thuốc
          <textarea id={`prescription-${appointmentId}`} value={draft.prescription} rows={4} onChange={(event) => setDraft({ ...draft, prescription: event.target.value })} />
        </label>
        <label className="field" htmlFor={`doctor-notes-${appointmentId}`}>Ghi chú của bác sĩ
          <textarea id={`doctor-notes-${appointmentId}`} value={draft.doctorNotes} rows={4} onChange={(event) => setDraft({ ...draft, doctorNotes: event.target.value })} />
        </label>
        {writeError && <p className="form-error" role="alert">{writeError}</p>}
        <Button type="submit" disabled={!draft.diagnosis.trim() || createRecord.isPending}>Kiểm tra và lưu bệnh án</Button>
      </form>}
    </div>}

    <Dialog title="Xác nhận lưu bệnh án" open={confirmOpen} onClose={() => setConfirmOpen(false)}>
      <p>Bệnh án sẽ được lưu vĩnh viễn cho cuộc hẹn #{appointmentId}. Vui lòng kiểm tra thông tin trước khi xác nhận.</p>
      <dl className="medical-record-confirmation">
        <div><dt>Chẩn đoán</dt><dd>{draft.diagnosis || '—'}</dd></div>
        <div><dt>Đơn thuốc</dt><dd>{draft.prescription || 'Không có thông tin'}</dd></div>
        <div><dt>Ghi chú của bác sĩ</dt><dd>{draft.doctorNotes || 'Không có thông tin'}</dd></div>
      </dl>
      {writeError && <p className="form-error" role="alert">{writeError}</p>}
      <div className="dialog-actions">
        <Button type="button" variant="secondary" onClick={() => setConfirmOpen(false)} disabled={createRecord.isPending}>Quay lại chỉnh sửa</Button>
        <Button type="button" disabled={!draft.diagnosis.trim() || createRecord.isPending} onClick={() => createRecord.mutate()}>{createRecord.isPending ? 'Đang lưu…' : 'Xác nhận lưu'}</Button>
      </div>
    </Dialog>
  </section>
}
