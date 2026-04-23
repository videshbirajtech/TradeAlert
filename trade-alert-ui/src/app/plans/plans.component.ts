import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';
import { SubscriptionService, SubscriptionDTO } from '../services/subscription.service';
import { ToastService } from '../services/toast.service';

interface Plan {
  key: string;
  label: string;
  price: number;
  period: string;
  duration: string;
  features: string[];
  highlighted: boolean;
}

@Component({
  selector: 'app-plans',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './plans.component.html',
  styleUrl: './plans.component.css'
})
export class PlansComponent implements OnInit {

  plans: Plan[] = [
    {
      key: 'MONTHLY',
      label: 'Monthly',
      price: 10,
      period: '/month',
      duration: '1 month',
      features: ['Real-time price alerts', 'Up to 10 alerts', 'Email notifications'],
      highlighted: false
    },
    {
      key: 'QUARTERLY',
      label: 'Quarterly',
      price: 27,
      period: '/quarter',
      duration: '3 months',
      features: ['Real-time price alerts', 'Up to 30 alerts', 'Email notifications', 'Save $3 vs monthly'],
      highlighted: true
    },
    {
      key: 'YEARLY',
      label: 'Yearly',
      price: 100,
      period: '/year',
      duration: '12 months',
      features: ['Real-time price alerts', 'Unlimited alerts', 'Email notifications', 'Save $20 vs monthly'],
      highlighted: false
    }
  ];

  activeSubscription: SubscriptionDTO | null = null;
  history: SubscriptionDTO[] = [];
  loadingPlan: string | null = null;
  historyLoading = false;

  constructor(
    private authService: AuthService,
    private subscriptionService: SubscriptionService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.loadActive();
    this.loadHistory();
  }

  loadActive(): void {
    const email = this.authService.getLoggedInEmail();
    if (!email) return;
    this.subscriptionService.getActive(email).subscribe({
      next: (data) => this.activeSubscription = data,
      error: () => this.activeSubscription = null
    });
  }

  loadHistory(): void {
    const email = this.authService.getLoggedInEmail();
    if (!email) return;
    this.historyLoading = true;
    this.subscriptionService.getAll(email).subscribe({
      next: (data) => { this.history = data; this.historyLoading = false; },
      error: () => { this.historyLoading = false; }
    });
  }

  purchase(planKey: string): void {
    const email = this.authService.getLoggedInEmail();
    if (!email) return;

    this.loadingPlan = planKey;
    this.subscriptionService.purchase(email, planKey).subscribe({
      next: (data) => {
        this.loadingPlan = null;
        this.activeSubscription = data;
        this.loadHistory();
        this.toast.success(`${planKey.charAt(0) + planKey.slice(1).toLowerCase()} plan activated!`);
      },
      error: (err) => {
        this.loadingPlan = null;
        this.toast.error(err.error?.message || 'Purchase failed. Please try again.');
      }
    });
  }

  isCurrentPlan(planKey: string): boolean {
    return this.activeSubscription?.planType === planKey && this.activeSubscription?.active === true;
  }

  getDaysLeft(): number {
    if (!this.activeSubscription) return 0;
    const end = new Date(this.activeSubscription.endDate);
    const now = new Date();
    return Math.max(0, Math.ceil((end.getTime() - now.getTime()) / (1000 * 60 * 60 * 24)));
  }
}
