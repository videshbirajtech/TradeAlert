import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface ApiResponse<T = any> {
  success: boolean;
  message: string;
  data?: T;
}

export interface UserProfile {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  verified: boolean;
  profilePhoto: string | null;
  subscriptionId: string | null;
  createdDate: string;
  roles: string[];
  superUser: boolean; // Changed from isSuperUser to match JSON response
}

const SESSION_EMAIL_KEY = 'user_email';
const SESSION_EXPIRY_KEY = 'user_session_expiry';
const USER_PROFILE_KEY = 'user_profile';
const SESSION_DURATION_DAYS = 5;

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private baseUrl = environment.apiBaseUrl;
  private userProfileSubject = new BehaviorSubject<UserProfile | null>(null);
  public userProfile$ = this.userProfileSubject.asObservable();

  constructor(private http: HttpClient) {
    // Load user profile from localStorage on service initialization
    this.loadUserProfileFromStorage();
  }

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

  getUserProfile(email: string): Observable<UserProfile> {
    return this.http.get<ApiResponse<UserProfile>>(`${this.baseUrl}/user/profile?email=${email}`)
      .pipe(
        tap(response => {
          console.log('Profile API response:', response);
        }),
        map(response => response.data!),
        tap(profile => {
          console.log('Parsed profile:', profile);
          console.log('superUser:', profile.superUser);
          console.log('roles:', profile.roles);
          this.setUserProfile(profile);
        })
      );
  }

  setLoggedIn(email: string): void {
    const expiryTime = Date.now() + SESSION_DURATION_DAYS * 24 * 60 * 60 * 1000;
    localStorage.setItem(SESSION_EMAIL_KEY, email);
    localStorage.setItem(SESSION_EXPIRY_KEY, expiryTime.toString());
  }

  loadUserProfileAndRedirect(): Observable<string> {
    const email = this.getLoggedInEmail();
    if (!email) {
      return new Observable(observer => observer.next('/login'));
    }

    return this.getUserProfile(email).pipe(
      map(profile => {
        // Determine redirect route based on highest role
        if (profile.superUser) {
          return '/admin';
        } else {
          return '/dashboard';
        }
      })
    );
  }

  private setUserProfile(profile: UserProfile): void {
    localStorage.setItem(USER_PROFILE_KEY, JSON.stringify(profile));
    this.userProfileSubject.next(profile);
  }

  private loadUserProfileFromStorage(): void {
    const profileData = localStorage.getItem(USER_PROFILE_KEY);
    if (profileData) {
      try {
        const profile = JSON.parse(profileData);
        this.userProfileSubject.next(profile);
      } catch (e) {
        console.error('Error parsing user profile from storage:', e);
        localStorage.removeItem(USER_PROFILE_KEY);
      }
    }
  }

  getCurrentUserProfile(): UserProfile | null {
    return this.userProfileSubject.value;
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

  isSuperUser(): boolean {
    const profile = this.getCurrentUserProfile();
    return profile?.superUser === true;
  }

  hasRole(role: string): boolean {
    const profile = this.getCurrentUserProfile();
    return profile?.roles?.includes(role) === true;
  }

  logout(): void {
    localStorage.removeItem(SESSION_EMAIL_KEY);
    localStorage.removeItem(SESSION_EXPIRY_KEY);
    localStorage.removeItem(USER_PROFILE_KEY);
    this.userProfileSubject.next(null);
  }
}
