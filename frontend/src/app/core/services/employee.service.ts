import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CreateEmployeeRequest, EmployeeResponse, Page } from '../models/employee.model';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {

  private readonly baseUrl = `${environment.apiBaseUrl}/employees`;

  constructor(private http: HttpClient) { }

  list(params: { q?: string; department?: string; countryId?: number; page?: number; size?: number }): Observable<Page<EmployeeResponse>> {
    let httpParams = new HttpParams();
    if (params.q) { httpParams = httpParams.set('q', params.q); }
    if (params.department) { httpParams = httpParams.set('department', params.department); }
    if (params.countryId != null) { httpParams = httpParams.set('countryId', params.countryId); }
    httpParams = httpParams.set('page', params.page ?? 0);
    httpParams = httpParams.set('size', params.size ?? 50);
    return this.http.get<Page<EmployeeResponse>>(this.baseUrl, { params: httpParams });
  }

  getById(id: number): Observable<EmployeeResponse> {
    return this.http.get<EmployeeResponse>(`${this.baseUrl}/${id}`);
  }

  create(request: CreateEmployeeRequest): Observable<EmployeeResponse> {
    return this.http.post<EmployeeResponse>(this.baseUrl, request);
  }

  deactivate(id: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/${id}/deactivate`, {});
  }
}
