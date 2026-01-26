import { Component, OnInit, inject, output, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { UserService } from '../../services/user-service/user-service';
import { AuthStorageService } from '../../services/storage-service/StorageService';
import {
  ProfileUpdateRequest,
  ChangePasswordRequest
} from '../../models/request/IAuthRequest';
import { ProfileUpdateResponse } from '../../models/response/IAuthResponse';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth-service/auth.service';
import Swal from 'sweetalert2'

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class Profile implements OnInit {

  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private authService = inject(AuthService);
  private router = inject(Router);
  save = output<void>();
  cancel = output<void>();
  deleteAccount = output<void>();
  showPassword = signal(false);
  showOldPassword = signal(false);
  showConfirmPassword = signal(false);

  /* ================= FORMS ================= */

  infoForm: FormGroup = this.fb.group({
    name: ['', Validators.required],
    email: [{ value: '', disabled: true }]
  });

  passwordForm: FormGroup = this.fb.group({
    currentPassword: ['', Validators.required],
    newPassword: ['', [Validators.required, Validators.minLength(3)]],
    confirmNewPassword: ['', Validators.required]
  });
  get f() {
    return this.passwordForm.controls;
  }

  /* ================= SIGNAL STATE ================= */

  isUpdatingInfo = signal(false);
  isUpdatingPassword = signal(false);

  infoError = signal<string | null>(null);
  passwordError = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  /* ================= INIT ================= */

  ngOnInit(): void {
    const user = AuthStorageService.getUser();

    if (user) {
      this.infoForm.patchValue({
        name: user.fullName,
        email: user.email
      });
    }
  }

  togglePassword() {
    this.showPassword.set(!this.showPassword());
  }

  /* ================= UPDATE INFO ================= */

  onUpdateInfo(event?: Event): void {
    event?.preventDefault();

    if (this.infoForm.invalid) {
      this.infoForm.markAllAsTouched();
      return;
    }

    this.isUpdatingInfo.set(true);
    this.infoError.set(null);
    this.successMessage.set(null);

    const fullName = this.infoForm.get('name')!.value.trim();
    const parts = fullName.split(' ');

    const request: ProfileUpdateRequest = {
      fullName: this.infoForm.value.name
    };

    this.userService.updateProfile(request).subscribe({
      next: (res: ProfileUpdateResponse) => {
        this.isUpdatingInfo.set(false);

        if (res.fullName) {
          const currentUser = AuthStorageService.getUser();
          if (currentUser) {
            AuthStorageService.saveUser({
              ...currentUser,
              fullName: res.fullName
            });
          }

          this.infoForm.patchValue({ name: res.fullName });
          this.successMessage.set('تم تحديث معلوماتك بنجاح');
        }
      },
      error: (err) => {
        this.isUpdatingInfo.set(false);
        this.infoError.set(
          err.error?.message || 'حدث خطأ أثناء تحديث البيانات'
        );
      }
    });
  }

  /* ================= UPDATE PASSWORD ================= */

  onUpdatePassword(event?: Event): void {
    event?.preventDefault();

    if (this.passwordForm.invalid) {
      this.passwordForm.markAllAsTouched();
      this.passwordError.set('يرجى إدخال جميع الحقول');
      return;
    }

    const { newPassword, confirmNewPassword } = this.passwordForm.value;

    if (newPassword !== confirmNewPassword) {
      this.passwordError.set('كلمات المرور غير متطابقة');
      return;
    }

    this.isUpdatingPassword.set(true);
    this.passwordError.set(null);
    this.successMessage.set(null);

    const request: ChangePasswordRequest =
      this.passwordForm.getRawValue();

    this.userService.changePassword(request).subscribe({
      next: () => {
        this.isUpdatingPassword.set(false);
        this.successMessage.set('تم تغيير كلمة المرور بنجاح');
        this.passwordForm.reset();
      },
      error: (err) => {
        this.isUpdatingPassword.set(false);
        this.passwordError.set(
          err.error?.message || 'حدث خطأ أثناء تغيير كلمة المرور'
        );
      }
    });
  }

  /* ================= DELETE ACCOUNT ================= */

onDeleteAccount(): void {
  Swal.fire({
    title: "تأكيد إيقاف الحساب",
    text: "سيتم إيقاف حسابك مؤقتًا، ويمكنك استعادته خلال مدة أقصاها 30 يومًا عن طريق تسجيل الدخول مرة أخرى وتنفيذ عملية استعادة الحساب (Restore). بعد انتهاء هذه المدة سيتم حذف الحساب نهائيًا ولا يمكن استرجاعه.",
    icon: "warning",
    showCancelButton: true,
    confirmButtonColor: "#3085d6",
    cancelButtonColor: "#d33",
    confirmButtonText: "نعم، إيقاف الحساب",
    cancelButtonText: "إلغاء"
  }).then((result) => {
    if (result.isConfirmed) {
      this.userService.deleteAccount().subscribe(() => {

        Swal.fire({
          title: "تم إيقاف الحساب",
          text: "تم إيقاف حسابك بنجاح. يمكنك استعادته خلال 30 يومًا عن طريق تسجيل الدخول مرة أخرى وتنفيذ عملية استعادة الحساب.",
          icon: "success",
          confirmButtonText: "حسنًا"
        }).then(() => {
          this.deleteAccount.emit();
          this.authService.logout();
          this.router.navigate(['/login']);
        });

      });
    }
  });
}




  // onSave() {
  //   // Mock implementation
  //   console.log('Profile changes saved!');
  //   this.save.emit();
  // }

  // onCancel() {
  //   this.cancel.emit();
  // }

  // onDeleteAccount() {
  //   this.deleteAccount.emit();
  // }

}
