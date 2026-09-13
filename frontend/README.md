# Frontend - Employee Salary Management System

## Quick Start

### Prerequisites
- Node.js 14+ (verify with `node -v`)
- Angular CLI 14+ (verify with `ng version`)

### Setup & Run

```bash
# 1. Navigate to frontend folder
cd frontend

# 2. Install dependencies
npm install

# 3. Start development server
ng serve
# Opens: http://localhost:4200

# 4. Run tests
ng test

# 5. Build for production
ng build --prod
# Output: dist/salary-management-ui
```

## Project Structure

```
frontend/
├── src/
│   ├── app/
│   │   ├── core/
│   │   │   ├── services/
│   │   │   │   ├── employee.service.ts      # HTTP calls to backend
│   │   │   │   ├── salary.service.ts
│   │   │   │   ├── analytics.service.ts
│   │   │   │   └── error.service.ts
│   │   │   ├── guards/
│   │   │   │   └── auth.guard.ts            # Route protection
│   │   │   ├── interceptors/
│   │   │   │   └── error.interceptor.ts     # Global error handling
│   │   │   └── core.module.ts               # Singleton services
│   │   │
│   │   ├── shared/
│   │   │   ├── components/
│   │   │   │   ├── header/
│   │   │   │   ├── footer/
│   │   │   │   ├── loading-spinner/
│   │   │   │   └── error-banner/
│   │   │   ├── pipes/
│   │   │   │   ├── currency.pipe.ts
│   │   │   │   └── phone.pipe.ts
│   │   │   ├── directives/
│   │   │   ├── models/
│   │   │   │   ├── employee.model.ts        # TypeScript interfaces/classes
│   │   │   │   ├── salary.model.ts
│   │   │   │   └── analytics.model.ts
│   │   │   └── shared.module.ts
│   │   │
│   │   ├── features/
│   │   │   ├── employees/
│   │   │   │   ├── pages/
│   │   │   │   │   ├── employee-list/      # List with pagination
│   │   │   │   │   │   ├── employee-list.component.ts
│   │   │   │   │   │   ├── employee-list.component.html
│   │   │   │   │   │   ├── employee-list.component.scss
│   │   │   │   │   │   └── employee-list.component.spec.ts
│   │   │   │   │   ├── employee-detail/    # Single employee + salary history
│   │   │   │   │   │   ├── employee-detail.component.ts
│   │   │   │   │   │   ├── employee-detail.component.html
│   │   │   │   │   │   └── employee-detail.component.spec.ts
│   │   │   │   │   └── employee-edit/      # Create/Edit employee
│   │   │   │   ├── components/
│   │   │   │   │   ├── employee-table/     # Reusable table
│   │   │   │   │   ├── employee-filter/    # Search & filter controls
│   │   │   │   │   └── salary-history/     # Salary history timeline
│   │   │   │   ├── services/
│   │   │   │   │   └── employee-state.service.ts  # Local state management
│   │   │   │   ├── store/ (if using NgRx)
│   │   │   │   │   ├── employee.actions.ts
│   │   │   │   │   ├── employee.reducer.ts
│   │   │   │   │   ├── employee.selector.ts
│   │   │   │   │   └── employee.effects.ts
│   │   │   │   └── employees.module.ts
│   │   │   │
│   │   │   └── analytics/
│   │   │       ├── pages/
│   │   │       │   └── dashboard/
│   │   │       │       ├── dashboard.component.ts
│   │   │       │       ├── dashboard.component.html
│   │   │       │       └── dashboard.component.spec.ts
│   │   │       ├── components/
│   │   │       │   ├── salary-by-dept-chart/     # Bar chart
│   │   │       │   ├── salary-distribution/      # Box plot or percentiles
│   │   │       │   ├── headcount-by-country/     # Pie chart
│   │   │       │   └── payroll-summary/          # KPI cards
│   │   │       ├── services/
│   │   │       │   └── analytics.state.service.ts
│   │   │       └── analytics.module.ts
│   │   │
│   │   ├── app.module.ts                    # Root module
│   │   ├── app-routing.module.ts            # Routes
│   │   ├── app.component.ts                 # Root component
│   │   └── app.component.html
│   │
│   ├── assets/
│   │   ├── images/
│   │   ├── icons/
│   │   └── styles/
│   │       └── global.scss
│   │
│   ├── environments/
│   │   ├── environment.ts                   # Development
│   │   └── environment.prod.ts              # Production
│   │
│   ├── index.html
│   └── main.ts
│
├── angular.json                             # Angular CLI config
├── tsconfig.json                            # TypeScript config
├── tsconfig.app.json
├── tsconfig.spec.json
├── karma.conf.js                            # Test runner
├── package.json
├── package-lock.json
├── .eslintrc.json                           # Linting
└── README.md (this file)
```

