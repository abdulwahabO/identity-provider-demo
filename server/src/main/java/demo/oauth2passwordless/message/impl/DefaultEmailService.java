package demo.oauth2passwordless.message.impl;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import demo.oauth2passwordless.message.EmailService;
import demo.oauth2passwordless.model.User;

@Service
public class DefaultEmailService implements EmailService {

    private Logger logger = LoggerFactory.getLogger(EmailService.class);
    private JavaMailSender javaMailSender;
    private TemplateEngine templateEngine;

    @Autowired
    public DefaultEmailService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public boolean sendUserVerificationEmail(User user, String verificationLink) {

        MimeMessage message = javaMailSender.createMimeMessage();
        Context ctx = new Context();
        ctx.setVariable("link", verificationLink);
        String content = templateEngine.process("verification-email", ctx);

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, false);
            helper.setTo(user.getId().getEmail());
            helper.setFrom("noreply@id-provider.me");
            helper.setSubject("Account Verification");
            helper.setText(content, true);

            javaMailSender.send(message);

        } catch(MessagingException | MailException e){
            logger.error(e.getMessage());
            return false;
        }

        return true;
    }
}
