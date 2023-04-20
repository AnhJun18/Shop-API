package com.myshop.api.service.email;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.myshop.repositories.user.entities.Account;
import com.nimbusds.jose.util.StandardCharset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Map;

@Service
public class EmailSenderService {
    @Autowired
    private JavaMailSender mailSender;
    private SpringTemplateEngine springTemplateEngine;

    public void sendEmail(Account account,String verifyCode)  {
        String from = "anhle180101@gmail.com";
        String to = account.getEmail();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("Quên Mật Khẩu");
            ResourceLoader loader = new DefaultResourceLoader();
            Resource resource = loader.getResource("classpath:/templates/forgot-password.html");
            Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharset.UTF_8);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(reader, "forgot-password");
            StringWriter htmlContent = new StringWriter();

            String link = "http://localhost:3000/change-pass/verify-code="+verifyCode;
            mustache.execute(htmlContent, Map.of("link", link,"name",account.getUsername())).flush();

            helper.setText(htmlContent.toString(), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
