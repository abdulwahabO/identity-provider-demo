package me.oauth2providerdemo.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;

public class AccessToken {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Request {

        @JsonProperty(required = true, value = "client_id")
        @NotEmpty
        private String clientId;

        @JsonProperty(required = true, value = "client_secret")
        @NotEmpty
        private String clientSecret;

        @JsonProperty(required = true, value = "code")
        @NotEmpty
        private String authorizationCode;

        public Request() {
            
        }

        public Request(String code, String id, String secret) {
            this.authorizationCode = code;
            this.clientId = id;
            this.clientSecret = secret;
        }

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
        private String acessToken;

        public String getScope() {
            return scope;
        }

        public long getExpires() {
            return expires;
        }

        public void setExpires(long expires) {
            this.expires = expires;
        }

        public String getAcessToken() {
            return acessToken;
        }

        public void setAcessToken(String acessToken) {
            this.acessToken = acessToken;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }
    }

}
