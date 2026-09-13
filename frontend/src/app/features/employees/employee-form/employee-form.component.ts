import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { EmployeeService } from '../../../core/services/employee.service';

@Component({
  selector: 'app-employee-form',
  templateUrl: './employee-form.component.html',
  styleUrls: ['./employee-form.component.scss']
})
export class EmployeeFormComponent {

  form: FormGroup;
  submitting = false;
  errorMessage = '';

  constructor(private fb: FormBuilder, private employeeService: EmployeeService, private router: Router) {
    this.form = this.fb.group({
      employeeId: ['', Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: [''],
      department: ['', Validators.required],
      designation: ['', Validators.required],
      countryName: ['', Validators.required],
      hiredDate: ['', Validators.required]
    });
  }

  submit(): void {
    if (this.form.invalid) { return; }
    this.submitting = true;
    this.errorMessage = '';
    this.employeeService.create(this.form.value).subscribe({
      next: created => this.router.navigate(['/employees', created.id]),
      error: err => {
        this.submitting = false;
        this.errorMessage = err?.error?.message || 'Failed to create employee.';
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/employees']);
  }
}
