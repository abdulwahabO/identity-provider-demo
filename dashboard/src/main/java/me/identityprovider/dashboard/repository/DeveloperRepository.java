package me.identityprovider.dashboard.repository;

import me.identityprovider.dashboard.model.Developer;
import org.springframework.data.repository.CrudRepository;

public interface DeveloperRepository extends CrudRepository<Developer, String> {

}
