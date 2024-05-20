package com.hoangtien2k3.shopappbackend.configurations;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "hoangtien2k3",
                        email = "hoangtien2k3qx1@gmail.com",
                        url = "hoangtien2k3.github.io/index.html"
                ),
                description = "Open Api documentation for Shopp App",
                title = "Open Api Shopp App (E-Ecommerce)",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache License Version 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                ),
                termsOfService = "Term of service"
        ),
        servers = {
                @Server(
                        description = "Local Environment",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "Pro Environment",
                        url = "http://tiens-mbp:8080"
                ),

        },
        security = {
                @SecurityRequirement(
                        name = "BearerAuth"
                )
        }
)
@SecurityScheme(
        name = "BearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
@Configuration
public class OpenApiConfig {
}