# Architecture Decisions & Trade-offs

## Overview
This document captures the implemented architectural decisions and trade-offs for the Employee Salary Management System.

---

## Decision 1: SQLite as Database

### Choice: SQLite
**Rationale:**
- **File-based**: No server setup is required; `salary_system.db` is a local application file
- **Relational model**: Perfect for structured HR data (employees, salaries, departments)
- **Sufficient for scale**: suitable for this local 10,000-employee exercise
- **Development speed**: No Docker/Docker Compose, no connection pooling hassle
- **Testing**: backend tests use H2 in memory for fast, deterministic tests

### Trade-offs:

| Aspect | SQLite | PostgreSQL | MySQL |
|--------|--------|-----------|-------|
| Setup | None (file) | Docker compose needed | Docker needed |
| Concurrency | Limited (single writer) | Excellent | Good |
| Scaling | Up to 1M+ rows (fine for 10k) | Multi-TB | Multi-TB |
| Cost | Free | Free/Paid | Free/Paid |
| Replication | Not built-in | Built-in | Built-in |

**Why NOT PostgreSQL/MySQL?**
- Adds operational complexity (containers, connection pools, backups)
- Overkill for MVP with single HR manager
- Can migrate to PostgreSQL later if needed (tables, queries stay same)

**Future Migration Path:**
1. Replace the SQLite JDBC driver and dialect with PostgreSQL equivalents.
2. Migrate the SQLite data and review SQLite-specific schema details.
3. Re-test repository queries and migrations against PostgreSQL.

---

## Decision 2: Layered Architecture (Controller → Service → Repository)

### Choice: Layered (3-tier) Architecture

```
Controller (REST API)
    ↓
Service (Business Logic)
    ↓
Repository (Data Access)
    ↓
Entity (Domain Model)
```

**Rationale:**
- **Separation of Concerns**: Each layer has single responsibility
  - Controller: HTTP handling, input validation
  - Service: Business logic, transactions, rules
  - Repository: Database queries
  - Entity: Domain model
  
- **Testability**: Each layer tested independently
  - Controller: Test with mock service
  - Service: Test with mock repository
  - Repository: Integration test with real DB
  
- **Maintainability**: Clear boundaries = easy to locate bugs

### Alternative: Domain-Driven Design (DDD)
**Why NOT DDD?**
- Overkill for MVP; adds bounded contexts, value objects, domain events
- Fine for complex domains (e.g., billing, insurance)
- HR salary data is relatively straightforward
- **Future consideration**: If analytics become complex (e.g., derived salary metrics), migrate to DDD

---

## Decision 3: JPA/Hibernate ORM

### Choice: Hibernate + Spring Data JPA

**Rationale:**
- **Productivity**: Automatic SQL generation for CRUD
- **Type safety**: Java objects instead of raw SQL
- **Database agnostic**: Change dialect, queries stay same
- **Relationships**: Built-in support for Employee ↔ Salary (1:N)

### Alternative: Raw JDBC
**Why NOT raw JDBC?**
- Boilerplate (ResultSet parsing, connection management)
- Slower development
- Losing database portability

### Pagination Strategy

**Server-side pagination** (NOT load all 10k into memory):

```java
// Service layer
public Page<EmployeeDTO> getEmployees(int page, int pageSize) {
    Pageable pageable = PageRequest.of(page, pageSize);
    return employeeRepository.findAll(pageable)
        .map(EmployeeDTO::from);
}
```

**Why server-side?**
- 10,000 records = ~5-10 MB in memory; scales poorly with more employees
- Network transfer: All 10k records = 5-10 MB over HTTP
- Browser rendering: Slow with 10k DOM nodes
- **User experience**: users can browse a bounded result set instead of a full employee table

---

## Decision 4: Salary Versioning (Immutable History)

### Choice: Immutable salary history table

```sql
CREATE TABLE salaries (
    id INTEGER PRIMARY KEY,
    employee_id INTEGER,
    base_salary DECIMAL,
    effective_date DATE,
    created_at TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);
```

**Rationale:**
- **Audit trail**: Never modify salary records → complete history
- **Temporal queries**: "What was John's salary on 2023-01-01?"
- **Compliance**: HR legal often requires 7-year salary history

### Example Workflow

```
Employee: Alice
Hire date: 2020-01-01, Salary: $50,000

2020-01-01: INSERT INTO salaries (employee_id, base_salary, effective_date) VALUES (1, 50000, '2020-01-01')
2021-01-01: INSERT INTO salaries (employee_id, base_salary, effective_date) VALUES (1, 55000, '2021-01-01')
2022-01-01: INSERT INTO salaries (employee_id, base_salary, effective_date) VALUES (1, 60000, '2022-01-01')

Query: "Current salary" 
SELECT * FROM salaries WHERE employee_id = 1 ORDER BY effective_date DESC LIMIT 1
```

---

## Decision 5: Pagination

### Choice: server-side pagination

**Rationale:**
- The API defaults to 500 records per page when no size is supplied.
- The Angular list requests 20 records per page and lets the user choose 10, 20, 50, or 100.
- Page size is sent to the server, so the browser never loads the full employee population.

### Calculation for 10,000 employees

| Aspect | Value |
|--------|-------|
| Total employees | 10,000 |
| API default page size | 500 |
| UI default page size | 20 |
| UI page-size options | 10, 20, 50, 100 |

**Trade-off:**
- Fewer records/page = more clicks to browse
- More records/page = slower response, harder to find data (need search instead)

---

