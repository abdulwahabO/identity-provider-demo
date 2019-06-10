package demo.oauth2passwordless.service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.auth0.jwt.exceptions.JWTCreationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import demo.oauth2passwordless.dto.AccessToken;
import demo.oauth2passwordless.exception.AuthenticationException;
import demo.oauth2passwordless.exception.NoSuchAppException;
import demo.oauth2passwordless.exception.NoSuchUserException;
import demo.oauth2passwordless.message.TextMessageService;
import demo.oauth2passwordless.model.User;
import demo.oauth2passwordless.utils.SecurityUtil;
import demo.oauth2passwordless.model.App;

import static demo.oauth2passwordless.utils.SecurityUtil.jwt;

@Service
public class LoginService {

    private Logger logger = LoggerFactory.getLogger(LoginService.class);
    private static final String OTP_CACHE = "otp";
    private static final String AUTH_CODE_CACHE = "auth_code";
    private static final long ACCESS_TOKEN_EXPIRY = 5000000;
    
    private Cache otpCache;
    private Cache authCodeCache;

    private CacheManager cacheManager;
    private TextMessageService textService;
    private UserService userService;
    private AppService appService;

    @Autowired
    public LoginService(UserService userService, CacheManager cacheManager, TextMessageService textService,
            AppService appService) {
        this.textService = textService;
        this.cacheManager = cacheManager;
        this.userService = userService;
        this.appService = appService;
    }

    /**
     * Returns a {@link User.UserId} if the given OTP is valid.
     *
     * @param otp the one-time password
     */
    public Optional<User.UserId> checkOtp(String otp) {
        otpCache = cacheManager.getCache(OTP_CACHE);
        Cache.ValueWrapper wrapper = otpCache.get(otp);

        if (wrapper == null) {
            return Optional.empty();
        }

        User.UserId id = (User.UserId) wrapper.get();
        return Optional.of(id);
    }

    /**
     * Returns an access token or an authorization code depending on the type of OAuth2 flow the app uses.
     *
     * @param userId
     * @return A URL to which the user should be redirected.
     * @throws NoSuchUserException if no user is found with the given id.
     * @throws NoSuchAppException if no app is associated with the user id.
     */
    public String finishLogin(User.UserId userId) throws AuthenticationException {

        try {

            App app = appService.read(userId.getAppId());
            String secret = app.getSecret();
            String redirect = app.getLoginRedirect();
    
            User user = userService.read(userId);

            switch (app.getGrantType()) {

                case IMPLICIT:
                    redirect += "#access_token=" + jwt(jwtClaims(user, app), secret);
                    user.setLastLogin(LocalDateTime.now());
                    userService.save(user);
                break;

                case AUTH_CODE:
                    authCodeCache = cacheManager.getCache(AUTH_CODE_CACHE);
                    String code = SecurityUtil.randomCode(6);
                    redirect += "?code=" + code;
                    authCodeCache.put(code, user);
                break;
            }
            
            
        return redirect;

        } catch (Exception e) {
           throw new AuthenticationException(e.getMessage());
        }

    }

    /**
     * Checks a user id for validity.
     *
     * @param id the Id to check for
     * @return true if a user with the given is exists.
     */
    public boolean userExists(User.UserId id) {
        return userService.exists(id);
    }

    /**
     * Starts the process of authenticating a user by sending a one-time password to their mobile.
     *
     * @param userId the id of the user to start auth flow for.
     * @return true if an sms has been sent to the user's mobile. false otherwise.
     */
    public boolean startLogin(User.UserId userId) {

        try {
            User user = userService.read(userId);
            String password = SecurityUtil.randomCode(5);
            boolean sent = textService.sendOneTimePassword(password, user.getMobile());

            if (sent) {
                otpCache = cacheManager.getCache(OTP_CACHE);
                otpCache.put(password, userId);
                return true;
            } else {
                return false;
            }
        } catch (NoSuchUserException e) {
            logger.info("No user exists with given id: [{}]", userId);
            return false;
        }
    }

    /**
     * Returns a JWT token which is used as access token for the client's API.
     *
     * @param request an {@link AccessToken.Request} which contains client credentials and auth code for a user.
     * @return an JWT token.
     * @throws AuthenticationException if the authorization code in the request is invalid.
     * @throws NoSuchAppException if no app exists with the client credentials provided.
     */
    public AccessToken.Response getAccessToken(AccessToken.Request request)
        throws AuthenticationException, NoSuchAppException {

        String clientId = request.getClientId();
        String clientSecret = request.getClientSecret();
        
        authCodeCache = cacheManager.getCache(AUTH_CODE_CACHE);
        Cache.ValueWrapper wrapper = authCodeCache.get(request.getAuthorizationCode());

        if (wrapper == null) {
            throw new AuthenticationException("Could verify authorization code");
        }

        User user = (User) wrapper.get();
        App app = appService.read(user.getId().getAppId());

        if (!app.getSecret().equals(clientSecret) || !app.getId().equals(clientId)) {
            throw new AuthenticationException("Client credentials are invalid");
        }

        String accessToken;

        try {
            accessToken = SecurityUtil.jwt(jwtClaims(user, app), clientSecret);
        } catch (JWTCreationException e) {
            throw new AuthenticationException("could not create access token");
        }

        user.setLastLogin(LocalDateTime.now());
        userService.save(user);

        AccessToken.Response response = new AccessToken.Response();
        response.setAcessToken(accessToken);
        response.setExpires(ACCESS_TOKEN_EXPIRY);

        return response;
    }

    private SecurityUtil.JwtClaims jwtClaims(User user, App app) {
        SecurityUtil.JwtClaims claims = new SecurityUtil.JwtClaims();
        claims.setAudience(app.getApiId());
        claims.setSubject(user.getId().getEmail());
        return claims;
    }

}
