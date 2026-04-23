package tech.derfeb.portfolio_cms.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.apache.commons.validator.routines.EmailValidator;

import tech.derfeb.portfolio_cms.Exception.InvalidInputException;

import java.util.regex.Pattern;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public String contactHtml(String name, String email, String message) {
        return "<div style=\"background-color: #0b0d17; color: #ffffff; padding: 40px; font-family: 'Helvetica', Arial, sans-serif; max-width: 600px; border: 2px solid #f38d31;\">" +
                // Header section
                "<div style=\"margin-bottom: 30px;\">" +
                "<div style=\"color: #f38d31; font-size: 12px; letter-spacing: 3px; text-transform: uppercase; margin-bottom: 5px;\">" +
                "◆ SECURE TRANSMISSION RECEIVED" +
                "</div>" +
                "<h1 style=\"font-size: 32px; margin: 0; text-transform: uppercase; font-family: 'Arial Black', sans-serif;\">" +
                "NEW <span style=\"color: #f38d31;\">MESSAGE</span>" +
                "</h1>" +
                "</div>" +

                // Data Table
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

                // Action Button
                // "<div style=\"text-align: center;\">" +
                // "<a href=\"mailto:" + email + "\" style=\"background-color: #f38d31; color: #000000; text-decoration: none; padding: 15px 40px; font-weight: bold; text-transform: uppercase; font-size: 14px; display: inline-block; letter-spacing: 2px;\">" +
                // "REPLY TO TRANSMISSION ⚡" +
                // "</a>" +
                // "</div>" +

                // Footer
                "<div style=\"margin-top: 40px; padding-top: 20px; border-top: 1px solid #1a1e2e; font-size: 10px; color: #444; letter-spacing: 2px; text-transform: uppercase; text-align: center;\">" +
                "BUGINGO ERIC DERICK — PORTFOLIO VOL. I" +
                "</div>" +
                "</div>";
    }

    public void verifyEmail(String email) throws InvalidInputException {
        boolean isValid = EmailValidator.getInstance().isValid(email);

        if (!isValid) {
            throw new InvalidInputException(
                    InvalidInputException.InputTypes.Email,
                    "Invalid email format: " + email
            );
        }
    }

    public void sendEmail(String to, String from, String subject, String text, String html) {

    }
}
