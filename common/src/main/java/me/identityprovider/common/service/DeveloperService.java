package me.identityprovider.common.service;

import me.identityprovider.common.model.Developer;
import me.identityprovider.common.repository.DeveloperRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeveloperService extends BaseService<Developer, String> {

    @Autowired
    public DeveloperService(DeveloperRepository repository) {
        super(repository);
    }

}
