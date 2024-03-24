import { createAction, props } from '@ngrx/store';
import { ErrorResponse } from '../../shared/models/ErrorResponse';

export const showSnackbar = createAction(
  '[Core] Show Snackbar',
  props<{
    message: string;
    action?: string;
    duration?: number;
  }>(),
);

export const openConfirmDialog = createAction(
  '[Core] Open Confirm Dialog',
  props<{
    dialogText: string;
    action: any;
  }>(),
);
