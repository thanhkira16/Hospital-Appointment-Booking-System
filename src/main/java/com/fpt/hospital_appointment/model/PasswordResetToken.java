package com.fpt.hospital_appointment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String token;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    @Column(nullable = false)
    private boolean used = false;
    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Long getId() {
        return this.id;
    }

    public String getToken() {
        return this.token;
    }

    public User getUser() {
        return this.user;
    }

    public LocalDateTime getExpiresAt() {
        return this.expiresAt;
    }

    public boolean isUsed() {
        return this.used;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public void setExpiresAt(final LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setUsed(final boolean used) {
        this.used = used;
    }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public PasswordResetToken() {
    }

    public PasswordResetToken(final String token, final User user, final LocalDateTime expiresAt) {
        this.token = token;
        this.user = user;
        this.expiresAt = expiresAt;
        this.used = false;
        this.createdAt = LocalDateTime.now();
    }
}
