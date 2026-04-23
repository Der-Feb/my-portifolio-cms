package tech.derfeb.portfolio_cms.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Injected from application.properties / .env
    @Value("${spring.mail.to}")
    private String adminEmail;

    @Value("${app.mail.from}")
    private String appNoReplyEmail;

    /**
     * Generates the Cyberpunk-styled HTML template for the email.
     */
    public String contactHtml(String name, String email, String message) {
        return "<div style=\"background-color: #0b0d17; color: #ffffff; padding: 40px; font-family: 'Helvetica', Arial, sans-serif; max-width: 600px; border: 2px solid #f38d31;\">" +
                "<div style=\"margin-bottom: 30px;\">" +
                "<div style=\"color: #f38d31; font-size: 12px; letter-spacing: 3px; text-transform: uppercase; margin-bottom: 5px;\">" +
                "◆ SECURE TRANSMISSION RECEIVED" +
                "</div>" +
                "<h1 style=\"font-size: 32px; margin: 0; text-transform: uppercase; font-family: 'Arial Black', sans-serif;\">" +
                "NEW <span style=\"color: #f38d31;\">MESSAGE</span>" +
                "</h1>" +
                "</div>" +

                "<div style=\"background-color: #121521; padding: 20px; border: 1px solid #1a1e2e; margin-bottom: 30px;\">" +
                "<div style=\"margin-bottom: 15px;\">" +
                "<label style=\"color: #f38d31; font-size: 10px; letter-spacing: 1px; text-transform: uppercase; display: block; margin-bottom: 5px;\">Identifier (Name)</label>" +
                "<div style=\"font-size: 16px; color: #ffffff; font-weight: bold;\">" + name + "</div>" +
                "</div>" +

                "<div style=\"margin-bottom: 15px;\">" +
                "<label style=\"color: #f38d31; font-size: 10px; letter-spacing: 1px; text-transform: uppercase; display: block; margin-bottom: 5px;\">Channel (Email)</label>" +
                "<div style=\"font-size: 16px; color: #ffffff;\">" + email + "</div>" +
                "</div>" +

                "<div>" +
                "<label style=\"color: #f38d31; font-size: 10px; letter-spacing: 1px; text-transform: uppercase; display: block; margin-bottom: 5px;\">Message Content</label>" +
                "<div style=\"font-size: 15px; color: #d1d1d1; line-height: 1.6; white-space: pre-wrap;\">" + message + "</div>" +
                "</div>" +
                "</div>" +

                "<div style=\"margin-top: 40px; padding-top: 20px; border-top: 1px solid #1a1e2e; font-size: 10px; color: #444; letter-spacing: 2px; text-transform: uppercase; text-align: center;\">" +
                "BUGINGO ERIC DERICK — PORTFOLIO VOL. I" +
                "</div>" +
                "</div>";
    }

    /**
     * Core sending logic using MimeMessage.
     * Note: 'from' is your app email, 'replyTo' is the user's email.
     */
    public void sendEmail(String to, String replyTo, String subject, String text, String html) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(appNoReplyEmail);
            helper.setTo(to);
            helper.setReplyTo(replyTo);
            helper.setSubject(subject);

            // If text is provided, it acts as the fallback for non-HTML clients
            if (text != null) {
                helper.setText(text, html);
            } else {
                helper.setText(html, true);
            }

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Public method to be called from your Controller.
     */
    public void sendContactMail(String userEmail, String name, String msg) {
        String htmlBody = contactHtml(name, userEmail, msg);
        String subject = "PORTFOLIO TRANSMISSION: " + name.toUpperCase();

        // We send TO our admin email, and set REPLY-TO to the user
        sendEmail(adminEmail, userEmail, subject, "New message from " + name + ": " + msg, htmlBody);
    }
}