## Decision 6: API Design - RESTful with DTO

### Choice: REST API with DTOs (Data Transfer Objects)

```
GET /api/employees?page=0&size=500&q=alice
→ Returns EmployeeDTO (subset of Employee entity)
```

**Rationale:**
- **Loose coupling**: Client code doesn't depend on Entity structure
- **Security**: Don't leak internal field (e.g., password hash)
- **API stability**: Change Entity without breaking client

### Example

```java
// Entity (database model)
@Entity
public class Employee {
    private Long id;
    private String firstName;
    private String lastName;
    // ... 10+ fields
}

// DTO (API response model)
@Data
public class EmployeeDTO {
    private String employeeId;
    private String fullName;
    private String department;
    private String country;
}
```

---

## Decision 7: Testing Strategy - Pyramid (Unit > Integration > E2E)

### Testing Pyramid

```
        /\        E2E (10%)
       /  \       - Full flow: seed DB, call API, check response
      /----\
     /      \    Integration (30%)
    /        \   - Repository + real DB, Service with mocks
   /----------\
  /            \ Unit (60%)
 /              \- Service logic, calculations, validators
/______________\
```

### Unit Tests (First)

**Example: EmployeeServiceTest**

```java
@Test
void testCalculateAverageSalaryByDepartment_Engineering() {
    // Setup: Mock repository
    List<Salary> engineeringSalaries = Arrays.asList(
        new Salary(50000),
        new Salary(60000),
        new Salary(70000)
    );
    when(salaryRepository.findByDepartment("Engineering"))
        .thenReturn(engineeringSalaries);
    
    // Execute
    BigDecimal avg = employeeService.getAverageSalaryByDepartment("Engineering");
    
    // Assert
    assertThat(avg).isEqualByComparingTo(BigDecimal.valueOf(60000));
}
```

### Integration Tests

```java
@SpringBootTest
class EmployeeRepositoryTest {
    @Autowired
    private EmployeeRepository repository;
    
    @Autowired
    private TestEntityManager em;
    
    @Test
    void testFindByDepartmentWithPagination() {
        // Setup: use H2 through the test profile
        Employee emp1 = new Employee("Alice", "Eng", ...);
        em.persistAndFlush(emp1);
        
        // Execute & Assert
        Page<Employee> page = repository.findByDepartment("Eng", PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(1);
    }
}
```

---

## Decision 8: Frontend Framework - Angular 15

### Choice: Angular 15

**Rationale:**
- **TypeScript**: Type-safe, IDE support, fewer runtime errors
- **RxJS**: Powerful for async data (pagination, search debounce)
- **Modularity**: Feature modules for employees, analytics, admin
- **Testing**: Jasmine/Karma baked in

The application currently uses feature modules, `HttpClient`, and RxJS observables. NgRx is unnecessary at the current scope; introduce it only if shared client state and caching become material.

---

## Decision 9: Seeding with JavaFaker

### Choice: Programmatic seed with JavaFaker

**Rationale:**
- **Realistic data**: Names, emails, phone numbers look real
- **Repeatable**: Same seed = same data (for testing)
- **Scalable**: Generate 1M records if needed
- **No dependencies**: No CSV files to manage

### Seed Script

```bash
mvn exec:java -Dexec.mainClass="com.salary.config.DataSeeder"
```

Generates:
- 10,000 employees across 5 countries
- Distributed across 10 departments
- Salary data matching country ranges (USD: 30k-250k, INR: 300k-5M, etc.)
- 1-3 salary history records per employee

**Why NOT CSV?**
- File size: 10k records = 2-5 MB CSV
- Parsing: Extra step in build pipeline
- Flexibility: Can adjust ranges, distributions in code

---

## Decision 10: Error Handling & Validation

### Strategy: Fail-fast validation

```java
@PostMapping("/employees")
public EmployeeDTO createEmployee(@Valid @RequestBody CreateEmployeeRequest req) {
    // @Valid triggers validation on request object
    // InvalidArgumentException caught by @ControllerAdvice
    return service.createEmployee(req);
}

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationError(MethodArgumentNotValidException ex) {
        return ErrorResponse.of("Validation failed", ex.getBindingResult());
    }
}
```

**Rationale:**
- **Centralized**: Single place to handle all exceptions
- **Consistent**: All errors returned in same format
- **Clean**: Services don't worry about HTTP details

---

## Summary Table

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Database | SQLite | File-based, relational, scales to 10k, zero setup |
| Architecture | Layered 3-tier | Testable, maintainable, clear separation |
| ORM | Hibernate/JPA | Type-safe, database agnostic, productive |
| Pagination | Server-side, 500/page | Performance, UX, network efficiency |
| Salary History | Immutable versioning | Audit trail, compliance, temporal queries |
| API | REST + DTO | Loose coupling, security, stability |
| Testing | Unit > Integration > E2E | Fast feedback, high confidence, pyramid |
| Frontend | Angular 14+ | Type-safe, modular, async handling (RxJS) |
| Seeding | JavaFaker programmatically | Realistic, repeatable, scalable |
| Error Handling | Centralized @ControllerAdvice | Consistent API responses |

---

## Analytics Trade-off

Analytics select the latest salary record for each employee and calculate grouped results in the service layer. Raw salary rows are not sent to the Angular client. This is acceptable for the local 10,000-employee exercise but is not database-level aggregation.

Country payroll and salary-distribution results retain local currency. Department salary averages have no exchange-rate conversion and should not be treated as a cross-currency financial total.
