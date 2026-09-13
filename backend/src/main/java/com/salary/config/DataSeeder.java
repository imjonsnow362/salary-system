package com.salary.config;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import com.github.javafaker.Faker;

/**
 * DataSeeder: Generates 10,000 realistic employee records across 5 countries.
 *
 * Design Decisions:
 * - Uses JavaFaker library for realistic data generation
 * - Creates employees in batches to manage memory efficiently
 * - Distributes employees across 5 countries and 10 departments
 * - Generates salary data compliant with country-specific ranges
 * - Creates salary history with 1-3 historical records per employee
 *
 * Performance Considerations:
 * - Batch inserts (100 records per batch) to reduce DB round trips
 * - Uses PreparedStatements for security and performance
 * - Generates data in memory, inserts in DB to avoid loading 10k+ rows
 * - Estimated seeding time: 30-60 seconds for 10,000 records
 *
 * Usage:
 *   DataSeeder seeder = new DataSeeder();
 *   seeder.seedDatabase();
 */
public class DataSeeder {

    private static final String JDBC_URL = "jdbc:sqlite:salary_system.db";
    private static final int TOTAL_EMPLOYEES = 10000;
    private static final int BATCH_SIZE = 100;

    // Country data: name -> (currency, min_salary, max_salary)
    private static final Map<String, CountryData> COUNTRY_DATA = buildCountryData();

    private static Map<String, CountryData> buildCountryData() {
        Map<String, CountryData> data = new LinkedHashMap<>();
        data.put("United States", new CountryData("USD", 30000, 250000));
        data.put("India", new CountryData("INR", 300000, 5000000));
        data.put("United Kingdom", new CountryData("GBP", 25000, 200000));
        data.put("Germany", new CountryData("EUR", 30000, 180000));
        data.put("Canada", new CountryData("CAD", 35000, 220000));
        return Collections.unmodifiableMap(data);
    }

    private static final String[] DEPARTMENTS = {
            "Engineering", "Sales", "Marketing", "HR", "Finance",
            "Operations", "Product", "Legal", "Support", "Executive"
    };

    private static final String[] DESIGNATIONS_BY_DEPARTMENT = {
            "Manager", "Senior Manager", "Lead", "Senior", "Junior",
            "Associate", "Analyst", "Coordinator", "Specialist", "Director"
    };

    private final Faker faker;
    private final Random random;

    public DataSeeder() {
        this.faker = new Faker();
        this.random = new Random();
    }

