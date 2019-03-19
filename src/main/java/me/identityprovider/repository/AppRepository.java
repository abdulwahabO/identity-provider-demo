package me.identityprovider.repository;

import java.util.List;

import me.identityprovider.model.App;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


import org.springframework.stereotype.Repository;

@Repository
public interface AppRepository extends CrudRepository<App, String> {

    @Query(nativeQuery = true, value = "SELECT * FROM APPS WHERE DEVELOPER_ID = ?1")
    List<App> findByDevId(String devId);

    @Query(nativeQuery = true, value = "DELETE FROM APP WHERE DEVELOPER_ID = ?1")
    void deleteByDevId(String devId);
}
