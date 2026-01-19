import { Component, signal, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthStorageService } from '../../services/storage-service/StorageService';
import { AuthService } from '../../services/auth-service/auth.service';
import { UserType } from '../../models/enums/UserType';
import { ClickOutsideDirective } from '../../directives/ClickOutside.directive';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, ClickOutsideDirective],
  templateUrl: './navbar.html',
})
export class Navbar {
  private readonly _AuthService = inject(AuthService);

  name = AuthStorageService.getUserFullName();
  type = AuthStorageService.getUserType();

  isProfileMenuOpen = signal(false);
  isMobileMenuOpen = signal(false);

  constructor(private router: Router) {}

  toggleProfileMenu(event: Event) {
    event.stopPropagation();
    this.isProfileMenuOpen.update(v => !v);
  }

  toggleMobileMenu() {
    this.isMobileMenuOpen.update(v => !v);
  }

  closeAllMenus() {
    this.isProfileMenuOpen.set(false);
    this.isMobileMenuOpen.set(false);
  }

  switchWizard() {
    this.closeAllMenus();
    if (this.type === UserType.ROLE_INDIVIDUAL) {
      this.router.navigate(['/individual/wizard']);
    } else if (this.type === UserType.ROLE_COMPANY) {
      this.router.navigate(['/company/wizard']);
    } else {
      this.router.navigate(['/company/company-software/wizard']);
    }
  }

  switchDashboard() {
    this.closeAllMenus();
    if (this.type === UserType.ROLE_INDIVIDUAL) {
      this.router.navigate(['/individual/dashboard']);
    } else {
      this.router.navigate(['/company/dashboard']);
    }
  }

  logout() {
    this._AuthService.logout();
    this.closeAllMenus();
    this.router.navigate(['/']);
  }
}
