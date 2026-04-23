import { Component, OnInit, HostListener } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PriceService, Coin } from '../price.service';
import { AlertService, PriceAlert } from '../services/alert.service';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {

  // Price checker
  coins: Coin[] = [];
  selectedSymbol = '';
  priceData: { symbol: string; price: string } | null = null;
  priceError = '';
  priceLoading = false;
  coinsLoading = false;

  // Alerts table
  alerts: PriceAlert[] = [];
  alertsLoading = false;
  openMenuId: number | null = null;
  menuPosition = { top: 0, left: 0 };

  // Edit modal
  editModalOpen = false;
  editAlert: PriceAlert | null = null;
  editCoinId: number | null = null;
  editPrice: number | null = null;
  editLoading = false;
  editError = '';

  constructor(
    private priceService: PriceService,
    private alertService: AlertService,
    private authService: AuthService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.loadCoins();
    this.loadAlerts();
  }

  // ── Price checker ──────────────────────────────
  loadCoins(): void {
    this.coinsLoading = true;
    this.priceService.getCoins().subscribe({
      next: (data) => {
        this.coins = data;
        if (data.length > 0) this.selectedSymbol = data[0].symbol;
        this.coinsLoading = false;
      },
      error: () => { this.coinsLoading = false; }
    });
  }

  getPrice(): void {
    if (!this.selectedSymbol) return;
    this.priceLoading = true;
    this.priceData = null;
    this.priceError = '';
    this.priceService.getPrice(this.selectedSymbol).subscribe({
      next: (data) => { this.priceData = data; this.priceLoading = false; },
      error: (err) => {
        this.priceError = err.error?.message || 'Failed to fetch price.';
        this.priceLoading = false;
      }
    });
  }

  // ── Alerts table ───────────────────────────────
  loadAlerts(): void {
    const email = this.authService.getLoggedInEmail();
    if (!email) return;
    this.alertsLoading = true;
    this.alertService.getAlerts(email).subscribe({
      next: (data) => { this.alerts = data; this.alertsLoading = false; },
      error: () => { this.alertsLoading = false; }
    });
  }

  toggleMenu(id: number, event: Event): void {
    event.stopPropagation();
    if (this.openMenuId === id) {
      this.openMenuId = null;
      return;
    }
    const btn = event.target as HTMLElement;
    const rect = btn.getBoundingClientRect();
    this.menuPosition = {
      top: rect.bottom + 4,
      left: rect.right - 140
    };
    this.openMenuId = id;
  }

  @HostListener('document:click')
  closeMenu(): void {
    this.openMenuId = null;
  }

  // ── Edit ───────────────────────────────────────
  openEdit(alert: PriceAlert, event: Event): void {
    event.stopPropagation();
    this.openMenuId = null;
    this.editAlert = alert;
    this.editCoinId = alert.coinId;
    this.editPrice = alert.alertPrice;
    this.editError = '';
    this.editModalOpen = true;
  }

  saveEdit(): void {
    if (!this.editAlert || !this.editCoinId || !this.editPrice) return;
    const email = this.authService.getLoggedInEmail()!;
    this.editLoading = true;
    this.alertService.updateAlert(this.editAlert.id, {
      coinId: this.editCoinId,
      alertPrice: this.editPrice,
      userEmail: email
    }).subscribe({
      next: (updated) => {
        const idx = this.alerts.findIndex(a => a.id === updated.id);
        if (idx !== -1) this.alerts[idx] = updated;
        this.editLoading = false;
        this.editModalOpen = false;
        this.toast.success('Alert updated successfully!');
      },
      error: (err) => {
        this.editError = err.error?.message || 'Failed to update alert.';
        this.editLoading = false;
      }
    });
  }

  closeEdit(): void {
    this.editModalOpen = false;
    this.editAlert = null;
  }

  // ── Delete ─────────────────────────────────────
  deleteAlert(id: number, event: Event): void {
    event.stopPropagation();
    this.openMenuId = null;
    if (!confirm('Delete this alert?')) return;
    this.alertService.deleteAlert(id).subscribe({
      next: () => {
        this.alerts = this.alerts.filter(a => a.id !== id);
        this.toast.success('Alert deleted.');
      },
      error: () => { this.toast.error('Failed to delete alert.'); }
    });
  }
}
