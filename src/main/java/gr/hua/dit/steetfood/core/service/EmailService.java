package gr.hua.dit.steetfood.core.service;

import org.aspectj.lang.annotation.Aspect;


public interface EmailService {

    void sendSimpleEmail(String to, String subject, String body);

    void sendHtmlEmail(String to, String subject, String htmlBody);

    void sendOrderStartEmail (String to, Long orderId);

    void sendOrderDeniedEmail (String to, Long orderId);
}
