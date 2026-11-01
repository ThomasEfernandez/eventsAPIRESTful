package com.gestion.eventos.api.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration 
public class OpenApiConfig {
    private static final String SCHEME_NAME = "bearerAuth";
    private static final String SCHEME_FORMAT = "JWT";
    private static final String DESCRIPTION = "JWT Authentication para la API de Eventos.";

    @Bean 
    public OpenAPI openAPI (){
        
        return new OpenAPI()
        .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME))
        .components(new Components()
        .addSecuritySchemes(SCHEME_NAME, new SecurityScheme()
        .name(SCHEME_NAME)
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat(SCHEME_FORMAT)
        .in(SecurityScheme.In.HEADER)
        .description(DESCRIPTION)
       )
    )
        .info(new Info()
            .title("API de Gestión de Eventos")
            .description("API RESTful para la gestión de eventos, categorías y ponentes.")
            .version("1.0.0")
            .contact(new Contact()
                .name("Thomas Fernandez")
                .email("thomasfernandezit@outlook.com")
                .url("https://tuportfolio.com")
            )
            .license(new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0.html")
            )




        );
    }
    
}
