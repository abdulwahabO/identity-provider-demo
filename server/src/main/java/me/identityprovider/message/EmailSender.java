package me.identityprovider.message;

import me.identityprovider.model.User;

/**
 *
 */
public interface EmailSender {

    /**
     *
     * @param user
     * @param verificationLink
     * @return
     */
    boolean sendUserVerificationEmail(User user, String verificationLink);
}
