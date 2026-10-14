package com.fpt.hospital_appointment.dto.response;

import com.fpt.hospital_appointment.model.Role;

public class AuthResponse {
    private Long userId;
    private String fullName;
    private String email;
    private Role role;
    private String token;
    private String tokenType;


    public static class AuthResponseBuilder {
        private Long userId;
        private String fullName;
        private String email;
        private Role role;
        private String token;
        private String tokenType;

        AuthResponseBuilder() {
        }

        /**
         * @return {@code this}.
         */
        public AuthResponse.AuthResponseBuilder userId(final Long userId) {
            this.userId = userId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public AuthResponse.AuthResponseBuilder fullName(final String fullName) {
            this.fullName = fullName;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public AuthResponse.AuthResponseBuilder email(final String email) {
            this.email = email;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public AuthResponse.AuthResponseBuilder role(final Role role) {
            this.role = role;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public AuthResponse.AuthResponseBuilder token(final String token) {
            this.token = token;
            return this;
        }

        /**
         * @return {@code this}.
         */
        public AuthResponse.AuthResponseBuilder tokenType(final String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public AuthResponse build() {
            return new AuthResponse(this.userId, this.fullName, this.email, this.role, this.token, this.tokenType);
        }

        @Override
        public String toString() {
            return "AuthResponse.AuthResponseBuilder(userId=" + this.userId + ", fullName=" + this.fullName + ", email=" + this.email + ", role=" + this.role + ", token=" + this.token + ", tokenType=" + this.tokenType + ")";
        }
    }

    public static AuthResponse.AuthResponseBuilder builder() {
        return new AuthResponse.AuthResponseBuilder();
    }

    public Long getUserId() {
        return this.userId;
    }

    public String getFullName() {
        return this.fullName;
    }

    public String getEmail() {
        return this.email;
    }

    public Role getRole() {
        return this.role;
    }

    public String getToken() {
        return this.token;
    }

    public String getTokenType() {
        return this.tokenType;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public void setRole(final Role role) {
        this.role = role;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public void setTokenType(final String tokenType) {
        this.tokenType = tokenType;
    }

    public AuthResponse() {
    }

    public AuthResponse(final Long userId, final String fullName, final String email, final Role role, final String token, final String tokenType) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.token = token;
        this.tokenType = tokenType;
    }
}
