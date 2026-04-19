package tech.derfeb.portfolio_cms.Dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserUpdateDto {
    private String username;
    private String password;
    private Set<String> roles;
}