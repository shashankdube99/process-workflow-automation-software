package com.dube.workflow.user;

import com.dube.workflow.user.dto.RoleChangeRequestDTO;
import com.dube.workflow.user.dto.UserCreateRequestDTO;
import com.dube.workflow.user.dto.UserResponseDTO;
import com.dube.workflow.user.dto.UserUpdateRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 🚀 1. POST /api/users (Admin Panel User Creation)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> createNewUser(@Valid @RequestBody UserCreateRequestDTO createRequest) {
        UserResponseDTO createdUser = userService.createUser(createRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // 🚀 2. GET /api/users (Fetch complete user pool)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsersList();
        return ResponseEntity.ok(users);
    }

    // 🚀 3. GET /api/users/{id} (Fetch an individual profile by its UUID)
    // For safety, users can see their own profile, but ADMINs can see anyone's profile
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(authentication, #id)")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID id) {
        // Since your current interface uses email for single lookups, we can leverage your profile retrieval
        // Or if you prefer to expand your interface later, you can map this cleanly.
        // For now, let's pull all users and filter, or fetch via context safely:
        UserResponseDTO userProfile = userService.getAllUsersList().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return ResponseEntity.ok(userProfile);
    }

    // 🚀 4. PUT /api/users/{id} (Modify an individual user's details by UUID)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateUserById(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequestDTO updateRequest) {
        
        // Find the user's email first to utilize your existing updateUserProfile logic cleanly
        UserResponseDTO targetUser = userService.getAllUsersList().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        UserResponseDTO updatedUser = userService.updateUserProfile(targetUser.getEmail(), updateRequest);
        return ResponseEntity.ok(updatedUser);
    }

 // 🚀 5. PATCH /api/users/{id}/deactivate (Uses your softDelete method to flip status to "INACTIVE")
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateUserAccount(
            @PathVariable UUID id, 
            @AuthenticationPrincipal Object principal) {
        
        String adminEmail = "SYSTEM_ADMIN";
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            adminEmail = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            adminEmail = (String) principal;
        }
        
        userService.softDeleteUser(id, adminEmail);
        return ResponseEntity.noContent().build();
    }

    // 🚀 6. PATCH /api/users/{id}/activate (Allows admins to re-enable user access)
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> activateUserAccount(
            @PathVariable UUID id,
            @AuthenticationPrincipal String adminEmail) {
        
        // Find the user's email first to safely process the operation
        UserResponseDTO targetUser = userService.getAllUsersList().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        // Re-using the role change pipeline or status updates to force activate back to state
        RoleChangeRequestDTO refreshRole = new RoleChangeRequestDTO();
        refreshRole.setRoleName(targetUser.getRole());
        
        // This triggers a save which can re-verify permissions and activate if your implementation handles status flipping
        UserResponseDTO updatedUser = userService.activateUser(id);
        return ResponseEntity.ok(updatedUser);
    }
}