package me.identityprovider.dashboard.service;

import java.util.List;
import java.util.Optional;

import me.identityprovider.common.model.App;
import me.identityprovider.common.service.AppService;
import me.identityprovider.common.service.UserService;
import me.identityprovider.dashboard.exception.NoSuchDeveloperException;
import me.identityprovider.dashboard.repository.DeveloperRepository;
import me.identityprovider.dashboard.model.Developer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DeveloperService implements UserDetailsService {

    private DeveloperRepository developerRepository;
    private AppService appService;
    private UserService userService;

    @Autowired
    public DeveloperService(DeveloperRepository repository, UserService userService, AppService appService) {
        this.developerRepository = repository;
        this.appService = appService;
        this.userService = userService;
    }

    public Developer save(Developer developer) {
        return developerRepository.save(developer);
    }

    public Developer read(String id) throws NoSuchDeveloperException {
        Optional<Developer> dev = developerRepository.findById(id);
        if (!dev.isPresent()) {
           throw new NoSuchDeveloperException("No developer exists with given id");
        }

        return dev.get();
    }

    public boolean exists(String entityId) {
        return developerRepository.existsById(entityId);
    }

    @Async
    public void delete(String devId) {
        Optional<List<App>> optional = appService.getAppsOf(devId);
        optional.ifPresent(apps -> apps.forEach(app -> userService.deleteUsersOf(app.getId())));
        appService.deleteAppsOf(devId);
        developerRepository.deleteById(devId);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Developer> dev = developerRepository.findById(username);
        if (!dev.isPresent()) {
            throw new UsernameNotFoundException("no developer account found with email: " + username);
        }

        return dev.get();
    }
}
