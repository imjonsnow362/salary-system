# Employee Salary Management System

> A web-based salary management platform for HR teams to manage 10,000+ employees across multiple countries.

## 📋 Overview

Currently, ACME organization's HR team manages employee salary data via Excel spreadsheets—tedious, error-prone, and lacking analytics. This project replaces that with a modern, scalable web application built with **Spring Boot** (backend) and **Angular** (frontend).

### Quick Stats
- **Employees**: 10,000 across 5 countries
- **Database**: SQLite (file-based, zero setup)
- **Backend**: Java 11, Spring Boot 3.1.5
- **Frontend**: Angular 14+ (TypeScript, RxJS)
- **Methodology**: Test-Driven Development (TDD), Incremental Delivery

## 🎯 Key Features

### For HR Managers
- ✅ **View & Manage Employees**: Browse 10,000 employees with pagination (not all at once)
- ✅ **Search & Filter**: Find employees by name, department, country, or role
- ✅ **Salary Management**: Track current salary and full history with effective dates
- ✅ **Analytics**: Answer strategic questions
  - Avg salary by department/country
  - Salary distribution (percentiles)
  - Total payroll cost
  - Headcount by country

### For Developers
- ✅ **Clean Architecture**: Layered design (Controller → Service → Repository)
- ✅ **TDD First**: Unit tests before implementation
- ✅ **Production-Ready Code**: SOLID principles, proper error handling
- ✅ **Scalable Design**: Server-side pagination, database indices
- ✅ **Easy Deployment**: Single JAR + static files

## 📁 Project Structure

```
salary-system-work/
├── backend/                    # Spring Boot API
│   ├── src/main/java/com/salary/
│   │   ├── domain/            # JPA Entities (Employee, Salary, Country)
│   │   ├── repository/        # Data access (JPA repos)
│   │   ├── service/           # Business logic
│   │   ├── controller/        # REST endpoints
│   │   ├── dto/               # Data transfer objects
│   │   ├── config/            # Configuration & seeding
│   │   └── exception/         # Error handling
│   ├── src/test/java/         # JUnit tests (TDD)
│   ├── pom.xml                # Maven configuration
│   └── README.md              # Backend docs
│
├── frontend/                   # Angular UI
│   ├── src/app/
│   │   ├── core/              # Services, guards, interceptors
│   │   ├── shared/            # Reusable components
│   │   ├── features/          # Feature modules
│   │   │   ├── employees/     # Employee list, detail pages
│   │   │   └── analytics/     # Analytics dashboard
│   │   └── app.module.ts
│   ├── angular.json
│   ├── package.json
│   └── README.md              # Frontend docs
│
├── REQUIREMENTS.md            # Feature requirements & scope
├── ARCHITECTURE.md            # Design decisions & trade-offs
└── README.md                  # This file
```

## 🚀 Quick Start

### Backend

```bash
cd backend

# Build
mvn clean install

# Seed database (generates 10,000 employees)
mvn exec:java -Dexec.mainClass="com.salary.config.DataSeeder"

# Start server (http://localhost:8080/api)
mvn spring-boot:run
```

### Frontend

```bash
cd frontend

# Install dependencies
npm install

# Start dev server (http://localhost:4200)
ng serve

# Build for production
ng build --prod
```

## 📖 Documentation

| Document | Purpose |
|----------|---------|
| [REQUIREMENTS.md](./REQUIREMENTS.md) | Feature list, scope, out-of-scope decisions |
| [ARCHITECTURE.md](./ARCHITECTURE.md) | Architectural decisions & trade-offs (SQLite, pagination, layered design, etc.) |
| [backend/README.md](./backend/README.md) | Backend setup, API endpoints, testing guide |
| [frontend/README.md](./frontend/README.md) | Frontend setup, component structure, state management |

## 🧪 Testing

