package org.cheonyakplanet.be.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.cheonyakplanet.be.infrastructure.jwt.JwtAuthenticationFilter;
import org.cheonyakplanet.be.infrastructure.jwt.JwtAuthorizationFilter;
import org.cheonyakplanet.be.infrastructure.jwt.JwtUtil;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsServiceImpl;
import org.cheonyakplanet.be.presentation.exception.CustomAccessDeniedHandler;
import org.cheonyakplanet.be.presentation.exception.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter= new JwtAuthenticationFilter(jwtUtil);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() throws Exception {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable());

        http.exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(customAuthenticationEntryPoint) // 인증 실패 처리
                .accessDeniedHandler(customAccessDeniedHandler)           // 권한 부족 처리
        );

        http.formLogin((form)->form.disable());
        http.sessionManagement((sessionManagement)->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth
                // 1) Swagger/OpenAPI 문서 경로 전부 허용
                .requestMatchers(
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/error"
                ).permitAll()

                // 2) 정적 리소스 (CSS, JS, 이미지 등)
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

                // 3) 회원가입/로그인 등 공개 API
                .requestMatchers("/api/member/**").permitAll()

                // 4) 나머지는 인증필요
                .anyRequest().authenticated()
        );

        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}
