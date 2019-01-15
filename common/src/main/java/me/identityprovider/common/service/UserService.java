package me.identityprovider.common.service;

import java.util.List;
import me.identityprovider.common.exception.UserException;
import me.identityprovider.common.model.User;
import me.identityprovider.common.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService extends BaseService<User, User.UserId> {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository repository) {
        super(repository);
        this.userRepository = repository;
    }

    public List<User> findByApp(String appId) throws UserException {
        List<User> users = userRepository.findByAppId(appId);
        if (users.isEmpty()){
            throw new UserException("There are no users for this app");
        }

        return users;
    }
}
