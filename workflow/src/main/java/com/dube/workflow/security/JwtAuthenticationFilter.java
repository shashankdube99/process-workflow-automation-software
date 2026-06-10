package com.dube.workflow.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// OncePerRequestFilter guarantees this code executes exactly once per API request
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. Extract the raw token string from the Authorization header
            String jwt = getJwtFromRequest(request);

            // 2. Validate token structural integrity and expiration
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String email = tokenProvider.getEmailFromJWT(jwt);
                String role = tokenProvider.getRoleFromJWT(jwt);

                // 3. Convert the role string into a format Spring Security expects ("ROLE_ADMIN")
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

                // 4. Build an authentication token object containing user details
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(email, null, List.of(authority));
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 5. Hand the user details over to Spring's security context for the rest of the request lifecycle
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        // 6. Pass the request along to the next filter in line
        filterChain.doFilter(request, response);
    }

    // Helper method to look into headers and strip away the "Bearer " prefix
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}