package tech.derfeb.portfolio_cms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import tech.derfeb.portfolio_cms.Dto.RoleDto;
import tech.derfeb.portfolio_cms.Dto.UserResponseDto;
import tech.derfeb.portfolio_cms.Dto.UserUpdateDto;
import tech.derfeb.portfolio_cms.Model.RoleModel;
import tech.derfeb.portfolio_cms.Model.UserModel;
import tech.derfeb.portfolio_cms.Repository.RoleRepository;
import tech.derfeb.portfolio_cms.Repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getProfile(@AuthenticationPrincipal String currentUsername) {
        UserModel user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return ResponseEntity.ok(UserResponseDto.fromUser(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDto> updateProfile(
            @AuthenticationPrincipal String currentUsername,
            @RequestBody UserUpdateDto updateDto) {

        UserModel user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Update username with uniqueness check
        if (updateDto.getUsername() != null && !updateDto.getUsername().isBlank()) {
            String newUsername = updateDto.getUsername().trim();
            if (!newUsername.equals(user.getUsername())) {
                if (userRepository.findByUsername(newUsername).isPresent()) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
                }
                user.setUsername(newUsername);
            }
        }

        // Update password
        if (updateDto.getPassword() != null && !updateDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        }

        // Roles cannot be changed via /me endpoint - security restriction
        if (updateDto.getRoles() != null && !updateDto.getRoles().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Cannot change roles via this endpoint. Contact an administrator.");
        }

        userRepository.save(user);

        return ResponseEntity.ok(UserResponseDto.fromUser(user));
    }

    @PutMapping("/{id}/roles")
    public ResponseEntity<UserResponseDto> grantRevokeRole(
            @AuthenticationPrincipal String currentUsername,
            @PathVariable String id,
            @RequestBody RoleDto roleDto) {

        UserModel currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Current user not found"));

        // Check if current user is admin
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You don't have permission to change roles");
        }

        UserModel targetUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        RoleModel targetRole = roleRepository.findByName(roleDto.getRoleName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Role not found: " + roleDto.getRoleName()));

        if (roleDto.getGrant()) {
            targetUser.getRoles().add(targetRole);
        } else {
            targetUser.getRoles().remove(targetRole);
        }

        userRepository.save(targetUser);

        return ResponseEntity.ok(UserResponseDto.fromUser(targetUser));
    }
}
