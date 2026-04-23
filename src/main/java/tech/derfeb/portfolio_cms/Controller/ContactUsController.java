package tech.derfeb.portfolio_cms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tech.derfeb.portfolio_cms.Dto.ContactRequestDto;
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

    @PostMapping
    public ResponseEntity<?> sendContactMessage(@RequestBody ContactRequestDto request) {
        try {
            emailService.sendContactMail(
                    request.getEmail(),
                    request.getName(),
                    request.getMessage());

            return ResponseEntity.ok(Map.of("message", "Transmission received."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
