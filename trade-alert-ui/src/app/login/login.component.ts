import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  // Step 1: email, Step 2: otp
  step: 'email' | 'otp' = 'email';

  email = '';
  otp: number | null = null;

  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  sendOtp(): void {
    if (!this.email) return;

    this.loading = true;
    this.errorMessage = '';

    this.authService.login(this.email).subscribe({
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

    this.authService.verifyOtp(this.email, this.otp).subscribe({
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
    this.step = 'email';
    this.otp = null;
    this.errorMessage = '';
    this.successMessage = '';
  }

  goToSignup(): void {
    this.router.navigate(['/signup']);
  }
}
