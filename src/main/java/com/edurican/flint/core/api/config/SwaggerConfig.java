package com.edurican.flint.core.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Flint API")
                .version("v1.0.0")
                .description("Flint API 명세서");

        return new OpenAPI()
                .components(new Components())
                .info(info);
    }
}
