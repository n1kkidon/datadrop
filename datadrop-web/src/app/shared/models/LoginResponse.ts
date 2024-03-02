import { RefreshToken } from './RefreshToken';
import { Token } from './Token';

export interface LoginResponse {
  token: string;
  refreshToken: string;
}