## Key Technologies

| Technology | Purpose |
|-----------|---------|
| **Angular** | Frontend framework (component-based, TypeScript) |
| **TypeScript** | Type-safe JavaScript |
| **RxJS** | Reactive programming (Observables, operators) |
| **Angular Material** OR **PrimeNG** | UI component library |
| **NgRx** (optional) | State management (centralized store) |
| **Jasmine/Karma** | Unit testing |

## Routes & Pages

```
/                           → Home / Dashboard
/employees                  → Employee list (paginated, searchable)
/employees/:id              → Employee detail + salary history
/employees/:id/edit         → Edit employee
/employees/new              → Create new employee
/analytics                  → Analytics dashboard
  - Salary by department
  - Salary distribution
  - Headcount by country
  - Total payroll cost
/404                        → Not found
```

## State Management

### Option 1: Simple Services (Recommended for MVP)

```typescript
// employee-state.service.ts
@Injectable({ providedIn: 'root' })
export class EmployeeStateService {
  private employeeListSubject = new BehaviorSubject<Employee[]>([]);
  employeeList$ = this.employeeListSubject.asObservable();

  loadEmployees(page: number, size: number) {
    this.employeeService.getEmployees(page, size)
      .subscribe(
        (data) => this.employeeListSubject.next(data.content),
        (error) => console.error(error)
      );
  }
}

// Component
export class EmployeeListComponent implements OnInit {
  employees$ = this.stateService.employeeList$;

  constructor(private stateService: EmployeeStateService) {}

  ngOnInit() {
    this.stateService.loadEmployees(0, 500);
  }
}
```

### Option 2: NgRx Store (For Complex State)

```typescript
// store/employee.actions.ts
export const loadEmployees = createAction(
  '[Employee Page] Load Employees',
  props<{ page: number; size: number }>()
);

// store/employee.effects.ts
@Injectable()
export class EmployeeEffects {
  loadEmployees$ = createEffect(() =>
    this.actions$.pipe(
      ofType(loadEmployees),
      switchMap(action => 
        this.employeeService.getEmployees(action.page, action.size)
          .pipe(
            map(data => loadEmployeesSuccess({ employees: data.content })),
            catchError(error => of(loadEmployeesFailure({ error })))
          )
      )
    )
  );
}

// store/employee.selector.ts
export const selectEmployees = createSelector(
  selectEmployeeState,
  (state) => state.employees
);

// Component
export class EmployeeListComponent {
  employees$ = this.store.select(selectEmployees);

  constructor(private store: Store) {}

  ngOnInit() {
    this.store.dispatch(loadEmployees({ page: 0, size: 500 }));
  }
}
```

**Recommendation**: Start with simple services for MVP, migrate to NgRx if state becomes complex.

## HTTP & API Integration

### EmployeeService

