// src/app/models/request/ZakahCompanyRequest.ts
export interface ZakahCompanyRecordRequest {
  balanceSheetDate: string;

  // Zakah Info
  goldPrice: number;

  // Assets
  cashEquivalents: number;
  accountsReceivable: number;
  inventory: number;
  investment: number;
  generatingFixedAssets: number; // new item

  // Liabilities
  accountsPayable: number;
  accruedExpenses: number;
  shortTermLiability: number;
  yearlyLongTermLiabilities: number;
  contraAssets: number; // new item
  provisionsUnderLiabilities: number; // new item
}
