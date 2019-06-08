package demo.oauth2passwordless.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "APPS")
public class App {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "SECRET", nullable = false)
    private String secret;

    @Column(nullable = false, name = "APP_NAME")
    private String name;

    @Column(name = "GRANT_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private GrantType grantType;

    @Column(name = "LOGIN_REDIRECT_URL")
    private String loginRedirect;

    @Column(name = "HOME_PAGE")
    private String homePage;

    @Column(name = "API_ID")
    private String apiId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public GrantType getGrantType() {
        return grantType;
    }

    public void setGrantType(GrantType grantType) {
        this.grantType = grantType;
    }

    public String getLoginRedirect() {
        return loginRedirect;
    }

    public void setLoginRedirect(String loginRedirect) {
        this.loginRedirect = loginRedirect;
    }

    public String getHomePage() {
        return homePage;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public enum GrantType {
        IMPLICIT,
        AUTH_CODE
    }

}
