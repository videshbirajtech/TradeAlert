import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PriceService {

  private apiUrl = 'http://localhost:8080/api/price';

  constructor(private http: HttpClient) {}

  getPrice(symbol: string): Observable<{ symbol: string; price: string }> {
    return this.http.get<{ symbol: string; price: string }>(
      `${this.apiUrl}?symbol=${symbol}`
    );
  }
}
