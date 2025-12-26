// src/app/components/wizard/wizard.component.ts
import { ChangeDetectionStrategy, Component, inject, computed, signal } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { ZakahService } from '../../services/zakah.service';
import { Persona, ZakahFormData } from '../../models/zakah.model';
import { TooltipComponent } from '../../shared/tooltip/tooltip.component';

@Component({
  selector: 'app-wizard',
  standalone: true,
  imports: [CommonModule, TooltipComponent, CurrencyPipe],
  templateUrl: './wizard.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WizardComponent {
  zakahService = inject(ZakahService);

  formData = this.zakahService.formData;
  currentStep = this.zakahService.currentWizardStep;
  steps = this.zakahService.wizardSteps;
  isCalculating = this.zakahService.isCalculating;

  persona = computed(() => this.formData().persona);
  fileName = signal<string | null>(null);

  onDateChange(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.zakahService.updateFormData({ balanceSheetDate: value });
  }

  onInputChange(event: Event) {
    const target = event.target as HTMLInputElement;
    const key = target.name as keyof ZakahFormData;
    const value = target.valueAsNumber || 0;

    const patch: Partial<ZakahFormData> = { [key]: value };

    if (key === 'goldWeightInGrams' || key === 'goldPricePerGram') {
        const currentData = this.zakahService.formData();
        const weight = key === 'goldWeightInGrams' ? value : currentData.goldWeightInGrams;
        const price = key === 'goldPricePerGram' ? value : currentData.goldPricePerGram;
        patch.goldValue = (weight || 0) * (price || 0);
    }

    this.zakahService.updateFormData(patch);
  }

  onFileChange(event: Event) {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      const file = target.files[0];
      this.fileName.set(file.name);
      console.log('File selected:', file);

      // In a real application, you would parse the Excel file here.
      // For demonstration, we'll mock the data and jump to the review step.
      this.zakahService.updateFormData({
        cash: 25000,
        stocks: 12000,
        inventory: 35000,
        receivables: 5000,
        accountPayable: 8000,
        expenses: 1500,
        shortTermLoans: 4000,
        longTermDebt: 2000,
      });

      const reviewStepIndex = this.steps().indexOf('مراجعة');
      if (reviewStepIndex !== -1) {
        this.zakahService.goToStep(reviewStepIndex);
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
    // App component will switch the view
  }
}
