package demo.oauth2passwordless.client.controller;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import demo.oauth2passwordless.client.dto.AccessToken;
import demo.oauth2passwordless.client.http.OAuthServerClient;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.okhttp.OkHttpClient;


@Controller
public class ClientController {

    @Value("${client.secret}")
    private String clientSecret;
    
    @Value("${client.id}")
    private String clientId;

    @Value("${authserver}")
    private String authServerUrl;

    @GetMapping(value = "/login")
    public String redirectToOauthServer() {
        String redirectURL = authServerUrl + "/login?client_id=" + clientId;
        return "redirect:" + redirectURL;
    }

    @GetMapping("/callback")
    public String callback(@RequestParam String code, Model model) {

        AccessToken.Request body = new AccessToken.Request(code, clientId, clientSecret);
        OAuthServerClient client = Feign.builder()
                                        .client(new OkHttpClient())
                                        .decoder(new JacksonDecoder())
                                        .target(OAuthServerClient.class, authServerUrl);
                                                                                  
        AccessToken.Response response = 
        client.accessToken(body.getClientId(), body.getClientSecret(), body.getAuthorizationCode());       
                            
        model.addAttribute("token", response.getAcessToken());

        ZonedDateTime tokenExpiry = 
                ZonedDateTime.ofInstant(Instant.now().plusSeconds(response.getExpires()), ZoneId.systemDefault());
                    
        model.addAttribute("expires", tokenExpiry.format(DateTimeFormatter.RFC_1123_DATE_TIME));

        return "login-success";
    }

    @GetMapping(value = "")
    public String home(Model model) {
        return "home";
    }
}
