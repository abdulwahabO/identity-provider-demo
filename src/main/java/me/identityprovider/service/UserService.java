package me.identityprovider.service;

import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;

import me.identityprovider.dto.UserDto;
import me.identityprovider.exception.NoSuchUserException;
import me.identityprovider.message.EmailSender;
import me.identityprovider.model.User;
import me.identityprovider.repository.UserRepository;
import me.identityprovider.utils.SecurityUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final String USER_REGISTRATION_CACHE = "user.registration";
    private static final String EMAIL_URL_PATH = "/signup/finish?code=";

    private EmailSender emailSender;
    private UserRepository userRepository;
    private CacheManager cacheManager;
    private Cache cache;

    @Value("${authserver.baseUrl}")
    private String baseUrl;

    @Autowired
    public UserService(UserRepository repository, CacheManager cacheManager, EmailSender emailSender) {
        this.cacheManager = cacheManager;
        this.userRepository = repository;
        this.emailSender = emailSender;
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

    /**
     * Finds and returns a user by id.
     * @throws NoSuchUserException if there is no user with given id.
     */
    public User read(User.UserId id) throws NoSuchUserException {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            throw new NoSuchUserException("no user with given id");
        }
        return user.get();
    }

    /**
     * Returns all the users of a given app.
     */
    public Optional<List<User>> getUsersOf(String appId) {
        List<User> users = userRepository.findByAppId(appId);
        return Optional.of(users);
    }

    /**
     * Delete all the users of a given app.
     * @param appId the app id
     */
    public void deleteUsersOf(String appId) {
        userRepository.deleteAppUsers(appId);
    }

    /**
     * Starts off the process of creating a new user.
     * @param dto the data for the new user
     * @return true if a verification link has been sent to the user's email, false otherwise.
     */
    public boolean createUser(UserDto dto) {

        User user = new User();
        user.setId(new User.UserId(dto.getAppId(), dto.getEmail()));
        user.setMobile(dto.getPhone());

        String token = SecurityUtil.randomCode(6);
        cache.put(token, user);
        String verifyUrl = baseUrl + EMAIL_URL_PATH + token;

        return emailSender.sendUserVerificationEmail(user, verifyUrl);
    }

    /**
     * Checks if sign-up token is valid and returns the user it's associated with.
     */
    public Optional<User> checkToken(String token) {
        Cache.ValueWrapper wrapper = cache.get(token);
        if (wrapper == null) {
            Optional.empty();
        }
        User user = (User) wrapper.get();
        return Optional.of(user);
    }

    @PostConstruct
    public void init(){
        cache = cacheManager.getCache(USER_REGISTRATION_CACHE);
    }
}
