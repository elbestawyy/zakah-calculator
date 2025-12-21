import {UserResponse} from './UserResponse';

export interface AuthenticationResponse {
  accessToken: string;
  refreshToken: string;
  userResponse: UserResponse;
}
