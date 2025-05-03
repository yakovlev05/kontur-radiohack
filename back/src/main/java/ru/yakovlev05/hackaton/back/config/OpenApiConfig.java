package ru.yakovlev05.hackaton.back.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Собеседование в Контур",
                description = "API игры"
        ),
        servers = @Server(url = "/")
)
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "AdminAuth", scheme = "basic")
public class OpenApiConfig {
}
