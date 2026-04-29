package com.resumeiq.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${app.backend-url:http://localhost:8080/api/v1}")
    private String backendUrl;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("ResumeIQ API")
                .version("1.0.0")
                .description("AI-Powered ATS Resume Analyzer SaaS Platform — REST API Documentation")
                .contact(new Contact()
                    .name("ResumeIQ Team")
                    .email("support@resumeiq.com")
                    .url("https://resumeiq.com"))
                .license(new License().name("MIT License")))
            .servers(List.of(
                new Server().url(backendUrl).description("Development Server"),
                new Server().url("https://api.resumeiq.com/api/v1").description("Production Server")))
            .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .name("Authorization")
                    .description("Enter JWT Bearer token")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
