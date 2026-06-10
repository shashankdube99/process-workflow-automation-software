package com.dube.workflow.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // 1. Pull values directly from your application.yml
    @Value("${app.jwt.secret}")
    private String jwtSecret;
//    private String jwtSecret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationInMs;
//    private int jwtExpirationInMs = 86400000;

    // Decode our hex/base64 secret key into a secure cryptographic key object
    private Key getSigningKey() {
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(this.jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 🚀 GENERATE TOKEN: Replaces the old mock string
    public String generateToken(String email, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(email)                 // Subject identifier (User email)
                .claim("role", role)               // Custom claim (User role for access control)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Sign it with our secret key
                .compact();                        // Mash it all into a single encrypted string
    }

    // 🔍 PARSE TOKEN: Extract data back out when a user sends it to us
    public String getEmailFromJWT(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getRoleFromJWT(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    // 🛡️ VALIDATE TOKEN: Ensure the token hasn't expired or been tampered with
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // Catches ExpiredJwtException, MalformedJwtException, SignatureException, etc.
            return false; 
        }
    }
}