# Vehicle Management System (VMS)

SE1020 Object Oriented Programming – Project.

A web-based parking & vehicle management system built with **Spring Boot + Java 17**
that stores all data in plain `.txt` files (no SQL database). Frontend is HTML +
custom CSS + vanilla JavaScript.

---

## Modules (each with full CRUD)

1. **User Management** — `users.txt`
2. **Vehicle Management** — `vehicles.txt`
3. **Parking Slot Management** — `parking_slots.txt`
4. **Parking Booking Management** — `bookings.txt`
5. **Payment Management** — `payments.txt`
6. **Feedback Management** — `feedback.txt`
7. **Reports & History Management** — aggregates the above

---

## Object-Oriented Concepts Used

| Concept             | Where it lives                                                       |
|---------------------|----------------------------------------------------------------------|
| **Encapsulation**   | All entities have `private` fields and `public` getters/setters      |
| **Inheritance**     | `User`, `Vehicle`, `ParkingSlot`, `Booking`, `Feedback` extend `BaseEntity` |
| **Abstraction**     | `BaseEntity` is an `abstract` class with abstract `toLine()`         |
| **Polymorphism**    | `FileRepository<T extends BaseEntity>` works for every entity type   |
| **Information hiding** | repository internals (file path, parsing) are package-private/private |

---

## Running the project

```bash
# From the project root
./mvnw spring-boot:run        # Linux / macOS
mvnw.cmd spring-boot:run      # Windows
```

Then open: **http://localhost:8080/**

Data files appear under `./data/` once you start adding records.

---

## REST API endpoints

Base URL: `http://localhost:8080`

### Users — `/api/users`
| Method | URL                  | Description                |
|--------|----------------------|----------------------------|
| GET    | `/api/users`         | List all users (or `?q=…` search) |
| GET    | `/api/users/{id}`    | Get one user               |
| POST   | `/api/users`         | Create user                |
| PUT    | `/api/users/{id}`    | Update user                |
| DELETE | `/api/users/{id}`    | Delete user                |

### Vehicles — `/api/vehicles`
| Method | URL                       | Description                       |
|--------|---------------------------|-----------------------------------|
| GET    | `/api/vehicles`           | List (or `?q=…` / `?ownerId=…`)  |
| GET    | `/api/vehicles/{id}`      | Get one                           |
| POST   | `/api/vehicles`           | Create                            |
| PUT    | `/api/vehicles/{id}`      | Update                            |
| DELETE | `/api/vehicles/{id}`      | Delete                            |

### Parking Slots — `/api/slots`
| Method | URL                        | Description                          |
|--------|----------------------------|--------------------------------------|
| GET    | `/api/slots`               | List (or `?q=…` / `?available=true`) |
| GET    | `/api/slots/{id}`          | Get one                              |
| POST   | `/api/slots`               | Create                               |
| PUT    | `/api/slots/{id}`          | Update                               |
| DELETE | `/api/slots/{id}`          | Delete                               |

### Bookings — `/api/bookings`
| Method | URL                          | Description                                |
|--------|------------------------------|--------------------------------------------|
| GET    | `/api/bookings`              | List (or `?userId=…` / `?status=…`)        |
| GET    | `/api/bookings/{id}`         | Get one                                    |
| POST   | `/api/bookings`              | Create (auto computes price, marks slot OCCUPIED) |
| PUT    | `/api/bookings/{id}`         | Update (frees slot if cancelled/completed) |
| DELETE | `/api/bookings/{id}`         | Delete (frees slot)                        |

### Payments — `/api/payments`
| Method | URL                       | Description                                            |
|--------|---------------------------|--------------------------------------------------------|
| GET    | `/api/payments`           | List (or `?q=…` / `?userId=…` / `?bookingId=…` / `?status=…`) |
| GET    | `/api/payments/{id}`      | Get one                                                |
| POST   | `/api/payments`           | Create                                                 |
| PUT    | `/api/payments/{id}`      | Update (auto-sets `paidAt` when status becomes PAID)   |
| DELETE | `/api/payments/{id}`      | Delete                                                 |

> When a Booking is created, a **PENDING** payment is auto-generated for its total amount.

### Feedback — `/api/feedback`
| Method | URL                       | Description                  |
|--------|---------------------------|------------------------------|
| GET    | `/api/feedback`           | List (or `?q=…` / `?userId=…`) |
| GET    | `/api/feedback/{id}`      | Get one                      |
| POST   | `/api/feedback`           | Create                       |
| PUT    | `/api/feedback/{id}`      | Update                       |
| DELETE | `/api/feedback/{id}`      | Delete                       |

### Reports — `/api/reports`
| Method | URL                                    | Description                       |
|--------|----------------------------------------|-----------------------------------|
| GET    | `/api/reports/dashboard`               | Aggregated stats                  |
| GET    | `/api/reports/history`                 | All bookings, newest first        |
| GET    | `/api/reports/revenue`                 | Revenue grouped by date           |
| GET    | `/api/reports/bookings-by-status`      | Booking count per status          |
| GET    | `/api/reports/vehicles-by-type`        | Vehicle count per type            |
| GET    | `/api/reports/payments-by-method`      | Payments grouped by method        |

---

## File Storage Format

One entity per line, fields separated by `|`. Example `users.txt`:

```
cbaadafc-7090-47cd-9f8f-1732df274177|Kasun Perera|kasun@vms.lk|0771234567|Colombo|ADMIN|admin123|2026-05-04T23:57:15|2026-05-04T23:57:15
```

Each repository extends the generic `FileRepository<T>` which handles
**Create / Read / Update / Delete** uniformly.
