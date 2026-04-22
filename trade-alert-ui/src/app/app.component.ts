import { Component, OnInit } from '@angular/core';
import { PriceService, Coin } from './price.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {

  coins: Coin[] = [];
  selectedSymbol = '';
  priceData: { symbol: string; price: string } | null = null;
  errorMessage = '';
  loading = false;
  coinsLoading = false;

  constructor(private priceService: PriceService) {}

  ngOnInit(): void {
    this.loadCoins();
  }

  loadCoins(): void {
    this.coinsLoading = true;
    this.priceService.getCoins().subscribe({
      next: (data) => {
        this.coins = data;
        if (data.length > 0) {
          this.selectedSymbol = data[0].symbol;
        }
        this.coinsLoading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load coins from server.';
        this.coinsLoading = false;
      }
    });
  }

  getPrice(): void {
    if (!this.selectedSymbol) return;

    this.loading = true;
    this.priceData = null;
    this.errorMessage = '';

    this.priceService.getPrice(this.selectedSymbol).subscribe({
      next: (data) => {
        this.priceData = data;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to fetch price. Please try again.';
        this.loading = false;
      }
    });
  }
}
