import { Injectable, signal } from '@angular/core';

export interface Toast {
  id: number;
  message: string;
  type: 'success' | 'error';
}

@Injectable({ providedIn: 'root' })
export class ToastService {

  toasts = signal<Toast[]>([]);
  private counter = 0;

  success(message: string): void {
    this.show(message, 'success');
  }

  error(message: string): void {
    this.show(message, 'error');
  }

  private show(message: string, type: 'success' | 'error'): void {
    const id = ++this.counter;
    this.toasts.update(t => [...t, { id, message, type }]);
    setTimeout(() => this.remove(id), 3000);
  }

  remove(id: number): void {
    this.toasts.update(t => t.filter(toast => toast.id !== id));
  }
}
