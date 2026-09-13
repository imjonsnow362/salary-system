# Frontend - Employee Salary Management System

Angular app for the HR-manager UI: employee list/search, employee detail with salary history, and an analytics dashboard. Not yet implemented — this describes the planned structure.

## Setup & Run

```bash
cd frontend
npm install
ng serve   # http://localhost:4200
ng test    # Jasmine/Karma
```

## Planned Structure

```
frontend/src/app/
├── core/          # HTTP services, guards, interceptors
├── shared/        # Reusable components, pipes, models
├── features/
│   ├── employees/   # List (paginated), detail, edit
│   └── analytics/    # Dashboard: salary by dept/country, distribution, headcount
└── app.module.ts
```

## Routes

```
/employees              paginated, searchable list
/employees/:id           detail + salary history
/employees/:id/edit
/employees/new
/analytics                dashboard
```

## Notes on approach
- Server-side pagination throughout — the backend returns 500 records/page, never all 10k at once.
- State management: start with plain services + RxJS `BehaviorSubject`; move to NgRx only if state complexity actually demands it.
- API base URL lives in `src/environments/environment.ts`.
