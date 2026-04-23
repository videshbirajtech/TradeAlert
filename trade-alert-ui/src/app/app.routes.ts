import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { SignupComponent } from './signup/signup.component';
import { LayoutComponent } from './layout/layout.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { CreateAlertComponent } from './create-alert/create-alert.component';
import { ProfileComponent } from './profile/profile.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'create-alert', component: CreateAlertComponent },
      { path: 'profile', component: ProfileComponent }
    ]
  },
  { path: '**', redirectTo: 'login' }
];
