import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { UserService } from '../services/user.service';
import * as UserActions from '../actions/user.actions';
import { map, of, switchMap, tap } from 'rxjs';
import { UtilsService } from '../../shared/services/utils.service';
import { GuestService } from '../services/guest.service';

@Injectable()
export class UserEffects {
  constructor(
    private router: Router,
    private actions$: Actions,
    private userService: UserService,
    private utilsService: UtilsService,
    private guestService: GuestService,
  ) {}

  logout$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(UserActions.logout),
        tap(() => {
          this.userService.logout();
          return this.router.navigate(['/login']);
        }),
      ),
    { dispatch: false },
  );

  reLogin = createEffect(() =>
    this.actions$.pipe(
      ofType(UserActions.reLogin),
      switchMap((action) => {
        return this.utilsService.reLogin().pipe(
          map((response) => {
            return UserActions.userLoggedIn({ auth: response.body! });
          }),
        );
      }),
    ),
  );

  login$ = createEffect(() =>
    this.actions$.pipe(
      ofType(UserActions.login),
      switchMap((action) => {
        return this.guestService.login(action.credentials).pipe(
          map((response) => {
            return UserActions.userLoggedIn({ auth: response.body! });
          }),
        );
      }),
    ),
  );

  userLoggedIn$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(UserActions.userLoggedIn),
        tap((action) => this.userService.userLoggedIn(action.auth)),
      ),
    { dispatch: false },
  );
}
