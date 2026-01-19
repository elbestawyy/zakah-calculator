import { AfterViewInit, Component, ElementRef, OnInit, signal, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../services/auth-service/auth.service';
import { AuthenticationRequest } from '../../../models/request/IAuthRequest';
import * as CryptoJS from 'crypto-js';
import { environment } from '../../../../environments/environment';
import { LeftSectionViewComponent } from "../left-section-view/left-section-view.component";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, LeftSectionViewComponent],
  templateUrl: './login.html'
})
export class Login implements OnInit {

  secretKey: string = environment.secretKey;
  loginForm!: FormGroup;
  isLoading = signal(false);
  serverError = signal<string | null>(null);
  showPassword = signal(false);

  @ViewChild('googleBtn', { static: true }) googleBtn!: ElementRef;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router 
  ) { }

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      email: [
        '',
        [
          Validators.required,
          Validators.email,
          Validators.minLength(5),
          Validators.maxLength(50)
        ]
      ],
      password: [
        '',
        [
          Validators.required,
          Validators.minLength(8),
          Validators.maxLength(64),
          Validators.pattern(/^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*\W).*$/)
        ]
      ]
    });
  }

  togglePassword() {
    this.showPassword.set(!this.showPassword());
  }
  get f() {
    return this.loginForm.controls;
  }

  onLogin(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.serverError.set(null);

    const request: AuthenticationRequest = {
      email: this.f['email'].value,
      password: this.f['password'].value
    };

    this.authService.login(request).subscribe({
      next: () => {
        this.router.navigate(['/intro']);
      },
      error: (err) => {
        const code = err?.error?.code;

        if (code === 'BAD_CREDENTIALS') {
          this.serverError.set('البريد الإلكتروني أو كلمة المرور غير صحيحة');
        }

        else if (code === 'ACCOUNT_NOT_VERIFIED') {
          const encryptedEmail = CryptoJS.AES.encrypt(
            request.email,
            this.secretKey
          ).toString();

          this.router.navigate(['/verify-otp'], {
            queryParams: { email: encryptedEmail }
          });
        }

        else {
          this.serverError.set('حدث خطأ غير متوقع، حاول مرة أخرى');
        }

        this.isLoading.set(false);
      }
    });

  }
}
