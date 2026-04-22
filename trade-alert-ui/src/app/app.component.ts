import { Component } from '@angular/core';
import { PriceService } from './price.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  coins = ['BTC', 'ETH', 'BNB', 'SOL', 'XRP'];
  selectedCoin = 'BTC';
  priceData: { symbol: string; price: string } | null = null;
  errorMessage = '';
  loading = false;

  constructor(private priceService: PriceService) {}

  getPrice() {
    this.loading = true;
    this.priceData = null;
    this.errorMessage = '';

    this.priceService.getPrice(this.selectedCoin).subscribe({
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
