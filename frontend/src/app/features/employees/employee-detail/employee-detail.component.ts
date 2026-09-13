import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EmployeeService } from '../../../core/services/employee.service';
import { SalaryService } from '../../../core/services/salary.service';
import { EmployeeResponse } from '../../../core/models/employee.model';
import { SalaryResponse } from '../../../core/models/salary.model';

@Component({
  selector: 'app-employee-detail',
  templateUrl: './employee-detail.component.html',
  styleUrls: ['./employee-detail.component.scss']
})
export class EmployeeDetailComponent implements OnInit {

  employeeId!: number;
  employee?: EmployeeResponse;
  currentSalary?: SalaryResponse;
  salaryHistory: SalaryResponse[] = [];
  displayedColumns = ['effectiveDate', 'baseSalary', 'annualBonus', 'benefitsValue', 'totalCompensation', 'currencyCode'];
  salaryForm: FormGroup;
  showAddSalary = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private employeeService: EmployeeService,
    private salaryService: SalaryService,
    private fb: FormBuilder) {
    this.salaryForm = this.fb.group({
      baseSalary: [null, [Validators.required, Validators.min(0.01)]],
      currencyCode: ['', Validators.required],
      annualBonus: [0],
      benefitsValue: [0],
      effectiveDate: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.employeeId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadEmployee();
    this.loadSalaries();
  }

  loadEmployee(): void {
    this.employeeService.getById(this.employeeId).subscribe(e => this.employee = e);
  }

  loadSalaries(): void {
    this.salaryService.current(this.employeeId).subscribe({
      next: s => this.currentSalary = s,
      error: () => this.currentSalary = undefined
    });
    this.salaryService.history(this.employeeId).subscribe(page => this.salaryHistory = page.content);
  }

  deactivate(): void {
    this.employeeService.deactivate(this.employeeId).subscribe(() => this.loadEmployee());
  }

  edit(): void {
    this.router.navigate(['/employees', this.employeeId, 'edit']);
  }

  toggleAddSalary(): void {
    this.showAddSalary = !this.showAddSalary;
  }

  submitSalary(): void {
    if (this.salaryForm.invalid) { return; }
    this.salaryService.add(this.employeeId, this.salaryForm.value).subscribe(() => {
      this.salaryForm.reset();
      this.showAddSalary = false;
      this.loadSalaries();
    });
  }
}
