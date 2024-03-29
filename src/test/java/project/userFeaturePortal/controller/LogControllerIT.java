package project.userFeaturePortal.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import project.userFeaturePortal.TestMessages;
import project.userFeaturePortal.common.message.ErrorMessages;
import project.userFeaturePortal.model.entity.Log;
import project.userFeaturePortal.model.entity.User;
import project.userFeaturePortal.model.repository.LogRepository;
import project.userFeaturePortal.model.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@AutoConfigureDataJpa
@ComponentScan(basePackages = { "project.userFeaturePortal" })
@Transactional
@TestPropertySource("/application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LogControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    void setup() {
        createLogs();
    }

    private static Stream<Arguments> addLogArguments() {
        return Stream.of(
                Arguments.of(
                        "MessageIsMissing",
                        TestMessages.MESSAGE_IS_MISSING,
                        status().isBadRequest(),
                        ErrorMessages.PARAMETER_IS_MISSING),
                Arguments.of(
                        "SeverityIsFalse",
                        TestMessages.SEVERITY_IS_FALSE,
                        status().isInternalServerError(),
                        String.format(ErrorMessages.SEVERITY_NOT_REGISTERED_CHOICE, "HI")),
                Arguments.of(
                        "UserIsNotAllowed",
                        TestMessages.USER_NOT_ALLOWED,
                        status().isForbidden(),
                        ErrorMessages.USER_NOT_ALLOWED));
    }

    private Stream<Arguments> getLogsArgument() {
        String jahr1999 = "1999-12-12-12-12-12";
        String jahr2005 = "2005-01-14-12-10-10";
        String jahr2004 = "2004-02-15-15-12-14";
        String jahr1995 = "1995-04-16-18-12-14";
        String jahr2015 = "2015-11-25-17-18-15";
        String jahr2002 = "2002-03-12-18-19-20";

        return Stream.of(
                Arguments.of("AllLogs", null, null, null, null, status().isOk(), 10),
                Arguments.of("FilterLogsBySeverity", "WARNING", null, null, null, status().isOk(), 2),
                Arguments.of("FilterLogsByMessage", null, "Test", null, null, status().isOk(), 5),
                Arguments.of("FilterLogsByStartTime", null, null, jahr2004, null, status().isOk(), 6),
                Arguments.of("FilterLogsByEndTime", null, null, null, jahr2002, status().isOk(), 2),
                Arguments.of(
                        "FilterLogsBySeverityAndStartTime", "INFO", null, jahr1999, null, status().isOk(), 2),
                Arguments.of(
                        "FilterLogsBySeverityAndEndTime", "INFO", null, null, jahr2005, status().isOk(), 2),
                Arguments.of(
                        "FilterLogsBySeverityAndMessage", "WARNING", "Warning", null, null, status().isOk(), 1),
                Arguments.of(
                        "FilterLogsByMessageAnStartTime", null, "Test", jahr1995, null, status().isOk(), 5),
                Arguments.of(
                        "FilterLogsByMessageAndEndTime", null, "Debug", null, jahr2015, status().isOk(), 1),
                Arguments.of(
                        "FilterLogsByStartTimeAndEndTime", null, null, jahr1999, jahr2005, status().isOk(), 5),
                Arguments.of(
                        "FilterLogsBySeverityAndMessageAndStartTime",
                        "TRACE",
                        "Trace",
                        jahr1999,
                        null,
                        status().isOk(),
                        1),
                Arguments.of(
                        "FilterLogsBySeverityAndMessageAndEndTime",
                        "TRACE",
                        "Trace",
                        null,
                        jahr2015,
                        status().isOk(),
                        1),
                Arguments.of(
                        "FilterLogsBySeverityAndStartTimeAndEndTime",
                        "TRACE",
                        null,
                        jahr1999,
                        jahr2015,
                        status().isOk(),
                        2),
                Arguments.of(
                        "FilterLogsByMessageAndStartTimeAndEndTime",
                        null,
                        "Debug",
                        jahr1999,
                        jahr2005,
                        status().isOk(),
                        1),
                Arguments.of(
                        "FilterLogsBySeverityAndMessageAndStartTimeAndEndTime",
                        "ERROR",
                        "Test",
                        jahr1999,
                        jahr2015,
                        status().isOk(),
                        1));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getLogsArgument")
    void testFilterLogs(
            String testName,
            String severity,
            String message,
            String startDateTime,
            String endDateTime,
            ResultMatcher status,
            Integer logNumber)
            throws Exception {
        mockMvc
                .perform(
                        get("/logs")
                                .param("severity", severity)
                                .param("message", message)
                                .param("startDateTime", startDateTime)
                                .param("endDateTime", endDateTime))
                .andDo(print())
                .andExpect(status)
                .andExpect(jsonPath("$.result", hasSize(logNumber)))
                .andReturn();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("addLogArguments")
    void testAddLog(String testName, String testData, ResultMatcher status, String returnMessage)
            throws Exception {
        createUser();
        MvcResult result = mockMvc
                .perform(
                        post("/log")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(testData)
                                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status)
                .andReturn();

        assertEquals(returnMessage, result.getResponse().getContentAsString());
    }

    @Test
    void testSuccessfullyAddedLog() throws Exception {
        createUser();
        mockMvc.perform(post("/log")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(TestMessages.TEST_LOG)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    void testKatzeToHund() throws Exception {
        createUser();
        mockMvc.perform(post("/log")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(TestMessages.KATZE_TO_HUND)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
    }

    private void createLog(Integer id, String severity, String message, LocalDateTime timestamp) {
        logRepository.save(
                Log.builder().id(id).severity(severity).message(message).timestamp(timestamp).build());
    }

  private void createUser() {
    User petra =
        User.builder()
            .id(1)
            .name("Petra")
            .birthdate(LocalDate.of(1999, 12, 13))
            .bmi(25.39)
            .weight(65)
            .height(1.60)
            .build();
    userRepository.save(petra);
  }

    private void createLogs() {
        createLog(1, "INFO", "Test", LocalDateTime.of(2000, 12, 12, 12, 12, 12));
        createLog(2, "INFO", "Info", LocalDateTime.of(2001, 12, 12, 12, 12, 12));
        createLog(3, "WARNING", "Warning", LocalDateTime.of(2002, 12, 12, 12, 12, 12));
        createLog(4, "WARNING", "Test", LocalDateTime.of(2003, 12, 12, 12, 12, 12));
        createLog(5, "DEBUG", "Debug", LocalDateTime.of(2004, 12, 12, 12, 12, 12));
        createLog(6, "DEBUG", "Test", LocalDateTime.of(2005, 12, 12, 12, 12, 12));
        createLog(7, "ERROR", "Error", LocalDateTime.of(2006, 12, 12, 12, 12, 12));
        createLog(8, "ERROR", "Test", LocalDateTime.of(2007, 12, 12, 12, 12, 12));
        createLog(9, "TRACE", "Trace", LocalDateTime.of(2008, 12, 12, 12, 12, 12));
        createLog(10, "TRACE", "Test", LocalDateTime.of(2009, 12, 12, 12, 12, 12));
    }

    @Nested
    class testFailsAtGetLogs {

        @Test
        void testSeverityIsFalseAtGetLogs() throws Exception {
            MvcResult result = mockMvc
                    .perform(get("/logs").param("severity", "hi"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            assertEquals(
                    "{\"result\":[],\"returnMessage\":null}", result.getResponse().getContentAsString());
        }

        @Test
        void testStartDateTimeHasWrongFormat() throws Exception {
            MvcResult result = mockMvc
                    .perform(get("/logs").param("startDateTime", "hallo"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andReturn();

            assertEquals(
                    "Required path variable was not found or request param has wrong format! "
                            + "Failed to convert value of type 'java.lang.String' to required type 'java.time.LocalDateTime'; "
                            + "nested exception is org.springframework.core.convert.ConversionFailedException: "
                            + "Failed to convert from type [java.lang.String] to type [@org.springframework.web.bind.annotation"
                            + ".RequestParam @org.springframework.format.annotation.DateTimeFormat java.time.LocalDateTime] "
                            + "for value 'hallo'; nested exception is java.lang.IllegalArgumentException: "
                            + "Parse attempt failed for value [hallo]",
                    result.getResponse().getContentAsString());
        }

        @Test
        void testEndDateTimeHasWrongFormat() throws Exception {
            MvcResult result = mockMvc
                    .perform(get("/logs").param("endDateTime", "hallo"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andReturn();

            assertEquals(
                    "Required path variable was not found or request param has wrong format! "
                            + "Failed to convert value of type 'java.lang.String' to required type 'java.time.LocalDateTime'; "
                            + "nested exception is org.springframework.core.convert.ConversionFailedException: "
                            + "Failed to convert from type [java.lang.String] to type [@org.springframework.web.bind.annotation"
                            + ".RequestParam @org.springframework.format.annotation.DateTimeFormat java.time.LocalDateTime] "
                            + "for value 'hallo'; nested exception is java.lang.IllegalArgumentException: "
                            + "Parse attempt failed for value [hallo]",
                    result.getResponse().getContentAsString());
        }
    }

    @Nested
    class testSearchLogsById {

        @Test
        void testGetLogsById() throws Exception {
            MvcResult result = mockMvc.perform(get("/logs/1")).andDo(print()).andExpect(status().isOk()).andReturn();

            assertEquals(TestMessages.LOG_EXAMPLE, result.getResponse().getContentAsString());
        }

        @Test
        void testIdForGetLogsNotFound() throws Exception {
            MvcResult result = mockMvc.perform(get("/logs/50")).andDo(print()).andExpect(status().isOk()).andReturn();

            assertEquals("[null]", result.getResponse().getContentAsString());
        }

        @Test
        void testIdForGetLogsHasWrongFormat() throws Exception {
            MvcResult result = mockMvc
                    .perform(get("/logs/hallo"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andReturn();

            assertEquals(
                    TestMessages.ID_FOR_LOGS_HAS_WRONG_FORMAT, result.getResponse().getContentAsString());
        }
    }

    @Nested
    class testDeleteLogs {

        @Test
        void testDeleteLogsById() throws Exception {
            MvcResult result = mockMvc
                    .perform(delete("/log/id/2"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            assertEquals(
                    "{\"result\":[{\"id\":1,\"severity\":\"INFO\",\"message\":\"Test\",\"timestamp\":\"2000-12-12T12:12:12\",\"user\":null},"
                            + "{\"id\":3,\"severity\":\"WARNING\",\"message\":\"Warning\",\"timestamp\":\"2002-12-12T12:12:12\",\"user\":null},"
                            + "{\"id\":4,\"severity\":\"WARNING\",\"message\":\"Test\",\"timestamp\":\"2003-12-12T12:12:12\",\"user\":null},"
                            + "{\"id\":5,\"severity\":\"DEBUG\",\"message\":\"Debug\",\"timestamp\":\"2004-12-12T12:12:12\",\"user\":null},"
                            + "{\"id\":6,\"severity\":\"DEBUG\",\"message\":\"Test\",\"timestamp\":\"2005-12-12T12:12:12\",\"user\":null},"
                            + "{\"id\":7,\"severity\":\"ERROR\",\"message\":\"Error\",\"timestamp\":\"2006-12-12T12:12:12\",\"user\":null},"
                            + "{\"id\":8,\"severity\":\"ERROR\",\"message\":\"Test\",\"timestamp\":\"2007-12-12T12:12:12\",\"user\":null},"
                            + "{\"id\":9,\"severity\":\"TRACE\",\"message\":\"Trace\",\"timestamp\":\"2008-12-12T12:12:12\",\"user\":null},"
                            + "{\"id\":10,\"severity\":\"TRACE\",\"message\":\"Test\",\"timestamp\":\"2009-12-12T12:12:12\",\"user\":null}],"
                            + "\"returnMessage\":\"Entry with the ID 2 was deleted from database.\"}",
                    result.getResponse().getContentAsString());
        }

        @Test
        void testIdForDeleteLogsNotFound() throws Exception {
            MvcResult result = mockMvc
                    .perform(delete("/log/id/20"))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andReturn();

            assertEquals(TestMessages.ID_NOT_EXISTS, result.getResponse().getContentAsString());
        }

        @Test
        void testIdForDeleteLogsHasWrongFormat() throws Exception {
            MvcResult result = mockMvc
                    .perform(delete("/log/id/hallo"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andReturn();

            assertEquals(
                    TestMessages.ID_FOR_LOGS_HAS_WRONG_FORMAT, result.getResponse().getContentAsString());
        }

        @Test
        void testDeleteLogsBySeverity() throws Exception {
            MvcResult result = mockMvc
                    .perform(delete("/log/severity").param("severity", "INFO"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            assertEquals(TestMessages.ENTRIES_DELETED, result.getResponse().getContentAsString());
        }

        @Test
        void testWrongSeverityForDelete() throws Exception {
            MvcResult result = mockMvc
                    .perform(delete("/log/severity").param("severity", "hi"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            assertEquals(ErrorMessages.NO_ENTRIES_FOUND, result.getResponse().getContentAsString());
        }

        @Test
        void testDeleteAll() throws Exception {
            MvcResult result = mockMvc
                    .perform(delete("/logs"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            assertEquals(
                    "{\"result\":[],\"returnMessage\":\"All logs were deleted from database!\"}",
                    result.getResponse().getContentAsString());
        }
    }
}
