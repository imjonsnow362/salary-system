# Architecture Decisions & Trade-offs

## Overview
This document captures key architectural decisions, trade-offs, and rationale for the Employee Salary Management System.

---

## Decision 1: SQLite as Database

### Choice: SQLite
**Rationale:**
- **File-based**: No server setup required—`salary_system.db` is deployed as a single file
- **Relational model**: Perfect for structured HR data (employees, salaries, departments)
- **Sufficient for scale**: 10,000 records fit easily in SQLite; ~50MB file size
- **Development speed**: No Docker/Docker Compose, no connection pooling hassle
- **Testing**: Can use in-memory SQLite for fast, deterministic tests

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
1. Code uses JPA—switch `spring.jpa.properties.hibernate.dialect` to PostgreSQL
2. Export SQLite data, import to PostgreSQL
3. No application code changes needed (repositories unchanged)

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
    Pageable pageable = PageRequest.of(page, pageSize, Sort.by("employee_id").ascending());
    return employeeRepository.findAll(pageable)
        .map(EmployeeDTO::from);
}
```

**Why server-side?**
- 10,000 records = ~5-10 MB in memory; scales poorly with more employees
- Network transfer: All 10k records = 5-10 MB over HTTP
- Browser rendering: Slow with 10k DOM nodes
- **User experience**: Users see first page instantly (< 500ms vs 2-3s)

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

## Decision 5: Pagination Size: 500 records per page

### Choice: 500 records/page

**Rationale:**
- **Network**: ~500KB per page (with typical DTO fields)
- **Browser rendering**: 500 rows ≈ 100KB of DOM, renders instantly
- **Usability**: Users can scroll 500 rows; more = "give me search"

### Calculation for 10,000 employees

| Aspect | Value |
|--------|-------|
| Total employees | 10,000 |
| Records per page | 500 |
| Total pages | 20 |
| Avg response time (w/ pagination) | < 200ms |
| Avg response time (all 10k) | 1-2 seconds |

**Trade-off:**
- Fewer records/page = more clicks to browse
- More records/page = slower response, harder to find data (need search instead)

---

## Decision 6: API Design - RESTful with DTO

### Choice: REST API with DTOs (Data Transfer Objects)

```
GET /api/employees?page=0&size=500&sort=department,asc
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
    List<Salary> engineeringSalaries = List.of(
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
        // Setup: Use real in-memory SQLite
        Employee emp1 = new Employee("Alice", "Eng", ...);
        em.persistAndFlush(emp1);
        
        // Execute & Assert
        Page<Employee> page = repository.findByDepartment("Eng", PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(1);
    }
}
```

---

## Decision 8: Frontend Framework - Angular 14+

### Choice: Angular (14-17 range)

**Rationale:**
- **TypeScript**: Type-safe, IDE support, fewer runtime errors
- **RxJS**: Powerful for async data (pagination, search debounce)
- **Modularity**: Feature modules for employees, analytics, admin
- **Testing**: Jasmine/Karma baked in

### State Management: NgRx (Optional, but recommended)

**If complexity grows:**
- Use NgRx for caching employee list (avoid re-fetching on pagination)
- Actions: `LoadEmployees`, `LoadEmployeesSuccess`, `LoadEmployeesFailure`
- Selectors: `selectEmployeeList`, `selectLoading`

**For MVP:** Can use simple service + RxJS BehaviorSubject

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

**Status**: Architecture locked in. Ready for implementation.
