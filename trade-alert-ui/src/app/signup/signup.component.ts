import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.css'
})
export class SignupComponent {

  step: 'form' | 'otp' = 'form';

  firstName = '';
  lastName = '';
  email = '';
  otp: number | null = null;

  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  signup(): void {
    if (!this.firstName || !this.lastName || !this.email) return;

    this.loading = true;
    this.errorMessage = '';

    this.authService.signup(this.firstName, this.lastName, this.email).subscribe({
      next: (res) => {
        this.loading = false;
        if (res.success) {
          this.successMessage = res.message;
          this.step = 'otp';
        } else {
          this.errorMessage = res.message;
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Something went wrong. Please try again.';
      }
    });
  }

  verifyOtp(): void {
    if (!this.otp) return;

    this.loading = true;
    this.errorMessage = '';

    this.authService.verifySignupOtp(this.email, this.otp).subscribe({
      next: (res) => {
        this.loading = false;
        if (res.success) {
          this.authService.setLoggedIn(this.email);
          this.router.navigate(['/dashboard']);
        } else {
          this.errorMessage = res.message;
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Something went wrong. Please try again.';
      }
    });
  }

  goBack(): void {
    this.step = 'form';
    this.otp = null;
    this.errorMessage = '';
    this.successMessage = '';
  }

  goToLogin(): void {
    this.router.navigate(['/login']);
  }
}
