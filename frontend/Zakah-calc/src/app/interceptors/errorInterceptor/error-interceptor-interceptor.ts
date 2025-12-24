import { inject, Injector } from '@angular/core';
import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { ToastrService } from 'ngx-toastr';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const injector = inject(Injector); // ⬅️ جيب Injector

  // Don't inject ToastrService here directly
  // const toastr = inject(ToastrService); // ❌ مش هنا

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // جيب ToastrService داخل الـ catchError
      const toastr = injector.get(ToastrService); // ✅ هنا

      let userMessage = 'حدث خطأ غير معروف، يرجى المحاولة لاحقًا.';

      // Validation Errors من الـ backend
      if (error.status === 400 && error.error?.validationErrors?.length) {
        const messages = error.error.validationErrors.map((ve: any) => {
          switch (ve.code) {
            case 'VALIDATION.AUTHENTICATION.EMAIL.NOT_BLANK':
              return 'الرجاء إدخال البريد الإلكتروني.';
            case 'VALIDATION.AUTHENTICATION.EMAIL.NOT_FORMAT':
              return 'صيغة البريد الإلكتروني غير صحيحة.';
            case 'VALIDATION.AUTHENTICATION.PASSWORD.NOT_BLANK':
              return 'الرجاء إدخال كلمة المرور.';
            case 'VALIDATION.REGISTRATION.PASSWORD.WEAK':
              return 'كلمة المرور ضعيفة، يجب أن تحتوي على حرف كبير وصغير ورقم ورمز خاص.';
            case 'VALIDATION.REGISTRATION.CONFIRM_PASSWORD.NOT_BLANK':
              return 'الرجاء تأكيد كلمة المرور.';
            case 'VALIDATION.REGISTRATION.FIRSTNAME.NOT_BLANK':
              return 'الرجاء إدخال الاسم الأول.';
            case 'VALIDATION.REGISTRATION.LASTNAME.NOT_BLANK':
              return 'الرجاء إدخال الاسم الأخير.';
            case 'VALIDATION.REGISTRATION.USERNAME.NOT_BLANK':
              return 'الرجاء إدخال البريد الإلكتروني للتسجيل.';
            case 'VALIDATION.REGISTRATION.USERNAME.NOT_FORMATED':
              return 'صيغة البريد الإلكتروني غير صحيحة.';
            case 'VALIDATION.FORGET_PASSWORD.EMAIL.NOT_BLANK':
              return 'الرجاء إدخال البريد الإلكتروني.';
            case 'VALIDATION.FORGET_PASSWORD.EMAIL.EMAIL_FORMAT':
              return 'صيغة البريد الإلكتروني غير صحيحة.';
            case 'VALIDATION.CHANGE.PASSWORD.NOT_BLANK':
              return 'الرجاء إدخال كلمة المرور الحالية.';
            case 'VALIDATION.REGISTRATION.PASSWORD.SIZE':
              return 'كلمة المرور يجب أن تكون بين 8 و 50 حرفاً.';
            case 'VALIDATION.REGISTRATION.CONFIRM_PASSWORD.NOT_BLANK':
              return 'الرجاء إدخال تأكيد كلمة المرور.';
            case 'VALIDATION.RESET_PASSWORD.PASSWORD.NOT_BLANK':
              return 'الرجاء إدخال كلمة المرور الجديدة.';
            case 'VALIDATION.RESET_PASSWORD.PASSWORD.WEAK':
              return 'كلمة المرور الجديدة ضعيفة، يجب أن تحتوي على حرف كبير وصغير ورقم ورمز خاص.';
            case 'VALIDATION.VERIFY_OTP.OTP.NOT_BLANK':
              return 'الرجاء إدخال رمز التحقق.';
            default:
              return ve.message || 'خطأ في الإدخال.';
          }
        });

        toastr.error(messages.join('<br>'), 'أخطاء في الإدخال', {
          enableHtml: true
        });
        userMessage = 'الرجاء تصحيح الأخطاء في النموذج.';
      }

      // Errors عامة من الباك اند
      else if (error.error?.message) {
        switch (error.status) {
          case 401:
            userMessage = 'بيانات الدخول غير صحيحة.';
            break;
          case 403:
            userMessage = 'ليس لديك صلاحية للوصول لهذا المورد.';
            break;
          case 404:
            userMessage = 'المورد المطلوب غير موجود.';
            break;
          case 409:
            userMessage = 'المورد موجود بالفعل.';
            break;
          case 500:
            userMessage = 'حدث خطأ في الخادم، يرجى المحاولة لاحقًا.';
            break;
          default:
            userMessage = error.error.message;
        }
        toastr.error(userMessage, `خطأ ${error.status}`);
      }

      // Default error
      else {
        toastr.error(userMessage, `خطأ ${error.status}`);
      }

      console.error('HTTP Error:', error);
      return throwError(() => error);
    })
  );
};
