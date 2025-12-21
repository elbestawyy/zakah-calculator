import {UserType} from '../enums/UserType';


export interface RegistrationRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  confirmPassword: string;
  userType?: UserType;
}
