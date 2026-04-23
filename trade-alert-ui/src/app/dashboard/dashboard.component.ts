import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Subscription, interval, switchMap, forkJoin } from 'rxjs';
import { PriceService, Coin } from '../price.service';
import { AlertService, PriceAlert, AlertDashboard, UserAlertStats } from '../services/alert.service';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';

export interface LivePrice {
  symbol: string;
  coinName: string;
  price: string;
  loading: boolean;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit, OnDestroy {

  // Price checker
  coins: Coin[] = [];
  selectedSymbol = '';
  priceData: { symbol: string; price: string } | null = null;
  priceError = '';
  priceLoading = false;
  coinsLoading = false;

  // Dashboard data
  dashboard: AlertDashboard | null = null;
  alerts: PriceAlert[] = [];
  alertStats: UserAlertStats | null = null;
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

  // Live prices
  livePrices: LivePrice[] = [];
  private pollSub: Subscription | null = null;

  constructor(
    private priceService: PriceService,
    private alertService: AlertService,
    private authService: AuthService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.loadCoins();
    this.loadDashboard();
  }

  ngOnDestroy(): void {
    this.pollSub?.unsubscribe();
  }

  // ── Price checker ──────────────────────────────
  loadCoins(): void {
    this.coinsLoading = true;
    this.priceService.getCoins().subscribe({
      next: (data) => {
        this.coins = data;
        if (data.length > 0) {
          this.selectedSymbol = data[0].symbol;
          this.setupLivePrices(); // start polling as soon as coins load
        }
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

  // ── Dashboard data ───────────────────────────────
  loadDashboard(): void {
    const email = this.authService.getLoggedInEmail();
    if (!email) return;
    
    this.alertsLoading = true;
    this.alertService.getAlertDashboard(email).subscribe({
      next: (data) => {
        console.log('Dashboard data received:', data);
        this.dashboard = data;
        this.alerts = Array.isArray(data.alerts) ? data.alerts : [];
        this.alertStats = this.validateAlertStats(data.alertStats);
        this.alertsLoading = false;
        // Setup live prices after both coins and alerts are loaded
        if (this.coins.length > 0) this.setupLivePrices();
      },
      error: (err) => {
        console.error('Dashboard error:', err);
        this.alertsLoading = false;
        this.toast.error('Failed to load dashboard data');
      }
    });
  }

  // ── Type validation helper ────────────────────
  private validateAlertStats(stats: UserAlertStats | null | undefined): UserAlertStats | null {
    if (!stats) return null;
    
    return {
      userId: stats.userId || 0,
      subscriptionId: stats.subscriptionId || null,
      subscriptionActive: stats.subscriptionActive === true,
      totalAlertCount: stats.totalAlertCount || 0,
      remainingAlertCount: stats.remainingAlertCount || 0,
      maxAlertsAllowed: stats.maxAlertsAllowed || 0,
      planType: stats.planType || null
    };
  }

  // ── Alert statistics helpers ────────────────────
  canCreateAlert(): boolean {
    return !!(this.alertStats?.subscriptionActive && this.alertStats?.remainingAlertCount > 0);
  }

  getPlanDisplayName(): string {
    if (!this.alertStats?.planType) return 'No Plan';
    switch (this.alertStats.planType) {
      case 'MONTHLY': return 'Monthly Plan';
      case 'QUARTERLY': return 'Quarterly Plan';
      case 'YEARLY': return 'Yearly Plan';
      default: return 'Unknown Plan';
    }
  }

  getAlertUsageText(): string {
    if (!this.alertStats) return '';
    if (!this.alertStats.subscriptionActive) {
      return 'No active subscription - 0 alerts allowed';
    }
    return `${this.alertStats.totalAlertCount || 0}/${this.alertStats.maxAlertsAllowed || 0} alerts used (${this.alertStats.remainingAlertCount || 0} remaining)`;
  }

  getUsagePercentage(): number {
    if (!this.alertStats || !this.alertStats.maxAlertsAllowed || this.alertStats.maxAlertsAllowed === 0) return 0;
    return ((this.alertStats.totalAlertCount || 0) / this.alertStats.maxAlertsAllowed) * 100;
  }

  // ── Live prices ────────────────────────────────
  setupLivePrices(): void {
    // Always show all coins — regardless of whether user has alerts
    this.livePrices = this.coins.map(c => ({
      symbol: c.symbol,
      coinName: c.coinName,
      price: '—',
      loading: true
    }));

    // Stop any existing poll
    this.pollSub?.unsubscribe();

    // Fetch immediately
    this.fetchAllPrices();

    // Then poll every 5 seconds
    this.pollSub = interval(5000).pipe(
      switchMap(() => forkJoin(
        this.livePrices.map(lp => this.priceService.getPrice(lp.symbol))
      ))
    ).subscribe({
      next: (results) => {
        results.forEach((res, i) => {
          this.livePrices[i].price = res.price;
          this.livePrices[i].loading = false;
        });
      }
    });
  }

  private fetchAllPrices(): void {
    forkJoin(this.livePrices.map(lp => this.priceService.getPrice(lp.symbol)))
      .subscribe({
        next: (results) => {
          results.forEach((res, i) => {
            this.livePrices[i].price = res.price;
            this.livePrices[i].loading = false;
          });
        }
      });
  }

  toggleMenu(id: number, event: Event): void {
    event.stopPropagation();
    if (this.openMenuId === id) { this.openMenuId = null; return; }
    const btn = event.target as HTMLElement;
    const rect = btn.getBoundingClientRect();
    this.menuPosition = { top: rect.bottom + 4, left: rect.right - 140 };
    this.openMenuId = id;
  }

  @HostListener('document:click')
  closeMenu(): void { this.openMenuId = null; }

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
        this.loadDashboard(); // Reload to get updated stats
      },
      error: (err) => {
        this.editError = err.error?.message || 'Failed to update alert.';
        this.editLoading = false;
      }
    });
  }

  closeEdit(): void { this.editModalOpen = false; this.editAlert = null; }

  // ── Delete ─────────────────────────────────────
  deleteAlert(id: number, event: Event): void {
    event.stopPropagation();
    this.openMenuId = null;
    if (!confirm('Delete this alert?')) return;
    this.alertService.deleteAlert(id).subscribe({
      next: () => {
        this.alerts = this.alerts.filter(a => a.id !== id);
        this.toast.success('Alert deleted.');
        this.loadDashboard(); // Reload to get updated stats
      },
      error: () => { this.toast.error('Failed to delete alert.'); }
    });
  }
}
