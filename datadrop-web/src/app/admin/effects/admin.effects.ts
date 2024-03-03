import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { AppState } from '../../app-state';
import { Store } from '@ngrx/store';
import * as AdminActions from '../actions/admin.actions';
import { of, switchMap } from 'rxjs';
import { AdminService } from '../services/admin.service';
import { showSnackbar } from '../../core/actions/core.actions';

@Injectable()
export class AdminEffects {
  constructor(
    private actions$: Actions,
    private store: Store<AppState>,
    private adminService: AdminService,
  ) {}

  changeUserAccountBlockState$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AdminActions.changeUserAccountBlockState),
      switchMap((action) => {
        return this.adminService.changeBlockStateOfUserAccount(action.id).pipe(
          switchMap((response) => {
            return [
              showSnackbar({
                message: `Status of user ${response.body?.name} set to ${response.body?.state}.`,
              }),
              AdminActions.loadUserList(),
            ];
          }),
        );
      }),
    ),
  );

  loadUserList$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AdminActions.loadUserList),
      switchMap(() => {
        return this.adminService.getAllUsers().pipe(
          switchMap((response) => {
            return of(AdminActions.userListLoaded({ data: response.body! }));
          }),
        );
      }),
    ),
  );

  deleteUserAccount$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AdminActions.deleteUserAccount),
      switchMap((action) => {
        return this.adminService.deleteUserAccountById(action.id).pipe(
          switchMap((response) => {
            return [
              showSnackbar({ message: 'User Deleted.' }),
              AdminActions.loadUserList(),
            ];
          }),
        );
      }),
    ),
  );
}
