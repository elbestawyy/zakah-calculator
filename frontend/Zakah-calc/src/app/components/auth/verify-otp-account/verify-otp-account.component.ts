import { Component, OnInit, OnDestroy, signal } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import { CommonModule } from '@angular/common';
import {interval, Subject, takeWhile} from 'rxjs';
import { takeUntil, map, tap } from 'rxjs/operators';

import { AuthService } from '../../../services/auth-service/auth.service';
import { VerifyAccountRequest } from '../../../models/request/IAuthRequest';
import {environment} from '../../../../environments/environment';
import * as CryptoJS from 'crypto-js';
import { LeftSectionViewComponent } from "../left-section-view/left-section-view.component";

@Component({
  selector: 'app-verify-otp',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LeftSectionViewComponent,RouterLink],
  templateUrl: './verify-otp-account.component.html',
  styleUrls: ['./verify-otp-account.component.css']
})
export class VerifyOtpAccountComponent implements OnInit, OnDestroy {

  otpForm!: FormGroup;
  isLoading = signal(false);
  errorMessage = signal('');
  email!: string;
  secretKey: string = environment.secretKey;

  resendCounter = signal(60);
  resendDisabled = signal(true);

  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.otpForm = this.fb.group({
      otpCode: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(6)]]
    });

    // 🔐 قراءة وفك تشفير الإيميل من queryParam
    this.activatedRoute.queryParams.subscribe(params => {
      const encryptedEmail = params['email'];
      if (encryptedEmail) {
        const bytes = CryptoJS.AES.decrypt(encryptedEmail, this.secretKey);
        this.email = bytes.toString(CryptoJS.enc.Utf8);
      }
      this.startResendTimer();
    });
  }

  // 🔹 RxJS timer + Signals
  startResendTimer(): void {
    this.resendDisabled.set(true);
    this.resendCounter.set(30);

    interval(1000).pipe(
      map(i => 30 - i - 1),           // 59,58,...,0
      takeWhile(val => val >= 0),     // stop عند الصفر
      tap(val => this.resendCounter.set(val)),
      takeUntil(this.destroy$)
    ).subscribe({
      complete: () => this.resendDisabled.set(false) // بعد انتهاء العد
    });
  }


  resendOtp(): void {
    if (!this.email || this.resendDisabled()) return;

    this.authService.resendPasswordOtp({ email: this.email }).subscribe({
      next: () => this.startResendTimer(),
      error: () => {
        this.startResendTimer();
        this.errorMessage.set('حدث خطأ أثناء إعادة إرسال الرمز.')
      }
    });
  }

  submitOtp(): void {
    if (this.otpForm.invalid) {
      this.otpForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set('');

    const request: VerifyAccountRequest = {
      email: this.email,
      otpCode: this.otpForm.value.otpCode
    };

    this.authService.verifyAccount(request).subscribe({
      next: () => this.router.navigate(['/intro']),
      error: (err) => {
        this.errorMessage.set(err?.error?.message || 'حدث خطأ أثناء التحقق من الحساب.');
        this.isLoading.set(false);
      },
      complete: () => this.isLoading.set(false)
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
