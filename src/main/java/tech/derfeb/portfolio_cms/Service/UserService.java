package tech.derfeb.portfolio_cms.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.derfeb.portfolio_cms.Dto.UserUpdateDto;
import tech.derfeb.portfolio_cms.Model.UserModel;
import tech.derfeb.portfolio_cms.Repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserModel getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public UserModel updateUserProfile(String username, UserUpdateDto updateDto) {
        UserModel user = getUserByUsername(username);

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

        // Roles cannot be changed via profile update - security restriction
        if (updateDto.getRoles() != null && !updateDto.getRoles().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Cannot change roles via this endpoint. Contact an administrator.");
        }

        return userRepository.save(user);
    }
}
