import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PageEvent } from '@angular/material/paginator';
import { EmployeeService } from '../../../core/services/employee.service';
import { EmployeeResponse } from '../../../core/models/employee.model';

@Component({
  selector: 'app-employee-list',
  templateUrl: './employee-list.component.html',
  styleUrls: ['./employee-list.component.scss']
})
export class EmployeeListComponent implements OnInit {

  displayedColumns = ['employeeId', 'name', 'department', 'designation', 'countryName', 'status', 'actions'];
  employees: EmployeeResponse[] = [];
  totalElements = 0;
  pageSize = 20;
  pageIndex = 0;
  searchTerm = '';
  loading = false;

  constructor(private employeeService: EmployeeService, private router: Router) { }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.employeeService.list({ q: this.searchTerm, page: this.pageIndex, size: this.pageSize }).subscribe({
      next: page => {
        this.employees = page.content;
        this.totalElements = page.totalElements;
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  onSearch(): void {
    this.pageIndex = 0;
    this.load();
  }

  onPage(event: PageEvent): void {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.load();
  }

  view(employee: EmployeeResponse): void {
    this.router.navigate(['/employees', employee.id]);
  }

  addNew(): void {
    this.router.navigate(['/employees/new']);
  }
}
