package me.identityprovider.common.service;

import java.util.List;
import me.identityprovider.common.exception.UserException;
import me.identityprovider.common.model.App;
import me.identityprovider.common.model.User;
import me.identityprovider.common.repository.AppRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppService extends BaseService<App, String> {

    private UserService userService;

    @Autowired
    public AppService(AppRepository appRepository, UserService userService) {
        super(appRepository);
        this.userService = userService;
    }

    public List<User> users(String appId) throws UserException {
        return userService.findByApp(appId);
    }

}
