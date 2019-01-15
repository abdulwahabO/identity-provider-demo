package me.identityprovider.common.message;

// todo: javadoc
public interface TextSender {

    // todo: javadoc
    boolean sendOneTimePassword(String password, String number);

    // todo: Impl with a spring managed bean for a specific provider.

}
