package demo.oauth2passwordless.message;

/**
 * Implementations of this interface send text messages to mobile numbers.
 */
public interface TextMessageService {

    /**
     * Sends a one-time password(OTP) to a mobile number.
     * 
     * @param password The password.
     * @param number The destination.
     * 
     * @return true if successfull. false otherwise.
     */
    boolean sendOneTimePassword(String password, String number);

}
