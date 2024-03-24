import {
  HttpEvent,
  HttpHandler,
  HttpHeaders,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  catchError,
  filter,
  Observable,
  switchMap,
  take,
  throwError,
} from 'rxjs';
import { environment } from 'src/environments/environment';
import { ErrorResponse } from '../models/ErrorResponse';
import { AppState } from '../../app-state';
import { Store } from '@ngrx/store';
import * as UserActions from '../../user/actions/user.actions';
import * as CoreActions from '../../core/actions/core.actions';
import { selectUserAuth } from '../../user/reducers/user.reducer';

@Injectable()
export class BackEndUrlInterceptor implements HttpInterceptor {
  constructor(private store: Store<AppState>) {}
  headers?: HttpHeaders;
  intercept(
    request: HttpRequest<any>,
    next: HttpHandler,
  ): Observable<HttpEvent<any>> {
    let cloneRequest = this.injectJwtToRequest(request);

    return next.handle(cloneRequest).pipe(
      catchError((error: any) => {
        let errorBody: ErrorResponse = error.error;

        if (errorBody.error === 'Invalid token') {
          if (!localStorage.getItem('rtoken')) {
            this.store.dispatch(UserActions.logout());
            return throwError(() => error);
          }
          this.store.dispatch(UserActions.reLogin());

          return this.store.select(selectUserAuth).pipe(
            filter(
              (x) =>
                x !== null &&
                !cloneRequest.headers.get('Authorization')!.includes(x.token),
            ),
            take(1),
            switchMap((auth) => {
              return next.handle(
                this.addJwtToHeader(cloneRequest, auth!.token),
              );
            }),
          );
        } else if (errorBody.error === 'Your session has expired!') {
          this.store.dispatch(UserActions.logout());
          this.store.dispatch(
            CoreActions.showSnackbar({
              message: errorBody.error,
              action: 'Close',
            }),
          );
          return throwError(() => error);
        } else {
          this.store.dispatch(
            CoreActions.showSnackbar({
              message: errorBody.error,
              action: 'Close',
            }),
          );
          return throwError(() => error);
        }
      }),
    );
  }

  addJwtToHeader(request: HttpRequest<any>, jwtToken: string | null) {
    if (jwtToken === null || !jwtToken) {
      this.store.dispatch(UserActions.logout());
    }
    this.headers = new HttpHeaders({
      Authorization: `Bearer ${jwtToken}`,
    });

    return request.clone({
      headers: this.headers,
    });
  }

  injectJwtToRequest(request: HttpRequest<any>) {
    if (!request.url.startsWith('/guest')) {
      const jwtToken = localStorage.getItem('token');
      return this.addJwtToHeader(request, jwtToken).clone({
        headers: this.headers,
        url: environment.backendUrl + request.url,
      });
    } else {
      return request.clone({
        url: environment.backendUrl + request.url,
      });
    }
  }
}
