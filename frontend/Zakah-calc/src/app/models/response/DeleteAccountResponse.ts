export interface DeleteAccountResponse {
  message: string;
  deletedAt: string;     // LocalDate -> ISO string
  restoreUntil: string;  // LocalDate -> ISO string
}
