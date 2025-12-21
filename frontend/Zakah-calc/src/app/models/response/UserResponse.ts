import {UserType} from '../enums/UserType';

export interface UserResponse {
  userId: number;     // Long -> number
  email: string;
  fullName: string;
  userType: UserType;
}
