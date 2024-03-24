import {
  createFeatureSelector,
  createReducer,
  createSelector,
  on,
} from '@ngrx/store';
import { UserService } from '../services/user.service';
import * as UserActions from '../actions/user.actions';
import { AuthenticationModel } from '../../shared/models/AuthenticationModel';

export const userFeatureKey = 'user';

export interface UserState {
  auth: AuthenticationModel | null;
}

export const initialState: UserState = {
  auth: null,
};

export const reducer = createReducer(
  initialState,
  on(UserActions.userLoggedIn, (state, action) => ({
    ...state,
    auth: action.auth,
  })),
);

const getState = createFeatureSelector<UserState>(userFeatureKey);

export const selectUserAuth = createSelector(getState, (state) => state.auth);
