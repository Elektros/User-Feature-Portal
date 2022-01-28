package project.logManager.service.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import project.logManager.common.message.ErrorMessages;
import project.logManager.model.entity.User;
import project.logManager.model.repository.UserRepository;
import project.logManager.service.model.LogService;
import project.logManager.service.model.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class UserValidationServiceTest {

    @InjectMocks
    UserValidationService systemUnderTest;

    @Mock
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    LogService logService;

    List<User> users;

    @BeforeEach
    void init() {
        users = addTestUser();
    }

    @Test
    void testIfColorIsNotCorrect() {
        RuntimeException ex = Assertions.assertThrows(IllegalArgumentException.class, () ->
                systemUnderTest.validateFarbenEnum("gold"));
        Assertions.assertEquals(ErrorMessages.COLOR_ILLEGAL_PLUS_CHOICE, ex.getMessage());
    }

    @Test
    void testCheckIfUsersListIsEmpty() {
        Assertions.assertTrue(systemUnderTest.checkIfUsersListIsEmpty("Peter", users.get(0)),
                "Test");
    }

    @Test
    void testIfUserNotEqualActor() {
        Mockito.when(logService.addLog(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenThrow(RuntimeException.class);
        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () ->
                systemUnderTest.checkIfUsersListIsEmpty("Peter", users.get(1)));
        Assertions.assertEquals(ErrorMessages.NO_USERS_YET + "Florian unequal Peter", ex.getMessage());
    }

    //Der Fall trifft aktuell nicht ein, wird aber aus Testcoverage-Gründen getestet
    @Test
    void testIfLogServiceDoesNotThrowException() {
        systemUnderTest.checkIfUsersListIsEmpty("Hänsel", users.get(0));
    }

    @Test
    void testIfActorExists() {
        Mockito.when(userRepository.findUserByName(Mockito.anyString())).thenReturn(users.get(0));
        Assertions.assertEquals(users.get(0),
                systemUnderTest.checkIfActorExists(users.get(0).getName()));
    }

    @Test
    void testIfActorIsNull() {
        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () ->
                systemUnderTest.checkIfActorExists("Hans"));
        Assertions.assertEquals(String.format(ErrorMessages.USER_NOT_FOUND_NAME, "Hans"), ex.getMessage());
    }

    @Test
    void testIfUserToPostExists() {
        Mockito.when(userRepository.findUserByName(Mockito.anyString())).thenReturn(users.get(0));
        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () ->
                systemUnderTest.checkIfUserToPostExists("Torsten"));
        Assertions.assertEquals(String.format(ErrorMessages.USER_EXISTS, "Torsten"), ex.getMessage());
    }

    @Test
    void testIfUserToPostIsNull() {
        systemUnderTest.checkIfUserToPostExists(users.get(0).getName());
    }

    private List<User> addTestUser() {
        List<User> users = new ArrayList<>();
        users.add(User.builder()
                .id(1)
                .name("Peter")
                .birthdate(LocalDate.of(2005, 12, 12))
                .weight(90.0)
                .height(1.85)
                .favouriteColor("yellow")
                .bmi(26.29)
                .build());

        users.add(User.builder()
                .id(2)
                .name("Florian")
                .birthdate(LocalDate.of(1988, 12, 12))
                .weight(70.0)
                .height(1.85)
                .favouriteColor("yellow")
                .bmi(20.45)
                .build());
        return users;
    }
}