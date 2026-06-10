package com.dube.workflow.auth.dto;

public class AuthResponse {
    private String accessToken;
    private String role;

    public AuthResponse(String accessToken, String role) {
        this.accessToken = accessToken;
        this.role = role;
    }

    // Getters and Setters for Eclipse
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}