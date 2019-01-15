package me.identityprovider.authserver.controller;

import javax.validation.Valid;
import me.identityprovider.authserver.dto.AccessToken;
import me.identityprovider.authserver.exception.LoginException;
import me.identityprovider.authserver.service.LoginService;
import me.identityprovider.common.exception.ServiceException;
import me.identityprovider.common.model.User;

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

    @Autowired
    private LoginService service;

    @GetMapping
    public String form(@RequestParam("client_id") String appId, Model model) {
        model.addAttribute("appId", appId); // todo: put appId in hidden field.
        return "login-form";
    }

    // todo: confirm from docs that requestAttribute annotation can bind to form param.
    @PostMapping("/submit-email")
    public String sendPassword(@RequestAttribute(name = "email") String email,
                               @RequestAttribute(name = "appId") String appId, Model model) throws ServiceException {

        User.UserId id = new User.UserId(appId, email);
        boolean started = service.startLogin(id); // todo: if started show form else show message on login page.

        if (!started) {
            model.addAttribute("loginError", "could not start login");
            return "login-form";
        }

        return "otp-page";
    }

    // todo; Read Spring Boot docs on how to write Exception Handlers, custom page.

    @PostMapping("/authenticate")
    public String authenticate(@RequestAttribute("otp") String password) throws ServiceException, LoginException {

        // todo: call service to check cache and sign JWT if Implicit app.
        String redirectUrl = service.finishLogin(password);

        // todo: if OTP don't match. display page again with failure. Do this with ExceptionHandler method

        return redirectUrl; // todo: redirect back to client with JWT access token.
    }

    // todo: REST endpoint used by server side client to obtain accesstoken.
    @PostMapping("/accesstoken")
    @ResponseBody
    public AccessToken.Response accessToken(@Valid @RequestBody AccessToken.Request body) throws LoginException {

        return service.getAccessToken(body);
    }

    // todo: write ExceptionHandler.
}
