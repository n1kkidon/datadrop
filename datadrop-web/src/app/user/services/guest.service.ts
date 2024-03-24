import {
  HttpClient,
  HttpErrorResponse,
  HttpResponse,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthenticationModel } from '../../shared/models/AuthenticationModel';
import { LoginRequest } from '../../shared/models/request/LoginRequest';

@Injectable({
  providedIn: 'root',
})
export class GuestService {
  constructor(private httpClient: HttpClient) {}

  login(credentials: LoginRequest) {
    return this.httpClient.post<AuthenticationModel>(
      '/guest/login',
      credentials,
      { observe: 'response', responseType: 'json' },
    );
  }

  register(email: string, username: string, password: string) {
    return this.httpClient.post(
      '/guest/register',
      { email, username, password },
      { observe: 'response', responseType: 'json' },
    );
  }

  checkIfNameAvailable(username: string) {
    return this.httpClient.post<boolean>(
      '/guest/username-available',
      username,
      { observe: 'response', responseType: 'json' },
    );
  }
}
