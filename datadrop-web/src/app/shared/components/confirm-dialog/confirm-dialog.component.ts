import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AppState } from '../../../app-state';
import { Store } from '@ngrx/store';

@Component({
  selector: 'app-confirm-dialog',
  templateUrl: './confirm-dialog.component.html',
  styleUrls: ['./confirm-dialog.component.scss'],
})
export class ConfirmDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { dialogText: string; action: any },
    private store: Store<AppState>,
  ) {}

  onCancelClick() {
    this.dialogRef.close(false);
  }

  onConfirmClick() {
    this.store.dispatch(this.data.action);
    this.dialogRef.close(true);
  }
}
