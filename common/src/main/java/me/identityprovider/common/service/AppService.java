package me.identityprovider.common.service;

import java.util.List;
import java.util.Optional;
import me.identityprovider.common.exception.AppCreationException;
import me.identityprovider.common.exception.NoSuchAppException;
import me.identityprovider.common.model.App;
import me.identityprovider.common.repository.AppRepository;

import me.identityprovider.common.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppService {

    private AppRepository appRepository;

    @Autowired
    public AppService(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    public App save(App app) throws AppCreationException {

        boolean redirectValid = isRedirectValid(app.getLoginRedirect());

        if (!redirectValid) {
            throw new AppCreationException("The redirect url is invalid");
        }

        app.setSecret(SecurityUtil.appSecret());
        app.setId(SecurityUtil.appId());

        return appRepository.save(app);
    }

    // todo: javadoc
    public App read(String appId) throws NoSuchAppException {
        Optional<App> app = appRepository.findById(appId);
        if (!app.isPresent()) {
            throw new NoSuchAppException("App Id did not match any existing apps");
        }
        return app.get();
    }

    public Optional<List<App>> getAppsOf(String devId) {
        return Optional.of(appRepository.findDevId(devId));
    }

    public void deleteAppsOf(String devId) {

    }

    public void delete(String id) {
        appRepository.deleteById(id);
    }

    public boolean exists(String entityId) {
        return appRepository.existsById(entityId);
    }

    private boolean isRedirectValid(String url) {

        // todo: among other things, it must be a subdomain of the homepage ?? domain??
        return true;
    }

}
