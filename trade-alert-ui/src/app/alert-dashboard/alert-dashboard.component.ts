import { Component, OnInit } from '@angular/core';
import { PriceService, AlertDashboard, UserAlertStats, PriceAlert, CreateAlertRequest } from '../price.service';

@Component({
  selector: 'app-alert-dashboard',
  templateUrl: './alert-dashboard.component.html',
  styleUrls: ['./alert-dashboard.component.css']
})
export class AlertDashboardComponent implements OnInit {

  dashboard: AlertDashboard | null = null;
  loading = false;
  error: string | null = null;
  userEmail = 'user@example.com'; // This should come from authentication service

  // Form data for creating new alert
  newAlert = {
    coinId: 0,
    alertPrice: 0
  };

  constructor(private priceService: PriceService) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.loading = true;
    this.error = null;

    this.priceService.getAlertDashboard(this.userEmail).subscribe({
      next: (response) => {
        if (response.success) {
          this.dashboard = response.data;
        } else {
          this.error = response.message;
        }
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load dashboard data';
        this.loading = false;
        console.error('Dashboard error:', err);
      }
    });
  }

  createAlert(): void {
    if (!this.canCreateAlert()) {
      this.error = 'You have reached your alert limit. Please upgrade your subscription.';
      return;
    }

    const request: CreateAlertRequest = {
      userEmail: this.userEmail,
      coinId: this.newAlert.coinId,
      alertPrice: this.newAlert.alertPrice
    };

    this.priceService.createAlert(request).subscribe({
      next: (response) => {
        if (response.success) {
          this.loadDashboard(); // Refresh dashboard
          this.resetForm();
        } else {
          this.error = response.message;
        }
      },
      error: (err) => {
        this.error = 'Failed to create alert';
        console.error('Create alert error:', err);
      }
    });
  }

  deleteAlert(alertId: number): void {
    this.priceService.deleteAlert(alertId).subscribe({
      next: (response) => {
        if (response.success) {
          this.loadDashboard(); // Refresh dashboard
        } else {
          this.error = response.message;
        }
      },
      error: (err) => {
        this.error = 'Failed to delete alert';
        console.error('Delete alert error:', err);
      }
    });
  }

  canCreateAlert(): boolean {
    return this.dashboard?.alertStats?.subscriptionActive && 
           this.dashboard?.alertStats?.remainingAlertCount > 0;
  }

  getPlanDisplayName(planType: string): string {
    switch (planType) {
      case 'MONTHLY': return 'Monthly Plan';
      case 'QUARTERLY': return 'Quarterly Plan';
      case 'YEARLY': return 'Yearly Plan';
      default: return 'No Plan';
    }
  }

  getAlertLimitText(): string {
    if (!this.dashboard?.alertStats) return '';
    
    const stats = this.dashboard.alertStats;
    if (!stats.subscriptionActive) {
      return 'No active subscription - 0 alerts allowed';
    }
    
    return `${stats.totalAlertCount}/${stats.maxAlertsAllowed} alerts used (${stats.remainingAlertCount} remaining)`;
  }

  private resetForm(): void {
    this.newAlert = {
      coinId: 0,
      alertPrice: 0
    };
  }
}