package tech.derfeb.portfolio_cms.Dto;

import lombok.Data;

@Data
public class RoleDto {
    private String roleName;
    private Boolean grant; // true grant, false revoke
}