import {Injectable} from '@angular/core';
import * as CryptoJS from 'crypto-js';

import {UserType} from '../../models/enums/UserType';
import {AuthenticationResponse, UserResponse} from '../../models/response/IAuthResponse';
import {environment} from '../../../environments/environment';


const ACCESS_TOKEN_KEY = 'access_token';
const REFRESH_TOKEN_KEY = 'refresh_token';
const USER_KEY = 'auth_user';

@Injectable({
  providedIn: 'root'
})
export class AuthStorageService {
  private static readonly ENCRYPTION_KEY = environment.secretKey;

  private static encrypt(data: string): string {
    if (!data) return '';
    try {
      return CryptoJS.AES.encrypt(data, this.ENCRYPTION_KEY).toString();
    } catch (error) {
      console.error('Encryption error:', error);
      return data;
    }
  }

  private static decrypt(encryptedData: string): string {
    if (!encryptedData) return '';
    try {
      const bytes = CryptoJS.AES.decrypt(encryptedData, this.ENCRYPTION_KEY);
      const result = bytes.toString(CryptoJS.enc.Utf8);
      return result || '';
    } catch (error) {
      console.error('Decryption error:', error);
      return '';
    }
  }

  // Encrypt
  private static encryptKey(key: string): string {
    return CryptoJS.SHA256(key + this.ENCRYPTION_KEY).toString();
  }

  // getEncryptedKey
  private static getEncryptedKey(originalKey: string): string {
    return this.encryptKey(originalKey);
  }

  /* ================= TOKENS ================= */

  static saveTokens(auth: AuthenticationResponse): void {
    try {
      const encryptedAccessKey = this.getEncryptedKey(ACCESS_TOKEN_KEY);
      const encryptedRefreshKey = this.getEncryptedKey(REFRESH_TOKEN_KEY);
      const encryptedUserKey = this.getEncryptedKey(USER_KEY);

      const encryptedAccessToken = this.encrypt(auth.accessToken);
      const encryptedRefreshToken = this.encrypt(auth.refreshToken);
      const encryptedUser = this.encrypt(JSON.stringify(auth.userResponse));

      localStorage.setItem(encryptedAccessKey, encryptedAccessToken);
      localStorage.setItem(encryptedRefreshKey, encryptedRefreshToken);
      localStorage.setItem(encryptedUserKey, encryptedUser);
    } catch (error) {
      console.error('Error saving tokens:', error);
    }
  }

  private static isBrowser(): boolean {
    return typeof window !== 'undefined';
  }

  static getAccessToken(): string | null {
    if (!this.isBrowser()) return null;

    const encryptedKey = this.getEncryptedKey(ACCESS_TOKEN_KEY);
    const encrypted = localStorage.getItem(encryptedKey);

    if (!encrypted) return null;

    const decrypted = this.decrypt(encrypted);
    return decrypted || null;
  }

  static getRefreshToken(): string | null {
    if (!this.isBrowser()) return null;

    const encryptedKey = this.getEncryptedKey(REFRESH_TOKEN_KEY);
    const encrypted = localStorage.getItem(encryptedKey);

    if (!encrypted) return null;

    const decrypted = this.decrypt(encrypted);
    return decrypted || null;
  }

  static hasAccessToken(): boolean {
    return !!this.getAccessToken();
  }

  /* ================= USER ================= */

  static saveUser(user: UserResponse): void {
    try {
      const encryptedKey = this.getEncryptedKey(USER_KEY);
      const encryptedUser = this.encrypt(JSON.stringify(user));
      localStorage.setItem(encryptedKey, encryptedUser);
    } catch (error) {
      console.error('Error saving user:', error);
    }
  }

  static getUser(): UserResponse | null {
    if (!this.isBrowser()) return null;

    const encryptedKey = this.getEncryptedKey(USER_KEY);
    const encrypted = localStorage.getItem(encryptedKey);

    if (!encrypted) return null;

    try {
      const decrypted = this.decrypt(encrypted);
      return decrypted ? JSON.parse(decrypted) : null;
    } catch (error) {
      console.error('Error getting user:', error);
      return null;
    }
  }

  static getUserType(): UserType | null {
    if (!this.isBrowser()) return null;
    return this.getUser()?.userType ?? null;
  }

  static getUserFullName(): string | null {
    if (!this.isBrowser()) return null;
    return this.getUser()?.fullName ?? null;
  }

  /* ================= ROLE HELPERS ================= */

  static isIndividual(): boolean {
    return this.getUserType() === UserType.ROLE_INDIVIDUAL;
  }

  static isCompany(): boolean {
    return this.getUserType() === UserType.ROLE_COMPANY;
  }

  static isLoggedIn(): boolean {
    return this.hasAccessToken();
  }

  /* ================= LOGOUT ================= */

  static clear(): void {
    try {
      const encryptedAccessKey = this.getEncryptedKey(ACCESS_TOKEN_KEY);
      const encryptedRefreshKey = this.getEncryptedKey(REFRESH_TOKEN_KEY);
      const encryptedUserKey = this.getEncryptedKey(USER_KEY);

      localStorage.removeItem(encryptedAccessKey);
      localStorage.removeItem(encryptedRefreshKey);
      localStorage.removeItem(encryptedUserKey);

      localStorage.removeItem(ACCESS_TOKEN_KEY);
      localStorage.removeItem(REFRESH_TOKEN_KEY);
      localStorage.removeItem(USER_KEY);
    } catch (error) {
      console.error('Error clearing storage:', error);
    }
  }

  /* ================= MIGRATION HELPER ================= */

  static migrateOldData(): void {
    if (!this.isBrowser()) return;

    const oldAccessToken = localStorage.getItem(ACCESS_TOKEN_KEY);
    const oldRefreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);
    const oldUser = localStorage.getItem(USER_KEY);

    if (oldAccessToken || oldRefreshToken || oldUser) {
      console.log('Migrating old data to encrypted format...');

      if (oldAccessToken) {
        const encryptedKey = this.getEncryptedKey(ACCESS_TOKEN_KEY);
        localStorage.setItem(encryptedKey, this.encrypt(oldAccessToken));
        localStorage.removeItem(ACCESS_TOKEN_KEY);
      }

      if (oldRefreshToken) {
        const encryptedKey = this.getEncryptedKey(REFRESH_TOKEN_KEY);
        localStorage.setItem(encryptedKey, this.encrypt(oldRefreshToken));
        localStorage.removeItem(REFRESH_TOKEN_KEY);
      }

      if (oldUser) {
        const encryptedKey = this.getEncryptedKey(USER_KEY);
        try {
          JSON.parse(oldUser);
          localStorage.setItem(encryptedKey, this.encrypt(oldUser));
        } catch {
          localStorage.setItem(encryptedKey, oldUser);
        }
        localStorage.removeItem(USER_KEY);
      }

      console.log('Migration completed!');
    }
  }
}
