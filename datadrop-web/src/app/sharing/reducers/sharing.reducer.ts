import { UserDto } from '../../shared/models/UserDto';
import {
  createFeatureSelector,
  createReducer,
  createSelector,
  on,
} from '@ngrx/store';
import * as SharingActions from '../actions/sharing.actions';

export const sharingFeatureKey = 'sharing';

export interface SharingState {
  userList: UserDto[];
}

export const initialState: SharingState = {
  userList: [],
};

export const reducer = createReducer(
  initialState,
  on(SharingActions.usersSharingFilesLoaded, (state, action) => ({
    ...state,
    userList: action.data,
  })),
);

export const getSharingState =
  createFeatureSelector<SharingState>(sharingFeatureKey);

export const selectSharingUserList = createSelector(
  getSharingState,
  (state) => state.userList,
);
