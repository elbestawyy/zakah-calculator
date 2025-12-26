// src/app/models/zakah.model.ts
export type Persona = 'individual' | 'company';

export interface ZakahFormData {
  // Account Details
  balanceSheetDate: string;
  persona: Persona;

  // Assets
  cash: number;
  stocks: number;
  inventory: number;
  receivables: number;

  // Liabilities
  accountPayable: number;
  expenses: number;
  shortTermLoans: number;
  longTermDebt: number;

  // Gold (for individuals)
  goldWeightInGrams: number;
  goldPricePerGram: number;
  goldValue: number;

  // Calculated values
  totalAssets: number;
  totalLiabilities: number;
  netAssets: number;
  zakahAmount: number;
}
