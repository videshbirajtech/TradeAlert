import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService, UserProfile } from '../services/auth.service';
import { UserService } from '../services/user.service';
import { ToastComponent } from '../shared/toast/toast.component';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule, ToastComponent],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.css'
})
export class LayoutComponent implements OnInit {

  userEmail = '';
  sidebarCollapsed = false;
  profile: UserProfile | null = null;
  menuItems: any[] = [];

  private baseMenuItems = [
    { label: 'Dashboard',     icon: '📊', route: '/dashboard' },
    { label: 'Create Alert',  icon: '🔔', route: '/create-alert' },
    { label: 'Plans',         icon: '💎', route: '/plans' }
  ];

  private adminMenuItems = [
    { label: 'Admin Panel',   icon: '⚙️', route: '/admin' }
  ];

  private serverBase = environment.apiBaseUrl.replace('/api', '');

  constructor(
    private authService: AuthService,
    private userService: UserService,
    public router: Router
  ) {
    this.userEmail = this.authService.getLoggedInEmail() || '';
  }

  ngOnInit(): void {
    // Subscribe to user profile changes
    this.authService.userProfile$.subscribe(profile => {
      this.profile = profile;
      this.updateMenuItems();
    });

    // Load profile if not already loaded
    if (this.userEmail && !this.profile) {
      this.authService.getUserProfile(this.userEmail).subscribe({
        error: (err) => console.error('Error loading profile:', err)
      });
    }
  }

  private updateMenuItems(): void {
    this.menuItems = [...this.baseMenuItems];
    
    // Add admin menu items for super users
    if (this.authService.isSuperUser()) {
      this.menuItems.push(...this.adminMenuItems);
    }
  }

  getInitials(): string {
    if (!this.profile) return '?';
    return `${this.profile.firstName?.charAt(0) || ''}${this.profile.lastName?.charAt(0) || ''}`.toUpperCase();
  }

  getPhotoUrl(): string | null {
    return this.profile?.profilePhoto ? `${this.serverBase}${this.profile.profilePhoto}` : null;
  }

  toggleSidebar(): void {
    this.sidebarCollapsed = !this.sidebarCollapsed;
  }

  goToProfile(): void {
    this.router.navigate(['/profile']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
