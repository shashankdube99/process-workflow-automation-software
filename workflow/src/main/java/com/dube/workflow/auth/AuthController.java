package com.dube.workflow.auth;

import com.dube.workflow.auth.dto.AuthResponse;   
import com.dube.workflow.auth.dto.LoginRequest;   
import com.dube.workflow.user.User;                
import com.dube.workflow.user.UserRepository;      
import com.dube.workflow.security.JwtTokenProvider;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthController(UserRepository userRepository, 
                          PasswordEncoder passwordEncoder, 
                          JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
        	// 🖨️ TEMPORARY: This will print an official, perfectly structured hash for "admin123"
        	String nativeHash = passwordEncoder.encode("admin123");
        	System.out.println("====== COPY THIS NATIVE HASH ======\n" + nativeHash + "\n==================================");
            // 1. Locate the user profile row
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Invalid Email or Password Credentials"));

            // 2. Validate password
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                throw new RuntimeException("Invalid Email or Password Credentials");
            }

            // 3. Process dynamic token creation
            String roleName = user.getRole().getRoleName();
            String token = tokenProvider.generateToken(user.getEmail(), roleName);
            
            return ResponseEntity.ok(new AuthResponse(token, roleName));
            
        } catch (RuntimeException e) {
            // 🏆 CATCH THE CRASH: Prevents Spring Security from masking it as a 403 Forbidden
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", e.getMessage()));
        }
    }
}