package com.inventario.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String OAUTH2_SCHEME = "OAuth2PasswordBearer";
    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inventario App API")
                        .description("REST API para sistema de gestión de inventario")
                        .version("1.0.0"))
                // Aplicar ambos esquemas a todos los endpoints
                .addSecurityItem(new SecurityRequirement().addList(OAUTH2_SCHEME))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                .components(new Components()
                        // OAuth2 Password Flow — muestra username/password en Swagger UI
                        .addSecuritySchemes(OAUTH2_SCHEME, new SecurityScheme()
                                .name(OAUTH2_SCHEME)
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new OAuthFlows()
                                        .password(new OAuthFlow()
                                                .tokenUrl("/api/v1/auth/login")
                                                .scopes(new Scopes()))))
                        // Bearer JWT — para pegar el token manualmente
                        .addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
                                .name(BEARER_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Pega tu token JWT aquí (sin 'Bearer ')")));
    }
}
