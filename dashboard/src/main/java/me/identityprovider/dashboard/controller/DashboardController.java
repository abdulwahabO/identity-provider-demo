package me.identityprovider.dashboard.controller;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

import me.identityprovider.common.exception.AppCreationException;

import me.identityprovider.common.model.App;
import me.identityprovider.common.model.User;
import me.identityprovider.common.service.AppService;
import me.identityprovider.common.service.UserService;
import me.identityprovider.dashboard.model.Developer;
import me.identityprovider.dashboard.service.DeveloperService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class DashboardController {

    @Autowired
    public DeveloperService developerService;

    @Autowired
    public AppService appService;

    @Autowired
    public UserService userService;

    @GetMapping
    public String getDashboard(@AuthenticationPrincipal Developer dev, Model model) {
        model.addAttribute("dev", dev);
        return ""; // todo: dashboard home
    }

    @GetMapping("/apps")
    public String getApps(@AuthenticationPrincipal Developer dev, Model model) {
        List<App> apps = appService.getAppsOf(dev.getUsername()).orElse(Collections.emptyList());
        model.addAttribute("apps", apps);
        return ""; // todo: a page that displays apps
    }

    @PostMapping("/create-account")
    public String register(Developer developer, Model model) {
        if (developerService.exists(developer.getUsername())){
            model.addAttribute("error", true);
            model.addAttribute("errorMsg", "This email is already in use");
            return ""; // todo: return dev creation page with error
        }

        model.addAttribute("dev", developerService.save(developer));

        return ""; // todo: Return new dev page.
    }

    @PostMapping("/create-app")
    public String createApp(App app, @AuthenticationPrincipal Developer developer, Model model) {
        try {
            App newApp = appService.save(app);
            model.addAttribute("app", newApp);
            return ""; // todo: return new page with app details
        } catch (AppCreationException e) {
            model.addAttribute("error", true);
            model.addAttribute("errorMsg", e.getMessage());
            return ""; // todo: return app creation form with error
        }
    }

    @GetMapping("/users")
    public String getUsers(@RequestParam String appId, @AuthenticationPrincipal Developer dev, Model model) {
       if  (developerService.isAppOwner(appId, dev.getUsername())){
           Optional<List<User>> users = userService.getUsersOf(appId);
           model.addAttribute("users", users);
       }

       return "";
    }

    @DeleteMapping("/remove-app")
    public String deleteApp(@RequestParam String appId, @AuthenticationPrincipal Developer dev) {

        if (developerService.isAppOwner(appId, dev.getUsername())) {
            userService.deleteUsersOf(appId);
            appService.delete(appId);
        }

        return "redirect:/apps"; // todo: just redirect to , will redirect contain auth principal?
    }

    @DeleteMapping("delete-acct")
    public String closeDevAccount(@AuthenticationPrincipal Developer developer) {
        developerService.delete(developer.getUsername());
        return "/login"; // todo: return default login page.
    }

}
