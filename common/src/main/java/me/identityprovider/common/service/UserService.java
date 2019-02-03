package me.identityprovider.common.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;

import me.identityprovider.common.dto.UserDto;
import me.identityprovider.common.exception.UserException;
import me.identityprovider.common.model.User;
import me.identityprovider.common.repository.UserRepository;
import me.identityprovider.common.utils.SecurityUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final String USER_REGISTRATION_CACHE = "user.registration";

    private UserRepository userRepository;
    private CacheManager cacheManager;
    private Cache cache;

    @Autowired
    public UserService(UserRepository repository, CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        this.userRepository = repository;
    }

    public void delete(User.UserId Id) {
        userRepository.deleteById(Id);
    }

    public boolean exists(User.UserId Id) {
        return userRepository.existsById(Id);
    }

    public User save(User entity) {
        return userRepository.save(entity);
    }

    public User read(User.UserId id) throws UserException {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            throw new UserException("no user with given id");
        }
        return user.get();
    }

    // todo: returns users of a particular app, Javadoc
    public List<User> usersOf(String appId) throws UserException {
        List<User> users = userRepository.findByAppId(appId);
        if (users.isEmpty()){
            throw new UserException("There are no users for this app");
        }

        return users;
    }

    public void startSignUp(UserDto details) throws UserException {

        User.UserId id = new User.UserId(details.getAppId(), details.getEmail());
        if (exists(id)){
            throw new UserException("user email already in use");
        }

        User user = new User();
        user.setId(id);
        user.setMobile(details.getPhone());
        user.setSignupDate(LocalDate.now());

        // todo: hide this user details in cache till they click link in email.
        String token = SecurityUtil.randomCode(6);
        cache.put(token, user);

        // todo: write method to prepare email signup link.
        // todo: send email to user with link like: https://localhost:8080/signup/finish?token=[token here]
        // todo: email sender.

        // todo: prepare magic link and send to email, which caching details using the code in link sent to emails.
    }

    // todo: Javadoc this
    public User finishSignUp(String token) throws Exception {

        Cache.ValueWrapper userWrapper = cache.get(token);
        if (userWrapper == null) {
            throw new UserException("user does not exist"); // todo: create a more specific exception class
        }
        User user = (User) userWrapper.get();

        return save(user);
    }

    @PostConstruct
    private void init(){
        cache = cacheManager.getCache(USER_REGISTRATION_CACHE);
    }

}
