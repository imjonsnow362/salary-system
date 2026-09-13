# Employee Salary Management System

A local web application for HR teams to manage employees and versioned salary records across five countries. The seed generates 10,000 employees with one to three salary records each.

## Stack
- Backend: Spring Boot 2.5.0, Java 8, JPA/Hibernate, SQLite
- Frontend: Angular 15, TypeScript, RxJS, Angular Material
- Testing: JUnit 5, Mockito, AssertJ, H2 test database, Jasmine/Karma

## Project Structure

```
salary-system-work/
├── backend/                    # Spring Boot API
│   ├── src/main/java/com/salary/
│   │   ├── domain/            # JPA entities (Employee, Salary, Country)
│   │   ├── repository/        # Data access (JPA repos)
│   │   ├── service/           # Business logic
│   │   ├── controller/        # REST endpoints
│   │   ├── dto/               # Data transfer objects
│   │   ├── config/            # Configuration & seeding
│   │   └── exception/         # Error handling
│   ├── src/test/java/         # JUnit tests
│   ├── pom.xml
│   └── README.md
│
├── frontend/                   # Angular UI
│   └── README.md
│
├── REQUIREMENTS.md            # Scope and out-of-scope decisions
├── ARCHITECTURE.md            # Design decisions & trade-offs
└── README.md
```

## Run Locally

The backend must be running before the development frontend, which calls `http://localhost:8080/api`.

```bash
# Terminal 1: install, test, seed, and run the API
cd backend
mvn clean install
rm -f salary_system.db
mvn exec:java -Dexec.mainClass=com.salary.config.DataSeeder
mvn spring-boot:run
```

```bash
# Terminal 2: install and run the UI
cd frontend
npm install
npm start
```

Open `http://localhost:4200`. The API is available at `http://localhost:8080/api`.

## Documentation

| Document | Purpose |
|----------|---------|
| [REQUIREMENTS.md](./REQUIREMENTS.md) | Feature scope and out-of-scope decisions |
| [ARCHITECTURE.md](./ARCHITECTURE.md) | Architectural decisions & trade-offs |
| [backend/README.md](./backend/README.md) | Backend setup, API endpoints, testing |
| [frontend/README.md](./frontend/README.md) | Frontend setup and structure |

## Testing

```bash
cd backend && mvn test
cd frontend && npm test
cd frontend && npm run build
```

Backend repository tests use H2 in memory; the running application uses the SQLite file `backend/salary_system.db`.

## Demo

Video recording: add the final submission URL here before submitting.

This submission is designed to run locally. Deployment is not included.

## Database Schema

```sql
CREATE TABLE countries (
    id INTEGER PRIMARY KEY,
    name VARCHAR(100),
    currency_code VARCHAR(3),
    salary_min DECIMAL(12,2),
    salary_max DECIMAL(12,2)
);

CREATE TABLE employees (
    id INTEGER PRIMARY KEY,
    employee_id VARCHAR(20) UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    email VARCHAR(150) UNIQUE,
    department VARCHAR(100),
    designation VARCHAR(100),
    country_id INTEGER REFERENCES countries(id),
    hired_date DATE,
    employment_status VARCHAR(20)
);

CREATE TABLE salaries (
    id INTEGER PRIMARY KEY,
    employee_id INTEGER REFERENCES employees(id),
    base_salary DECIMAL(12,2),
    currency_code VARCHAR(3),
    annual_bonus DECIMAL(12,2),
    benefits_value DECIMAL(12,2),
    effective_date DATE,       -- versioned: new row per salary change, never overwritten
    created_at TIMESTAMP
);
```

Indices on `department`, `country_id`, `employment_status`, and `(employee_id, effective_date)` support pagination and analytics at 10k+ record scale.

## Key Decisions

See [ARCHITECTURE.md](./ARCHITECTURE.md) for full detail. Summary:

| Decision | Rationale |
|----------|-----------|
| SQLite | File-based, zero setup, sufficient for 10k records; JPA makes a future Postgres migration low-cost |
| Server-side pagination | 10k records is too much to hold in browser memory or transfer at once |
| Salary versioning (append-only) | Full audit trail, supports "what was salary X on date Y" queries |
| Layered architecture | Controller/Service/Repository separation keeps each layer independently testable |
