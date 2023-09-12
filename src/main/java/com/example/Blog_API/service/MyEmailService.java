package com.example.Blog_API.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
public class MyEmailService {

    @Autowired
    JavaMailSender javaMailSender;

    public void sendEmail(String to, String title, String content){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(title);
        message.setText(content);
        javaMailSender.send(message);
    }

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;


    public void sendEmailMime(){
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            messageHelper.setFrom(username);
            messageHelper.setTo(password);
            messageHelper.setSubject("Test email");
            String htmlContent = "<html><body><h1>This is a test email</h1></body></html>";
            messageHelper.setText(htmlContent, true);
        };
        javaMailSender.send(messagePreparator);
    }
}
