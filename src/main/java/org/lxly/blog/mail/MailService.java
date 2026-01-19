package org.lxly.blog.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private final org.springframework.core.env.Environment env;

    @Async
    public void sendVerifyCode(String toEmail, String type, String code) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setSubject("LXLY Blog - " + type + " Verification Code");
        msg.setText("Your verification code is: " + code + "\nThis code expires in 10 minutes.");
        String from = env.getProperty("spring.mail.username");
        if (from != null) {
            msg.setFrom(from);
        }
        mailSender.send(msg);
    }
}
