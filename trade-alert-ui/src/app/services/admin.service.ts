import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface AdminUser {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  verified: boolean;
  subscriptionId: string | null;
  createdDate: string;
  roles: string[];
  totalAlerts: number;
  hasActiveSubscription: boolean;
  currentPlan: string;
}

export interface ApiResponse<T = any> {
  success: boolean;
  message: string;
  data?: T;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private baseUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  getAllUsers(adminEmail: string): Observable<AdminUser[]> {
    return this.http.get<ApiResponse<AdminUser[]>>(`${this.baseUrl}/super-admin/users?adminEmail=${adminEmail}`)
      .pipe(map(response => response.data || []));
  }

  assignRole(adminEmail: string, userEmail: string, role: string): Observable<string> {
    return this.http.post<ApiResponse<string>>(`${this.baseUrl}/super-admin/assign-role`, null, {
      params: { adminEmail, userEmail, role }
    }).pipe(map(response => response.message));
  }

  removeRole(adminEmail: string, userEmail: string, role: string): Observable<string> {
    return this.http.post<ApiResponse<string>>(`${this.baseUrl}/super-admin/remove-role`, null, {
      params: { adminEmail, userEmail, role }
    }).pipe(map(response => response.message));
  }
}