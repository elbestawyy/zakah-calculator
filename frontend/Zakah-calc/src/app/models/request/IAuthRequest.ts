export interface AuthenticationRequest {
  email: string;
  password: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmNewPassword: string;
}

export interface ForgetPasswordRequest {
  email: string;
}

export interface RefreshRequest {
  refreshToken: string;
}

import {UserType} from '../enums/UserType';

export interface RegistrationRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  confirmPassword: string;
  userType?: UserType;
}

export interface ResetPasswordRequest {
  resetToken: string;
  newPassword: string;
  confirmNewPassword: string;
}

export interface VerifyAccountRequest {
  otpCode: string;
}

export interface VerifyOtpRequest {
  otp: string;
}

export interface ProfileUpdateRequest {
  firstName?: string;
  lastName?: string;
}

export interface ResendOtpRequest{
  email?: string;
}
