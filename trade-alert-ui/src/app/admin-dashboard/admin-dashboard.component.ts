import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService, AdminUser } from '../services/admin.service';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {

  users: AdminUser[] = [];
  loading = false;
  error: string | null = null;
  
  // Role management
  selectedUser: AdminUser | null = null;
  showRoleModal = false;
  availableRoles = ['USER', 'SUPER_USER'];

  constructor(
    private adminService: AdminService,
    private authService: AuthService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    const adminEmail = this.authService.getLoggedInEmail();
    if (!adminEmail) {
      this.error = 'Admin email not found';
      return;
    }

    this.loading = true;
    this.error = null;

    this.adminService.getAllUsers(adminEmail).subscribe({
      next: (users) => {
        this.users = users;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to load users';
        this.loading = false;
        console.error('Error loading users:', err);
      }
    });
  }

  openRoleModal(user: AdminUser): void {
    this.selectedUser = user;
    this.showRoleModal = true;
  }

  closeRoleModal(): void {
    this.selectedUser = null;
    this.showRoleModal = false;
  }

  assignRole(role: string): void {
    if (!this.selectedUser) return;

    const adminEmail = this.authService.getLoggedInEmail();
    if (!adminEmail) return;

    this.adminService.assignRole(adminEmail, this.selectedUser.email, role).subscribe({
      next: (message) => {
        this.toast.success(message);
        this.loadUsers(); // Refresh the list
        this.closeRoleModal();
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to assign role');
      }
    });
  }

  removeRole(role: string): void {
    if (!this.selectedUser) return;

    const adminEmail = this.authService.getLoggedInEmail();
    if (!adminEmail) return;

    if (!confirm(`Remove ${role} role from ${this.selectedUser.firstName} ${this.selectedUser.lastName}?`)) {
      return;
    }

    this.adminService.removeRole(adminEmail, this.selectedUser.email, role).subscribe({
      next: (message) => {
        this.toast.success(message);
        this.loadUsers(); // Refresh the list
        this.closeRoleModal();
      },
      error: (err) => {
        this.toast.error(err.error?.message || 'Failed to remove role');
      }
    });
  }

  hasRole(user: AdminUser, role: string): boolean {
    return user.roles.includes(role);
  }

  getRoleDisplayName(role: string): string {
    switch (role) {
      case 'USER': return 'Regular User';
      case 'SUPER_USER': return 'Super Admin';
      default: return role;
    }
  }

  getStatusBadgeClass(user: AdminUser): string {
    if (!user.verified) return 'status-unverified';
    if (user.hasActiveSubscription) return 'status-active';
    return 'status-inactive';
  }

  getStatusText(user: AdminUser): string {
    if (!user.verified) return 'Unverified';
    if (user.hasActiveSubscription) return 'Active';
    return 'No Subscription';
  }
}