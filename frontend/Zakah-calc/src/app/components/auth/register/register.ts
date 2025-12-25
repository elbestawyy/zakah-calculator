// register.component.ts
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';

import { AuthService } from '../../../services/auth-service/auth.service';
import { RegistrationRequest } from '../../../models/request/IAuthRequest';
import { UserType } from '../../../models/enums/UserType';
import {environment} from '../../../../environments/environment';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.html'
})
export class Register implements OnInit {


  secretKey: string = environment.secretKey;
  registerForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.registerForm = this.fb.group(
      {
        name: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(8)]],
        confirmPassword: ['', Validators.required],
        persona: ['individual', Validators.required],
        // termsAccepted: [false, Validators.requiredTrue]
      },
      { validators: this.passwordMatchValidator }
    );
  }

  get f() {
    return this.registerForm.controls;
  }

  selectPersona(type: 'individual' | 'company') {
    this.registerForm.patchValue({ persona: type });
  }

  passwordMatchValidator(group: FormGroup) {
    return group.get('password')?.value === group.get('confirmPassword')?.value
      ? null
      : { passwordMismatch: true };
  }

  onSubmit() {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    const nameParts = this.registerForm.value.name.trim().split(' ');

    const request: RegistrationRequest = {
      firstName: nameParts[0],
      lastName: nameParts.slice(1).join(' ') || '',
      email: this.registerForm.value.email,
      password: this.registerForm.value.password,
      confirmPassword: this.registerForm.value.confirmPassword,
      userType:
        this.registerForm.value.persona === 'individual'
          ? UserType.ROLE_INDIVIDUAL
          : UserType.ROLE_COMPANY
    };

    this.authService.register(request).subscribe({
      next: () => {
        const encryptedEmail = CryptoJS.AES.encrypt(request.email, this.secretKey).toString();
        this.router.navigate(['/verify-otp'], {
          queryParams: { email: encryptedEmail }
        });
      },
      error: () => {
        alert('فشل إنشاء الحساب');
      }
    });
  }
}
