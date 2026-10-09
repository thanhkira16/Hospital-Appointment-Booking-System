# Hospital Appointment Booking System

A production-style REST API for managing hospital appointments, built with Java 21, Spring Boot 4, Spring Security (JWT), Spring Data JPA, and MySQL. Supports three roles (ADMIN, DOCTOR, PATIENT), full appointment lifecycle management, doctor scheduling with real-time slot availability, email notifications, and medical records.

## Features

- **JWT Authentication & Role-Based Authorization** — ADMIN, DOCTOR, PATIENT roles with method-level `@PreAuthorize` ownership checks
- **Slot Availability Engine** — computes real bookable time slots from a doctor's weekly schedule minus existing bookings, preventing double-booking
- **Appointment Lifecycle** — enforced state machine: `PENDING → CONFIRMED → COMPLETED` / `CANCELLED`
- **Async Email Notifications** — booking, confirmation, and cancellation emails via `@Async` + JavaMailSender, isolated so failures never break bookings
- **Medical Records** — created only after an appointment reaches `COMPLETED`, one record per appointment
- **Global Exception Handling** — consistent `ApiResponse<T>` JSON shape across every success and error case
- **Pagination, Sorting & Search** — dynamic filtering via JPA Specifications on doctors and appointments
- **Input Validation** — Bean Validation (`@Valid`) at the DTO layer, business-rule validation at the Service layer
- **Swagger/OpenAPI Docs** — interactive API docs with JWT "Authorize" support
- **Test Coverage** — JUnit 5 + Mockito unit tests (Service layer) and MockMvc integration tests (Controller layer)

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1.0 |
| Security | Spring Security 7 + JWT (jjwt 0.12.6) |
| Persistence | Spring Data JPA + Hibernate 7 |
| Database | MySQL 8 |
| API Docs | springdoc-openapi (Swagger UI) |
| Testing | JUnit 5, Mockito, MockMvc |
| Build Tool | Maven |

## Architecture

Layered architecture throughout: **Controller → Service → Repository**, with DTOs at every API boundary (entities are never exposed directly).

```
src/main/java/com/fpt/hospital_appointment/
├── config/          # Security, Swagger, Async configuration
├── controller/       # REST endpoints
├── dto/              # Request/Response DTOs + ApiResponse/PageResponse wrappers
├── exception/         # Custom exceptions + GlobalExceptionHandler
├── model/             # JPA entities
├── repository/        # Spring Data JPA repositories
├── security/           # JWT utility, filter, UserDetailsService
└── service/             # Business logic + mappers + specifications
```

## Getting Started

### Prerequisites

- Java 21 (JDK)
- Maven 3.9+
- XAMPP with MySQL/MariaDB running locally (default port `3306`)

### Setup

1. Clone the repository:
```bash
git clone https://github.com/Sahil-Patil123/hospital-appointment-api.git
cd hospital-appointment-api
```

2. Start **MySQL** in the XAMPP Control Panel. From the project root, import `hospital_db.sql`; this creates `hospital_db` and inserts the sample data:
```powershell
Get-Content hospital_db.sql -Raw | C:\xampp\mysql\bin\mysql.exe -u root
```
You can also open phpMyAdmin at `http://localhost/phpmyadmin`, choose **Import**, select `hospital_db.sql`, and run it. The script can be imported again without duplicating the demo accounts, profiles, schedules, or same-date appointments. It creates one admin, three doctors, two patients, weekly schedules, example appointments across the lifecycle statuses, and medical records. All demo accounts use password `Password123!`:

| Role | Name | Email | Password |
|---|---|---|---|
| Admin | System Administrator | `thanhtch.22git@vku.udn.vn` | `Password123!` |
| Doctor | Nguyen Minh Anh | `minh.anh.doctor@hospital.test` | `Password123!` |
| Doctor | Tran Quoc Bao | `quoc.bao.doctor@hospital.test` | `Password123!` |
| Doctor | Bác sĩ Nguyễn An | `hthanh16092004@gmail.com` | `Password123!` |
| Patient | Le Thu Ha | `thu.ha.patient@hospital.test` | `Password123!` |
| Patient | Pham Gia Huy | `gia.huy.patient@hospital.test` | `Password123!` |

3. `src/main/resources/application.properties` is ready for a local XAMPP install using `root` with an empty password. If your MySQL root account has a password, set `DB_PASSWORD` before starting the application. Set `JWT_SECRET` to use a custom JWT key:
```properties
DB_USERNAME=root
JWT_SECRET=replace-with-a-random-secret-of-at-least-32-characters
```
Email notifications are optional for local API use; configure `MAIL_USERNAME` and `MAIL_PASSWORD` with a Gmail App Password to enable SMTP.

4. Build and run:
```bash
mvn clean install
mvn spring-boot:run
```

The API will be available at `http://localhost:8081`.

5. Open Swagger UI to explore and test the API interactively:
```
http://localhost:8081/swagger-ui.html
```

### Running Tests

```bash
mvn test
```

Generates a JaCoCo coverage report at `target/site/jacoco/index.html`.

## API Overview

| Category | Example Endpoints |
|---|---|
| Auth | `POST /api/auth/register`, `POST /api/auth/login` |
| Doctors | `GET /api/doctors`, `PUT /api/doctors/{id}`, `GET /api/doctors/search` |
| Patients | `GET /api/patients`, `PUT /api/patients/{id}` |
| Schedules | `POST /api/doctors/{doctorId}/schedules` |
| Slots | `GET /api/doctors/{doctorId}/slots?date=2026-07-15` |
| Appointments | `POST /api/appointments/patients/{patientId}`, `PATCH /api/appointments/{id}/confirm`, `PATCH /api/appointments/{id}/cancel` |
| Medical Records | `POST /api/medical-records`, `GET /api/medical-records/appointment/{appointmentId}` |

Full request/response schemas are available in Swagger UI once the app is running.

## Authentication Flow

1. `POST /api/auth/register` or `/api/auth/login` → returns a JWT in the response body
2. Include the token on all subsequent requests:
```
Authorization: Bearer <token>
```

## Author

**Sahil Patil**
GitHub: [@Sahil-Patil123](https://github.com/Sahil-Patil123)
