package me.identityprovider.dashboard.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import me.identityprovider.common.exception.AppCreationException;
import me.identityprovider.common.exception.NoSuchAppException;
import me.identityprovider.common.model.App;
import me.identityprovider.common.model.User;
import me.identityprovider.common.service.AppService;
import me.identityprovider.common.service.UserService;
import me.identityprovider.dashboard.exception.IllegalAppAccessException;
import me.identityprovider.dashboard.exception.NoSuchDeveloperException;
import me.identityprovider.dashboard.model.Developer;
import me.identityprovider.dashboard.service.DeveloperService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/dev")
public class DeveloperController {

    @Autowired
    public DeveloperService developerService;

    @Autowired
    public AppService appService;

    @Autowired
    public UserService userService;

    @GetMapping("/")
    public Developer get(String id) throws NoSuchDeveloperException {
        return developerService.read(id);
    }

    @GetMapping("/apps")
    public List<App> getApps(String devId) {
        return appService.getAppsOf(devId).orElse(Collections.emptyList());
    }

    @PostMapping("/create-account")
    public Developer register(@RequestBody Developer developer) {
        return developerService.save(developer);
    }

    @GetMapping("/check-email")
    public boolean isDevExists(@RequestParam String email) {
        return developerService.exists(email);
    }

    @PostMapping("/create-app")
    public App createApp(@RequestBody App app, @AuthenticationPrincipal Developer developer)
            throws AppCreationException {
        return appService.save(app);
    }

    @GetMapping("/users")
    public List<User> getUsers(@RequestParam String appId, @RequestParam String devId)
            throws NoSuchAppException, IllegalAppAccessException {
        checkAppOwner(appId, devId);
        Optional<List<User>> users = userService.getUsersOf(appId);
        return users.orElse(Collections.emptyList());
    }

    @DeleteMapping("/remove-app")
    public void deleteApp(String appId, String devId) throws NoSuchAppException, IllegalAppAccessException {
        checkAppOwner(appId, devId);
        userService.deleteUsersOf(appId);
        appService.delete(appId);
    }

    @DeleteMapping("delete-acct")
    public void closeDevAccount(@AuthenticationPrincipal Developer developer) {
        developerService.delete(developer.getUsername());
    }

    // checks if the dev owns the app they are trying to access.
    private void checkAppOwner(String appId, String devId) throws NoSuchAppException, IllegalAppAccessException {
        App app = appService.read(appId);
        if (!app.getDeveloperId().equals(devId)) {
            throw new IllegalAppAccessException("you don't own the app you are trying to access");
        }
    }
}
