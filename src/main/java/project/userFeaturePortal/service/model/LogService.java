package project.userFeaturePortal.service.model;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import project.userFeaturePortal.common.dto.log.LogDTO;
import project.userFeaturePortal.common.dto.log.LogMessageDto;
import project.userFeaturePortal.common.dto.log.LogRequestDto;
import project.userFeaturePortal.common.message.ErrorMessages;
import project.userFeaturePortal.common.message.InfoMessages;
import project.userFeaturePortal.model.entity.Log;
import project.userFeaturePortal.model.entity.User;
import project.userFeaturePortal.model.mapper.LogDTOMapper;
import project.userFeaturePortal.model.repository.LogRepository;
import project.userFeaturePortal.model.repository.UserRepository;
import project.userFeaturePortal.service.validation.LogValidationService;
import project.userFeaturePortal.service.validation.UserValidationService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author - EugenFriesen 12.02.2021
 */
@Transactional
@Service
@RequiredArgsConstructor
public class LogService {

  private static final Logger LOGGER = LogManager.getLogger(LogService.class);

  private final LogRepository logRepository;
  private final LogValidationService logValidationService;
  private final LogDTOMapper logDTOMapper;
  private final UserRepository userRepository;
  private final UserValidationService userValidationService;

  public List<LogDTO> getLogs(String severity, String message, LocalDateTime startDate, LocalDateTime endDate, String userName) {
    User user = userRepository.findUserByName(userName);
    return logDTOMapper.logsToLogDTOs(
        logRepository.findLogs(severity, message, startDate, endDate, user));
  }

  public String addLog(LogRequestDto logRequestDto) {
    // validate log entry
    logValidationService.checkIfAnyEntriesAreNull(logRequestDto);
    logValidationService.validateSeverity(logRequestDto.addLogRequest.getSeverity());
    LogMessageDto logMessage = logValidationService.validateMessage(logRequestDto.addLogRequest.message);
    User user = userValidationService.checkIfNameExists(logRequestDto.user, true, ErrorMessages.USER_NOT_ALLOWED);

    // build Log
    LocalDateTime timeStamp = LocalDateTime.now();
    Log log = Log.builder().message(logRequestDto.addLogRequest.message).severity(logRequestDto.addLogRequest.severity).user(user).timestamp(timeStamp).build();

    // save Log
    logRepository.save(log);

    // return message to User-Interface
    logMessage.setReturnMessage(
        logMessage.getReturnMessage()
            + String.format(
            InfoMessages.MESSAGE_SAVED, logMessage.getMessage(), logRequestDto.addLogRequest.getSeverity()));

    LOGGER.info(
        String.format(
            InfoMessages.MESSAGE_SAVED, logMessage.getMessage(), logRequestDto.addLogRequest.getSeverity()));
    return logMessage.getReturnMessage();
  }

  public Log searchLogsByID(Integer id) {
    return logRepository.findById(id).isPresent() ? logRepository.findById(id).get() : null;
  }

  public String deleteById(Integer id) {
    logRepository.deleteById(id);
    LOGGER.info(String.format(InfoMessages.ENTRY_DELETED_ID, id));
    return String.format(InfoMessages.ENTRY_DELETED_ID, id);
  }

  public String deleteBySeverity(String severity) {
    List<Log> deletedLogs = logRepository.deleteBySeverity(severity);
    if (deletedLogs.isEmpty()) {
      return ErrorMessages.NO_ENTRIES_FOUND;
    }

    StringBuilder sb = new StringBuilder();
    StringBuilder iDs = new StringBuilder();

    for (Log log : deletedLogs) {
      iDs.append(log.getId());
      if (deletedLogs.lastIndexOf(log) < deletedLogs.size() - 1) {
        iDs.append(", ");
      }
    }

    sb.append("Entries with the ID(s) ").append(iDs).append(" were deleted from database.");
    LOGGER.info(sb.toString());
    return sb.toString();
  }

  public String deleteAll() {
    logRepository.deleteAll();
    LOGGER.info(InfoMessages.ALL_LOGS_DELETED);
    return InfoMessages.ALL_LOGS_DELETED;
  }
}
