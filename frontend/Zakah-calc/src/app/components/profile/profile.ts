import { Component, Injector, OnInit, Signal, inject, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user-service/user-service';
import { AuthStorageService } from '../../services/storage-service/StorageService';
import { ProfileUpdateRequest, ChangePasswordRequest } from '../../models/request/IAuthRequest';
import { ProfileUpdateResponse } from '../../models/response/IAuthResponse';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-profile',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
  standalone: true,
})
export class Profile implements OnInit {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);

  infoForm!: FormGroup;
  passwordForm!: FormGroup;

  isUpdatingInfo = false;
  isUpdatingPassword = false;
  infoError: string | null = '';
  passwordError: string | null = '';
  successMessage: string | null = '';

  isInitialized = false;

  ngOnInit() {
    console.log('Profile ngOnInit called');
    this.initForms();
    this.isInitialized = true;
  }

  private initForms() {
    console.log('Initializing forms...');

    const user = AuthStorageService.getUser();
    console.log('User from storage:', user);

    // Personal Info Form
    this.infoForm = this.fb.group({
      name: [user?.fullName || '', [Validators.required]],
      email: [{value: user?.email || '', disabled: true}]
    });

    console.log('Info form created:', this.infoForm);

    // Password Change Form
    this.passwordForm = this.fb.group({
      currentPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required, Validators.minLength(3)]],
      confirmNewPassword: ['', [Validators.required]]
    });

    console.log('Password form created:', this.passwordForm);
  }

  onUpdateInfo(event?: Event) {
    // ✅ منع السلوك الافتراضي للنموذج إذا كان هناك event
    if (event) {
      event.preventDefault();
    }

    console.log('onUpdateInfo called');
    console.log('Info form valid?', this.infoForm.valid);
    console.log('Info form value:', this.infoForm.value);

    if (this.infoForm.invalid) {
      console.log('Info form invalid');
      return;
    }

    this.isUpdatingInfo = true;
    this.infoError = '';
    this.successMessage = '';

    const fullName = this.infoForm.get('name')?.value;
    const nameParts = fullName.split(' ');
    const firstName = nameParts[0] || '';
    const lastName = nameParts.slice(1).join(' ') || '';

    const request: ProfileUpdateRequest = {
      firstName: firstName,
      lastName: lastName
    };

    console.log('Sending profile update:', request);

    this.userService.updateProfile(request).subscribe({
      next: (response: ProfileUpdateResponse) => {
        console.log('Profile update success:', response);
        this.isUpdatingInfo = false;

        if (response.fullName) {
          const currentUser = AuthStorageService.getUser();
          if (currentUser) {
            const updatedUser = {
              ...currentUser,
              fullName: response.fullName
            };

            console.log('Updating user in localStorage:', updatedUser);
            AuthStorageService.saveUser(updatedUser);

            this.infoForm.patchValue({
              name: response.fullName
            });

            this.successMessage = 'تم تحديث معلوماتك بنجاح!';


          } else {
            console.warn('No user found in localStorage to update');
          }
        }
      },
      error: (error) => {
        console.error('Profile update error:', error);
        this.isUpdatingInfo = false;
        this.infoError = error.error?.message || 'حدث خطأ في تحديث المعلومات';
      }
    });
  }

  onUpdatePassword(event?: Event) {
    // ✅ منع السلوك الافتراضي للنموذج إذا كان هناك event
    if (event) {
      event.preventDefault();
    }

    console.log('onUpdatePassword called');
    console.log('Form valid?', this.passwordForm.valid);
    console.log('Form value:', this.passwordForm.value);

    if (this.passwordForm.invalid) {
      console.log('Password form invalid');
      console.log('Form errors:', this.passwordForm.errors);

      // ✅ استخدام رسالة خطأ بدلاً من alert
      this.passwordError = 'البيانات غير صالحة. يرجى تعبئة جميع الحقول';
      setTimeout(() => {
        this.passwordError = null;
      }, 3000);
      return;
    }

    // ✅ التحقق من تطابق كلمات المرور
    const newPassword = this.passwordForm.get('newPassword')?.value;
    const confirmPassword = this.passwordForm.get('confirmNewPassword')?.value;

    if (newPassword !== confirmPassword) {
      this.passwordError = 'كلمات المرور غير متطابقة';
      setTimeout(() => {
        this.passwordError = null;
      }, 3000);
      return;
    }

    this.isUpdatingPassword = true;
    this.passwordError = null;
    this.successMessage = null;

    const request: ChangePasswordRequest = {
      currentPassword: this.passwordForm.get('currentPassword')?.value,
      newPassword: newPassword,
      confirmNewPassword: confirmPassword
    };

    console.log('Sending password change:', request);

    this.userService.changePassword(request).subscribe({
      next: () => {
        console.log('Password change success');
        this.isUpdatingPassword = false;
        this.successMessage = 'تم تغيير كلمة المرور بنجاح!';
        this.passwordForm.reset();

        // ✅ إخفاء رسالة النجاح بعد 3 ثواني
        setTimeout(() => {
          this.successMessage = null;
        }, 3000);
      },
      error: (error) => {
        console.error('Password change error:', error);
        console.error('Error status:', error.status);
        console.error('Error message:', error.message);
        console.error('Error response:', error.error);

        this.isUpdatingPassword = false;

        if (error.status === 400) {
          this.passwordError = error.error?.message || 'بيانات غير صالحة';
        } else if (error.status === 401) {
          this.passwordError = 'انتهت جلستك. يرجى تسجيل الدخول مرة أخرى';
        } else {
          this.passwordError = error.error?.message || 'حدث خطأ في تغيير كلمة المرور';
        }

        // ✅ إخفاء رسالة الخطأ بعد 3 ثواني
        setTimeout(() => {
          this.passwordError = null;
        }, 3000);
      }
    });
  }

  // دالة حذف الحساب
  onDeleteAccount() {
    if (confirm('هل أنت متأكد من حذف حسابك؟ هذه العملية لا يمكن التراجع عنها.')) {
      this.userService.deleteAccount().subscribe(() => {
        window.location.href = '/';
      });
    }
  }
}
