package me.identityprovider.common.repository;

import java.util.List;

import me.identityprovider.common.model.User;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, User.UserId> {

    // todo: write at query
    @Query(nativeQuery = true, name = "....todo...")
    List<User>findByAppId(String appId);

    // todo: delete users where app Id = 
    @Query()
    void deleteAppUsers(String appId);
}
