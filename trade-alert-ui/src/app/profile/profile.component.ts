import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';
import { UserService, UserProfile } from '../services/user.service';
import { ToastService } from '../services/toast.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {

  profile: UserProfile | null = null;
  loading = false;
  saving = false;
  uploadingPhoto = false;

  firstName = '';
  lastName = '';
  email = '';

  photoPreview: string | null = null;
  selectedFile: File | null = null;

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    const email = this.authService.getLoggedInEmail();
    if (!email) return;
    this.loading = true;
    this.userService.getProfile(email).subscribe({
      next: (data) => {
        this.profile = data;
        this.firstName = data.firstName;
        this.lastName = data.lastName;
        this.email = data.email;
        this.photoPreview = data.profilePhoto
          ? `http://localhost:8090${data.profilePhoto}`
          : null;
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;
    this.selectedFile = input.files[0];

    // Preview
    const reader = new FileReader();
    reader.onload = (e) => this.photoPreview = e.target?.result as string;
    reader.readAsDataURL(this.selectedFile);

    // Auto-upload
    this.uploadPhoto();
  }

  uploadPhoto(): void {
    if (!this.selectedFile || !this.profile) return;
    this.uploadingPhoto = true;
    this.userService.uploadPhoto(this.profile.email, this.selectedFile).subscribe({
      next: (data) => {
        this.profile = data;
        this.uploadingPhoto = false;
        this.toast.success('Profile photo updated!');
      },
      error: () => {
        this.uploadingPhoto = false;
        this.toast.error('Failed to upload photo.');
      }
    });
  }

  saveProfile(): void {
    if (!this.profile) return;
    this.saving = true;

    this.userService.updateProfile(this.profile.email, {
      firstName: this.firstName,
      lastName: this.lastName,
      email: this.email
    }).subscribe({
      next: (data) => {
        this.profile = data;
        this.saving = false;
        this.toast.success('Profile updated successfully!');
      },
      error: (err) => {
        this.saving = false;
        this.toast.error(err.error?.message || 'Failed to update profile.');
      }
    });
  }

  getInitials(): string {
    return `${this.firstName?.charAt(0) || ''}${this.lastName?.charAt(0) || ''}`.toUpperCase();
  }
}
