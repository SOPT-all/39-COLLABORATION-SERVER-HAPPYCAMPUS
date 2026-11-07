package org.sopt.happycampus.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
	@Bean
	OpenAPI openAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("HAPPYCAMPUS API")
				.description("SOPT 39기 합동세미나 HAPPYCAMPUS API 문서입니다.")
				.version("v1.0.0"));
	}
}
