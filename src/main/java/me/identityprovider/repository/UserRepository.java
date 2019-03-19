package me.identityprovider.repository;

import java.util.List;
import me.identityprovider.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


public interface UserRepository extends CrudRepository<User, User.UserId> {

    @Query(nativeQuery = true, value = "SELECT * FROM USERS WHERE APP_ID = ?1")
    List<User>findByAppId(String appId);

    @Query(nativeQuery = true, value = "DELETE FROM USERS WHERE APP_ID = ?1")
    void deleteAppUsers(String appId);
}
