import { createAction, props } from '@ngrx/store';
import { AuthenticationModel } from '../../shared/models/AuthenticationModel';
import { LoginRequest } from '../../shared/models/request/LoginRequest';

export const logout = createAction('[User] Logout');

export const login = createAction(
  '[User] Login',
  props<{ credentials: LoginRequest }>(),
);
export const reLogin = createAction('[User] ReLogin');

export const userLoggedIn = createAction(
  '[User] Logged In',
  props<{ auth: AuthenticationModel }>(),
);
