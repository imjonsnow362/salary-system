export interface DepartmentSalaryStat {
  department: string;
  averageSalary: number;
  employeeCount: number;
}

export interface CountryHeadcount {
  countryName: string;
  employeeCount: number;
}

export interface CountryPayrollCost {
  countryName: string;
  currencyCode: string;
  totalCost: number;
}

export interface SalaryDistribution {
  countryName: string;
  currencyCode: string;
  p10: number;
  p50: number;
  p90: number;
  sampleSize: number;
}
