# Hovedopgave Booking System

## Project Overview
This is a backend for a booking system built with Spring Boot and MySQL. It manages admins, services, timeslots, and bookings with authentication and validation.

## Technologies Used
- Java 17+
- Spring Boot
- Spring Data JPA
- MySQL
- Jakarta Validation
- Spring Security
- Maven

## Setup Instructions

### Configuration

The application reads database settings from environment variables defined in a file named `env.properties`. This file is **not included** in the repository and must be created manually.

Create an env.properties file in src/main/resources with the following content:

```properties
DATABASE_URL=jdbc:mysql://localhost:3306/your_db_name
DATABASE_USERNAME=your_db_username
DATABASE_PASSWORD=your_db_password
```

### Running the Application

1. Start your MySQL database and ensure the `env.properties` file is configured correctly.
2. Open a terminal and navigate to the project directory.
3. Run the application with:

```bash
mvn spring-boot:run
```
Or directly from your IDE if it supports Maven.


### Running Tests

To run tests:

```bash
mvn test
```
Or directly from your IDE if it supports Maven.
### Test Data

Test data is automatically inserted on application startup. This is handled by the `DataInit` class located in the `config` package.

For each of the 3 test admins, the following is created:
- 2 services: *Private Session* and *Group Session*
- For each service, 7 timeslots are created — one for each of the next 7 days, starting from **tomorrow**
- 2 Bookings

You can log in using one of the predefined admins:

- **Email:** `yogamaster@email.com` — **Password:** `secret123`
- **Email:** `test@email.com` — **Password:** `test123`
- **Email:** `daniel@email.com` — **Password:** `daniel123`

Alternatively, you can register your own admin account via the frontend. New admins will be able to log in immediately and create their own services and timeslots.

## Frontend

The frontend is a separate React application that communicates with the backend via REST APIs. It can be found here:

[Frontend Repository](https://github.com/TheDanishMexican/booking_system_frontend)
