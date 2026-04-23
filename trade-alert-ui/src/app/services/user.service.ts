import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface UserProfile {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  profilePhoto: string | null;
  createdDate: string;
}

@Injectable({ providedIn: 'root' })
export class UserService {

  private baseUrl = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  getProfile(email: string): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.baseUrl}/users/profile?email=${email}`);
  }

  updateProfile(email: string, data: { firstName: string; lastName: string; email: string }): Observable<UserProfile> {
    return this.http.put<UserProfile>(`${this.baseUrl}/users/profile?email=${email}`, data);
  }

  uploadPhoto(email: string, file: File): Observable<UserProfile> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<UserProfile>(`${this.baseUrl}/users/profile/photo?email=${email}`, formData);
  }
}
