import { openConfirmDialog, showSnackbar } from '../actions/core.actions';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Injectable } from '@angular/core';
import { tap } from 'rxjs';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog.component';

@Injectable()
export class CoreEffects {
  constructor(
    private actions$: Actions,
    private snackbar: MatSnackBar,
    private dialog: MatDialog,
  ) {}

  showSnackbar$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(showSnackbar),
        tap((action) => {
          this.snackbar.open(action.message, action.action ?? '', {
            duration: action.duration ?? 4000,
          });
        }),
      ),
    { dispatch: false },
  );

  openConfirmDialog$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(openConfirmDialog),
        tap((action) => {
          this.dialog.open(ConfirmDialogComponent, {
            data: {
              dialogText: action.dialogText,
              action: action.action,
            },
            panelClass: 'confirm-dialog',
          });
        }),
      ),
    { dispatch: false },
  );
}
