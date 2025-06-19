package org.cheonyakplanet.be.infrastructure.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	private static final String SECURITY_SCHEME_NAME = "JWT";

	@Bean
	public OpenAPI customOpenAPI() {
		Server prodServer = new Server()
			.url("https://run.blu2print.site/api")
			.description("Production Server");

		return new OpenAPI()
			.openapi("3.0.3")
			.info(apiInfo())
			.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
			.components(securityComponents()).servers(List.of(prodServer));
	}

	private Info apiInfo() {
		return new Info()
			.title("청약플래닛 API")
			.version("1.0.0")
			.description("청약플래닛 API 문서입니다.")
			.contact(new Contact()
				.name("청약플래닛")
				.email("cheonyakplanet@gmail.com")
				.url("https://www.cheonyakplanet.site")
			);
	}

	private Components securityComponents() {
		return new Components()
			.addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
				.type(SecurityScheme.Type.HTTP)
				.scheme("bearer")
				.bearerFormat("JWT"));
	}
}
