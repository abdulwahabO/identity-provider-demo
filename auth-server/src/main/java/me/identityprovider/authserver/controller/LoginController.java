package me.identityprovider.authserver.controller;

import java.util.Optional;
import javax.validation.Valid;

import me.identityprovider.authserver.dto.AccessToken;
import me.identityprovider.authserver.exception.AuthenticationException;
import me.identityprovider.authserver.service.LoginService;
import me.identityprovider.common.exception.NoSuchAppException;
import me.identityprovider.common.exception.NoSuchUserException;
import me.identityprovider.common.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/login")
public class LoginController {

    private Logger logger = LoggerFactory.getLogger(LoginController.class);
    private static final String OTP_FORM = "";
    private static final String LOGIN_FORM = "";
    private static final String ERROR_KEY = "error";
    private static final String ERROR_MSG_KEY = "errorMessage";

    @Autowired
    private LoginService service;

    @GetMapping
    public String form(@RequestParam("client_id") String appId, Model model) {
        model.addAttribute("appId", appId); // todo: put appId in hidden field.
        return LOGIN_FORM;
    }

    // todo: confirm from docs that requestAttribute annotation can bind to form param.
    @PostMapping("/submit-email")
    public String sendPassword(@RequestAttribute(name = "email") String email,
            @RequestAttribute(name = "appId") String appId, Model model) {

        User.UserId id = new User.UserId(appId, email);

        if (service.userExists(id)) {

            if (service.startLogin(id)) {
                return OTP_FORM;
            } else {
                model.addAttribute(ERROR_KEY, true);
                model.addAttribute(ERROR_MSG_KEY, "Could not sent OTP. Please try again");
                logger.warn("SMS service failed. Could not send OTP");
                return LOGIN_FORM;
            }
        } else {
            model.addAttribute(ERROR_KEY, true);
            model.addAttribute(ERROR_MSG_KEY, "No account exists for " + email);
            return LOGIN_FORM;
        }
    }

    @PostMapping("/authenticate")
    public String authenticate(@RequestAttribute("otp") String password, Model model)
            throws NoSuchUserException, NoSuchAppException {

        Optional<User.UserId> id = service.checkOtp(password);

        if (!id.isPresent()) {
            model.addAttribute(ERROR_KEY, true);
            model.addAttribute(ERROR_MSG_KEY, "The OTP is incorrect");
            return OTP_FORM;
        }

        String appRedirect = service.finishLogin(id.get());
        return "redirect:" + appRedirect;
    }

    @PostMapping("/accesstoken")
    @ResponseBody
    public AccessToken.Response accessToken(@Valid @RequestBody AccessToken.Request body)
            throws NoSuchAppException, AuthenticationException {

        return service.getAccessToken(body);
    }
}
