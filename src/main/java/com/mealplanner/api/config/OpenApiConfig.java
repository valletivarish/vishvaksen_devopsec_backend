package com.mealplanner.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for automatic API documentation.
 * Configures JWT Bearer authentication scheme so Swagger UI can
 * send authenticated requests for testing protected endpoints.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smart Recipe Meal Planner API")
                        .version("1.0.0")
                        .description("REST API for meal planning with nutritional analysis and forecasting")
                        .contact(new Contact()
                                .name("Vishvaksen Machana")
                                .email("vishvaksen.machana@student.ncirl.ie")))
                /* Add JWT Bearer authentication to all API endpoints */
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .bearerFormat("JWT")
                                        .scheme("bearer")));
    }
}