```typescript
@Injectable({ providedIn: 'root' })
export class EmployeeService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getEmployees(page: number, size: number): Observable<Page<Employee>> {
    return this.http.get<Page<Employee>>(
      `${this.apiUrl}/employees?page=${page}&size=${size}`
    ).pipe(
      tap(data => console.log('Loaded employees', data)),
      catchError(error => {
        console.error('Error loading employees', error);
        return throwError(() => error);
      })
    );
  }

  searchEmployees(query: string, dept?: string): Observable<Employee[]> {
    let params = new HttpParams().set('q', query);
    if (dept) params = params.set('dept', dept);
    
    return this.http.get<Employee[]>(
      `${this.apiUrl}/employees/search`,
      { params }
    );
  }

  getEmployeeById(id: number): Observable<Employee> {
    return this.http.get<Employee>(`${this.apiUrl}/employees/${id}`);
  }

  createEmployee(emp: CreateEmployeeRequest): Observable<Employee> {
    return this.http.post<Employee>(`${this.apiUrl}/employees`, emp);
  }

  updateEmployee(id: number, emp: UpdateEmployeeRequest): Observable<Employee> {
    return this.http.put<Employee>(`${this.apiUrl}/employees/${id}`, emp);
  }

  deleteEmployee(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/employees/${id}`);
  }
}
```

### Environment Configuration

```typescript
// environment.ts (development)
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};

// environment.prod.ts (production)
export const environment = {
  production: true,
  apiUrl: 'https://api.acme.com/api'
};

// Use in services
import { environment } from '@env/environment';
private apiUrl = environment.apiUrl;
```

## Components Examples

### Employee List Component

```typescript
// employee-list.component.ts
@Component({
  selector: 'app-employee-list',
  templateUrl: './employee-list.component.html',
  styleUrls: ['./employee-list.component.scss']
})
export class EmployeeListComponent implements OnInit {
  employees$ = this.employeeService.getEmployees(0, 500);
  
  currentPage = 0;
  pageSize = 500;
  totalElements = 0;
  
  searchQuery = '';
  selectedDept: string | null = null;

  constructor(private employeeService: EmployeeService) {}

  ngOnInit() {
    this.loadEmployees();
  }

  loadEmployees() {
    this.employees$ = this.employeeService.getEmployees(
      this.currentPage,
      this.pageSize
    );
  }

  onSearch(query: string) {
    this.searchQuery = query;
    this.currentPage = 0; // Reset to first page
    this.loadEmployees();
  }

  onFilterDept(dept: string | null) {
    this.selectedDept = dept;
    this.currentPage = 0;
    this.loadEmployees();
  }

  onPageChange(newPage: number) {
    this.currentPage = newPage;
    this.loadEmployees();
  }
}
```

```html
<!-- employee-list.component.html -->
<div class="employee-list">
  <h1>Employees</h1>
  
  <app-employee-filter 
    (search)="onSearch($event)"
    (filterDept)="onFilterDept($event)">
  </app-employee-filter>

  <div *ngIf="(employees$ | async) as employees; else loading">
    <app-employee-table [employees]="employees"></app-employee-table>
    
    <app-paginator 
      [currentPage]="currentPage"
      [pageSize]="pageSize"
      [totalElements]="totalElements"
      (pageChange)="onPageChange($event)">
    </app-paginator>
  </div>

  <ng-template #loading>
    <app-loading-spinner></app-loading-spinner>
  </ng-template>
