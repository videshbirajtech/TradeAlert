import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
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

@Injectable({ providedIn: 'root' })
export class AlertService {

  private baseUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  getAlerts(email: string): Observable<PriceAlert[]> {
    return this.http.get<PriceAlert[]>(`${this.baseUrl}/alerts?email=${email}`);
  }

  createAlert(request: PriceAlertRequest): Observable<PriceAlert> {
    return this.http.post<PriceAlert>(`${this.baseUrl}/alerts`, request);
  }

  updateAlert(id: number, request: PriceAlertRequest): Observable<PriceAlert> {
    return this.http.put<PriceAlert>(`${this.baseUrl}/alerts/${id}`, request);
  }

  deleteAlert(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/alerts/${id}`);
  }
}
