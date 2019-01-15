package me.identityprovider.common.repository;

import me.identityprovider.common.model.App;
import org.springframework.data.repository.CrudRepository;

public interface AppRepository extends CrudRepository<App, String> {

}
