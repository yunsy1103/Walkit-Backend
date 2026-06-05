package com.walkit.walkit.global.config;

import com.walkit.walkit.global.security.jwt.JwtAuthenticationFilter;
import com.walkit.walkit.global.security.oauth.CustomOAuth2UserService;
import com.walkit.walkit.global.security.oauth.OAuth2LoginFailureHandler;
import com.walkit.walkit.global.security.oauth.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/", "/error", "/favicon.ico", "/actuator/health").permitAll()
                        .requestMatchers("/.well-known/**").permitAll()  // Apple domain verification
                        .requestMatchers("/images/upload", "/images/download/**", "/images/delete/**", "/oauth2/**", "/login/oauth2/**").permitAll()
                        .requestMatchers("/auth/refresh", "/auth/logout", "/auth/kakao", "/auth/naver", "/auth/apple", "/login").permitAll()
                        .requestMatchers("/users/nickname/**", "/users/summary/nickname/**").permitAll()
                        .requestMatchers("/api/callback/apple").permitAll()  // Apple OAuth callback
                        .requestMatchers("/api/auth/apple/token").permitAll()  // Apple SDK identityToken login
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll() // Prometheus
                        .requestMatchers("/items").permitAll()
                        .requestMatchers("/spots/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo ->
                            userInfo.userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                            response.setStatus(HttpStatus.UNAUTHORIZED.value())
                        )
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
