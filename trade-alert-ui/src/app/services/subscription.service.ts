import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface SubscriptionDTO {
  id: number;
  subscriptionId: string;
  planType: string;
  price: number;
  startDate: string;
  endDate: string;
  createdAt: string;
  active: boolean;
}

@Injectable({ providedIn: 'root' })
export class SubscriptionService {

  private baseUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  purchase(email: string, planType: string): Observable<SubscriptionDTO> {
    return this.http.post<SubscriptionDTO>(`${this.baseUrl}/subscriptions/purchase`, { email, planType });
  }

  getAll(email: string): Observable<SubscriptionDTO[]> {
    return this.http.get<SubscriptionDTO[]>(`${this.baseUrl}/subscriptions?email=${email}`);
  }

  getActive(email: string): Observable<SubscriptionDTO> {
    return this.http.get<SubscriptionDTO>(`${this.baseUrl}/subscriptions/active?email=${email}`);
  }
}
