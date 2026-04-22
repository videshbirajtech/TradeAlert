import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Coin {
  id: number;
  coinName: string;
  symbol: string;
}

@Injectable({
  providedIn: 'root'
})
export class PriceService {

  private baseUrl = 'http://localhost:8080/api';

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
