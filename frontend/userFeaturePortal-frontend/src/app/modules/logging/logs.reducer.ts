import {createReducer, on} from "@ngrx/store";
import {
  addLogResponseAction,
  deleteLogResponseAction,
  deleteLogsResponseAction,
  getLogsResponseAction
} from "./logs.actions";
import {LOGS_GET_INITIAL_STATE, LogsState} from "./logs.state";
import {GetLogsResponse} from "./getLogs/dto/get-logs-response";

const handleLogsResponse = (state: LogsState, resp: GetLogsResponse): LogsState => {
  return {
    ...state,
    logsList: resp.result,
  };
}

export const LogsReducer = createReducer(
  LOGS_GET_INITIAL_STATE,
  on(deleteLogsResponseAction, handleLogsResponse),
  on(getLogsResponseAction, handleLogsResponse),
  on(addLogResponseAction, handleLogsResponse),
  on(deleteLogResponseAction, handleLogsResponse)
)
