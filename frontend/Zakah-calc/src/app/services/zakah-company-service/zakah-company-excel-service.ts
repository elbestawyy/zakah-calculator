// src/app/services/zakah-company-service/zakah-company-excel-service.ts
import { Injectable } from '@angular/core';
import * as XLSX from 'xlsx';
import { ZakahCompanyRecordRequest } from '../../models/request/ZakahCompanyRequest';

@Injectable({
  providedIn: 'root'
})
export class ZakahCompanyExcelService {

  /* ================= READ EXCEL FILE ================= */
  readCompanyExcel(file: File): Promise<ZakahCompanyRecordRequest> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();

      reader.onload = () => {
        try {
          const buffer = reader.result as ArrayBuffer;
          const workbook = XLSX.read(buffer, { type: 'array' });
          const sheetName = workbook.SheetNames[0];
          const worksheet = workbook.Sheets[sheetName];

          // تحويل الورقة إلى JSON
          const rows = XLSX.utils.sheet_to_json<any>(worksheet, {
            defval: 0,
            raw: false
          });

          if (!rows.length) {
            throw new Error('Excel file is empty');
          }

          const firstRow = rows[0];
          const result: ZakahCompanyRecordRequest = {
            balanceSheetDate: this.formatDate(new Date()), // تاريخ اليوم كافتراضي
            cashEquivalents: this.toNumber(firstRow['Cash Equivalents']),
            accountsReceivable: this.toNumber(firstRow['Accounts Receivable']) ,
            inventory: this.toNumber(firstRow['Inventory']),
            investment: this.toNumber(firstRow['Investment']),
            accountsPayable: this.toNumber(firstRow['Accounts Payable']),
            accruedExpenses: this.toNumber(firstRow['Accrued Expenses']),
            shortTermLiability: this.toNumber(firstRow['Short Term Liability']),
            yearlyLongTermLiabilities: this.toNumber(firstRow['Yearly Long Term Liabilities']),
            goldPrice: 75.21, // قيمة افتراضية
            generatingFixedAssets: this.toNumber(firstRow['Generating Fixed Assets']),
            contraAssets: this.toNumber(firstRow['Contra Assets']),
            provisionsUnderLiabilities: this.toNumber(firstRow['Provisions Under Liabilities'])
          };

          console.log('Excel data parsed (Request):', result);
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
