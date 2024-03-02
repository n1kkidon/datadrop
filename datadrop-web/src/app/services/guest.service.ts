import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of } from 'rxjs';
import { LoginResponse } from '../models/LoginResponse';
import { UtilsService } from './utils.service';

@Injectable({
  providedIn: 'root'
})
export class GuestService {

  constructor(private httpClient: HttpClient) { }

  login(username: string, password: string) : Observable<HttpResponse<LoginResponse> | HttpErrorResponse>{
    return this.httpClient.post<LoginResponse>('/guest/login', {username, password}, {observe: 'response', responseType: 'json'});
  }

  register(email: string, username: string, password: string){
    return this.httpClient.post('/guest/register', {email, username, password}, {observe: 'response', responseType: 'json'});
  }

  checkIfNameAvailable(username: string){
    return this.httpClient.post<boolean>('/guest/username-available', username, {observe: 'response', responseType: 'json'});
  }
}
