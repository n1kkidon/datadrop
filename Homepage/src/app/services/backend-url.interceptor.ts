import { HttpErrorResponse, HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, mergeMap, retry, switchMap, throwError, timer } from 'rxjs';
import { environment } from 'src/environments/environment';
import { ErrorResponse } from '../models/ErrorResponse';
import { UtilsService } from './utils.service';
import { Token } from '../models/Token';
import { jwtDecode } from 'jwt-decode';
import { NgxPermissionsService } from 'ngx-permissions';
import { Router } from '@angular/router';
import { ErrorModel } from '../models/ErrorModel';

@Injectable()
export class BackEndUrlInterceptor implements HttpInterceptor {

    constructor(private utilsService: UtilsService, private permissionsService: NgxPermissionsService, private router: Router){}
    headers?: HttpHeaders;
    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        let cloneRequest = this.injectJwtToRequest(request);

        return next.handle(cloneRequest).pipe(
        catchError((error: any) => {
            let errorBody: ErrorResponse = error.error;
            if(errorBody.path == null){
                let error1 = errorBody as unknown as ErrorModel;
                this.utilsService.openSnackBar(error1.message, "Close");
                return throwError(() => error);
            }
            if(errorBody == null || errorBody == undefined){
                this.utilsService.openSnackBar(error.message, "Close");
                return throwError(() => error);
            }
            if (errorBody.error === 'Invalid token') {
                if(!localStorage.getItem('rtoken')){
                    this.router.navigate(["/login"]);
                    return throwError(() => error);
                }
                return this.utilsService.reLogin().pipe(
                    switchMap(resp => {
                      console.log(resp);
            
                      if (resp.ok) {
                        localStorage.setItem('token', resp.body!.token);
                        localStorage.setItem('rtoken', resp.body!.refreshToken);
                        let token: Token = jwtDecode(resp.body!.token);
                        this.permissionsService.loadPermissions([token.roles[0].authority]);
                      } 
                      else {
                        localStorage.removeItem('token');
                        localStorage.removeItem('rtoken');
                        this.router.navigate(["/login"]);
                      }
                      return next.handle(this.addJwtToHeader(cloneRequest));
                    })
                  );
            } 
            else {
                this.utilsService.openSnackBar(errorBody.error, "Close");
                return throwError(() => error);
            }
          })
        );
    }

    addJwtToHeader(request: HttpRequest<any>){
        const jwtToken = localStorage.getItem('token');
        if(jwtToken == null || !jwtToken){
            this.router.navigate(['/login']);
        }
       
        this.headers = new HttpHeaders({
            Authorization: `Bearer ${jwtToken}`
        });
        const cloneRequest = request.clone({
            headers: this.headers
        });
        return cloneRequest;
            
    }

    injectJwtToRequest(request: HttpRequest<any>){
        if(!request.url.startsWith("/guest")){
            return this.addJwtToHeader(request).clone({
                headers: this.headers,
                url: environment.backendUrl + request.url
            });
        }
        else {
            const cloneRequest = request.clone({
                url: environment.backendUrl + request.url,
            });
            return cloneRequest;
        }
    }
}
