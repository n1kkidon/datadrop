import { createAction, props } from '@ngrx/store';
import { UserDto } from '../../shared/models/UserDto';
import { DirectoryInfo } from '../../shared/models/DirectoryInfo';

export const changeUserAccountBlockState = createAction(
  '[Admin] Change Account Block State',
  props<{ id: number }>(),
);

export const loadUserList = createAction('[Admin] Load User List');

export const userListLoaded = createAction(
  '[Admin] User List Loaded',
  props<{ data: UserDto[] }>(),
);

export const deleteUserAccount = createAction(
  '[Admin] Delete User Account',
  props<{ id: number }>(),
);

export const loadUserRootDirectory = createAction(
  '[Admin] Load User Root Directory',
  props<{ id: number }>(),
);

export const userRootDirectoryLoaded = createAction(
  '[Admin] User Root Directory Loaded',
  props<{ data: DirectoryInfo }>(),
);
