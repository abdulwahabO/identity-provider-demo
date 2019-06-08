package demo.oauth2passwordless.exception;

public class NoSuchUserException extends Exception {

    public NoSuchUserException(String message) {
        super(message);
    }
}
