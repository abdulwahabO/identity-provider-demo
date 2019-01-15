package me.identityprovider.common.utils;

import java.util.Map;

public class Generator {

    // todo: consider renaming this class to SecurityUtils


    public static String randomCode(int length) {

        // todo: generate code to be used as OTP in SMS , sign-up token or authorization code in OAuth dance.

        return null;
    }

    public static String appId() {


        return null;
    }

    public static String appSecret() {

        // todo: use java.security.Key to generate a strong key.

        // todo: should be at least 32 bytes long, will be used to sign JWT.

        return null;
    }

    public static String jwt(Map<String, String> claims, String signingSecret) {

        // todo: decide on what goes into the JWT tokwn.


        // todo: use JJWT library to prepare this.

        return null;
    }

}
