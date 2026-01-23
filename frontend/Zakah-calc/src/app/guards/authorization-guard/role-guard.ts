import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthStorageService } from '../../services/storage-service/StorageService';
import { UserType } from '../../models/enums/UserType';

export const roleGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const userType = AuthStorageService.getUserType();

  if (!userType) {
    router.navigate(['/login']);
    return false;
  }

  const url = state.url;

  /* ================= COMPANY SOFTWARE ================= */
  if (
    url.startsWith('/company/company-software') &&
    userType !== UserType.ROLE_COMPANY_SOFTWARE
  ) {
    router.navigate(['/intro']);
    return false;
  }

  /* ================= COMPANY ================= */
  else if (
    url.startsWith('/company') &&
    userType !== UserType.ROLE_COMPANY &&
    userType !== UserType.ROLE_COMPANY_SOFTWARE
  ) {
    router.navigate(['/intro']);
    return false;
  }

  /* ================= INDIVIDUAL ================= */
  if (
    url.startsWith('/individual') &&
    userType !== UserType.ROLE_INDIVIDUAL
  ) {
    router.navigate(['/intro']);
    return false;
  }

  return true;
};
