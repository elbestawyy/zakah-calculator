import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { AuthStorageService } from '../storage-service/StorageService';
import {ChangePasswordRequest, ProfileUpdateRequest} from '../../models/request/IAuthRequest';
import {DeleteAccountResponse, ProfileUpdateResponse} from '../../models/response/IAuthResponse';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly BASE_URL = 'http://localhost:8080/users';

  constructor(private http: HttpClient) {}

  /* ================= PASSWORD ================= */

  changePassword(
    request: ChangePasswordRequest
  ): Observable<void> {
    return this.http.patch<void>(
      `${this.BASE_URL}/change-password`,
      request
    );
  }

  /* ================= PROFILE ================= */

  updateProfile(
    request: ProfileUpdateRequest
  ): Observable<ProfileUpdateResponse> {
    return this.http.patch<ProfileUpdateResponse>(
      `${this.BASE_URL}/update-profile`,
      request
    );
  }

  /* ================= ACCOUNT ================= */

  deleteAccount(): Observable<DeleteAccountResponse> {
    return this.http
      .delete<DeleteAccountResponse>(`${this.BASE_URL}/delete`)
      .pipe(
        tap(() => {
          // اختياري: تعمل logout بعد الحذف
          AuthStorageService.clear();
        })
      );
  }

  restoreAccount(): Observable<void> {
    return this.http.patch<void>(
      `${this.BASE_URL}/restore`,
      {}
    );
  }
}
