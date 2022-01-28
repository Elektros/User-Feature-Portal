package project.logManager.service.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import project.logManager.common.message.ErrorMessages;
import project.logManager.common.message.InfoMessages;
import project.logManager.exception.UserNotFoundException;
import project.logManager.model.entity.User;
import project.logManager.model.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)

class BmiServiceTest {

    @InjectMocks
    BmiService systemUnderTest;

    @Mock
    UserRepository userRepository;

    List<User> users;

    @BeforeEach
    void init() {
        users = addTestUser();
    }

    @Test
    void testBerechneBMIWhenUserTooYoung() {
        Assertions.assertEquals(ErrorMessages.USER_TOO_YOUNG, systemUnderTest.getBmiMessage(users.get(0).getBirthdate(),
                        users.get(0).getWeight(),
                        users.get(0).getHeight()));
    }

    @Test
    void testBerechneBMIWithNormalWeight() {
        Assertions.assertEquals(String.format(InfoMessages.BMI_MESSAGE, 22.11) + InfoMessages.NORMAL_WEIGHT,
                systemUnderTest.getBmiMessage(LocalDate.of(1988, 12, 12),
                        75.7, 1.85));
    }
    @Test
    void testBerechneBMIWithUnderweight() {
        Assertions.assertEquals(String.format(InfoMessages.BMI_MESSAGE, 18.3) + InfoMessages.UNDERWEIGHT,
                systemUnderTest.getBmiMessage(LocalDate.of(1988, 12, 12),
                        61.3, 1.83));
    }
    @Test
    void testBerechneBMIWithOverweight() {
        Assertions.assertEquals(String.format(InfoMessages.BMI_MESSAGE, 28.74) + InfoMessages.OVERWEIGHT,
                systemUnderTest.getBmiMessage(LocalDate.of(2000, 12,12),
                        95.2, 1.82));
    }

    @Test
    void testBMIisUnexpectedValue() {
        RuntimeException ex = Assertions.assertThrows(IllegalStateException.class, () ->
                systemUnderTest.getBmiMessage(LocalDate.of(2000, 12, 12),
                        -100.0, 1.85));
        Assertions.assertEquals(ErrorMessages.COULD_NOT_CALCULATE, ex.getMessage());
    }

    @Test
    void testBerechneBMI() {
        Assertions.assertEquals(30.86,
                systemUnderTest.calculateBMI(100.0, 1.8));
    }

    @Test
    void testFindUserAndCalculateBMI() {
        Mockito.when(userRepository.findUserByName(users.get(1).getName())).thenReturn(users.get(1));
        systemUnderTest.findUserAndCalculateBMI(users.get(1).getName());
    }

    @Test
    void testUserIsNull() {
        UserNotFoundException ex = Assertions.assertThrows(UserNotFoundException.class, () ->
                systemUnderTest.findUserAndCalculateBMI("Paul"));
        Assertions.assertEquals(String.format(ErrorMessages.USER_NOT_IDENTIFIED,"Paul"), ex.getMessage());
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