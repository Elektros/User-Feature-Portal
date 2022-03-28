import {createAction, props} from "@ngrx/store";
import {GetLogsErrorResponse, GetLogsResponse} from "./getLogs/dto/get-logs-response";
import {DeleteLogsErrorResponse, DeleteLogsResponse} from "./deleteLogs/dto/delete-logs-response";
import {AddLogErrorResponse, AddLogResponse} from "./addLogs/dto/add-log-response";
import {AddLogRequest} from "./addLogs/dto/add-log-request";

export const getLogsAction = createAction('Get logs');
export const getLogsResponseAction = createAction('Get list of logs', props<GetLogsResponse>())
export const loadGetLogsErrorAction = createAction('Load Get logs failure', props<GetLogsErrorResponse>())

export const deleteLogsAction = createAction('Delete logs');
export const deleteLogsResponseAction = createAction('Get response if logs deleting succeed', props<DeleteLogsResponse>());
export const loadDeleteLogsErrorAction = createAction('Load Delete Logs failure', props<DeleteLogsErrorResponse>());

export const addLogAction = createAction('Add log', props<AddLogRequest>());
export const addLogResponseAction = createAction('Get response if log creation succeed', props<AddLogResponse>());
export const loadAddLogErrorAction = createAction('Load Add Log failure', props<AddLogErrorResponse>());
