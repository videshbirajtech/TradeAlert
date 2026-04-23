import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

export interface Coin {
  id: number;
  coinName: string;
  symbol: string;
}

export interface PriceAlert {
  id: number;
  coinId: number;
  coinName: string;
  symbol: string;
  alertPrice: number;
  createdAt: string;
  updatedAt: string;
}

export interface UserAlertStats {
  userId: number;
  subscriptionId: string | null;
  subscriptionActive: boolean;
  totalAlertCount: number;
  remainingAlertCount: number;
  maxAlertsAllowed: number;
  planType: string | null;
}

export interface AlertDashboard {
  alerts: PriceAlert[];
  alertStats: UserAlertStats;
}

export interface ApiResponse<T> {
  message: string;
  data: T;
  success: boolean;
}

export interface CreateAlertRequest {
  userEmail: string;
  coinId: number;
  alertPrice: number;
}

@Injectable({
  providedIn: 'root'
})
export class PriceService {

  private baseUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  getCoins(): Observable<Coin[]> {
    return this.http.get<Coin[]>(`${this.baseUrl}/coins`);
  }

  getPrice(symbol: string): Observable<{ symbol: string; price: string }> {
    return this.http.get<{ symbol: string; price: string }>(
      `${this.baseUrl}/price?symbol=${symbol}`
    );
  }

  // Alert Management Methods
  createAlert(request: CreateAlertRequest): Observable<ApiResponse<PriceAlert>> {
    return this.http.post<ApiResponse<PriceAlert>>(`${this.baseUrl}/alerts`, request);
  }

  getAlerts(email: string): Observable<ApiResponse<PriceAlert[]>> {
    return this.http.get<ApiResponse<PriceAlert[]>>(`${this.baseUrl}/alerts?email=${email}`);
  }

  getAlertDashboard(email: string): Observable<ApiResponse<AlertDashboard>> {
    return this.http.get<ApiResponse<AlertDashboard>>(`${this.baseUrl}/alerts/dashboard?email=${email}`);
  }

  updateAlert(id: number, request: CreateAlertRequest): Observable<ApiResponse<PriceAlert>> {
    return this.http.put<ApiResponse<PriceAlert>>(`${this.baseUrl}/alerts/${id}`, request);
  }

  deleteAlert(id: number): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(`${this.baseUrl}/alerts/${id}`);
  }

  // Alert Stats Methods
  getUserAlertStats(userId: number): Observable<ApiResponse<UserAlertStats>> {
    return this.http.get<ApiResponse<UserAlertStats>>(`${this.baseUrl}/user-alert-stats/user/${userId}`);
  }

  getUserAlertStatsByEmail(email: string): Observable<ApiResponse<UserAlertStats>> {
    return this.http.get<ApiResponse<UserAlertStats>>(`${this.baseUrl}/user-alert-stats/email/${email}`);
  }

  canCreateAlert(userId: number): Observable<ApiResponse<boolean>> {
    return this.http.get<ApiResponse<boolean>>(`${this.baseUrl}/user-alert-stats/can-create-alert/${userId}`);
  }

  refreshUserAlertStats(userId: number): Observable<ApiResponse<UserAlertStats>> {
    return this.http.post<ApiResponse<UserAlertStats>>(`${this.baseUrl}/user-alert-stats/refresh/${userId}`, {});
  }
}
