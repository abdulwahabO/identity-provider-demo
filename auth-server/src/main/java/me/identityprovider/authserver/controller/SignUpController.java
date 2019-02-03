package me.identityprovider.authserver.controller;


import me.identityprovider.common.dto.UserDto;
import me.identityprovider.common.exception.UserException;
import me.identityprovider.common.model.User;

import me.identityprovider.common.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/signup")
public class SignUpController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String signUp(@RequestParam("app_id") String client, Model model) {

        UserDto details = new UserDto();
        details.setAppId(client);
        model.addAttribute("details", details);

        return "page-name";
    }

    @PostMapping("/start")
    public String start(UserDto dto) throws UserException {
        userService.startSignUp(dto);
        return "return page telling user to check their email";
    }


    // todo: called when link in email is clicked.
    @GetMapping("/verify-email")
    public RedirectView finish(@RequestParam("token") String signupToken) throws Exception {

        User user = userService.finishSignUp(signupToken);

        // todo: redirect to login controller using the user's details
        // todo: use RedirectAttributes()

        return null;
    }

    // todo: consider writing exception handlers for ServiceExceptions
}
