import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'typeLabel',
  standalone: true
})
export class TypeLabelPipe implements PipeTransform {
  transform(value: string): string {
    switch (value) {
      case 'ROLE_INDIVIDUAL':
        return 'فرد';
      case 'ROLE_COMPANY':
        return 'شركة';
      case 'FORGOT_PASSWORD':
        return 'نسيت كلمة المرور';
      default:
        return value;
    }
  }
}
