package me.identityprovider.service;

import java.util.List;
import java.util.Optional;
import me.identityprovider.exception.AppCreationException;
import me.identityprovider.exception.NoSuchAppException;
import me.identityprovider.model.App;
import me.identityprovider.repository.AppRepository;

import me.identityprovider.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppService {

    private AppRepository appRepository;

    @Autowired
    public AppService(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    /**
     * Create or update an {@link App}.
     *
     * @param app
     * @return
     * @throws AppCreationException
     */
    public App save(App app) throws AppCreationException {

        boolean redirectValid = isRedirectValid(app.getLoginRedirect());
        if (!redirectValid) {
            throw new AppCreationException("The redirect url is invalid");
        }

        app.setSecret(SecurityUtil.appSecret());
        app.setId(SecurityUtil.appId());

        return appRepository.save(app);
    }

    /**
     * Find an app by its id.
     *
     * @param appId id of the app to find.
     * @return an {@link App} whose id matches the id supplied.
     * @throws NoSuchAppException if there is no app with the given id
     */
    public App read(String appId) throws NoSuchAppException {
        Optional<App> app = appRepository.findById(appId);
        if (!app.isPresent()) {
            throw new NoSuchAppException("App Id did not match any existing apps");
        }
        return app.get();
    }

    /**
     * Delete app with given id.
     * @param id id of the app to delete.
     */
    public void delete(String id) {
        appRepository.deleteById(id);
    }

    /**
     * @return true if an app exists with the given id.
     */
    public boolean exists(String entityId) {
        return appRepository.existsById(entityId);
    }

    private boolean isRedirectValid(String url) {
        return true;
    }

}
