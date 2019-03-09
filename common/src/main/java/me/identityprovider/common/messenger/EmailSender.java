package me.identityprovider.common.messenger;

import me.identityprovider.common.model.User;

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
