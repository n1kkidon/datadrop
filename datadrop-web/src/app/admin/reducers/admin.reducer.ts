import { UserDto } from '../../shared/models/UserDto';
import {
  createFeatureSelector,
  createReducer,
  createSelector,
  on,
} from '@ngrx/store';
import * as AdminActions from '../actions/admin.actions';
import { DirectoryDto } from '../../shared/models/DirectoryDto';

export const adminFeatureKey = 'admin';

export interface AdminState {
  usersList: UserDto[];
  userRootDirectory: DirectoryDto | null;
}

export const initialState: AdminState = {
  usersList: [],
  userRootDirectory: null,
};

export const reducer = createReducer(
  initialState,
  on(AdminActions.userListLoaded, (state, action) => ({
    ...state,
    usersList: action.data,
  })),
  on(AdminActions.userRootDirectoryLoaded, (state, action) => ({
    ...state,
    userRootDirectory: action.data,
  })),
);

const getAdminState = createFeatureSelector<AdminState>(adminFeatureKey);

export const selectUserList = createSelector(
  getAdminState,
  (state) => state.usersList,
);

export const selectUserRootDirectory = createSelector(
  getAdminState,
  (state) => state.userRootDirectory,
);
