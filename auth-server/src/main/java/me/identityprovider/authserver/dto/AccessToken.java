package me.identityprovider.authserver.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;

public class AccessToken {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Request {

        @JsonProperty(required = true, value = "client_id")
        @NotNull
        private String clientId;

        @JsonProperty(required = true, value = "client_secret")
        @NotNull
        private String clientSecret;

        @JsonProperty(required = true, value = "code")
        @NotNull
        private String authorizationCode;

        public String getAuthorizationCode() {
            return authorizationCode;
        }

        public String getClientId() {
            return clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public void setAuthorizationCode(String authorizationCode) {
            this.authorizationCode = authorizationCode;
        }
    }

    public static class Response {

        @JsonProperty(value = "scope")
        private String scope;

        @JsonProperty(value = "expires_in")
        private long expires;

        @JsonProperty(value = "access_token")
        private String acesstoken;

        public String getScope() {
            return scope;
        }

        public long getExpires() {
            return expires;
        }

        public void setExpires(long expires) {
            this.expires = expires;
        }

        public String getAcesstoken() {
            return acesstoken;
        }

        public void setAcesstoken(String acesstoken) {
            this.acesstoken = acesstoken;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }
    }

}
