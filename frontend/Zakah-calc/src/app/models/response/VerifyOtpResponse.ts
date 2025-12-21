export interface VerifyOtpResponse {
  message: string;
  resetToken?: string; // optional لو بييجي في سيناريوهات معينة
}
