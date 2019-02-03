package me.identityprovider.common.message.impl;

import me.identityprovider.common.message.TextSender;
import org.springframework.stereotype.Service;

@Service
public class TextSenderImpl implements TextSender {

    @Override
    public boolean sendOneTimePassword(String password, String number) {

        return false;
    }
}
