package me.identityprovider.dashboard.controller;

import me.identityprovider.common.exception.AppCreationException;
import me.identityprovider.common.exception.NoSuchAppException;
import me.identityprovider.common.model.App;
import me.identityprovider.common.service.AppService;
import me.identityprovider.dashboard.exception.DeveloperCreationException;
import me.identityprovider.dashboard.security.Developer;
import me.identityprovider.dashboard.service.DeveloperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class DeveloperController {

    @Autowired
    public DeveloperService developerService;

    @Autowired
    public AppService appService;

    @GetMapping
    public String signUp() {
        // todo: consider using a controller registry
        return "developer-signUp-page";
    }

    @PostMapping
    public Developer signUp(Developer developer) throws DeveloperCreationException {
        return developerService.save(developer);
    }

    @PostMapping
    public App createApp(@RequestBody App app,
                         @AuthenticationPrincipal Developer developer) throws AppCreationException {
        app.setDeveloperId(developer.getUsername());
        return appService.save(app);
    }

    public void deleteApp() {

    }


}
