package me.oauth2providerdemo.client.controller;

import java.time.Instant;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import me.oauth2providerdemo.client.dto.AccessToken;

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
        authServerUrl += "/login?client_id=" + clientId;
        return "redirect:" + authServerUrl;
    }

    @GetMapping("/callback")
    public String callback(@RequestParam String code, Model model) {

        AccessToken.Request body = new AccessToken.Request(code, clientId, clientSecret);
        HttpEntity<AccessToken.Request> httpEntity = new HttpEntity<>(body);
        RestTemplate template = new RestTemplate();

        authServerUrl += "/accesstoken";
        AccessToken.Response entity = 
                template.postForObject(authServerUrl, httpEntity, AccessToken.Response.class);
                            
        model.addAttribute("token", entity.getAcessToken());
        model.addAttribute("expires", Instant.ofEpochMilli(entity.getExpires()).toString());

        return "login-success";
    }

    @GetMapping(value = "")
    public String home(Model model) {
        return "home";
    }

}
