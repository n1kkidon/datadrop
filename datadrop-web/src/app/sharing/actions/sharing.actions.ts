import { createAction, props } from '@ngrx/store';
import { UserDto } from '../../shared/models/UserDto';

export const loadUsersSharingFiles = createAction(
  '[Sharing] Load Users Sharing Files',
);

export const usersSharingFilesLoaded = createAction(
  '[Sharing] Users Sharing Files Loaded',
  props<{ data: UserDto[] }>(),
);
