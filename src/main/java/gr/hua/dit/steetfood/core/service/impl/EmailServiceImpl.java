package gr.hua.dit.steetfood.core.service.impl;

import gr.hua.dit.steetfood.core.service.EmailService;

import jakarta.mail.MessagingException;

import jakarta.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Value ("${MAIL_USERNAME}")
    private String mailUsername;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    @Override
    public void sendSimpleEmail(String to,  String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom(mailUsername);

            mailSender.send(message);
        }catch (MailException e){
            LOGGER.error("SIMPLE MAIL ERROR: ",e);
            throw new RuntimeException("Email could not be sent"+e.getMessage()+"\n Dont Worry! Your Action Perfomed!");

        }
    }
    @Async //Async γιατι δεν θελω οταν αλλαζει η κατασταση να περιμενει στην οθονη ο χρηστης μεχρι να σταλθει το mail
    @Override
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            helper.setFrom(mailUsername);

            mailSender.send(message);

        } catch (MessagingException e) {
            LOGGER.error("MAIL ERROR: ",e);
            throw new RuntimeException("Email failed to send", e);
        }
    }
    @Async
    @Override
    public void sendOrderStartEmail(String to, Long orderId) {

        String subject = "Your order has Started!";
        String url= "http://localhost/orders/" + orderId;;
        String body ="""
        <h3>Your order has started</h3>
        <p>
        Click
        <a href="%s">here</a>
        to review your order.
    </p>
""".formatted(url);
    sendHtmlEmail(to, subject, body);
    }

    @Async
    @Override
    public void sendOrderDeniedEmail(String to, Long orderId) {
        String subject = "Your order has been denied!";
        String url= "http://localhost/orders/" + orderId;
        String body ="""
        <h3>Your order has been denied</h3>
        <p>
        Click
        <a href="%s">here</a>
        to review your order.
    </p>
""".formatted(url);
        sendHtmlEmail(to, subject, body);
    }
}
