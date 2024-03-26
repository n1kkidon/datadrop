import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { UserService } from '../services/user.service';
import * as UserActions from '../actions/user.actions';
import {EMPTY, map, of, switchMap, tap} from 'rxjs';
import { UtilsService } from '../../shared/services/utils.service';
import { GuestService } from '../services/guest.service';
import { loadSpaceUsageStats } from '../actions/user.actions';

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
          switchMap((response) => {
            return [
              UserActions.userLoggedIn({ auth: response.body! }),
            ];
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

  loadSpaceUsageStats$ = createEffect(() =>
    this.actions$.pipe(
      ofType(UserActions.loadSpaceUsageStats),
      switchMap((action) =>
        this.userService
          .getSpaceUsageStatsOfCurrentUser()
          .pipe(
            map((response) =>
              UserActions.spaceUsageStatsLoaded({ data: response.body! }),
            ),
          ),
      ),
    ),
  );

  onLoad$ = createEffect(() =>
    this.actions$.pipe(
      ofType(UserActions.onLoad),
      switchMap(() => {
        let authModel = this.userService.loginFromStorage();
        if(authModel)
          return [UserActions.userLoggedIn({auth: authModel})];
        else return EMPTY;
      })
    )
  );
}
