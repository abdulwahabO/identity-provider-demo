package demo.oauth2passwordless.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import org.springframework.stereotype.Repository;

import demo.oauth2passwordless.model.App;

@Repository
public interface AppRepository extends CrudRepository<App, String> {

    @Query(nativeQuery = true, value = "SELECT * FROM APPS WHERE DEVELOPER_ID = ?1")
    List<App> findByDevId(String devId);

    @Query(nativeQuery = true, value = "DELETE FROM APP WHERE DEVELOPER_ID = ?1")
    void deleteByDevId(String devId);
}
