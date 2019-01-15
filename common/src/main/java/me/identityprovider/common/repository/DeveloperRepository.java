package me.identityprovider.common.repository;

import me.identityprovider.common.model.Developer;
import org.springframework.data.repository.CrudRepository;

public interface DeveloperRepository extends CrudRepository<Developer, String> {

}
