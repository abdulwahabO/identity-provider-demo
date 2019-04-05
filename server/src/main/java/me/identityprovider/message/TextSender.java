package me.identityprovider.message;

/**
 *
 */
public interface TextSender {

    /**
     *
     * @param password
     * @param number
     * @return
     */
    boolean sendOneTimePassword(String password, String number);

}
