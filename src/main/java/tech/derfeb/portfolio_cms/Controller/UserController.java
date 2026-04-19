package tech.derfeb.portfolio_cms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
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
}