import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

export interface Coin {
  id: number;
  coinName: string;
  symbol: string;
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
}
