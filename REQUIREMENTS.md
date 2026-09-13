# Employee Salary Management System - Requirements Document

## Goal
Build a web-based salary management platform to replace manual Excel-based processes for ACME organization's 10,000 employees across 5 countries.

## Problem Statement
ACME's HR team currently manages employee salary data for 10,000 employees across multiple countries using Excel spreadsheets. This is tedious, error-prone, and lacks real-time analytics. They need a centralized, web-based system to:
- View and manage employee salary data
- Answer strategic questions about compensation across the organization
- Maintain audit trails for salary changes
- Scale as the organization grows

## Scope & Features

### In Scope (MVP)

#### 1. **Employee Management**
- View paginated list of all employees (500 per page)
- Search employees by name, employee ID, or email
- Filter by department, country, or employment status
- View detailed employee profile with salary history
- CRUD operations for employees (Create, Read, Update)

#### 2. **Salary Management**
- View current salary data (base, bonus, benefits) for each employee
- Access salary history (versioned records)
- Update salary with effective date tracking
- Maintain audit trail for all changes

#### 3. **Analytics & Reporting** (Read-only aggregations)
- **Dashboard**: High-level org metrics
  - Total headcount by country
  - Average salary by department
  - Salary distribution percentiles (10th, 50th, 90th)
  - Cost of payroll by country
  
- **Advanced Filters**: Answer ad-hoc questions
  - "Show employees earning > X in Y department"
  - "How much do we pay for role Z in country W?"
  - Salary comparison: department-to-department, country-to-country

#### 4. **Data Seeding**
- Pre-populate database with 10,000 realistic employees
- Distributed across 5 countries: US, India, UK, Germany, Canada
- Distributed across 10 departments
- Realistic salary ranges per country
- Salary history (1-3 versions per employee)

### Out of Scope (Deliberately Left Out)

| Feature | Why Left Out | Tradeoff |
|---------|-------------|----------|
| **Multi-language support** | Complexity in UI + DB; first version English-only | Faster MVP, can add later |
| **Tax/Compliance calculations** | Varies by country and year; requires legal review | Separate compliance layer needed |
| **Benefits marketplace** | Complex configuration; out of MVP scope | Treat as fixed "benefits_value" |
| **Payroll scheduling** | Requires accounting integration + regulatory complexity | Focus on data management only |
| **Performance reviews** | Different workflow; separates concerns | Future module integration |
| **Org chart visualization** | Nice-to-have; not core HR need | Can add with graph DB later |
| **Export to Excel/PDF** | Frontend complexity; not critical for MVP | Can implement later |
| **Role-based access control (RBAC)** | Security complexity; assume single HR user initially | Add authentication layer later |
| **Real-time notifications** | Requires message queues; not critical | Can add with WebSocket later |

## Technical Architecture

### Backend
- **Framework**: Spring Boot 2.5.0, Java 1.8 (matches verified local dev environment)
- **Database**: SQLite (file-based, relational)
- **ORM**: JPA/Hibernate
- **Testing**: JUnit 5, Mockito, AssertJ
- **Design Pattern**: Layered architecture (Controller → Service → Repository → Entity)

### Frontend
- **Framework**: Angular 14+ (9-17 supported)
- **State Management**: NgRx or plain services
- **UI Components**: Angular Material or PrimeNG
- **Testing**: Jasmine/Karma

### Database Design
- **Tables**: `countries`, `employees`, `salaries` (versioned history)
- **Indices**: Strategic indices on `department`, `country_id`, `employment_status`, employee/salary lookups
- **Scaling**: Pagination (500 per page) + server-side sorting/filtering for 10k records

## Success Metrics
- ✅ All 10,000 employees seeded with realistic data
- ✅ HR manager can view paginated employee list (not all 10k at once)
- ✅ HR manager can search and filter employees efficiently
- ✅ HR manager can view salary data and history
- ✅ HR manager can run analytics: avg salary by dept, salary distribution, etc.
- ✅ All core endpoints tested with unit and integration tests
- ✅ Performance: Page load < 500ms, search < 200ms (measured with pagination)
- ✅ Code coverage: Core business logic ≥ 80%

## Non-Functional Requirements
- **Performance**: Support 10,000 employee records with pagination
- **Maintainability**: Clean code, SOLID principles, comprehensive tests
- **Scalability**: Architecture supports future growth (multi-country, more employees)
- **Security**: Input validation, prepared statements (SQLi prevention)
- **Deployability**: Single JAR (backend), static files (frontend)

## Assumptions
- Single HR manager initially (no RBAC)
- Salary data is sensitive but not encrypted (can add later)
- Network latency: < 100ms
- Database size: ~50MB SQLite file for 10k employees
- Browser support: Modern browsers (Chrome, Firefox, Safari, Edge)

## Success Criteria
1. ✅ Requirements document completed (this doc)
2. ✅ Database schema designed with indices
3. ✅ 10,000 employee seed script working
4. ✅ API endpoints with pagination working
5. ✅ Frontend displays employee list and analytics
6. ✅ Meaningful tests (80%+ coverage on core logic)
7. ✅ Incremental git commits showing evolution

---

**Status**: Ready for Phase 1 - Domain Modeling & Testing
