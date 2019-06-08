package demo.oauth2passwordless.message;

/**
 *
 */
public interface TextMessageService {

    /**
     *
     * @param password
     * @param number
     * @return
     */
    boolean sendOneTimePassword(String password, String number);

}
