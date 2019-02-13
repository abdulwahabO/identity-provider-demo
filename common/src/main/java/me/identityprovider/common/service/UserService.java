package me.identityprovider.common.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;

import me.identityprovider.common.dto.UserDto;
import me.identityprovider.common.exception.NoSuchUserException;
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

    public User read(User.UserId id) throws NoSuchUserException {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            throw new NoSuchUserException("no user with given id");
        }
        return user.get();
    }

    // todo: returns users of a particular app, Javadoc
    public Optional<List<User>> getUsersOf(String appId) {
        List<User> users = userRepository.findByAppId(appId);
        return Optional.of(users);
    }

    public void deleteUsersOf(String appId) {
        userRepository.deleteAppUsers(appId);
    }

    public boolean startSignUp(UserDto dto) {

        User user = new User();
        user.setId(new User.UserId(dto.getAppId(), dto.getEmail()));
        user.setMobile(dto.getPhone());

        String token = SecurityUtil.randomCode(6);
        cache.put(token, user);

        // todo: write method to prepare email signup link.
        // todo: send email to user with link like: https://localhost:8080/signup/finish?token=[token here]
        // todo: email sender.
        // todo: prepare magic link and send to email, which caching details using the code in link sent to emails.
        // todo: emailSender should return boolean indicating that email was sent.

        return true;
    }

    public Optional<User> checkToken(String token) {
        Cache.ValueWrapper wrapper = cache.get(token);
        if (wrapper == null) {
            Optional.empty();
        }
        User user = (User) wrapper.get();
        return Optional.of(user);
    }

    // todo: Javadoc this
    public User finishSignUp(User user) {
        user.setSignupDate(LocalDate.now());
        return save(user);
    }

    @PostConstruct
    private void init(){
        cache = cacheManager.getCache(USER_REGISTRATION_CACHE);
    }

}