### Backend (JUnit 5 + Mockito)
```bash
cd backend
mvn test                    # Run all tests
mvn test -Dtest=EmployeeServiceTest  # Run specific test
```

### Frontend (Jasmine/Karma)
```bash
cd frontend
npm test                    # Run all tests
npm run test:coverage       # Generate coverage report
```

### Test Pyramid
- **60% Unit Tests**: Fast, isolated, mock dependencies
- **30% Integration Tests**: Real database, test queries
- **10% E2E Tests**: Full flow, seed → API → UI

## 🏗️ Architecture Highlights

### Backend Architecture
```
REST API (Spring Boot)
    ↓
Controllers (HTTP handling)
    ↓
Services (Business logic)
    ↓
Repositories (JPA)
    ↓
Entities (Domain models)
    ↓
SQLite Database
```

### Key Design Decisions

| Decision | Rationale |
|----------|-----------|
| **SQLite** | File-based, relational, zero setup, sufficient for 10k records |
| **JPA/Hibernate** | Type-safe, database agnostic, productive |
| **Server-side Pagination** | Performance: 500 records/page, not 10k at once |
| **Salary Versioning** | Immutable history for audit trail & compliance |
| **REST + DTO** | Loose coupling, security, API stability |
| **TDD** | High confidence, self-documenting tests |

## 📊 Database Schema

```sql
-- Countries (5 records)
CREATE TABLE countries (
    id INTEGER PRIMARY KEY,
    name VARCHAR(100),              -- US, India, UK, Germany, Canada
    currency_code VARCHAR(3),       -- USD, INR, GBP, EUR, CAD
    salary_min DECIMAL(12,2),
    salary_max DECIMAL(12,2)
);

-- Employees (10,000 records)
CREATE TABLE employees (
    id INTEGER PRIMARY KEY,
    employee_id VARCHAR(20) UNIQUE, -- EMP000001
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    email VARCHAR(150) UNIQUE,
    department VARCHAR(100),        -- Engineering, Sales, HR, etc.
    designation VARCHAR(100),
    country_id INTEGER FOREIGN KEY,
    hired_date DATE,
    employment_status VARCHAR(20)   -- ACTIVE, INACTIVE
);

-- Salaries (15k-30k records, 1-3 per employee)
CREATE TABLE salaries (
    id INTEGER PRIMARY KEY,
    employee_id INTEGER FOREIGN KEY,
    base_salary DECIMAL(12,2),
    currency_code VARCHAR(3),
    annual_bonus DECIMAL(12,2),
    benefits_value DECIMAL(12,2),
    effective_date DATE,            -- When this salary became active
    created_at TIMESTAMP
);
```

**Indices** optimized for pagination, filtering, and analytics:
- `idx_employees_department`
- `idx_employees_country_id`
- `idx_salaries_employee_id`
- `idx_salaries_employee_effective`

## 🔄 Development Workflow (TDD)

1. **Write Test** (Red): Define expected behavior
2. **Run Test** (fails): Confirm test catches bug
3. **Write Code** (Green): Minimal implementation
4. **Refactor** (Green): Improve code quality
5. **Commit**: Incremental git commits

Example:
```bash
# 1. Write test in EmployeeServiceTest.java
@Test
void testCalculateAverageSalaryByDepartment_Engineering() {
    // Arrange, Act, Assert
}

# 2. Run test (FAILS)
mvn test -Dtest=EmployeeServiceTest

# 3. Implement service method
// public BigDecimal calculateAverageSalaryByDepartment(String dept) { ... }

# 4. Run test (PASSES)
mvn test -Dtest=EmployeeServiceTest

# 5. Commit
git commit -m "feat: add salary calculation service"
```

## 🎯 Phase Breakdown

### ✅ Phase 0: Foundation (COMPLETE)
- [x] Project structure
- [x] Database schema
- [x] Data seeding script (10,000 employees)
- [x] Requirements & architecture docs
- [x] Backend pom.xml configuration

