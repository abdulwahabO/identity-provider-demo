package me.identityprovider.authserver.controller;

import java.util.Optional;
import me.identityprovider.common.dto.UserDto;
import me.identityprovider.common.model.App;
import me.identityprovider.common.model.User;

import me.identityprovider.common.service.AppService;
import me.identityprovider.common.service.UserService;
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

    private static final String SIGN_UP_FORM = ""; // todo
    private static final String SIGN_UP_SUCCESS_PAGE = ""; // todo
    private static final String LINK_EXPIRED_PAGE = ""; // todo
    private static final String EMAIL_SENT_PAGE = ""; // todo

    @Autowired
    private UserService userService;

    @Autowired
    private AppService appService;

    @GetMapping
    public String signUp(@RequestParam("app_id") String client, Model model) {
        UserDto user = new UserDto();
        user.setAppId(client);
        model.addAttribute("user", user);
        return SIGN_UP_FORM;
    }

    @PostMapping("/start")
    public String start(UserDto dto, Model model) {

        boolean userExists = userService.exists(new User.UserId(dto.getAppId(), dto.getEmail()));

        if (userExists) {
            model.addAttribute("message", "This email is already in use");
            model.addAttribute("user", dto);
            return SIGN_UP_FORM;
        }

        if (!userService.startSignUp(dto)) {
            model.addAttribute("message", "Could not send sign-up link to your mail");
            model.addAttribute("user", dto);
            return SIGN_UP_FORM;
        }

        return EMAIL_SENT_PAGE;
    }

    // todo: called when link in email is clicked.
    @GetMapping("/verify-email")
    public String finish(@RequestParam("token") String token, Model model) throws Exception {

        Optional<User> optionalUser = userService.checkToken(token);

        if (!optionalUser.isPresent()) {
            return LINK_EXPIRED_PAGE;
        }

        User user = optionalUser.get();
        userService.finishSignUp(user);

        App app = appService.read(user.getId().getAppId());

        // todo: Success page should have a link that says "login to {app_name}", when user clicks send them to
        // todo: the loginController using appId

        model.addAttribute("appId", app.getId());
        model.addAttribute("appName", app.getName());

        return SIGN_UP_SUCCESS_PAGE;
    }
}
