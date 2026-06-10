package com.rikkeibank.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.*;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenApi() {
        final String schemeName = "bearerAuth";

        return new OpenAPI().info(new Info().title("Rikkei Bank API").version("v1").description("""
                        Banking API
                        JWT Authentication
                        Refresh Rotation
                        eKYC
                        Money Transfer
                        """)).addSecurityItem(new SecurityRequirement().addList(schemeName))
                .components(new Components().addSecuritySchemes(schemeName,
                        new SecurityScheme().name(schemeName).type(SecurityScheme.Type.HTTP)
                                .scheme("bearer").bearerFormat("JWT")));
    }
}