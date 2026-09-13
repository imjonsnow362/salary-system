# Employee Salary Management System - Delivered Scope

## Goal
Replace ACME's Excel-based salary tracking with a web app for managing and analyzing compensation data for 10,000 employees across 5 countries.

## Problem
HR currently manages salary data in spreadsheets — slow to update, error-prone, and impossible to query for org-wide insights (e.g. "what's our average engineering salary in India?").

## Scope

### Included
- **Employee management**: paginated list, name search, employee profile view, creation, and deactivation
- **API filters**: department and country query parameters on the employee list endpoint
- **Salary management**: current salary, append-only versioned history, and new salary records with effective dates
- **Analytics**: headcount by country, average base salary by department, salary percentiles by country, and payroll cost by country
- **Seed data**: 10,000 employees across five countries and 10 departments, with one to three salary records each

### Current constraints
| Constraint | Detail |
|---|---|
| Frontend filters | The UI exposes name search only; it does not yet expose department or country filters. |
| Employee operations | Search by employee ID or email, profile editing, status filtering, and API sorting are not implemented. |
| Analytics | Analytics are calculated server-side from the current-salary snapshot, not with SQL `GROUP BY` queries. |
| Currency | Salary values remain in local country currency; no exchange-rate conversion is applied. |
| Access | The local MVP has one HR-manager persona and no authentication or authorization. |

### Out of scope
| Feature | Reason |
|---|---|
| Multi-language UI | Not needed for MVP, adds i18n overhead |
| Tax/compliance calculations | Country-specific, legal risk, separate workstream |
| Payroll processing/scheduling | Requires accounting integration, out of scope for a data management tool |
| Role-based access control | Single HR-manager persona for MVP; add auth later if multi-user is needed |
| Export to Excel/PDF | Nice-to-have, not core to answering "how do we pay people" |
| Org chart / performance reviews | Different domain, separate tool |
| Deployment | The submission is demonstrated locally in a video; hosted infrastructure is not included |

## Technical Approach
- **Backend**: Spring Boot 2.5.0, Java 1.8 (matches verified local environment), JPA/Hibernate, SQLite
- **Frontend**: Angular 15, TypeScript, RxJS, Angular Material
- **Testing**: JUnit 5, Mockito, AssertJ (backend); Jasmine/Karma (frontend)
- **Architecture**: Controller → Service → Repository → Entity, server-side pagination for all list views

## Success Criteria
- 10,000 employees seeded with realistic, country-appropriate salary data
- Employee list and name search work without loading all records into the browser
- Salary history is fully auditable (append-only, never overwritten)
- Analytics endpoints compute the stated HR metrics from the current-salary snapshot on the server
- Core business logic has meaningful, fast, deterministic test coverage

## Assumptions
- Single HR manager, no authentication/authorization in MVP
- Salary data not encrypted at rest (acceptable for this exercise)
- Modern browser support only
