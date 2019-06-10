package demo.oauth2passwordless.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import demo.oauth2passwordless.exception.AppCreationException;
import demo.oauth2passwordless.exception.NoSuchAppException;
import demo.oauth2passwordless.model.App;
import demo.oauth2passwordless.repository.AppRepository;
import demo.oauth2passwordless.utils.SecurityUtil;

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

    public App read(String appId) throws NoSuchAppException {
        Optional<App> app = appRepository.findById(appId);
        if (!app.isPresent()) {
            throw new NoSuchAppException("App Id did not match any existing apps");
        }
        return app.get();
    }

    public void delete(String id) {
        appRepository.deleteById(id);
    }

    public boolean exists(String entityId) {
        return appRepository.existsById(entityId);
    }

    private boolean isRedirectValid(String url) {
        return true;
    }

}
