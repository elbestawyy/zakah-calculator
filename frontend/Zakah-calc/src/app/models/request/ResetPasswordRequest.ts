export interface ResetPasswordRequest {
  resetToken: string;
  newPassword: string;
  confirmNewPassword: string;
}
