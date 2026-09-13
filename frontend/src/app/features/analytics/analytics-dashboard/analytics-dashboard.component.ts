import { Component, OnInit } from '@angular/core';
import { AnalyticsService } from '../../../core/services/analytics.service';
import {
  CountryHeadcount,
  CountryPayrollCost,
  DepartmentSalaryStat,
  SalaryDistribution
} from '../../../core/models/analytics.model';

@Component({
  selector: 'app-analytics-dashboard',
  templateUrl: './analytics-dashboard.component.html',
  styleUrls: ['./analytics-dashboard.component.scss']
})
export class AnalyticsDashboardComponent implements OnInit {

  salaryByDepartment: DepartmentSalaryStat[] = [];
  headcountByCountry: CountryHeadcount[] = [];
  payrollCostByCountry: CountryPayrollCost[] = [];
  salaryDistribution: SalaryDistribution[] = [];

  departmentColumns = ['department', 'averageSalary', 'employeeCount'];
  headcountColumns = ['countryName', 'employeeCount'];
  payrollColumns = ['countryName', 'currencyCode', 'totalCost'];
  distributionColumns = ['countryName', 'currencyCode', 'p10', 'p50', 'p90', 'sampleSize'];

  constructor(private analyticsService: AnalyticsService) { }

  ngOnInit(): void {
    this.analyticsService.salaryByDepartment().subscribe(data => this.salaryByDepartment = data);
    this.analyticsService.headcountByCountry().subscribe(data => this.headcountByCountry = data);
    this.analyticsService.payrollCostByCountry().subscribe(data => this.payrollCostByCountry = data);
    this.analyticsService.salaryDistributionByCountry().subscribe(data => this.salaryDistribution = data);
  }
}
