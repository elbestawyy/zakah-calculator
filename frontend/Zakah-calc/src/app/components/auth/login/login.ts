import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../services/auth-service/auth.service';
import { AuthenticationRequest } from '../../../models/request/IAuthRequest';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.html'
})
export class Login implements OnInit {

  loginForm!: FormGroup;
  isLoading = signal(false);
  serverError = signal<string | null>(null);

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      email: [
        '',
        [
          Validators.required,
          Validators.email
        ]
      ],
      password: [
        '',
        [
          Validators.required,
          Validators.minLength(8),
          Validators.maxLength(64)
        ]
      ]
    });
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
      next: (res) => {
        console.log(res)
        this.router.navigate(['/intro']);
      },
      error: (err) => {
        if (err.status === 401) {
          this.serverError.set('البريد الإلكتروني أو كلمة المرور غير صحيحة');
        }else if (err.status === 403){
          this.router.navigate(['/verify-otp'], {
            queryParams: { email: request.email }
          });
        }
        // else if (err.messages === 403){
        //   this.router.navigate(['/verify-otp'], {
        //     queryParams: { email: request.email }
        //   });
        // }
        else {
          this.serverError.set('حدث خطأ غير متوقع، حاول مرة أخرى');
        }
        this.isLoading.set(false);
      },
      complete: () => this.isLoading.set(false)
    });
  }
}
