package tech.derfeb.portfolio_cms.Controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import tech.derfeb.portfolio_cms.Dto.UserResponseDto;
import tech.derfeb.portfolio_cms.Dto.UserUpdateDto;
import tech.derfeb.portfolio_cms.Model.UserModel;
import tech.derfeb.portfolio_cms.Service.AuditLogService;
import tech.derfeb.portfolio_cms.Service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getProfile(@AuthenticationPrincipal String currentUsername) {
        UserModel user = userService.getUserByUsername(currentUsername);
        return ResponseEntity.ok(UserResponseDto.fromUser(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDto> updateProfile(
            @AuthenticationPrincipal String currentUsername,
            @Valid @RequestBody UserUpdateDto updateDto) {

        StringBuilder updateDetails = new StringBuilder("Profile updated: ");
        boolean hasChanges = false;

        UserModel user = userService.getUserByUsername(currentUsername);
        String oldUsername = user.getUsername();

        // Track changes for audit
        if (updateDto.getUsername() != null && !updateDto.getUsername().isBlank() 
                && !updateDto.getUsername().equals(oldUsername)) {
            updateDetails.append("username changed from '").append(oldUsername)
                    .append("' to '").append(updateDto.getUsername()).append("'; ");
            hasChanges = true;
        }

        if (updateDto.getPassword() != null && !updateDto.getPassword().isBlank()) {
            updateDetails.append("password changed; ");
            hasChanges = true;
        }

        UserModel updatedUser = userService.updateUserProfile(currentUsername, updateDto);

        // Log profile update
        if (hasChanges) {
            auditLogService.logAction(
                    "UPDATE",
                    "User",
                    updatedUser.getId(),
                    currentUsername,
                    updateDetails.toString()
            );
        }

        return ResponseEntity.ok(UserResponseDto.fromUser(updatedUser));
    }
}
