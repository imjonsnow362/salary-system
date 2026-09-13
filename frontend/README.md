# Salary Frontend

Angular 15 UI for employee records, salary history, and analytics.

## Run

Start the backend API on port 8080 first, then run:

```bash
npm install
npm start
```

Open `http://localhost:4200`. Development requests target `http://localhost:8080/api`.

## Commands

```bash
npm test
npm run build
```

## Structure

- `src/app/core`: API services and shared models
- `src/app/features/employees`: employee list, profile, create form, and salary history
- `src/app/features/analytics`: analytics dashboard
