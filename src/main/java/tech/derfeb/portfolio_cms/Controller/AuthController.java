package tech.derfeb.portfolio_cms.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tech.derfeb.portfolio_cms.Dto.AuthRequestDto;
import tech.derfeb.portfolio_cms.Dto.UserResponseDto;
import tech.derfeb.portfolio_cms.Repository.UserRepository;
import tech.derfeb.portfolio_cms.Service.AuditLogService;
import tech.derfeb.portfolio_cms.security.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private JwtService jwtService;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private AuditLogService auditLogService;

        @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody AuthRequestDto request) {
                return userRepository.findByUsername(request.getUsername())
                                .map(user -> {
                                        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                                                String token = jwtService.generateToken(user.getId(),
                                                                user.getUsername());

                                                ResponseCookie cookie = ResponseCookie.from("jwt_token", token)
                                                                .httpOnly(true)
                                                                .secure(false)
                                                                .path("/")
                                                                .maxAge(60 * 60 * 24)
                                                                .sameSite("Lax")
                                                                .build();

                                                // Log successful login
                                                auditLogService.logAction(
                                                        "LOGIN",
                                                        "Auth",
                                                        user.getId(),
                                                        user.getUsername(),
                                                        "User logged in successfully"
                                                );

                                                return ResponseEntity.ok()
                                                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                                                .body(Map.of(
                                                                        "message", "Login successful",
                                                                        "user", UserResponseDto.fromUser(user)
                                                                ));
                                        }

                                        // Log failed login attempt
                                        auditLogService.logAction(
                                                "LOGIN_FAILED",
                                                "Auth",
                                                "N/A",
                                                request.getUsername(),
                                                "Failed login attempt - invalid credentials"
                                        );

                                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                        .body(Map.of("error", "Invalid credentials"));
                                })
                                .orElseGet(() -> {
                                        // Log failed login attempt for non-existent user
                                        auditLogService.logAction(
                                                "LOGIN_FAILED",
                                                "Auth",
                                                "N/A",
                                                request.getUsername(),
                                                "Failed login attempt - user not found"
                                        );
                                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                        .body(Map.of("error", "User not found"));
                                });
        }

        @PostMapping("/logout")
        public ResponseEntity<?> logout(@org.springframework.security.core.annotation.AuthenticationPrincipal String currentUsername) {
                ResponseCookie cookie = ResponseCookie.from("jwt_token", "")
                                .httpOnly(true)
                                .path("/")
                                .maxAge(0)
                                .build();

                // Log logout action
                String username = (currentUsername != null) ? currentUsername : "anonymous";
                auditLogService.logAction(
                        "LOGOUT",
                        "Auth",
                        "N/A",
                        username,
                        "User logged out"
                );

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                                .body(Map.of("message", "Logout successful"));
        }
}