</div>
```

## Testing (Jasmine/Karma)

### Unit Test Example

```typescript
// employee-list.component.spec.ts
describe('EmployeeListComponent', () => {
  let component: EmployeeListComponent;
  let fixture: ComponentFixture<EmployeeListComponent>;
  let employeeService: jasmine.SpyObj<EmployeeService>;

  beforeEach(async () => {
    const employeeServiceSpy = jasmine.createSpyObj('EmployeeService', [
      'getEmployees'
    ]);

    await TestBed.configureTestingModule({
      declarations: [EmployeeListComponent],
      providers: [
        { provide: EmployeeService, useValue: employeeServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(EmployeeListComponent);
    component = fixture.componentInstance;
    employeeService = TestBed.inject(EmployeeService) as jasmine.SpyObj<EmployeeService>;
  });

  it('should load employees on init', () => {
    const mockEmployees = [
      { id: 1, name: 'Alice', dept: 'Engineering' }
    ];
    employeeService.getEmployees.and.returnValue(of(mockEmployees));

    fixture.detectChanges(); // Triggers ngOnInit

    expect(employeeService.getEmployees).toHaveBeenCalledWith(0, 500);
  });

  it('should display employees in table', () => {
    const mockEmployees = [
      { id: 1, name: 'Alice', dept: 'Engineering' }
    ];
    employeeService.getEmployees.and.returnValue(of(mockEmployees));

    fixture.detectChanges();

    const compiled = fixture.nativeElement;
    expect(compiled.querySelector('app-employee-table')).toBeTruthy();
  });
});
```

### Run Tests

```bash
# Run all tests
ng test

# Run with coverage
ng test --code-coverage
# Report: coverage/salary-management-ui/index.html

# Run in headless mode (CI)
ng test --watch=false --browsers=ChromeHeadless
```

## Global Styles & Theme

```scss
// global.scss
@import '@angular/material/prebuilt-themes/indigo-pink.css';
// or
@import 'theme.scss';

:root {
  --primary-color: #1976d2;
  --accent-color: #ff4081;
  --warn-color: #f44336;
  --text-dark: #212121;
  --text-light: #757575;
  --border-color: #bdbdbd;
  --bg-light: #fafafa;
}

body {
  font-family: Roboto, sans-serif;
  color: var(--text-dark);
  background-color: var(--bg-light);
}
```

## Performance Tips

1. **OnPush Change Detection**
   ```typescript
   @Component({
     selector: 'app-employee-table',
     changeDetection: ChangeDetectionStrategy.OnPush
   })
   ```

2. **Lazy Load Routes**
   ```typescript
   const routes: Routes = [
     { path: 'employees', loadChildren: () => import('./features/employees/employees.module').then(m => m.EmployeesModule) },
     { path: 'analytics', loadChildren: () => import('./features/analytics/analytics.module').then(m => m.AnalyticsModule) }
   ];
   ```

3. **Use trackBy in *ngFor**
   ```html
   <div *ngFor="let emp of employees; trackBy: trackByEmployeeId">
     {{ emp.name }}
   </div>
   ```
   ```typescript
   trackByEmployeeId(index: number, emp: Employee) {
     return emp.id;
   }
   ```

4. **Unsubscribe from Observables**
   ```typescript
   private destroy$ = new Subject<void>();

   ngOnInit() {
     this.employees$
       .pipe(takeUntil(this.destroy$))
       .subscribe(employees => { /* ... */ });
   }

   ngOnDestroy() {
     this.destroy$.next();
     this.destroy$.complete();
   }
   ```

## Debugging

### Angular DevTools
1. Install: [Angular DevTools Extension](https://chrome.google.com/webstore/detail/angular-devtools/)
2. Open DevTools → "Angular" tab
3. Inspect components, check change detection

### Browser Console
```javascript
// Get component instance
ng.probe(document.querySelector('app-employee-list')).componentInstance

// Check state
console.log(stateService.employeeList);
```

## Deployment

### Build for Production
```bash
ng build --prod
# Output: dist/salary-management-ui (ready for static hosting)
```

### Deploy to S3 (AWS)
```bash
aws s3 sync dist/salary-management-ui s3://my-bucket
```

### Deploy to Vercel (Recommended for Angular)
```bash
npm install -g vercel
vercel
```

## CI/CD Integration

### GitHub Actions Example
```yaml
name: Build & Test

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-node@v2
        with:
          node-version: '16'
      - run: npm ci
      - run: npm run lint
      - run: npm run test:ci
      - run: npm run build:prod
      - name: Upload to S3
        run: aws s3 sync dist/salary-management-ui s3://my-bucket
```

## Useful Commands

```bash
npm install                    # Install dependencies
ng serve                       # Start dev server
ng build --prod               # Production build
ng test                        # Run tests
ng test --code-coverage       # Generate coverage
ng lint                        # Check code style
ng generate component employee-list  # Generate component
ng generate service employee   # Generate service
ng generate module employees   # Generate module
```

## Next Steps

1. **Phase 0**: Project structure & setup (THIS DOCUMENT) ✅
2. **Phase 1**: Backend domain models & seeding
3. **Phase 2**: Backend services & tests
4. **Phase 3**: Backend REST API
5. **Phase 4**: Frontend modules & components
6. **Phase 5**: Frontend state management & integration
7. **Phase 6**: Deployment & polish

---

**Status**: Ready for implementation.

**Note**: This document assumes you're familiar with Angular basics. For deep dives, refer to [Angular Official Docs](https://angular.io/docs).
