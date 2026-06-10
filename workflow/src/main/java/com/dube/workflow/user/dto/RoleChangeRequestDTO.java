package com.dube.workflow.user.dto;

import jakarta.validation.constraints.NotBlank;

public class RoleChangeRequestDTO {
    @NotBlank(message = "Target role name is required")
    private String roleName; // e.g., "ROLE_ADMIN", "ROLE_USER"

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
}