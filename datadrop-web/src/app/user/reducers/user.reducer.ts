import {
  createFeatureSelector,
  createReducer,
  createSelector,
  on,
} from '@ngrx/store';
import { UserService } from '../services/user.service';
import * as UserActions from '../actions/user.actions';
import { AuthenticationModel } from '../../shared/models/AuthenticationModel';
import {SpaceUsageModel} from "../../shared/models/SpaceUsageModel";

export const userFeatureKey = 'user';

export interface UserState {
  auth: AuthenticationModel | null;
  spaceUsage: SpaceUsageModel | null;
}

export const initialState: UserState = {
  auth: null,
  spaceUsage: null
};

export const reducer = createReducer(
  initialState,
  on(UserActions.userLoggedIn, (state, action) => ({
    ...state,
    auth: action.auth,
  })),
  on(UserActions.spaceUsageStatsLoaded, (state, action) => ({
    ...state,
    spaceUsage: action.data
  }))
);

const getState = createFeatureSelector<UserState>(userFeatureKey);

export const selectUserAuth = createSelector(getState, (state) => state.auth);

export const selectUserSpaceUsage = createSelector(getState, (state) => state.spaceUsage);
