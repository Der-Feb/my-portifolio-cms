package tech.derfeb.portfolio_cms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import tech.derfeb.portfolio_cms.Dto.AvailabilityStatusDto;
import tech.derfeb.portfolio_cms.Service.AuditLogService;
import tech.derfeb.portfolio_cms.Service.AvailabilityService;

/**
 * Controller for managing availability status (available for work).
 * Provides both SSE (real-time) and polling endpoints.
 */
@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private AuditLogService auditLogService;

    /**
     * PUBLIC endpoint - Get current availability status (for frontend polling)
     * No authentication required - public visitors can check status
     */
    @GetMapping("/status")
    public ResponseEntity<AvailabilityStatusDto> getStatus() {
        Boolean status = availabilityService.getAvailabilityStatus();
        return ResponseEntity.ok(new AvailabilityStatusDto(status));
    }

    /**
     * AUTHENTICATED endpoint - Update availability status
     * Only the authenticated user can update their status
     */
    @PutMapping("/status")
    public ResponseEntity<AvailabilityStatusDto> updateStatus(
            @AuthenticationPrincipal String currentUsername,
            @RequestBody AvailabilityStatusDto statusDto) {

        Boolean newStatus = availabilityService.updateAvailabilityStatus(
                currentUsername,
                statusDto.getAvailableForWork()
        );

        // Log status change
        auditLogService.logAction(
                "UPDATE",
                "Availability",
                "N/A",
                currentUsername,
                String.format("Availability status changed to: %s", newStatus ? "Available" : "Not Available")
        );

        return ResponseEntity.ok(new AvailabilityStatusDto(newStatus));
    }

    /**
     * SSE endpoint - Real-time updates for availability status
     * Clients connect and receive instant updates when status changes
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamStatus() {
        return availabilityService.createEmitter();
    }
}
