import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  CountryHeadcount,
  CountryPayrollCost,
  DepartmentSalaryStat,
  SalaryDistribution
} from '../models/analytics.model';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {

  private readonly baseUrl = `${environment.apiBaseUrl}/analytics`;

  constructor(private http: HttpClient) { }

  salaryByDepartment(): Observable<DepartmentSalaryStat[]> {
    return this.http.get<DepartmentSalaryStat[]>(`${this.baseUrl}/salary-by-department`);
  }

  headcountByCountry(): Observable<CountryHeadcount[]> {
    return this.http.get<CountryHeadcount[]>(`${this.baseUrl}/headcount-by-country`);
  }

  payrollCostByCountry(): Observable<CountryPayrollCost[]> {
    return this.http.get<CountryPayrollCost[]>(`${this.baseUrl}/payroll-cost-by-country`);
  }

  salaryDistributionByCountry(): Observable<SalaryDistribution[]> {
    return this.http.get<SalaryDistribution[]>(`${this.baseUrl}/salary-distribution-by-country`);
  }
}
