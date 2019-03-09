package me.identityprovider.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

public class SecurityUtil {

    private static final String JWT_ISSUER = "auth-server";

    /**
     * Returns a random String of numeric characters.
     *
     * @param length the length of the String
     * @return a random code.
     */
    public static String randomCode(int length) {
        Random random = new Random();
        return String.valueOf(random.nextInt(length));
    }

    /**
     * Returns a 256 bit String that is used as id for apps.
     *
     * @return The app id
     */
    public static String appId() {
        return generateKey();
    }

    /**
     * Returns a 256 bit String that is used as secret key for apps.
     *
     * @return The secret
     */
    public static String appSecret() {
        return generateKey();
    }

    private static String generateKey() {

        StringBuilder charactersBuilder = new StringBuilder();

        for (char character = 'a'; character <= 'z'; character++) {
            charactersBuilder.append(character);
        }
        for (char character = 'A'; character <= 'Z'; character++) {
            charactersBuilder.append(character);
        }
        for (int character = 0; character <= 9; character++) {
            charactersBuilder.append(character);
        }

        char[] chars = charactersBuilder.toString().toCharArray();

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 32; i++) {
            Random random = new Random();
            int next = random.nextInt(chars.length);
            builder.append(chars[next]);
        }

        return builder.toString();
    }

    /**
     * Returns a signed JWT.
     *
     * @param claims - a {@link JwtClaims}
     * @param signingSecret - the app secret
     * @return an Optional which if not empty would contain a signed JWT.
     */
    public static Optional<String> jwt(JwtClaims claims, String signingSecret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(signingSecret);
            String token = JWT.create()
                              .withIssuedAt(new Date())
                              .withIssuer(JWT_ISSUER)
                              .withAudience(claims.getAudience())
                              .withSubject(claims.getSubject())
                              .withExpiresAt(Date.from(Instant.now().plusSeconds(350000)))
                              .sign(algorithm);
            return Optional.of(token);
        } catch (JWTCreationException e) {
            return Optional.empty();
        }
    }

    /**
     * DTO for the claims that should go into a JWT.
     */
    public static class JwtClaims {

        private String audience;
        private String subject;

        public String getAudience() {
            return audience;
        }

        public void setAudience(String audience) {
            this.audience = audience;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }
    }
}
