package ru.yakovlev05.hackaton.back.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Собеседование в Контур",
                description = "API игры"
        ),
        servers = @Server(url = "/")
)
public class OpenApiConfig {
}
