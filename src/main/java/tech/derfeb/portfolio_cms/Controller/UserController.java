package tech.derfeb.portfolio_cms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import tech.derfeb.portfolio_cms.Dto.RoleDto;
import tech.derfeb.portfolio_cms.Dto.UserUpdateDto;
import tech.derfeb.portfolio_cms.Model.RoleModel;
import tech.derfeb.portfolio_cms.Model.UserModel;
import tech.derfeb.portfolio_cms.Repository.RoleRepository;
import tech.derfeb.portfolio_cms.Repository.UserRepository;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    public ResponseEntity<?> getProfile() {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        UserModel user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(@RequestBody UserUpdateDto updateDto) {

        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        System.out.println("[LOG] current user: " + currentUserId);

        UserModel user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (updateDto.getUsername() != null && !updateDto.getUsername().isBlank()) {
            user.setUsername(updateDto.getUsername());
        }

        if (updateDto.getPassword() != null && !updateDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        }

        if (updateDto.getRoles() != null && !updateDto.getRoles().isEmpty()) {
            Set<RoleModel> newRoles = updateDto.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());

            user.setRoles(newRoles);
        }

        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "User updated successfully"));
    }

    @PutMapping("/:{id}")
    public ResponseEntity<?> grantRevokeRole(@PathVariable String id, @RequestBody RoleDto roleDto) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        UserModel currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Logged-In User not found"));

        boolean canChangeRoles = currentUser.getRoles().stream().anyMatch(
                role -> role.getName().equals("ROLE_ADMIN"));

        if (!canChangeRoles) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "You don't have permission to change roles"));
        }

        UserModel targetUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RoleModel targetRole = roleRepository.findByName(roleDto.getRoleName())
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleDto.getRoleName()));

        if (roleDto.getGrant()) {
            targetUser.getRoles().add(targetRole);
        } else {
            targetUser.getRoles().remove(targetRole);
        }

        userRepository.save(targetUser);

        return ResponseEntity.ok(Map.of("message", "Role changed successfully"));
    }
}