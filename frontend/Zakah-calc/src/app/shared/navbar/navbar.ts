import { Component, signal, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthStorageService } from '../../services/storage-service/StorageService';
import { AuthService } from '../../services/auth-service/auth.service';
import { UserResponse } from '../../models/response/IAuthResponse';
import { TypeLabelPipe } from '../../pipes/pipes/type-label-pipe';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive , TypeLabelPipe],
  templateUrl: './navbar.html',
})
export class Navbar {
  private router = inject(Router);
  private readonly _AuthService = inject(AuthService)
  name = AuthStorageService.getUserFullName();
  type = AuthStorageService.getUserType();
  //  : UserResponse

  isProfileMenuOpen = signal(false);

  // Inside your component class
  toggleProfileMenu(event: Event) {
    event.stopPropagation(); // Prevents the click from hitting the 'document' listener
    this.isProfileMenuOpen.update(val => !val);
  }

  closeMenu() {
    this.isProfileMenuOpen.set(false);
  }

  logout() {
    AuthStorageService.clear();
    this._AuthService.logout();
    this.isProfileMenuOpen.set(false);
    this.router.navigate(['/']);
  }
}
