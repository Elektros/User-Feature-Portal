package project.logManager.service.model;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import project.logManager.common.dto.LogMessageDto;
import project.logManager.common.message.ErrorMessages;
import project.logManager.common.message.InfoMessages;
import project.logManager.exception.SeverityNotFoundException;
import project.logManager.model.entity.Log;
import project.logManager.model.entity.User;
import project.logManager.model.repository.LogRepository;
import project.logManager.model.repository.UserRepository;
import project.logManager.service.validation.LogValidationService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * @author - EugenFriesen
 * 12.02.2021
 **/

@Transactional
@Service
@RequiredArgsConstructor
public class LogService {
    private static final Logger LOGGER = LogManager.getLogger(LogService.class);

    private final LogRepository logRepository;
    private final LogValidationService logValidationService;
    private final UserRepository userRepository;

    public List<Log> getLogs(String severity, String message, LocalDateTime startDate, LocalDateTime endDate) {
        if (!logValidationService.validateSeverity(severity)) {
            LOGGER.error(ErrorMessages.SEVERITY_NOT_REGISTERED, severity);
            throw new SeverityNotFoundException(severity);
        }
        return logRepository.findLogs(severity, message, startDate, endDate);
    }

    public String addLog(String message, String severity, String userName) {
        if (!logValidationService.validateSeverity(severity)) {
            LOGGER.error(ErrorMessages.SEVERITY_NOT_REGISTERED, severity);
            throw new SeverityNotFoundException(severity);
        }
        LogMessageDto logMessage = logValidationService.validateMessage(message);
        User user = checkActor(userName);
        saveLog(logMessage.getMessage(), severity, user);

        logMessage.setReturnMessage(logMessage.getReturnMessage() +
                String.format(InfoMessages.MESSAGE_SAVED, logMessage.getMessage(), severity));
        LOGGER.info(String.format(InfoMessages.MESSAGE_SAVED, logMessage.getMessage(), severity));
        return logMessage.getReturnMessage();
    }

    private void saveLog(String message, String severity, User user) {
        Log log = new Log();
        log.setMessage(message);
        log.setSeverity(severity);
        log.setUser(user);
        Date timestamp = new Date();
        log.setTimestamp(timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        logRepository.save(log);
    }

    private User checkActor(String userName) {
        User user = userRepository.findUserByName(userName);
        if (user == null) {
            LOGGER.error(String.format(ErrorMessages.USER_NOT_FOUND_NAME, userName));
            throw new RuntimeException(String.format(ErrorMessages.USER_NOT_FOUND_NAME, userName));
        }
        return user;
    }

    public Log searchLogsByID(Integer id) {
        return logRepository.findById(id).isPresent() ? logRepository.findById(id).get() : null;
    }

    public String deleteById(Integer id) {
        logRepository.deleteById(id);
        return String.format(InfoMessages.ENTRY_DELETED_ID, id);
    }

    public boolean existLogByUserToDelete(User actor) {
        List<Log> logs = logRepository.findByUser(actor);
        return !logs.isEmpty();
    }

    public String deleteBySeverity(String severity) {
        List<Log> deletedLogs = logRepository.deleteBySeverity(severity);
        if (deletedLogs.isEmpty()) {
            return ErrorMessages.NO_ENTRIES_FOUND;
        }

        StringBuilder sb = new StringBuilder();
        String iDs = "";

        for (Log log : deletedLogs) {
            iDs += log.getId();
            if (deletedLogs.lastIndexOf(log) < deletedLogs.size() - 1) {
                iDs += ", ";
            }
        }

        sb.append("Entries with the ID(s) ").append(iDs).append(" were deleted from database.");
        return sb.toString();
    }

    public String deleteAll() {
        logRepository.deleteAll();
        LOGGER.info(InfoMessages.ALL_LOGS_DELETED);
        return InfoMessages.ALL_LOGS_DELETED;
    }
}
