import {UserDto} from "../getUsers/user.dto";

export interface DeleteUserResponse {
  result: UserDto[]
  returnMessage: string
}

export interface DeleteUserErrorResponse {
  error: string;
}
