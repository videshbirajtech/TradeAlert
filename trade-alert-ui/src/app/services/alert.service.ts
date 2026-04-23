import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface PriceAlert {
  id: number;
  coinId: number;
  coinName: string;
  symbol: string;
  alertPrice: number;
  createdAt: string;
  updatedAt: string;
}

export interface PriceAlertRequest {
  coinId: number;
  alertPrice: number;
  userEmail: string;
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

@Injectable({ providedIn: 'root' })
export class AlertService {

  private baseUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  getAlerts(email: string): Observable<PriceAlert[]> {
    return this.http.get<ApiResponse<PriceAlert[]>>(`${this.baseUrl}/alerts?email=${email}`)
      .pipe(map(response => response.data || []));
  }

  getAlertDashboard(email: string): Observable<AlertDashboard> {
    return this.http.get<ApiResponse<AlertDashboard>>(`${this.baseUrl}/alerts/dashboard?email=${email}`)
      .pipe(
        map(response => {
          const data = response.data;
          return {
            alerts: data?.alerts || [],
            alertStats: {
              userId: data?.alertStats?.userId || 0,
              subscriptionId: data?.alertStats?.subscriptionId || null,
              subscriptionActive: data?.alertStats?.subscriptionActive === true,
              totalAlertCount: data?.alertStats?.totalAlertCount || 0,
              remainingAlertCount: data?.alertStats?.remainingAlertCount || 0,
              maxAlertsAllowed: data?.alertStats?.maxAlertsAllowed || 0,
              planType: data?.alertStats?.planType || null
            }
          };
        })
      );
  }

  createAlert(request: PriceAlertRequest): Observable<PriceAlert> {
    return this.http.post<ApiResponse<PriceAlert>>(`${this.baseUrl}/alerts`, request)
      .pipe(map(response => response.data));
  }

  updateAlert(id: number, request: PriceAlertRequest): Observable<PriceAlert> {
    return this.http.put<ApiResponse<PriceAlert>>(`${this.baseUrl}/alerts/${id}`, request)
      .pipe(map(response => response.data));
  }

  deleteAlert(id: number): Observable<void> {
    return this.http.delete<ApiResponse<string>>(`${this.baseUrl}/alerts/${id}`)
      .pipe(map(() => void 0));
  }
}
