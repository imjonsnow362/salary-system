export interface SalaryResponse {
  id: number;
  baseSalary: number;
  currencyCode: string;
  annualBonus: number;
  benefitsValue: number;
  effectiveDate: string;
  totalCompensation: number;
}

export interface AddSalaryRequest {
  baseSalary: number;
  currencyCode: string;
  annualBonus?: number;
  benefitsValue?: number;
  effectiveDate: string;
}
