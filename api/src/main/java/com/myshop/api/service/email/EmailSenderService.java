package com.myshop.api.service.email;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

@Service
public class EmailSenderService {
    @Autowired
    private JavaMailSender mailSender;
    private SpringTemplateEngine springTemplateEngine;

    public void sendEmail()  {
        String from = "anhle180101@gmail.com";
        String to = "n19dccn006@student.ptithcm.edu.vn";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("Quên Mật Khẩu");

            ResourceLoader loader = new DefaultResourceLoader();
            MustacheFactory mf = new DefaultMustacheFactory(loader.getResource("classpath:/templates/").getFile());
            Mustache mustache = mf.compile( "register.html");
            StringWriter htmlContent = new StringWriter();
            mustache.execute(htmlContent, Map.of("link", "https://abc.com","name","Anh Jun"));

            helper.setText(htmlContent.toString(), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
