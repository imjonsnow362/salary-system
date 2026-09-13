# Backend - Employee Salary Management System

## Quick Start

### Prerequisites (locked to verified local environment)
- Java 1.8 (Zulu 8) — verify with `java -version`
- Maven 3.6.3 — verify with `mvn -version`
- Spring Boot 2.5.0 (matches this machine's proven working config)

### Setup & Run

```bash
# 1. Navigate to backend folder
cd backend

# 2. Build the project
mvn clean install

# 3. Seed the database with 10,000 employees
mvn exec:java -Dexec.mainClass="com.salary.config.DataSeeder"
# Output: "🎉 Database seeding complete!"
# Creates: salary_system.db (SQLite file, ~50MB)

# 4. Start the Spring Boot server
mvn spring-boot:run
# Server runs on: http://localhost:8080/api

# 5. Verify it's running
curl http://localhost:8080/api/health
# (when health endpoint is implemented)
```

## Project Structure

```
backend/
├── src/
│   ├── main/java/com/salary/
│   │   ├── SalarySystemApplication.java      # Spring Boot entry point
│   │   ├── config/
│   │   │   ├── DataSeeder.java               # 10k employee seeding script
│   │   │   └── WebConfig.java                # CORS, interceptors
│   │   ├── domain/
│   │   │   ├── Employee.java                 # JPA Entity
│   │   │   ├── Salary.java                   # JPA Entity
│   │   │   └── Country.java                  # JPA Entity
│   │   ├── repository/
│   │   │   ├── EmployeeRepository.java       # JPA Repository
│   │   │   ├── SalaryRepository.java
│   │   │   └── CountryRepository.java
│   │   ├── service/
│   │   │   ├── EmployeeService.java          # Business logic
│   │   │   ├── SalaryService.java
│   │   │   └── AnalyticsService.java         # Aggregations
│   │   ├── controller/
│   │   │   ├── EmployeeController.java       # REST endpoints
│   │   │   ├── SalaryController.java
│   │   │   └── AnalyticsController.java
│   │   ├── dto/
│   │   │   ├── EmployeeDTO.java              # API response model
│   │   │   ├── SalaryDTO.java
│   │   │   └── CreateEmployeeRequest.java    # API request model
│   │   ├── exception/
│   │   │   ├── EmployeeNotFoundException.java
│   │   │   └── GlobalExceptionHandler.java
│   │   └── util/
│   │       └── ValidationUtil.java
│   ├── resources/
│   │   ├── application.properties             # Configuration
│   │   └── schema.sql                         # Database schema
│   └── test/java/com/salary/
│       ├── service/
│       │   ├── EmployeeServiceTest.java       # Unit tests (mocked repo)
│       │   ├── SalaryServiceTest.java
│       │   └── AnalyticsServiceTest.java
│       ├── repository/
│       │   ├── EmployeeRepositoryTest.java    # Integration tests (real DB)
│       │   └── SalaryRepositoryTest.java
│       └── controller/
│           └── EmployeeControllerTest.java    # Controller tests
├── pom.xml
└── README.md (this file)
```

## Key Technologies

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Framework** | Spring Boot 3.1.5 | REST API, IoC, transaction management |
| **ORM** | Hibernate/JPA | Object-relational mapping, database agnostic |
| **Database** | SQLite | File-based relational DB, 10k records |
| **Testing** | JUnit 5, Mockito, AssertJ | Unit, integration, contract tests |
| **Data Gen** | JavaFaker | Realistic 10k employee seed data |

## API Endpoints (Planned)

### Employees
```
GET    /api/employees?page=0&size=500              # List with pagination
GET    /api/employees/{id}                         # Get single employee
POST   /api/employees                              # Create employee
PUT    /api/employees/{id}                         # Update employee
DELETE /api/employees/{id}                         # Delete employee
GET    /api/employees/search?q=alice&dept=eng      # Search with filters
```

### Salaries
```
GET    /api/employees/{id}/salaries                # Salary history
POST   /api/employees/{id}/salaries                # Add new salary record
GET    /api/employees/{id}/salaries/current        # Get latest salary
```

### Analytics
```
GET    /api/analytics/salary-by-department         # Avg salary per dept
GET    /api/analytics/salary-by-country            # Avg salary per country
GET    /api/analytics/salary-distribution          # Percentiles (10th, 50th, 90th)
GET    /api/analytics/headcount-by-country         # Count employees per country
GET    /api/analytics/payroll-cost                 # Total payroll cost
```

## Testing

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=EmployeeServiceTest
```

### Run with Coverage Report
```bash
mvn test jacoco:report
# Report: target/site/jacoco/index.html
```

### Test Structure

**Unit Tests (60%)**
- Mock repository and external dependencies
- Focus on service business logic
- Fast: < 1ms per test
- Example: `EmployeeServiceTest.testCalculateAverageSalaryByDepartment()`

**Integration Tests (30%)**
- Use real SQLite in-memory database
- Test repository queries and transactions
- Slower: 50-100ms per test
- Example: `EmployeeRepositoryTest.testFindByDepartmentWithPagination()`

**E2E Tests (10%)**
- Full Spring context, real database
- Test full request → response cycle
- Slowest: 200-500ms per test
- Example: `EmployeeControllerTest.testGetEmployees_ReturnsPaginatedList()`

## Database

### SQLite Location
```
salary_system.db (in project root after seeding)
```

### View Database
```bash
# Using sqlite3 CLI (if installed)
sqlite3 salary_system.db
sqlite> SELECT COUNT(*) FROM employees;
sqlite> SELECT * FROM employees LIMIT 1;
```

### Database Schema
- **employees**: 10,000 records
- **salaries**: 15,000-30,000 records (1-3 per employee)
- **countries**: 5 records (US, India, UK, Germany, Canada)

### Indices for Performance
```sql
-- Employee lookups
idx_employees_department
idx_employees_country_id

-- Salary queries
idx_salaries_employee_id
idx_salaries_employee_effective  -- For finding latest salary
```

## Configuration

### application.properties
```properties
server.port=8080                              # Server port
spring.datasource.url=jdbc:sqlite:salary_system.db
spring.jpa.hibernate.ddl-auto=update          # Auto-migrate schema
spring.jpa.show-sql=false                     # Disable query logging
```

### Customize Configuration
Edit `src/main/resources/application.properties`:

```properties
# Change server port
server.port=9090

# Enable SQL logging for debugging
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Change database location
spring.datasource.url=jdbc:sqlite:/path/to/salary_system.db
```

## Common Issues & Troubleshooting

### Issue: "SQLite JDBC driver not found"
**Solution:**
```bash
mvn clean install
# Forces dependency download
```

### Issue: "salary_system.db locked" after seeding
**Solution:**
```bash
# Remove old DB and re-seed
rm salary_system.db
mvn exec:java -Dexec.mainClass="com.salary.config.DataSeeder"
```

### Issue: "Port 8080 already in use"
**Solution:**
```bash
# Use different port
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Issue: "Out of memory" during seeding
**Solution:**
```bash
# Increase heap size
export MAVEN_OPTS="-Xmx2g"
mvn exec:java -Dexec.mainClass="com.salary.config.DataSeeder"
```

## Development Workflow

### 1. Write Test First (TDD)
```bash
# Create test in src/test/java/com/salary/service/EmployeeServiceTest.java
# Follow AAA pattern: Arrange, Act, Assert
```

### 2. Run Test (Red Phase)
```bash
mvn test -Dtest=EmployeeServiceTest#testCalculateAverageSalaryByDepartment
# Should FAIL
```

### 3. Implement Code (Green Phase)
```java
// Write minimal implementation to make test pass
public BigDecimal calculateAverageSalaryByDepartment(String dept) {
    List<Salary> salaries = salaryRepository.findByDepartment(dept);
    return salaries.stream()
        .map(Salary::getBaseSalary)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .divide(BigDecimal.valueOf(salaries.size()));
}
```

### 4. Run Test (Green Phase)
```bash
mvn test -Dtest=EmployeeServiceTest#testCalculateAverageSalaryByDepartment
# Should PASS
```

### 5. Refactor (Refactor Phase)
```java
// Clean up, extract methods, improve naming
// Tests still pass
```

### 6. Commit
```bash
git add .
git commit -m "feat: implement calculateAverageSalaryByDepartment service"
```

## Next Steps

1. **Phase 1**: Domain Models & DB Layer (Employee, Salary entities + repositories)
2. **Phase 2**: Service Layer with Business Logic & Unit Tests
3. **Phase 3**: REST Controllers & Integration Tests
4. **Phase 4**: Analytics Endpoints
5. **Phase 5**: Frontend Integration

---

**Ready to start Phase 1?** Confirm, and I'll provide the first domain model entities with TDD tests.
