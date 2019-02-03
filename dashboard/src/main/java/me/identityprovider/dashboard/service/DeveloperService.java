package me.identityprovider.dashboard.service;


import java.util.Optional;
import me.identityprovider.common.service.AppService;
import me.identityprovider.dashboard.exception.DeveloperCreationException;
import me.identityprovider.dashboard.repository.DeveloperRepository;
import me.identityprovider.dashboard.security.Developer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DeveloperService implements UserDetailsService {

    private AppService appService;
    private DeveloperRepository developerRepository;

    @Autowired
    public DeveloperService(DeveloperRepository repository, AppService appService) {
        this.developerRepository = repository;
        this.appService = appService;
    }

    public Developer save(Developer developer) {
        return developerRepository.save(developer);
    }

    public Developer read(String id) throws Exception { // todo: throw a more specific, no such Developer??
        Optional<Developer> dev = developerRepository.findById(id);
        if (!dev.isPresent()) {
            throw new DeveloperCreationException("");
        }

        return dev.get();
    }

    public boolean exists(String entityId) {
        return developerRepository.existsById(entityId);
    }

    public void delete(String id) {
        developerRepository.deleteById(id);
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
