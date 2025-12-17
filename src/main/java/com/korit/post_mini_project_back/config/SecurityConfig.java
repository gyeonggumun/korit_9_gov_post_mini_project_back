package com.korit.post_mini_project_back.config;

import com.korit.post_mini_project_back.filter.JwtAuthenticationFilter;
import com.korit.post_mini_project_back.security.JwtAuthenticationEntryPoint;
import com.korit.post_mini_project_back.security.OAuth2SuccessHandler;
import com.korit.post_mini_project_back.service.OAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    //CSR 방식이라서 하는 세팅
    @Bean  // Security에서 꼭 세팅 필요
    public SecurityFilterChain FilterChain(HttpSecurity http) throws Exception{
        // 모든 세팅들은 다 람다로 들어감

        // CrossOrigin 적용
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // 세션 비활성화(무상태) 설정 - 세션 안쓰겠다 => 토큰 인증방식 사용?
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // http 기본 로그인 비활성화
        http.httpBasic(httpBasic -> httpBasic.disable());

        //form 로그인 비활성화
        http.formLogin(formLogin -> formLogin.disable());

        //csrf 비활성화 - 워터마크 -> 대체: JWT 토큰 사용 (로그인시, CSR 만 가능)
        http.csrf(csrf -> csrf.disable());

        http.oauth2Login(oauth2 ->
                oauth2.userInfoEndpoint(userinfo -> userinfo.userService(oAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
        );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // 모든 요청(URL)에 대해 인증 없이 누구나 접근할 수 있도록 허용하는 설정
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/api/auth/**").permitAll();
            auth.requestMatchers("/v3/api-docs/**").permitAll();
            auth.requestMatchers("/swagger-ui/**").permitAll();
            auth.requestMatchers("/swagger-ui.html").permitAll();
            auth.requestMatchers("/doc").permitAll();
            auth.anyRequest().authenticated();
        });
        // 회원가입은 인증이 필요없어야함 - 필터 안 거쳐야함
        // 로그인 - 인증 필요없음
        // 로그인 완료 시 토큰 지급다음

        http.exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint));

        return http.build(); // 예외처리 필요
    }

    @Bean
    //CorsConfigurationSource ->  reactive안붙은거 사용
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        // 5173, 5174 프론트앤드 URL 주소로 접속하는 사람들 다 허용
        cors.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:5174"));
        // 허용해주고 싶은 요청들만 CrossOrigin 풀어주기
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // 모든 Header 다 허용
        cors.setAllowedHeaders(List.of("*"));
        // 인증을 할 때 인증서 등의 쿠키 허용
        cors.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //  /** : 모든 URL에 요청을 다 허용 -> 프론트앤드에서 서버로 요청을 날릴 수 있다.
        source.registerCorsConfiguration("/**", cors);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder();
    }
}