    /**
     * Main seeding entry point. Creates all tables, populates countries, and seeds employees.
     */
    public void seedDatabase() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL)) {
            System.out.println("🌱 Starting database seeding...");

            // Step 1: Create schema
            createSchema(conn);
            System.out.println("✓ Schema created.");

            // Step 2: Get country IDs
            Map<String, Integer> countryIds = getCountryIds(conn);
            System.out.println("✓ Countries loaded: " + countryIds.size());

            // Step 3: Seed employees in batches
            seedEmployees(conn, countryIds);
            System.out.println("✓ Employees seeded.");

            System.out.println("🎉 Database seeding complete!");

        } catch (SQLException e) {
            System.err.println("❌ Seeding failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // DDL kept as an array (not a text block) for Java 8 compatibility.
    private static final String[] SCHEMA_STATEMENTS = {
            "CREATE TABLE IF NOT EXISTS countries (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name VARCHAR(100) NOT NULL UNIQUE," +
                    "currency_code VARCHAR(3) NOT NULL," +
                    "salary_min DECIMAL(12, 2) NOT NULL," +
                    "salary_max DECIMAL(12, 2) NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)",

            "CREATE TABLE IF NOT EXISTS employees (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "employee_id VARCHAR(20) NOT NULL UNIQUE," +
                    "first_name VARCHAR(100) NOT NULL," +
                    "last_name VARCHAR(100) NOT NULL," +
                    "email VARCHAR(150) NOT NULL UNIQUE," +
                    "phone_number VARCHAR(20)," +
                    "department VARCHAR(100) NOT NULL," +
                    "designation VARCHAR(100) NOT NULL," +
                    "country_id INTEGER NOT NULL," +
                    "hired_date DATE NOT NULL," +
                    "employment_status VARCHAR(20) DEFAULT 'ACTIVE'," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE RESTRICT)",

            "CREATE TABLE IF NOT EXISTS salaries (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "employee_id INTEGER NOT NULL," +
                    "base_salary DECIMAL(12, 2) NOT NULL," +
                    "currency_code VARCHAR(3) NOT NULL," +
                    "annual_bonus DECIMAL(12, 2)," +
                    "benefits_value DECIMAL(12, 2)," +
                    "effective_date DATE NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE)",

            "CREATE INDEX IF NOT EXISTS idx_employees_department ON employees(department)",
            "CREATE INDEX IF NOT EXISTS idx_employees_country_id ON employees(country_id)",
            "CREATE INDEX IF NOT EXISTS idx_employees_employment_status ON employees(employment_status)",
            "CREATE INDEX IF NOT EXISTS idx_employees_hired_date ON employees(hired_date)",
            "CREATE INDEX IF NOT EXISTS idx_salaries_employee_id ON salaries(employee_id)",
            "CREATE INDEX IF NOT EXISTS idx_salaries_effective_date ON salaries(effective_date)",
            "CREATE INDEX IF NOT EXISTS idx_salaries_employee_effective ON salaries(employee_id, effective_date DESC)"
    };

    /**
     * Creates all database tables using the schema.
     */
    private void createSchema(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            for (String sql : SCHEMA_STATEMENTS) {
                stmt.execute(sql);
            }
        }
    }

    /**
     * Fetches or creates country records and returns their IDs.
     */
    private Map<String, Integer> getCountryIds(Connection conn) throws SQLException {
        Map<String, Integer> countryIds = new HashMap<>();

        // Check if countries already exist
        String selectSql = "SELECT id, name FROM countries";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSql)) {
            while (rs.next()) {
                countryIds.put(rs.getString("name"), rs.getInt("id"));
            }
        }

        // Insert missing countries
        if (countryIds.size() < COUNTRY_DATA.size()) {
            String insertSql = "INSERT OR IGNORE INTO countries (name, currency_code, salary_min, salary_max) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                for (Map.Entry<String, CountryData> entry : COUNTRY_DATA.entrySet()) {
                    String countryName = entry.getKey();
                    CountryData data = entry.getValue();

                    pstmt.setString(1, countryName);
                    pstmt.setString(2, data.currency);
                    pstmt.setBigDecimal(3, BigDecimal.valueOf(data.minSalary));
                    pstmt.setBigDecimal(4, BigDecimal.valueOf(data.maxSalary));
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            // Re-fetch IDs after insert
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(selectSql)) {
                while (rs.next()) {
                    countryIds.put(rs.getString("name"), rs.getInt("id"));
                }
            }
        }

        return countryIds;
    }

    /**
     * Seeds employees in batches to manage memory efficiently.
     */
    private void seedEmployees(Connection conn, Map<String, Integer> countryIds) throws SQLException {
        String insertEmployeeSql = "INSERT INTO employees (employee_id, first_name, last_name, email, phone_number, department, designation, country_id, hired_date, employment_status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        List<String> countryNames = new ArrayList<>(countryIds.keySet());
        int employeesCreated = 0;

        try (PreparedStatement empStmt = conn.prepareStatement(insertEmployeeSql)) {

            for (int i = 0; i < TOTAL_EMPLOYEES; i++) {
                // Generate employee data
                String countryName = countryNames.get(i % countryNames.size());
                Integer countryId = countryIds.get(countryName);
                CountryData countryData = COUNTRY_DATA.get(countryName);

                String firstName = faker.name().firstName();
                String lastName = faker.name().lastName();
                String employeeId = generateEmployeeId(i + 1);
                String email = generateEmail(firstName, lastName, i);
                String phone = faker.phoneNumber().cellPhone();
                String department = DEPARTMENTS[random.nextInt(DEPARTMENTS.length)];
                String designation = DESIGNATIONS_BY_DEPARTMENT[random.nextInt(DESIGNATIONS_BY_DEPARTMENT.length)];
                LocalDate hiredDate = generateHiredDate();

                // Insert employee
                String now = timestampNow();
                empStmt.setString(1, employeeId);
                empStmt.setString(2, firstName);
                empStmt.setString(3, lastName);
                empStmt.setString(4, email);
                empStmt.setString(5, phone);
                empStmt.setString(6, department);
                empStmt.setString(7, designation);
                empStmt.setInt(8, countryId);
                empStmt.setString(9, hiredDate.toString());
                empStmt.setString(10, "ACTIVE");
                empStmt.setString(11, now);
                empStmt.setString(12, now);
                empStmt.addBatch();

                if ((i + 1) % BATCH_SIZE == 0) {
                    empStmt.executeBatch();
                    employeesCreated += BATCH_SIZE;
                    System.out.printf("Progress: %d / %d employees\n", employeesCreated, TOTAL_EMPLOYEES);
                }
            }

            // Execute remaining batch
            empStmt.executeBatch();
            System.out.printf("Progress: %d / %d employees\n", TOTAL_EMPLOYEES, TOTAL_EMPLOYEES);
        }

        // Now seed salaries for all employees
        seedSalaries(conn, countryIds);
    }

    /**
     * Seeds salary history for all employees.
     * Creates 1-3 historical salary records per employee.
     */
    private void seedSalaries(Connection conn, Map<String, Integer> countryIds) throws SQLException {
        String selectEmployeesSql = "SELECT id, country_id FROM employees ORDER BY id";
        String insertSalarySql = "INSERT INTO salaries (employee_id, base_salary, currency_code, annual_bonus, benefits_value, effective_date, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectEmployeesSql);
             PreparedStatement salaryStmt = conn.prepareStatement(insertSalarySql)) {

            int count = 0;
            while (rs.next()) {
                int employeeId = rs.getInt("id");
                int countryId = rs.getInt("country_id");

                // Find country name for this employee
                String countryName = countryIds.entrySet().stream()
                        .filter(e -> e.getValue().equals(countryId))
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse("United States");

                CountryData countryData = COUNTRY_DATA.get(countryName);

                // Generate 1-3 salary records for history
                int numRecords = random.nextInt(3) + 1;
                LocalDate today = LocalDate.now();

                for (int i = numRecords; i >= 1; i--) {
                    BigDecimal baseSalary = generateSalary(countryData.minSalary, countryData.maxSalary);
                    BigDecimal bonus = baseSalary.multiply(BigDecimal.valueOf(0.1 + random.nextDouble() * 0.3)); // 10-40%
                    BigDecimal benefits = baseSalary.multiply(BigDecimal.valueOf(0.05 + random.nextDouble() * 0.15)); // 5-20%
                    LocalDate effectiveDate = today.minus((i - 1) * 365L, ChronoUnit.DAYS);

                    salaryStmt.setInt(1, employeeId);
                    salaryStmt.setBigDecimal(2, baseSalary);
                    salaryStmt.setString(3, countryData.currency);
                    salaryStmt.setBigDecimal(4, bonus);
                    salaryStmt.setBigDecimal(5, benefits);
                    salaryStmt.setString(6, effectiveDate.toString());
                    salaryStmt.setString(7, timestampNow());
                    salaryStmt.addBatch();
                }

                count++;
                if (count % BATCH_SIZE == 0) {
                    salaryStmt.executeBatch();
                    System.out.printf("Salaries: %d / %d employees processed\n", count, TOTAL_EMPLOYEES);
                }
            }

            salaryStmt.executeBatch();
            System.out.printf("Salaries: %d / %d employees processed\n", TOTAL_EMPLOYEES, TOTAL_EMPLOYEES);
        }
    }

    // ========== Helper Methods ==========

    private String generateEmployeeId(int index) {
        return String.format("EMP%06d", index);
    }

    private String generateEmail(String firstName, String lastName, int index) {
        return String.format("%s.%s.%d@company.com", firstName.toLowerCase(), lastName.toLowerCase(), index).replaceAll("\\s+", "");
    }

    private LocalDate generateHiredDate() {
        // Employees hired between 1-10 years ago
        long daysAgo = 365 + random.nextInt(365 * 9);
        return LocalDate.now().minus(daysAgo, ChronoUnit.DAYS);
    }

    // Millisecond precision so the SQLite JDBC driver's timestamp parser doesn't choke (unlike its own CURRENT_TIMESTAMP default)
    private String timestampNow() {
        return java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()).toString();
    }

    private BigDecimal generateSalary(double minSalary, double maxSalary) {
        double salary = minSalary + random.nextDouble() * (maxSalary - minSalary);
        // Round to nearest 100
        return BigDecimal.valueOf(Math.round(salary / 100.0) * 100.0);
    }

    // ========== Helper Data Classes ==========

    private static class CountryData {
        String currency;
        double minSalary;
        double maxSalary;

        CountryData(String currency, double minSalary, double maxSalary) {
            this.currency = currency;
            this.minSalary = minSalary;
            this.maxSalary = maxSalary;
        }
    }

    // ========== Entry Point ==========

    public static void main(String[] args) {
        new DataSeeder().seedDatabase();
    }
}
