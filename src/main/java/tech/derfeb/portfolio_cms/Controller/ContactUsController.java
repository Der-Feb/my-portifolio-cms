package tech.derfeb.portfolio_cms.Controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tech.derfeb.portfolio_cms.Dto.ContactRequestDto;
import tech.derfeb.portfolio_cms.Service.AuditLogService;
import tech.derfeb.portfolio_cms.Service.EmailService;

import java.util.Map;

/**
 * Controller for handling "Contact Us" messages.
 */
@RestController
@RequestMapping("api/contact-us")
public class ContactUsController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuditLogService auditLogService;

    @PostMapping
    public ResponseEntity<?> sendContactMessage(@Valid @RequestBody ContactRequestDto request) {
        try {
            emailService.sendContactMail(
                    request.getEmail(),
                    request.getName(),
                    request.getMessage());

            // Log contact message sent
            auditLogService.logAction(
                    "CONTACT_MESSAGE",
                    "ContactUs",
                    "N/A",
                    request.getEmail(),
                    String.format("Contact message sent by %s (%s)", request.getName(), request.getEmail())
            );

            return ResponseEntity.ok(Map.of("message", "Transmission received."));
        } catch (Exception e) {
            // Log failed contact attempt
            auditLogService.logAction(
                    "CONTACT_MESSAGE_FAILED",
                    "ContactUs",
                    "N/A",
                    request.getEmail(),
                    String.format("Failed to send contact message: %s", e.getMessage())
            );
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
