import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { AuthStorageService } from '../storage-service/StorageService';
import { ChangePasswordRequest, ProfileUpdateRequest } from '../../models/request/IAuthRequest';
import { DeleteAccountResponse, ProfileUpdateResponse } from '../../models/response/IAuthResponse';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly BASE_URL = 'http://localhost:8080/users';

  constructor(private http: HttpClient) {}

  /* ================= PASSWORD ================= */

  changePassword(request: ChangePasswordRequest): Observable<void> {
    console.log('UserService: Sending change password request to:', `${this.BASE_URL}/change-password`);
    console.log('Request data:', request);

    return this.http.patch<void>(
      `${this.BASE_URL}/change-password`,
      request,
      {
        headers: {
          'Content-Type': 'application/json'
        }
      }
    ).pipe(
      tap(() => {
        console.log('UserService: Password change successful');
      })
    );
  }

  /* ================= PROFILE ================= */

  updateProfile(request: ProfileUpdateRequest): Observable<ProfileUpdateResponse> {
    console.log('UserService: Sending profile update to:', `${this.BASE_URL}/update-profile`);
    console.log('Request data:', request);

    return this.http.patch<ProfileUpdateResponse>(
      `${this.BASE_URL}/update-profile`,
      request,
      {
        headers: {
          'Content-Type': 'application/json'
        }
      }
    ).pipe(
      tap((response) => {
        console.log('UserService: Profile update successful:', response);
      })
    );
  }

  /* ================= ACCOUNT ================= */

  deleteAccount(): Observable<DeleteAccountResponse> {
    return this.http.delete<DeleteAccountResponse>(`${this.BASE_URL}/delete`)
      .pipe(
        tap(() => {
          AuthStorageService.clear();
        })
      );
  }

  restoreAccount(): Observable<void> {
    return this.http.patch<void>(`${this.BASE_URL}/restore`, {});
  }
}
