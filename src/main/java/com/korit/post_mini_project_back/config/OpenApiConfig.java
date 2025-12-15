package com.korit.post_mini_project_back.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    // 스웨거 JWT 토큰을 입력 할 수 있는 셋팅
    @Bean
    public OpenAPI openAPIConfig() {
        OpenAPI openAPI = new OpenAPI();
        Info info = new Info();
        info.title("소셜 게시판 미니 프로젝트");   // 제목
        info.version("1.0");  // 버전
        info.description("소셜 게시판 미니 프로젝트."); // 설명

        SecurityRequirement securityRequirement = new SecurityRequirement();
        securityRequirement.addList("Bearer Authentication"); // Bearer는 JWT토큰의 종류

        // JWT인증을 적용하겠다는 코드
        SecurityScheme securityScheme = new SecurityScheme();
        securityScheme.name("Bearer Authentication");
        securityScheme.type(SecurityScheme.Type.HTTP);
        securityScheme.scheme("bearer");
        securityScheme.bearerFormat("JWT");

        // Bearer토큰에 대한 셋팅을 추가해주는 코드
        Components components = new Components();
        components.addSecuritySchemes("Bearer Authentication", securityScheme);

        //SecurityRequirementController에서 처럼 개별적으로 적용 가능
        return openAPI
                .info(info)
                .addSecurityItem(securityRequirement) // 이 부분 비활성화시 적용이 다꺼짐
                .components(components);

    }
}
