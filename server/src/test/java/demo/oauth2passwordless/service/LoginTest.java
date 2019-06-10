package demo.oauth2passwordless.service;

import demo.oauth2passwordless.dto.AccessToken;
import demo.oauth2passwordless.message.TextMessageService;
import demo.oauth2passwordless.model.App;
import demo.oauth2passwordless.model.User;
import demo.oauth2passwordless.model.App.GrantType;
import demo.oauth2passwordless.utils.SecurityUtil;

import org.junit.Before;
import org.junit.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class LoginTest {

    private static final String SERVER_SIDE_CLIENT_APP_ID = SecurityUtil.appId();
    private static final String FRONTEND_CLIENT_APP_ID = SecurityUtil.appId();

    private AppService appService = mock(AppService.class);
    private UserService userService = mock(UserService.class);
    private CacheManager cacheManager = mock(CacheManager.class);
    private TextMessageService textService = mock(TextMessageService.class);
    private Cache cache = mock(Cache.class);
    private Cache.ValueWrapper cacheValueWrapper = mock(Cache.ValueWrapper.class);
   
    private User frontAppUser;
    private User serverAppUser;

    App frontendApp;
    App serverApp;

    @Before
    public void setup() {

        frontAppUser = new User();
        frontAppUser.setId(new User.UserId(FRONTEND_CLIENT_APP_ID, "x@client.com"));
        frontAppUser.setMobile("070XXXXX5");

        serverAppUser = new User();
        serverAppUser.setId(new User.UserId(SERVER_SIDE_CLIENT_APP_ID, "x@client.com"));
        serverAppUser.setMobile("080XXXXXX6");

        frontendApp = new App();
        frontendApp.setId(FRONTEND_CLIENT_APP_ID);
        frontendApp.setGrantType(GrantType.IMPLICIT);
        frontendApp.setSecret(SecurityUtil.appSecret());
        frontendApp.setLoginRedirect("https://frontend.com/oauth");

        serverApp = new App();
        serverApp.setId(SERVER_SIDE_CLIENT_APP_ID);
        serverApp.setGrantType(GrantType.AUTH_CODE);
        serverApp.setSecret(SecurityUtil.appSecret());
        serverApp.setLoginRedirect("https://backend.com/oauth");

    }

    @Test
    public void shouldStartLoginForValidUser() throws Exception {

        when(cacheManager.getCache(anyString())).thenReturn(cache);
        when(userService.read(serverAppUser.getId())).thenReturn(serverAppUser);
        when(textService.sendOneTimePassword(anyString(), eq(serverAppUser.getMobile()))).thenReturn(true);
        doNothing().when(cache).put(anyString(), any());

        LoginService service = new LoginService(userService, cacheManager, textService, appService);
        assertTrue(service.startLogin(serverAppUser.getId()));

    }

    @Test
    public void shouldReturnAccessToken() throws Exception {

        AccessToken.Request tokenRequest = new AccessToken.Request();
        tokenRequest.setClientId(serverApp.getId());
        tokenRequest.setClientSecret(serverApp.getSecret());
        tokenRequest.setAuthorizationCode("123456");

        when(cacheManager.getCache(anyString())).thenReturn(cache);

        when(cache.get(tokenRequest.getAuthorizationCode())).thenReturn(cacheValueWrapper);

        when(cacheValueWrapper.get()).thenReturn(serverAppUser);

        when(userService.save(serverAppUser)).thenReturn(serverAppUser);

        when(appService.read(serverApp.getId())).thenReturn(serverApp);

        LoginService service = new LoginService(userService, cacheManager, textService, appService);

        assertNotNull(service.getAccessToken(tokenRequest));
    }

    @Test
    public void shoudReturnTokenOrAuthCode() throws Exception {

        when(appService.read(frontendApp.getId())).thenReturn(frontendApp);        
        when(appService.read(serverApp.getId())).thenReturn(serverApp);

        when(userService.save(serverAppUser)).thenReturn(serverAppUser);
        when(userService.save(frontAppUser)).thenReturn(frontAppUser);

        when(userService.read(serverAppUser.getId())).thenReturn(serverAppUser);
        when(userService.read(frontAppUser.getId())).thenReturn(frontAppUser);
        
        when(cacheManager.getCache(anyString())).thenReturn(cache);

        doNothing().when(cache).put(anyString(), any());

        LoginService service = new LoginService(userService, cacheManager, textService, appService);

        String authCodeRedirect = service.finishLogin(serverAppUser.getId());
        String accessTokenRedirect = service.finishLogin(frontAppUser.getId());

        assertTrue(authCodeRedirect.contains("?code="));
        assertTrue(accessTokenRedirect.contains("#access_token="));

    }
}
