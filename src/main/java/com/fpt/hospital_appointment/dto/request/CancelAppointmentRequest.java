package com.fpt.hospital_appointment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Replaces the raw Map<String,String> shortcut used in Step 13 - now
 * that we're wiring @Valid everywhere, every request body should be a
 * proper DTO so validation and Swagger docs (Step 21) stay consistent.
 */
public class CancelAppointmentRequest {
    @NotBlank(message = "Cancellation reason is required")
    @Size(max = 500, message = "Cancellation reason must not exceed 500 characters")
    private String cancellationReason;

    public String getCancellationReason() {
        return this.cancellationReason;
    }

    public void setCancellationReason(final String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public CancelAppointmentRequest() {
    }

    public CancelAppointmentRequest(final String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
}
