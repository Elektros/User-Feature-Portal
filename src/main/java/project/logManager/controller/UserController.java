package project.logManager.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.logManager.service.model.UserService;

import java.time.LocalDate;

@AllArgsConstructor(onConstructor_ = {@Autowired})
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/user")
    public void addUser(@RequestParam String name,
                          @RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate geburtsdatum,
                          @RequestParam double gewicht,
                          @RequestParam double groesse,
                          @RequestParam String lieblingsfarbe) {

        try {
            userService.addUser(name, geburtsdatum, gewicht, groesse, lieblingsfarbe);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}