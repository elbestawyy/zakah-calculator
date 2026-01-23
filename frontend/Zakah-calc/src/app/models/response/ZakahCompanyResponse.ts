// src/app/models/response/ZakahCompanyResponse.ts
import { ZakahStatus } from '../enums/ZakahStatus';

export interface ZakahCompanyRecordResponse {
  id: number;
  status: ZakahStatus;

  // Assets
  cashEquivalents: number;
  investment: number;
  inventory: number;
  accountsReceivable: number;
  generatingFixedAssets: number;

  // Liabilities
  accountsPayable: number;
  accruedExpenses: number;
  shortTermLiability: number;
  yearlyLongTermLiabilities: number;
  contraAssets: number;
  provisionsUnderLiabilities: number;

  // Zakah Info
  goldPrice: number;

  // Calculations
  totalAssets: number;
  totalLiabilities: number;
  zakahPool: number;
  zakahAmount: number;
  nisabAmount: number;

  balanceSheetDate: string;
}

export interface ZakahCompanyRecordSummaryResponse {
  id: number;
  balanceSheetDate: string;
  status: ZakahStatus;
  zakahAmount: number;
}
