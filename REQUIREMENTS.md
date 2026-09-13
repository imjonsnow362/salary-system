# Employee Salary Management System - Requirements

## Goal
Replace ACME's Excel-based salary tracking with a web app for managing and analyzing compensation data for 10,000 employees across 5 countries.

## Problem
HR currently manages salary data in spreadsheets — slow to update, error-prone, and impossible to query for org-wide insights (e.g. "what's our average engineering salary in India?").

## Scope

### In scope
- **Employee management**: paginated list, search (name/ID/email), filter by department/country/status, view/edit profile
- **Salary management**: current salary + full versioned history, add new salary record with effective date
- **Analytics**: headcount by country, average salary by department, salary percentiles, payroll cost by country
- **Seed data**: 10,000 employees across 5 countries, 10 departments, realistic salary ranges, 1-3 salary records each

### Out of scope (and why)
| Feature | Reason |
|---|---|
| Multi-language UI | Not needed for MVP, adds i18n overhead |
| Tax/compliance calculations | Country-specific, legal risk, separate workstream |
| Payroll processing/scheduling | Requires accounting integration, out of scope for a data management tool |
| Role-based access control | Single HR-manager persona for MVP; add auth later if multi-user is needed |
| Export to Excel/PDF | Nice-to-have, not core to answering "how do we pay people" |
| Org chart / performance reviews | Different domain, separate tool |

## Technical Approach
- **Backend**: Spring Boot 2.5.0, Java 1.8 (matches verified local environment), JPA/Hibernate, SQLite
- **Frontend**: Angular
- **Testing**: JUnit 5, Mockito, AssertJ (backend); Jasmine/Karma (frontend)
- **Architecture**: Controller → Service → Repository → Entity, server-side pagination for all list views

## Success Criteria
- 10,000 employees seeded with realistic, country-appropriate salary data
- Employee list and search work without loading all records into memory/browser
- Salary history is fully auditable (append-only, never overwritten)
- Analytics endpoints answer the stated HR questions with DB-level aggregation, not in-memory computation
- Core business logic has meaningful, fast, deterministic test coverage

## Assumptions
- Single HR manager, no authentication/authorization in MVP
- Salary data not encrypted at rest (acceptable for this exercise)
- Modern browser support only
