package com.dube.workflow.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Workflow Automation Platform API",
                version = "1.0",
                description = "API documentation for the Workflow Automation System"
        ),
        security = @SecurityRequirement(name = "BearerAuth") // This applies the security globally
)
@SecurityScheme(
        name = "BearerAuth",
        description = "JWT authentication. Type 'Bearer ' followed by your token if your UI requires it, or just paste the token.",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}