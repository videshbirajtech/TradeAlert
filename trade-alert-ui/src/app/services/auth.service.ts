import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ApiResponse {
  success: boolean;
  message: string;
}

const SESSION_EMAIL_KEY = 'user_email';
const SESSION_EXPIRY_KEY = 'user_session_expiry';
const SESSION_DURATION_DAYS = 5;

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private baseUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  login(email: string): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.baseUrl}/auth/login`, { email });
  }

  verifyOtp(email: string, otp: number): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.baseUrl}/auth/verify-otp`, { email, otp });
  }

  signup(firstName: string, lastName: string, email: string): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.baseUrl}/auth/signup`, { firstName, lastName, email });
  }

  verifySignupOtp(email: string, otp: number): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.baseUrl}/auth/verify-signup-otp`, { email, otp });
  }

  setLoggedIn(email: string): void {
    const expiryTime = Date.now() + SESSION_DURATION_DAYS * 24 * 60 * 60 * 1000;
    localStorage.setItem(SESSION_EMAIL_KEY, email);
    localStorage.setItem(SESSION_EXPIRY_KEY, expiryTime.toString());
  }

  getLoggedInEmail(): string | null {
    if (!this.isLoggedIn()) return null;
    return localStorage.getItem(SESSION_EMAIL_KEY);
  }

  isLoggedIn(): boolean {
    const email = localStorage.getItem(SESSION_EMAIL_KEY);
    const expiry = localStorage.getItem(SESSION_EXPIRY_KEY);

    if (!email || !expiry) return false;

    // Check if session has expired
    if (Date.now() > parseInt(expiry)) {
      this.logout(); // auto-clear expired session
      return false;
    }

    return true;
  }

  logout(): void {
    localStorage.removeItem(SESSION_EMAIL_KEY);
    localStorage.removeItem(SESSION_EXPIRY_KEY);
  }
}
