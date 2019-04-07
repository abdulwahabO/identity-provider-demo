package me.oauth2providerdemo.client.http;

import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import me.oauth2providerdemo.client.dto.AccessToken;

public interface OAuthServerClient {

    @RequestLine("POST /login/accesstoken")
    @Headers("Content-type: application/json")
    @Body("%7B\"client_id\": \"{client_id}\", \"client_secret\": \"{client_secret}\", \"code\": \"{code}\" %7D")
    AccessToken.Response accessToken(@Param("client_id") String id, @Param("client_secret") String secret, @Param("code") String code);

}
