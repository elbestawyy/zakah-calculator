// src/app/services/zakah.service.ts
import { Injectable, signal, computed } from '@angular/core';
import { Persona, ZakahFormData } from '../models/zakah.model';
import * as XLSX from 'xlsx';

@Injectable({
  providedIn: 'root'
})
export class ZakahService {
  // Form data
  private formDataSignal = signal<ZakahFormData>({
    balanceSheetDate: new Date().toISOString().split('T')[0],
    persona: 'individual',
    cash: 0,
    stocks: 0,
    inventory: 0,
    receivables: 0,
    accountPayable: 0,
    expenses: 0,
    shortTermLoans: 0,
    longTermDebt: 0,
    goldWeightInGrams: 0,
    goldPricePerGram: 75.21,
    goldValue: 0,
    totalAssets: 0,
    totalLiabilities: 0,
    netAssets: 0,
    zakahAmount: 0
  });

  // Steps management
  private currentStepSignal = signal(0);
  private isCalculatingSignal = signal(false);

  // Steps for different personas
  private individualSteps = ['البداية', 'التفاصيل', 'الأصول', 'الخصوم', 'الذهب', 'مراجعة'];
  private companySteps = ['البداية', 'التفاصيل', 'أصول الشركة', 'الخصوم', 'مراجعة'];

  // Computed properties
  formData = this.formDataSignal.asReadonly();
  currentWizardStep = this.currentStepSignal.asReadonly();
  isCalculating = this.isCalculatingSignal.asReadonly();

  wizardSteps = computed(() => {
    const persona = this.formDataSignal().persona;
    return persona === 'individual' ? this.individualSteps : this.companySteps;
  });

  updateFormData(patch: Partial<ZakahFormData>) {
    this.formDataSignal.update(current => ({
      ...current,
      ...patch
    }));
  }

  nextStep() {
    const current = this.currentStepSignal();
    const steps = this.wizardSteps();
    if (current < steps.length - 1) {
      this.currentStepSignal.set(current + 1);
    }
  }

  prevStep() {
    const current = this.currentStepSignal();
    if (current > 0) {
      this.currentStepSignal.set(current - 1);
    }
  }

  goToStep(stepIndex: number) {
    const steps = this.wizardSteps();
    if (stepIndex >= 0 && stepIndex < steps.length) {
      this.currentStepSignal.set(stepIndex);
    }
  }

  async calculateZakah() {
    this.isCalculatingSignal.set(true);

    try {
      // Simulate API call delay
      await new Promise(resolve => setTimeout(resolve, 2000));

      const data = this.formDataSignal();

      // Calculate gold value
      const goldValue = (data.goldWeightInGrams || 0) * (data.goldPricePerGram || 0);

      // Calculate totals
      const totalAssets = (data.cash || 0) + (data.stocks || 0) + (data.inventory || 0) +
                         (data.receivables || 0) + (data.persona === 'individual' ? goldValue : 0);

      const totalLiabilities = (data.accountPayable || 0) + (data.expenses || 0) +
                              (data.shortTermLoans || 0) + (data.longTermDebt || 0);

      const netAssets = totalAssets - totalLiabilities;
      const zakahAmount = Math.max(0, netAssets) * 0.025; // 2.5%

      this.updateFormData({
        goldValue,
        totalAssets,
        totalLiabilities,
        netAssets,
        zakahAmount
      });

    } finally {
      this.isCalculatingSignal.set(false);
    }
  }

  // دالة لقراءة ملف Excel
  async readExcelFile(file: File): Promise<Partial<ZakahFormData>> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();

      reader.onload = (e: any) => {
        try {
          const data = e.target.result;
          const workbook = XLSX.read(data, { type: 'binary' });

          // افتراض أن البيانات في الورقة الأولى
          const sheetName = workbook.SheetNames[0];
          const worksheet = workbook.Sheets[sheetName];

          // تحويل الورقة إلى مصفوفة مع تحديد نوع البيانات
          const jsonData: any[][] = XLSX.utils.sheet_to_json(worksheet, { header: 1, defval: null });

          // تحليل البيانات بناءً على هيكل Excel الذي أرسلته
          const parsedData = this.parseExcelData(jsonData);

          resolve(parsedData);
        } catch (error) {
          reject(error);
        }
      };

      reader.onerror = (error) => reject(error);
      reader.readAsBinaryString(file);
    });
  }

  private parseExcelData(jsonData: any[][]): Partial<ZakahFormData> {
    // القيم الافتراضية 0
    const parsedData: Partial<ZakahFormData> = {
      cash: 0,
      stocks: 0,
      inventory: 0,
      receivables: 0,
      accountPayable: 0,
      expenses: 0,
      shortTermLoans: 0,
      goldWeightInGrams: 0,
      longTermDebt: 0
    };

    try {
      // البحث عن العناوين والبيانات
      if (jsonData.length >= 2) {
        const headers = jsonData[0]; // الصف الأول: العناوين
        const values = jsonData[1];  // الصف الثاني: القيم

        // تعيين تخطيط Excel بناءً على المثال الذي أرسلته
        const columnMapping: { [key: string]: keyof ZakahFormData } = {
          'النقد': 'cash',
          'الأسهم/الاستثمارات': 'stocks',
          'المخزون': 'inventory',
          'المستحقات': 'receivables',
          'الذمم الدائنة': 'accountPayable',
          'المصاريف': 'expenses',
          'قروض قصيرة الأجل': 'shortTermLoans',
          'قيمة الذهب': 'goldWeightInGrams'
        };

        // معالجة كل عمود
        headers.forEach((header: any, index: number) => {
          if (header && columnMapping[header as string]) {
            const field = columnMapping[header as string];
            const value = values[index];

            // تحويل القيمة إلى عدد، إذا لم تكن صحيحة ضع 0
            const parsedValue = this.parseNumber(value);

            // استخدام type assertion للتعامل مع dynamic keys
            (parsedData as any)[field] = parsedValue;
          }
        });
      }
    } catch (error) {
      console.error('Error parsing Excel data:', error);
    }

    return parsedData;
  }

  private parseNumber(value: any): number {
    if (value === null || value === undefined || value === '') {
      return 0;
    }

    // تحويل النص إلى عدد
    const num = Number(value);

    // إذا كان القيمة غير صالحة، إرجاع 0
    return isNaN(num) ? 0 : num;
  }
}
