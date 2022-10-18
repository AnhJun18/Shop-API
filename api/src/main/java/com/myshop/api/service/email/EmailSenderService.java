package com.myshop.api.service.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailSenderService {
    @Autowired
    private JavaMailSender mailSender;
    private SpringTemplateEngine springTemplateEngine;

    public void sendEmail() {
        String from = "anhle180101@gmail.com";
        String to = "n19dccn006@student.ptithcm.edu.vn";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);


        try {
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("This is an HTML email");
            Context ctx = new Context();
            String  htmlContent = springTemplateEngine.process("register",ctx);

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
