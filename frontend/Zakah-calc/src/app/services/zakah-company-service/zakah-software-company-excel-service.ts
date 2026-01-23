// src/app/services/zakah-company-service/zakah-company-excel-service.ts

import { Injectable } from '@angular/core';
import * as XLSX from 'xlsx';
import {SoftwareCompanyModel} from '../../models/software-company-model';

@Injectable({
  providedIn: 'root'
})
export class ZakahSoftwareCompanyExcelService {

  /* ================= READ EXCEL FILE ================= */
  readCompanyExcel(file: File): Promise<SoftwareCompanyModel> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();

      reader.onload = () => {
        try {
          const buffer = reader.result as ArrayBuffer;
          const workbook = XLSX.read(buffer, { type: 'array' });
          const sheetName = workbook.SheetNames[0];
          const worksheet = workbook.Sheets[sheetName];

          const rows = XLSX.utils.sheet_to_json<any>(worksheet, {
            defval: 0,
            raw: false
          });

          if (!rows.length) {
            throw new Error('Excel file is empty');
          }

          const row = rows[0];

          const result: SoftwareCompanyModel = {

            /* ========= ZAKAT BASE – ADDITIONS ========= */
            generatingFixedAssets: this.toNumber(row['Fixed Assets for Income Generation']),
            investmentsEquity: this.toNumber(row['Equity Investments']),
            affiliatesSubsidiaries: this.toNumber(row['Investments in Subsidiaries/Affiliates']),
            investmentsSukukIslamic: this.toNumber(row['Islamic Sukuk Investments']),
            receivablesTrade: this.toNumber(row['Trade Receivables']),
            receivableNotes: this.toNumber(row['Notes Receivable']),
            incomeAccrued: this.toNumber(row['Accrued Income']),
            expensesContractPrepaid: this.toNumber(row['Prepaid Contract Expenses']),
            handOnCash: this.toNumber(row['Cash on Hand']),
            accountsCurrentDepositsBank: this.toNumber(row['Bank Deposits and Current Accounts']),
            depositsStatutory: this.toNumber(row['Statutory Deposits']),
            securitiesTrading: this.toNumber(row['Trading Securities']),
            partiesThirdWithDeposits: this.toNumber(row['Deposits with Third Parties']),

            /* ========= ZAKAT BASE – DEDUCTIONS ========= */
            assetsFixedProvisionDepreciation: this.toNumber(row['Depreciation Provision for Fixed Assets']),
            provisionOverhaulMaintenance: this.toNumber(row['Maintenance/Overhaul Provision']),
            assetsProvisionInsurance: this.toNumber(row['Insurance Provision for Assets']),
            investmentsProvisionImpairment: this.toNumber(row['Impairment Provision for Investments']),
            debtsDoubtfulAllowance: this.toNumber(row['Allowance for Doubtful Debts']),
            discountsCashProvision: this.toNumber(row['Provision for Cash Discounts']),
            payableAccounts: this.toNumber(row['Accounts Payable']),
            payableNotes: this.toNumber(row['Notes Payable']),
            loansTermShort: this.toNumber(row['Short-Term Loans']),
            debtTermLongPortionCurrent: this.toNumber(row['Current Portion of Long-Term Debt']),
            expensesAccrued: this.toNumber(row['Accrued Expenses']),

            /* ========= META ========= */
            balanceSheetDate: this.formatDate(new Date()),
            goldPrice: 75.21,
          };

          console.log('Excel parsed as SoftwareCompanyModel:', result);
          resolve(result);

        } catch (err) {
          console.error('Error reading Excel file:', err);
          reject(err);
        }
      };

      reader.onerror = err => reject(err);
      reader.readAsArrayBuffer(file);
    });
  }

  private toNumber(value: any): number {
    if (value === null || value === undefined || value === '') {
      return 0;
    }
    const num = Number(value);
    return isNaN(num) ? 0 : num;
  }

  private formatDate(date: Date): string {
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}-${month}-${year}`;
  }
}
