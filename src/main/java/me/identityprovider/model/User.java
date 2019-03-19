package me.identityprovider.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "USERS")
public class User {

    @EmbeddedId
    @AttributeOverrides({@AttributeOverride(name = "appId", column = @Column(name = "APP_ID")),
                         @AttributeOverride(name = "email", column = @Column(name = "EMAIL"))})
    private UserId id;

    @Column(name = "PHONE_NUMBER")
    private String mobile;

    @Column(name = "LAST_LOGIN_DATE", nullable = false)
    private LocalDateTime lastLogin;

    @Column(name = "SIGN_UP_DATE")
    private LocalDate signupDate;

    /* Getters and Setters */

    public UserId getId() {
        return id;
    }

    public void setId(UserId id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public LocalDate getSignupDate() {
        return signupDate;
    }

    public void setSignupDate(LocalDate signupDate) {
        this.signupDate = signupDate;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Embeddable
    public static class UserId implements Serializable {

        private String email;
        private String appId;

        public UserId(String appId, String email) {
            this.appId = appId;
            this.email = email;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

}
