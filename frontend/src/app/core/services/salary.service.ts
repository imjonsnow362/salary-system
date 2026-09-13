import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AddSalaryRequest, SalaryResponse } from '../models/salary.model';
import { Page } from '../models/employee.model';

@Injectable({
  providedIn: 'root'
})
export class SalaryService {

  constructor(private http: HttpClient) { }

  private baseUrl(employeeId: number): string {
    return `${environment.apiBaseUrl}/employees/${employeeId}/salaries`;
  }

  history(employeeId: number, page = 0, size = 50): Observable<Page<SalaryResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<SalaryResponse>>(this.baseUrl(employeeId), { params });
  }

  current(employeeId: number): Observable<SalaryResponse> {
    return this.http.get<SalaryResponse>(`${this.baseUrl(employeeId)}/current`);
  }

  add(employeeId: number, request: AddSalaryRequest): Observable<SalaryResponse> {
    return this.http.post<SalaryResponse>(this.baseUrl(employeeId), request);
  }
}
