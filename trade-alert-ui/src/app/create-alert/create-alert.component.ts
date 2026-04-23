import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { PriceService, Coin } from '../price.service';
import { AlertService } from '../services/alert.service';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';

@Component({
  selector: 'app-create-alert',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './create-alert.component.html',
  styleUrl: './create-alert.component.css'
})
export class CreateAlertComponent implements OnInit {

  coins: Coin[] = [];
  selectedCoinId: number | null = null;
  alertPrice: number | null = null;

  loading = false;
  coinsLoading = false;

  constructor(
    private priceService: PriceService,
    private alertService: AlertService,
    private authService: AuthService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.loadCoins();
  }

  loadCoins(): void {
    this.coinsLoading = true;
    this.priceService.getCoins().subscribe({
      next: (data) => {
        this.coins = data;
        if (data.length > 0) this.selectedCoinId = data[0].id;
        this.coinsLoading = false;
      },
      error: () => {
        this.toast.error('Failed to load coins.');
        this.coinsLoading = false;
      }
    });
  }

  saveAlert(): void {
    if (!this.selectedCoinId || !this.alertPrice) return;

    const email = this.authService.getLoggedInEmail();
    if (!email) return;

    this.loading = true;

    this.alertService.createAlert({
      coinId: this.selectedCoinId,
      alertPrice: this.alertPrice,
      userEmail: email
    }).subscribe({
      next: () => {
        this.loading = false;
        this.toast.success('Alert created successfully!');
        this.alertPrice = null;
      },
      error: (err) => {
        this.loading = false;
        this.toast.error(err.error?.message || 'Failed to create alert.');
      }
    });
  }
}
