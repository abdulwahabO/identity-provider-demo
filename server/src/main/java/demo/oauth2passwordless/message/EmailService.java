package demo.oauth2passwordless.message;

import demo.oauth2passwordless.model.User;

/**
 * Implementations of this interface send email.
 */
public interface EmailService {

    /**
     *
     * @param user
     * @param verificationLink
     * @return
     */
    boolean sendUserVerificationEmail(User user, String verificationLink);
}
