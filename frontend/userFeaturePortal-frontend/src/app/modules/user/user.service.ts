import {HttpClient} from '@angular/common/http';
import {Injectable, OnDestroy} from '@angular/core';
import {Observable, Subject, throwError} from 'rxjs';
import {GetUsersResponse} from 'src/app/modules/user/getUsers/get-users-response';
import {catchError, map, takeUntil} from "rxjs/operators";
import {AddUserResponse} from "./addUser/add-user-response";
import {AddUserRequest} from "./addUser/add-user-request";
import {DeleteUserResponse} from "./deleteUser/delete-user-response";
import {ActorFacade} from "../actor/actor.facade";
import {DeleteUsersResponse} from "./deleteUsers/delete-users-response";
import {FeatureManager} from "../../../assets/utils/feature.manager";

const API_GET_USERS = 'http://localhost:8081/users';
const API_GET_USER = 'http://localhost:8081/user';
const API_ADD_USER = 'http://localhost:8081/user';
const API_DELETE_USERS = 'http://localhost:8081/users';
const API_DELETE_USER = 'http://localhost:8081/user/name/';

@Injectable({
  providedIn: 'root'
})
export class UserService implements OnDestroy {
  constructor(private readonly http: HttpClient, private readonly actorFacade: ActorFacade, private featureManager: FeatureManager) {
  }

  ngOnDestroy() {
    this.onDestroy.next(null)
    this.onDestroy.complete()
  }

  name: string | undefined
  onDestroy = new Subject()

  getUsers(): Observable<GetUsersResponse> {
    return this.http.get<GetUsersResponse>(API_GET_USERS, {
      observe: 'response'
    }).pipe(
      map((r) => {
        return r.body || {
          result: [],
          returnMessage: ""
        }
      }),
      catchError((err) => {
        if(err.error instanceof Object) {
          this.featureManager.openSnackbar(err.error.text);
        } else {
          this.featureManager.openSnackbar(err.error);
        }
        return throwError('Due to technical issues it is currently not possible to request users.');
      })
    );
  }

  addUser(addUserRequest: AddUserRequest): Observable<AddUserResponse> {
    this.actorFacade.stateActor$.pipe(takeUntil(this.onDestroy)).subscribe(r => {
      this.name = r
    })
    return this.http.post<AddUserResponse>(API_ADD_USER, {...addUserRequest, actor: this.name}, {
      observe: 'response'
    }).pipe(
      map((r) => {
        this.featureManager.openSnackbar(r.body?.returnMessage);
        return r.body || {
          result: [],
          returnMessage: ""
        }
      }),
      catchError((err) => {
        if(err.error instanceof Object) {
          this.featureManager.openSnackbar(err.error.text);
        } else {
          this.featureManager.openSnackbar(err.error);
        }
        return throwError('Due to technical issues it is currently not possible to add users.');
      })
    );
  }

  deleteUsers(): Observable<DeleteUsersResponse> {
    return this.http.delete<DeleteUsersResponse>(API_DELETE_USERS, {
      observe: 'response'
    }).pipe(
      map((r) => {
        this.featureManager.openSnackbar(r.body?.returnMessage);
        return r.body || {
          result: [],
          returnMessage: ""
        }
      }),
      catchError((err) => {
        if(err.error instanceof Object) {
          this.featureManager.openSnackbar(err.error.text);
        } else {
          this.featureManager.openSnackbar(err.error);
        }
        return throwError('Due to technical issues it is currently not possible to delete users.');
      })
    );
  }

  deleteUser(i: string | undefined): Observable<DeleteUserResponse> {
    this.actorFacade.stateActor$.pipe(takeUntil(this.onDestroy)).subscribe(r => {
      this.name = r
    })
    return this.http.delete<DeleteUserResponse>(API_DELETE_USER + i + "?actor=" + this.name, {
      observe: 'response'
    }).pipe(
      map((r) => {
        this.featureManager.openSnackbar("User named " + i + " was deleted.");
        return r.body || {
          result: [],
          returnMessage: ""
        }
      }),
      catchError((err) => {
        if(err.error instanceof Object) {
          this.featureManager.openSnackbar(err.error.text);
        } else {
          this.featureManager.openSnackbar(err.error);
        }
        return throwError('Due to technical issues it is currently not possible to delete this user.');
      })
    );
  }
}
