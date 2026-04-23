package tech.derfeb.portfolio_cms.Dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Set;

@Data
public class UserUpdateDto {
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private Set<String> roles;
}