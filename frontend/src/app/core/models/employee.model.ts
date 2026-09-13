export interface EmployeeResponse {
  id: number;
  employeeId: string;
  fullName: string;
  email: string;
  phoneNumber: string;
  department: string;
  designation: string;
  countryName: string;
  hiredDate: string;
  employmentStatus: string;
}

export interface CreateEmployeeRequest {
  employeeId: string;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  department: string;
  designation: string;
  countryName: string;
  hiredDate: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}
