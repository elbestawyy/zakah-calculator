import {
  ChangeDetectionStrategy,
  Component,
  inject, OnInit,
  signal
} from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { ZakahCompanyRecordService } from '../../../../services/zakah-company-service/zakah-company-service';
import { ZakahCompanyExcelService } from '../../../../services/zakah-company-service/zakah-company-excel-service';
import { TooltipComponent } from '../../../../shared/tooltip/tooltip';
import { ZakahCompanyRecordRequest } from '../../../../models/request/ZakahCompanyRequest';
import { Router } from '@angular/router';
import {SoftwareCompanyModel} from '../../../../models/software-company-model';

@Component({
  selector: 'app-wizard',
  standalone: true,
  imports: [CommonModule, TooltipComponent, CurrencyPipe],
  templateUrl: './wizard.html',
  styleUrls: ['./wizard.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ZakahCompanyRecordComponent implements OnInit {
  private excelService = inject(ZakahCompanyExcelService);
  zakahService = inject(ZakahCompanyRecordService);
  private router = inject(Router);

  formData = this.zakahService.formData;
  currentStep = this.zakahService.currentWizardStep;
  steps = this.zakahService.wizardSteps;
  isCalculating = this.zakahService.isCalculating;

  fileName = signal<string | null>(null);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  downloadInProgress = signal(false);

  // ================= Validation =================
  fieldErrors = signal<Partial<Record<keyof ZakahCompanyRecordRequest, string>>>({});

  ngOnInit() {
    // Initialize balanceSheetDate if not set
    const currentData = this.formData();
    if (!currentData.balanceSheetDate) {
      const today = new Date().toISOString().split('T')[0];
      this.zakahService.updateFormData({ balanceSheetDate: today });
    }
  }

  // ================= Date Helpers =================

  private normalizeToISO(dateStr: string): string {
    if (!dateStr) return '';
    const trimmed = dateStr.trim();

    // 1. معالجة الصيغة DD-MM-YYYY القادمة من الإكسيل
    const dmyMatch = trimmed.match(/^(\d{1,2})-(\d{1,2})-(\d{4})$/);
    if (dmyMatch) {
      const [, d, m, y] = dmyMatch;
      return `${y}-${m.padStart(2, '0')}-${d.padStart(2, '0')}`;
    }

    // 2. معالجة صيغة YYYY-MM-DD القياسية
    if (/^\d{4}-\d{2}-\d{2}$/.test(trimmed)) {
      return trimmed;
    }

    // 3. محاولة أخيرة باستخدام كائن Date
    const date = new Date(trimmed);
    return !isNaN(date.getTime()) ? date.toISOString().split('T')[0] : '';
  }

  getDisplayDate(): string {
    return this.formData().balanceSheetDate || '';
  }

  // ================= Inputs =================

  private validateField(
    key: keyof ZakahCompanyRecordRequest,
    value: number | string
  ): string | null {

    if (key === 'balanceSheetDate') {
      return value ? null : 'يرجى اختيار تاريخ';
    }

    if (value === null || value === undefined || value === '') {
      return 'هذا الحقل مطلوب';
    }

    if (typeof value === 'string' && isNaN(Number(value))) {
      return 'من فضلك أدخل رقمًا صحيحًا';
    }

    const numericValue = Number(value);

    if (numericValue < 0) {
      return 'القيمة لا يمكن أن تكون سالبة';
    }

    return null;
  }

  onInputChange(event: Event): void {
    const target = event.target as HTMLInputElement;
    const key = target.name as keyof ZakahCompanyRecordRequest;
    const value = target.valueAsNumber || 0;

    this.zakahService.updateFormData({ [key]: value });

    const error = this.validateField(key, value);
    this.fieldErrors.update(errors => ({
      ...errors,
      [key]: error || undefined
    }));
  }

  onDateChange(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    this.zakahService.updateFormData({ balanceSheetDate: value });

    this.fieldErrors.update(errors => ({
      ...errors,
      balanceSheetDate: value ? undefined : 'يرجى اختيار تاريخ'
    }));
  }

  private validateAll(): boolean {
    const data = this.formData();
    let valid = true;
    const errors: Partial<Record<keyof ZakahCompanyRecordRequest, string>> = {};

    (Object.keys(data) as (keyof ZakahCompanyRecordRequest)[]).forEach(key => {
      const value = data[key] as any;
      const error = this.validateField(key, value);
      if (error) {
        errors[key] = error;
        valid = false;
      }
    });

    this.fieldErrors.set(errors);
    return valid;
  }

  // ================= Excel =================

  downloadExcelTemplate(): void {
    this.downloadInProgress.set(true);
    this.zakahService.getTemplate().subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'balance_sheet_template.xlsx';
        link.click();
        URL.revokeObjectURL(url);
      },
      error: () => this.errorMessage.set('حدث خطأ في تحميل النموذج.'),
      complete: () => this.downloadInProgress.set(false)
    });
  }

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;

    const file = input.files[0];
    this.fileName.set(file.name);
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.zakahService.readCompanyExcelObservable(file).subscribe({
      next: (excelData) => {
        this.zakahService.updateFormData({
          cashEquivalents: excelData.cashEquivalents || 0,
          investment: excelData.investment || 0,
          inventory: excelData.inventory || 0,
          accountsReceivable: excelData.accountsReceivable || 0,
          accountsPayable: excelData.accountsPayable || 0,
          accruedExpenses: excelData.accruedExpenses || 0,
          shortTermLiability: excelData.shortTermLiability || 0,
          yearlyLongTermLiabilities: excelData.yearlyLongTermLiabilities || 0,
          goldPrice: excelData.goldPrice || 0,
          balanceSheetDate: excelData.balanceSheetDate
            ? this.normalizeToISO(excelData.balanceSheetDate.toString())
            : new Date().toISOString().split('T')[0],
          generatingFixedAssets: excelData.generatingFixedAssets || 0,
          contraAssets: excelData.contraAssets || 0,
          provisionsUnderLiabilities: excelData.provisionsUnderLiabilities || 0
        });

        const detailsStep = this.steps().indexOf('التفاصيل');
        if (detailsStep !== -1) this.zakahService.goToStep(detailsStep);
      },
      error: () => this.errorMessage.set('حدث خطأ في قراءة ملف Excel.'),
      complete: () => {
        this.isLoading.set(false);
        input.value = '';
      }
    });
  }

  // ================= Wizard =================

  next(): void {
    this.zakahService.nextStep();
  }

  back(): void {
    this.zakahService.prevStep();
  }

  calculate(): void {
    if (!this.validateAll()) return;

    this.errorMessage.set(null);
    this.isCalculating.set(true);

    this.zakahService.calculate().subscribe({
      next: (result) => {
        this.zakahService.latestResult.set(result);
        this.isCalculating.set(false);
        this.router.navigate(['/company/after-calc']);
      },
      error: (err) => {
        console.error('Calculation error:', err);
        this.errorMessage.set('حدث خطأ أثناء حساب الزكاة. يرجى التأكد من البيانات والمحاولة لاحقاً.');
        this.isCalculating.set(false);
      },
      complete: () => {
        this.isCalculating.set(false);
      }
    });
  }

  // ================= Display =================

  formatDateForDisplay(dateStr: string): string {
    if (!dateStr) return '';

    const match = dateStr.match(/^(\d{4})-(\d{2})-(\d{2})$/);
    if (match) {
      const [, y, m, d] = match;
      return `${d}/${m}/${y}`;
    }

    return dateStr;
  }

    onNumberInput(event: Event) {
  const input = event.target as HTMLInputElement;

  let value = input.value;

  // إزالة أي شيء غير رقم أو نقطة
  value = value.replace(/[^0-9.]/g, '');

  // منع أكثر من نقطة
  const parts = value.split('.');
  if (parts.length > 2) {
    value = parts[0] + '.' + parts.slice(1).join('');
  }

  input.value = value;

  this.zakahService.updateFormData({
    [input.name]: value === '' ? 0 : Number(value)
  });
}

blockInvalidNumberKeys(event: KeyboardEvent) {
  const invalidKeys = ['e', 'E', '+', '-'];
  if (invalidKeys.includes(event.key)) {
    event.preventDefault();
  }
}

}
