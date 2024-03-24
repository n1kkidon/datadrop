import * as SharingActions from '../actions/sharing.actions';
import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { map, switchMap } from 'rxjs';
import { UserService } from '../../user/services/user.service';

@Injectable()
export class SharingEffects {
  constructor(
    private actions$: Actions,
    private userService: UserService,
  ) {}

  loadUsersSharingFiles$ = createEffect(() =>
    this.actions$.pipe(
      ofType(SharingActions.loadUsersSharingFiles),
      switchMap((action) => {
        return this.userService.getUsersSharingFiles().pipe(
          map((response) => {
            return SharingActions.usersSharingFilesLoaded({
              data: response.body!,
            });
          }),
        );
      }),
    ),
  );
}
