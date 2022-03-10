import {Injectable} from "@angular/core";
import {Observable, of} from "rxjs";
import {Action} from "@ngrx/store";
import {Actions, createEffect, ofType} from "@ngrx/effects";
import {map, switchMap} from "rxjs/internal/operators";
import {catchError} from "rxjs/operators";
import {
  deleteLogsAction,
  deleteLogsResponseAction,
  getLogsAction,
  getLogsResponseAction,
  loadDeleteLogsErrorAction,
  loadGetLogsErrorAction
} from "./logs.actions";
import {LogService} from "./logs.service";


@Injectable({providedIn: 'root'})
export class LogEffects {
  get$: Observable<Action> = createEffect(() =>
    this.actions$.pipe(
      ofType(getLogsAction),
      switchMap(() =>
        this.logService.getLogs().pipe(
          map((getLogsResponse) => getLogsResponseAction(getLogsResponse)),
          catchError((error: string) => of(loadGetLogsErrorAction({error})))
        )
      )
    )
  );

  delete$: Observable<Action> = createEffect(() =>
    this.actions$.pipe(
      ofType(deleteLogsAction),
      switchMap(() =>
        this.logService.deleteLogs().pipe(
          map((deleteLogsResponse) => deleteLogsResponseAction(deleteLogsResponse)),
          catchError((error: string) => of(loadDeleteLogsErrorAction({ error })))
        )
      )
    )
  );

  constructor(private readonly actions$: Actions, private readonly logService: LogService) {
  }
}