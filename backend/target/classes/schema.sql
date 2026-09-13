-- ============================================================================
-- SALARY SYSTEM DATABASE SCHEMA - SQLite
-- ============================================================================
-- This schema is designed for 10,000 employees across 5 countries
-- Performance focus: Proper indexing for pagination, sorting, and aggregations
-- ============================================================================

-- Countries Master Data Table
CREATE TABLE IF NOT EXISTS countries (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    currency_code VARCHAR(3) NOT NULL,
    salary_min DECIMAL(12, 2) NOT NULL,
    salary_max DECIMAL(12, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Employees Table
-- Design: Single source of truth for employee record
-- No salary data here; salaries are versioned in a separate table for audit trail
CREATE TABLE IF NOT EXISTS employees (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    employee_id VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    department VARCHAR(100) NOT NULL,
    designation VARCHAR(100) NOT NULL,
    country_id INTEGER NOT NULL,
    hired_date DATE NOT NULL,
    employment_status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE RESTRICT
);

-- Salaries Table (Versioned History)
-- Design: Keep salary history for audit and analytics
-- Each salary change creates a new record with effective_date
-- Query latest salary: WHERE effective_date <= TODAY ORDER BY effective_date DESC LIMIT 1
CREATE TABLE IF NOT EXISTS salaries (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    employee_id INTEGER NOT NULL,
    base_salary DECIMAL(12, 2) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    annual_bonus DECIMAL(12, 2),
    benefits_value DECIMAL(12, 2),
    effective_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
);

-- ============================================================================
-- INDEXES - Critical for Performance with 10,000 Records
-- ============================================================================

-- Employee lookup and filtering
CREATE INDEX IF NOT EXISTS idx_employees_department ON employees(department);
CREATE INDEX IF NOT EXISTS idx_employees_country_id ON employees(country_id);
CREATE INDEX IF NOT EXISTS idx_employees_employment_status ON employees(employment_status);
CREATE INDEX IF NOT EXISTS idx_employees_hired_date ON employees(hired_date);

-- Salary lookups
CREATE INDEX IF NOT EXISTS idx_salaries_employee_id ON salaries(employee_id);
CREATE INDEX IF NOT EXISTS idx_salaries_effective_date ON salaries(effective_date);
-- Composite index for finding latest salary per employee efficiently
CREATE INDEX IF NOT EXISTS idx_salaries_employee_effective ON salaries(employee_id, effective_date DESC);

-- ============================================================================
-- POPULATE MASTER DATA (Countries)
-- ============================================================================

INSERT OR IGNORE INTO countries (name, currency_code, salary_min, salary_max) VALUES
('United States', 'USD', 30000.00, 250000.00),
('India', 'INR', 300000.00, 5000000.00),
('United Kingdom', 'GBP', 25000.00, 200000.00),
('Germany', 'EUR', 30000.00, 180000.00),
('Canada', 'CAD', 35000.00, 220000.00);
