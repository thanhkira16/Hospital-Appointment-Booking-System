package com.fpt.hospital_appointment.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Name cannot be empty")
    @Column(nullable = false)
    private String fullName;
    @Email(message = "Email must be valid")
    @NotBlank(message = "Email cannot be empty")
    @Column(nullable = false, unique = true)
    private String email;
    @NotBlank(message = "Password cannot be empty")
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @Column(nullable = false)
    private boolean enabled = true;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


    public static class UserBuilder {
        private Long id;
        private String fullName;
        private String email;
        private String password;
        private Role role;
        private boolean enabled = true;
        private LocalDateTime createdAt;

        UserBuilder() {
        }

        /**
         * @return {@code this}.
         */
        public User.UserBuilder id(final Long id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public User.UserBuilder fullName(final String fullName) {
            this.fullName = fullName;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public User.UserBuilder email(final String email) {
            this.email = email;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public User.UserBuilder password(final String password) {
            this.password = password;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public User.UserBuilder role(final Role role) {
            this.role = role;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public User.UserBuilder enabled(final boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public User.UserBuilder createdAt(final LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public User build() {
            return new User(this.id, this.fullName, this.email, this.password, this.role, this.enabled, this.createdAt);
        }

        @Override
        public String toString() {
            return "User.UserBuilder(id=" + this.id + ", fullName=" + this.fullName + ", email=" + this.email + ", password=" + this.password + ", role=" + this.role + ", enabled=" + this.enabled + ", createdAt=" + this.createdAt + ")";
        }
    }

    public static User.UserBuilder builder() {
        return new User.UserBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public String getFullName() {
        return this.fullName;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public Role getRole() {
        return this.role;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setRole(final Role role) {
        this.role = role;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User() {
    }

    public User(final Long id, final String fullName, final String email, final String password, final Role role, final boolean enabled, final LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
        this.createdAt = createdAt;
    }
}
