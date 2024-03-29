package project.userFeaturePortal.service.validation;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import project.userFeaturePortal.common.dto.user.UserRequestDto;
import project.userFeaturePortal.common.message.ErrorMessages;
import project.userFeaturePortal.common.message.InfoMessages;
import project.userFeaturePortal.exception.FirstUserUnequalActorException;
import project.userFeaturePortal.exception.ParameterNotPresentException;
import project.userFeaturePortal.exception.UserNotAllowedException;
import project.userFeaturePortal.exception.UserNotFoundException;
import project.userFeaturePortal.model.entity.Log;
import project.userFeaturePortal.model.entity.User;
import project.userFeaturePortal.model.repository.LogRepository;
import project.userFeaturePortal.model.repository.UserRepository;
import project.userFeaturePortal.service.model.UserService;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserValidationService {

  private static final Logger LOGGER = LogManager.getLogger(UserService.class);
  private final UserRepository userRepository;
  private final LogRepository logRepository;

  public void checkIfAnyEntriesAreNull(UserRequestDto allParameters) {
    if (allParameters.actor == null
        || allParameters.actor.equals("")
        || allParameters.name == null
        || allParameters.name.equals("")
        || allParameters.birthdate == null
        || allParameters.birthdate.equals("")
        || allParameters.weight == null
        || allParameters.height == null) {
      LOGGER.info(ErrorMessages.PARAMETER_IS_MISSING);
      throw new ParameterNotPresentException();
    }
    LOGGER.debug(InfoMessages.PARAMETERS_ARE_VALID);
  }

  public void checkIfUsersAreReferenced() {
    if (!logRepository.findAll().isEmpty()) {
      LOGGER.warn(ErrorMessages.USERS_REFERENCED);
      throw new RuntimeException(ErrorMessages.USERS_REFERENCED);
    }
  }

  public void validateActor(String name, String actor) {
    List<User> usersList = userRepository.findAll();

    // proof that the usersList in the db is empty
    // --> if so, first user has to be equal to the acting user (actor)
    boolean userListEmpty = false;
    if (usersList.isEmpty()) {
      userListEmpty = true;
      if (!name.equals(actor)) {
        LOGGER.warn(ErrorMessages.NO_USERS_YET + name + " unequal " + actor);
        throw new FirstUserUnequalActorException(actor, name);
      }
    }

    // if userList is not empty, check if actor is allowed to create user
    if (!userListEmpty) {
      checkIfNameExists(actor, true,
              String.format(ErrorMessages.USER_NOT_ALLOWED_CREATE_USER, actor));
    }
  }

  public void validateUserToCreate(String name) {
    // proof that the user you want to create is not existing yet
    if (userRepository.findUserByName(name) != null) {
      LOGGER.warn(String.format(ErrorMessages.USER_EXISTS, name));
      throw new RuntimeException(String.format(ErrorMessages.USER_EXISTS, name));
    }
  }

  public User validateUserToDelete(String name, String actorName) {
    User userToDelete = checkIfNameExists(name, false, ErrorMessages.CANNOT_DELETE_USER);

    // proof that there are no logs created by the user you want to delete
    List<Log> logs = logRepository.findByUser(userToDelete);
    if (!logs.isEmpty()) {
      LOGGER.error(String.format(ErrorMessages.USER_REFERENCED, userToDelete.getName()));
      throw new RuntimeException(
              String.format(ErrorMessages.USER_REFERENCED, userToDelete.getName()));
    }

    // proof that user is not deleting himself
    if (userToDelete.getName().equals(actorName)) {
      LOGGER.error(ErrorMessages.USER_DELETE_HIMSELF);
      throw new RuntimeException(ErrorMessages.USER_DELETE_HIMSELF);
    }

    return userToDelete;
  }

  public User checkIfIdExists(int id) {
    Optional<User> user = userRepository.findById(id);

    // if no users with wanted id found throw exception
    if (user.isEmpty()) {
      LOGGER.info(String.format(ErrorMessages.USER_NOT_FOUND_ID, id));
      throw new RuntimeException(String.format(ErrorMessages.USER_NOT_FOUND_ID, id));
    }

    return user.get();
  }

  public User checkIfNameExists(String name, boolean isActor, String action) {
    User user = userRepository.findUserByName(name);

    // proof that user is not null -->
    // if so, decide whether userName was simply not found
    // or he is not authorized to execute wanted action
    if (user == null) {
      if (isActor) {
        LOGGER.info(String.format(action, name));
        throw new UserNotAllowedException(String.format(action, name));
      }
      LOGGER.warn(String.format(ErrorMessages.USER_NOT_FOUND_NAME, name));
      throw new UserNotFoundException(name);
    }

    return user;
  }
}
