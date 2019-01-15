package me.identityprovider.common.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CLIENT_APP")
public class App {

    @Id
    private String id;

    @Column(name = "SECRET", nullable = false)
    private String secret; // todo: use Generator.

    @Column(name = "GRANT_TYPE", nullable = false)
    private GrantType grantType;

    @Column(name = "LOGIN_REDIRECT_URL")
    private String loginRedirect;

    @ManyToOne
    @JoinColumn(name = "DEVELOPER_ID")
    private Developer developer;

    public Developer getDeveloper() {
        return developer;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }

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

    public enum GrantType {
        IMPLICIT,
        AUTH_CODE
    }

}
