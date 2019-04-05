package me.identityprovider.controller;

import java.time.LocalDate;
import java.util.Optional;

import me.identityprovider.dto.UserDto;
import me.identityprovider.model.App;
import me.identityprovider.model.User;
import me.identityprovider.service.AppService;
import me.identityprovider.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/signup")
public class SignUpController {

    private static final String SIGN_UP_FORM = "signup";
    private static final String SIGN_UP_SUCCESS_PAGE = "signup-success";
    private static final String LINK_EXPIRED_PAGE = "email-link-expired";
    private static final String EMAIL_SENT_PAGE = "verify-email-sent";

    private static final String ERROR_KEY = "error";
    private static final String ERROR_MSG_KEY = "errorMessage";

    @Autowired
    private UserService userService;

    @Autowired
    private AppService appService;

    @GetMapping
    public String signUp(@RequestParam("app_id") String appId, Model model) {
        model.addAttribute("app_id", appId);
        return SIGN_UP_FORM;
    }

    @PostMapping("/start")
    public String start(UserDto dto, Model model) {

        boolean userExists = userService.exists(new User.UserId(dto.getAppId(), dto.getEmail()));

        if (userExists) {
            model.addAttribute(ERROR_KEY, true);
            model.addAttribute(ERROR_MSG_KEY, "This email is already in use");
            model.addAttribute("user", dto);
            return SIGN_UP_FORM;
        }

        if (!userService.createUser(dto)) {
            model.addAttribute(ERROR_KEY, true);
            model.addAttribute(ERROR_MSG_KEY, "Could not send sign-up link to your mail");
            model.addAttribute("user", dto);
            return SIGN_UP_FORM;
        }

        return EMAIL_SENT_PAGE;
    }

    @GetMapping("/finish")
    public String finish(@RequestParam("token") String token, Model model) throws Exception {

        Optional<User> optionalUser = userService.checkToken(token);

        if (!optionalUser.isPresent()) {
            return LINK_EXPIRED_PAGE;
        }

        User user = optionalUser.get();
        user.setSignupDate(LocalDate.now());
        userService.save(user);
        App app = appService.read(user.getId().getAppId());

        model.addAttribute("app", app.getHomePage());

        return SIGN_UP_SUCCESS_PAGE;
    }
}
