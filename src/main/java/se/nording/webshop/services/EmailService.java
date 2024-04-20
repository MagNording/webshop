package se.nording.webshop.services;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("name@test.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (MailException e) {
            System.err.println("Failed to send email " + e.getMessage());
        }
    }

    public String regMessage(String username) {
        return "Thank you for registering!\nHi " + username
                + ",\nYour registration was successful. Welcome to our webshop!";
    }

    public String orderMessage(String username) {
        return "Thank you for your order!\nHi " + username
                + ",\nYour order has been successfully received and is now being processed.";
    }
}

