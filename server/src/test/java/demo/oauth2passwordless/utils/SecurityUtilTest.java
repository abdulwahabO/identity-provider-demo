package demo.oauth2passwordless.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SecurityUtilTest {

    private static final String TEST_JWT_SUBJECT = "user@example.com";

    @Test
    public void checkThatJwtEncodedCorrectly() {

        SecurityUtil.JwtClaims claims = new SecurityUtil.JwtClaims();
        String signingSecret = SecurityUtil.appSecret();

        claims.setAudience("resource-API-server");
        claims.setSubject(TEST_JWT_SUBJECT);

        String token = SecurityUtil.jwt(claims, signingSecret);

        DecodedJWT decodedJWT = JWT.decode(token);

        assertEquals(TEST_JWT_SUBJECT, decodedJWT.getSubject());
        assertEquals("HS256", decodedJWT.getAlgorithm());
        assertEquals(SecurityUtil.JWT_ISSUER, decodedJWT.getIssuer());

    }

    @Test
    public void generatedStringShouldBeCorrectLength() {
        assertEquals(7, SecurityUtil.randomCode(7).length());
        assertEquals(32, SecurityUtil.appSecret().length());
        assertEquals(32, SecurityUtil.appId().length());
    }

}
