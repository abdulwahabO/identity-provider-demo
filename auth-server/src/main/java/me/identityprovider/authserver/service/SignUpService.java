package me.identityprovider.authserver.service;

import java.time.LocalDate;

import javax.annotation.PostConstruct;

import me.identityprovider.authserver.dto.SignUpDetails;
import me.identityprovider.common.exception.UserException;
import me.identityprovider.common.model.User;
import me.identityprovider.common.service.UserService;
import me.identityprovider.common.utils.Generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class SignUpService {

    private static final String SIGNUP_CACHE = "signup";

    private UserService userService;
    private CacheManager cacheManager;

    private Cache cache;

    @Autowired
    public SignUpService(UserService userService, CacheManager cacheManager) {
        this.userService = userService;
        this.cacheManager = cacheManager;
    }

    // todo: check if user exists on DB already

    // todo: prepare magic link and send to email, which caching details using the code in link sent to emails.

    public void start(SignUpDetails details) throws UserException {

        User.UserId id = new User.UserId(details.getAppId(), details.getEmail());
        if (userService.exists(id)){
            throw new UserException("user email already in use");
        }

        User user = new User();
        user.setId(id);
        user.setMobile(details.getPhone());
        user.setSignupDate(LocalDate.now());

        // todo: hide this user details in cache till they click link in email.
        String token = Generator.randomCode(6);
        cache.put(token, user);

        // todo: send email to user with link like: https://localhost:8080/signup/finish?token=[token here]
        // todo: email sender.

    }


    public User finish(String token) throws Exception {

         Cache.ValueWrapper userWrapper = cache.get(token);

         if (null == userWrapper.get()) {
             throw new UserException("user does not exist");
         }

         User user = (User) userWrapper.get();

         return userService.save(user);
    }

    // todo: need to write excpetion handlers to display proper pages.

    // todo: One handler to take details and shows page telling user to check their email for verification link.

    // todo: One handler for when user clicks email link, send token to phone and display input for token.

    // todo: when user enters token, move details from cache to DB and sign JWT and send to redirect_url of client.

    @PostConstruct
    private void init(){
        cache = cacheManager.getCache(SIGNUP_CACHE);
    }

}