### 📋 Phase 1: Domain Models & Data Layer (NEXT)
- Domain entities: `Employee`, `Salary`, `Country`
- JPA repositories with pagination support
- Unit tests for queries
- Integration tests with real SQLite

### Phase 2: Business Logic & Services
- `EmployeeService`: CRUD, search, filter
- `SalaryService`: Salary history, calculations
- `AnalyticsService`: Aggregations (avg, percentiles, totals)
- Unit tests with mocked repositories

### Phase 3: REST API & Controllers
- `EmployeeController`: GET/POST/PUT/DELETE employees
- `SalaryController`: Salary history endpoints
- `AnalyticsController`: Aggregation endpoints
- Integration tests (mock service layer)
- E2E tests with seeded data

### Phase 4: Frontend (Angular)
- Employee list page with pagination
- Search & filter UI
- Employee detail page with salary history
- Analytics dashboard
- Routing & state management (NgRx)
- Component tests

### Phase 5: Deployment & Polish
- Build & deploy backend JAR
- Build & deploy frontend static files
- Performance testing
- Documentation polish

## 💡 Key Decisions Explained

### Why SQLite, Not PostgreSQL?
- **MVP simplicity**: No Docker, no server setup
- **File-based**: Easy to back up, version control
- **Sufficient scale**: 10k records = ~50MB, queries < 200ms
- **Future migration**: JPA makes switching trivial (just change dialect)

### Why Server-Side Pagination?
- **Network**: All 10k records = 5-10 MB over HTTP
- **Browser**: Rendering 10k rows is slow
- **UX**: First page loads in < 500ms instead of 2-3s
- **Practical**: Users search instead of scrolling 10k rows

### Why TDD?
- **Confidence**: Tests catch bugs before production
- **Documentation**: Tests show how to use the code
- **Refactoring**: Change code without fear
- **Design**: Testable code is well-designed code

## 📚 Useful Commands

```bash
# Backend
mvn clean install                   # Build backend
mvn spring-boot:run                 # Start server
mvn test                            # Run all tests
mvn exec:java -Dexec.mainClass="com.salary.config.DataSeeder"  # Seed DB

# Frontend
npm install                         # Install dependencies
ng serve                           # Start dev server
ng build --prod                    # Production build
npm test                           # Run tests
npm run lint                       # Check code style

# Database
sqlite3 salary_system.db           # Open database
SELECT COUNT(*) FROM employees;   # Check employee count
```

## 🤝 Contribution Guidelines

1. **Create a feature branch**: `git checkout -b feature/employee-search`
2. **Write tests first**: Implement TDD
3. **Make incremental commits**: Show your thinking
4. **Push and create PR**: Include test results

## ❓ FAQ

**Q: Why not use MongoDB instead of SQLite?**
A: SQL is better for structured HR data with fixed schema. NoSQL adds complexity without benefits.

**Q: Should I use Angular Material or PrimeNG?**
A: Either works. Material is lighter, PrimeNG has more components. Choose based on design needs.

**Q: Can I use React instead of Angular?**
A: Yes, but this doc assumes Angular. Feel free to use React if preferred.

**Q: How do I extend this to 100k employees?**
A: Migrate SQLite → PostgreSQL (JPA handles it), add caching (Redis), implement ES (Elasticsearch) for search.

## 📞 Support

- **Docs**: Read [REQUIREMENTS.md](./REQUIREMENTS.md) and [ARCHITECTURE.md](./ARCHITECTURE.md)
- **Backend Issues**: Check [backend/README.md](./backend/README.md)
- **Frontend Issues**: Check [frontend/README.md](./frontend/README.md) (when created)

---

**Status**: Ready for Phase 1 - Domain Models & Data Layer

**Next Step**: Implement domain entities (`Employee.java`, `Salary.java`) and JPA repositories with comprehensive tests.

Confirm when ready! ✅
