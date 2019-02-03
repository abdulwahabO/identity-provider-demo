package me.identityprovider.authserver.service;

import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;

import me.identityprovider.authserver.dto.AccessToken;
import me.identityprovider.authserver.exception.LoginException;
import me.identityprovider.common.exception.NoSuchAppException;
import me.identityprovider.common.exception.UserException;
import me.identityprovider.common.message.TextSender;
import me.identityprovider.common.model.App;
import me.identityprovider.common.model.User;
import me.identityprovider.common.service.AppService;
import me.identityprovider.common.service.UserService;
import me.identityprovider.common.utils.SecurityUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import static me.identityprovider.common.utils.SecurityUtil.jwt;

@Service
public class LoginService {

    private Logger logger = LoggerFactory.getLogger(LoginService.class);
    private static final String OTP_CACHE = ""; // todo
    private static final String AUTH_CODE_CACHE = ""; // todo
    private static final long ACCESS_TOKEN_EXPIRY = 50000;

    private Cache otpCache;
    private Cache authCodeCache;
    private CacheManager cacheManager;
    private TextSender textSender;
    private UserService userService;
    private AppService appService;

    @Autowired
    public LoginService(UserService userService, CacheManager cacheManager, TextSender textSender,
            AppService appService) {
        this.textSender = textSender;
        this.cacheManager = cacheManager;
        this.userService = userService;
        this.appService = appService;
    }

    public String finishLogin(String password) throws LoginException, UserException, NoSuchAppException {

        Cache.ValueWrapper wrapper = otpCache.get(password);
        String redirect;

        if (wrapper != null) {

            User.UserId userId = (User.UserId) wrapper.get();
            App app = appService.read(userId.getAppId());
            String secret = app.getSecret();
            redirect = app.getLoginRedirect(); // return this.

            User user = userService.read(userId);

            switch (app.getGrantType()) {

                case IMPLICIT:
                    String jwt = jwt(jwtClaims(user), secret);
                    redirect += "#access_token=" + jwt;
                    user.setLastLogin(LocalDateTime.now());
                    userService.save(user);
                    break;

                case AUTH_CODE:
                    String code = SecurityUtil.randomCode(6);
                    redirect += "?code=" + code;
                    authCodeCache.put(code, user);
                    break;
            }

        } else {
            throw new LoginException("could not complete login. OTP not valid");
        }

        return redirect;
    }

    // todo: javadoc
    public boolean startLogin(User.UserId userId) throws UserException {
        boolean exists = userService.exists(userId);

        if (exists) {
            User user = userService.read(userId);
            String password = SecurityUtil.randomCode(5);
            boolean sent = textSender.sendOneTimePassword(password, user.getMobile());
            otpCache.put(password, userId);

            return sent;
        }

        return false;
    }

    public AccessToken.Response getAccessToken(AccessToken.Request request) throws LoginException, UserException,
            NoSuchAppException {

        String clientId = request.getClientId();
        String clientSecret = request.getClientSecret();
        Cache.ValueWrapper wrapper = authCodeCache.get(request.getAuthorizationCode());

        if (wrapper == null) {
            throw new LoginException("Could not obtain access token for this user");
        }

        User user = (User) wrapper.get();
        App app = appService.read(user.getId().getAppId());

        if (!app.getSecret().equals(clientSecret) || !app.getId().equals(clientId)) {
            throw new LoginException("Could not get access token. Client is not recognised");
        }

        String accessToken = SecurityUtil.jwt(jwtClaims(user), clientSecret);
        user.setLastLogin(LocalDateTime.now());
        userService.save(user);

        AccessToken.Response response = new AccessToken.Response();
        response.setAcesstoken(accessToken);
        response.setExpires(ACCESS_TOKEN_EXPIRY);
        response.setScope("all");

        return response;
    }

    // todo: finish and Javadoc.
    private Map<String, String> jwtClaims(User user) {

        // todo: use user details to populate a claims map.
        Map<String, String> claims = new HashMap<>();
        claims.put("email", user.getId().getEmail());
        claims.put("", "");
        claims.put("", ""); // todo: issuer ??

        return claims;
    }

    @PostConstruct
    public void init() {
        otpCache = cacheManager.getCache(OTP_CACHE);
        authCodeCache = cacheManager.getCache(AUTH_CODE_CACHE);
    }
}
