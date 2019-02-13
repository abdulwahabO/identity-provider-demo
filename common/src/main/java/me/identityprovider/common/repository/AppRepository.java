package me.identityprovider.common.repository;

import java.util.List;
import java.util.Optional;
import me.identityprovider.common.model.App;
import me.identityprovider.common.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface AppRepository extends CrudRepository<App, String> {

    // todo: write at query
    @Query(nativeQuery = true, name = "....todo...")
    List<App> findDevId(String devId);

}
