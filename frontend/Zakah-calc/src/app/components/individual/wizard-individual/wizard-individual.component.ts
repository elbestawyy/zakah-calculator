import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { ZakahFormData } from '../../../models/zakah.model';
import { ZakahService } from '../../../services/zakah.service';
import { TooltipComponent } from "../../../shared/tooltip/tooltip";
import { CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-wizard-individual',
  templateUrl: './wizard-individual.component.html',
  styleUrls: ['./wizard-individual.component.css'],
  imports: [TooltipComponent , CurrencyPipe]
})
export class WizardIndividualComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }
  zakahService = inject(ZakahService);

  formData = this.zakahService.formData;
  currentStep = this.zakahService.currentWizardStep;
  steps = this.zakahService.wizardSteps;
  isCalculating = this.zakahService.isCalculating;

  persona = computed(() => this.formData().persona);
  fileName = signal<string | null>(null);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  downloadInProgress = signal(false);

  onDateChange(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.zakahService.updateFormData({ balanceSheetDate: value });
  }

  onInputChange(event: Event) {
    const target = event.target as HTMLInputElement;
    const key = target.name as keyof ZakahFormData;
    const value = target.valueAsNumber || 0;

    const patch: Partial<ZakahFormData> = { [key]: value };
    this.zakahService.updateFormData(patch);
  }

  // دالة لتحميل ملف Excel template من مجلد public
  async downloadExcelTemplate() {
    this.downloadInProgress.set(true);

    try {
      // مسار الملف في مجلد public
      const templateUrl = '/templates/balance_sheet_template.xlsx';

      // تحميل الملف
      const response = await fetch(templateUrl);

      if (!response.ok) {
        throw new Error('لم يتم العثور على ملف النموذج');
      }

      const blob = await response.blob();

      // إنشاء رابط للتحميل مع الاسم المطلوب
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = 'balance_sheet_template.xlsx';

      // إضافة الرابط للصفحة والنقر عليه
      document.body.appendChild(link);
      link.click();

      // تنظيف
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);

    } catch (error) {
      console.error('Error downloading template:', error);
      this.errorMessage.set('حدث خطأ في تحميل النموذج. تأكد من وجود الملف.');

      // استدعاء الدالة البديلة التي ستنشئ ملف Excel حقيقي
      await this.downloadFallbackExcelTemplate();
    } finally {
      this.downloadInProgress.set(false);
    }
  }

  // دالة بديلة لإنشاء ملف Excel فعلي باستخدام مكتبة xlsx
  private async downloadFallbackExcelTemplate() {
    try {
      // استيراد مكتبة xlsx ديناميكيًا
      const XLSX = await import('xlsx');

      // إنشاء بيانات الورقة
      const wsData = [
        ['نموذج البيانات المالية لحساب الزكاة'],
        ['يرجى تعبئة البيانات أدناه وحفظ الملف ثم رفعه للحساب'],
        [],
        ['الأصول', 'القيمة', '', 'الخصوم', 'القيمة'],
        ['النقد والأرصدة البنكية', 0, '', 'الذمم الدائنة', 0],
        ['الأسهم والاستثمارات', 0, '', 'المصاريف المستحقة', 0],
        ['المخزون', 0, '', 'قروض قصيرة الأجل', 0],
        ['الذمم المدينة (المستحقات)', 0, '', 'الجزء السنوي من الديون طويلة الأجل', 0],
        ['الذهب (بالجرام)', 0, '', '', ''],
        [],
        ['ملاحظات:', ''],
        ['1. أدخل القيم بالأرقام فقط بدون رموز عملة'],
        ['2. يمكن ترك الخلايا الفارغة أو وضع 0 إذا لم توجد قيمة'],
        ['3. تاريخ الميزانية العمومية: ' + new Date().toLocaleDateString('ar-EG')]
      ];

      // إنشاء ورقة عمل
      const ws = XLSX.utils.aoa_to_sheet(wsData);

      // تنسيق الخلايا (جعل العنوان في وسط الخلايا)
      const merge = [{ s: { r: 0, c: 0 }, e: { r: 0, c: 4 } }];
      ws['!merges'] = merge;

      // إنشاء مصنف وإضافة الورقة
      const wb = XLSX.utils.book_new();
      XLSX.utils.book_append_sheet(wb, ws, 'بيانات الزكاة');

      // إنشاء الملف وتحميله
      const wbout = XLSX.write(wb, { bookType: 'xlsx', type: 'array' });
      const blob = new Blob([wbout], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = 'balance_sheet_template.xlsx';

      document.body.appendChild(link);
      link.click();

      // تنظيف
      setTimeout(() => {
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
      }, 100);

    } catch (error) {
      console.error('Error creating Excel template:', error);

      // نسخة احتياطية بسيطة كمستند نصي مع امتداد xlsx
      this.downloadSimpleTemplate();
    }
  }

  // دالة احتياطية إذا فشلت مكتبة xlsx
  private downloadSimpleTemplate() {
    try {
      // إنشاء محتوى بسيط يمكن تحويله لاحقًا لـ Excel
      const templateContent = `نموذج,البيانات,المالية,لحساب,الزكاة
يرجى,تعبئة,البيانات,أدناه,وحفظ,الملف,ثم,رفعه,للحساب

الأصول,القيمة,,الخصوم,القيمة
النقد والأرصدة البنكية,0,,الذمم الدائنة,0
الأسهم والاستثمارات,0,,المصاريف المستحقة,0
المخزون,0,,قروض قصيرة الأجل,0
الذمم المدينة (المستحقات),0,,الجزء السنوي من الديون طويلة الأجل,0
الذهب (بالجرام),0,,,

ملاحظات:
1. أدخل القيم بالأرقام فقط بدون رموز عملة
2. يمكن ترك الخلايا الفارغة أو وضع 0 إذا لم توجد قيمة
3. تاريخ الميزانية العمومية: ${new Date().toLocaleDateString('ar-EG')}`;

      const blob = new Blob([templateContent], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = 'balance_sheet_template.xlsx';

      document.body.appendChild(link);
      link.click();

      setTimeout(() => {
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
      }, 100);

    } catch (error) {
      console.error('Error creating simple template:', error);
      this.errorMessage.set('تعذر إنشاء النموذج. حاول تنزيله يدويًا من مجلد templates.');
    }
  }

  async onFileChange(event: Event) {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      const file = target.files[0];

      // التحقق من امتداد الملف
      if (!file.name.toLowerCase().endsWith('.xlsx')) {
        this.errorMessage.set('الرجاء تحميل ملف بصيغة Excel (.xlsx) فقط');
        target.value = '';
        return;
      }

      this.fileName.set(file.name);
      this.isLoading.set(true);
      this.errorMessage.set(null);

      try {
        const parsedData = await this.zakahService.readExcelFile(file);
        this.zakahService.updateFormData(parsedData);

        // الانتقال إلى صفحة الذهب
        const goldStepIndex = this.steps().indexOf('الذهب');
        if (goldStepIndex !== -1) {
          this.zakahService.goToStep(goldStepIndex);
        } else {
          const reviewStepIndex = this.steps().indexOf('مراجعة');
          if (reviewStepIndex !== -1) {
            this.zakahService.goToStep(reviewStepIndex);
          }
        }

        this.errorMessage.set(null);

      } catch (error) {
        console.error('Error reading Excel file:', error);
        this.errorMessage.set('حدث خطأ في قراءة ملف Excel. تأكد من تنسيق الملف.');

        // تعيين بيانات افتراضية للاختبار
        this.zakahService.updateFormData({
          cash: 1000000,
          stocks: 1000000,
          inventory: 1000000,
          receivables: 1000000,
          accountPayable: 1000,
          expenses: 1000,
          shortTermLoans: 1000,
          goldWeightInGrams: 1000,
          longTermDebt: 0
        });

        const goldStepIndex = this.steps().indexOf('الذهب');
        if (goldStepIndex !== -1) {
          this.zakahService.goToStep(goldStepIndex);
        }

      } finally {
        this.isLoading.set(false);
        target.value = '';
      }
    } else {
      this.fileName.set(null);
    }
  }

  next() {
    this.zakahService.nextStep();
  }

  back() {
    this.zakahService.prevStep();
  }

  async calculate() {
    await this.zakahService.calculateZakah();
  }

}
