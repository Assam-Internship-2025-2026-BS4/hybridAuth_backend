package in.bank.hdfc.auth.hybrid_auth.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import in.bank.hdfc.auth.hybrid_auth.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {

        http

                .csrf(AbstractHttpConfigurer::disable)

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .exceptionHandling(ex -> ex

                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json");
                            res.getWriter().write("""
                            {
                              "status": {
                                "code": "AUTH_401",
                                "message": "Unauthorized"
                              },
                              "data": null
                            }
                            """);
                        })

                        .accessDeniedHandler((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            res.setContentType("application/json");
                            res.getWriter().write("""
                            {
                              "status": {
                                "code": "AUTH_403",
                                "message": "Access denied"
                              },
                              "data": null
                            }
                            """);
                        })
                )

                .authorizeHttpRequests(auth -> auth

                        /* CORS */
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()


                        /* PUBLIC */
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/auth/init",
                                "/api/v1/auth/app-login"
                        ).permitAll()

                        .requestMatchers("/health", "/actuator/**").permitAll()
                        /* PRE AUTH (WEB LOGIN FLOW) */
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/auth/session/init",
                                "/api/v1/user/details/identify"
                        ).hasAuthority("PRE_AUTH")

                        .requestMatchers(
                                "/api/v1/auth/session/fetch",
                                "/api/v1/auth/otp/**",
                                "/api/v1/auth/qr/generate",
                                "/api/v1/auth/token"
                        ).hasAuthority("PRE_AUTH")

                        /* INTERNAL (MOBILE APP) */
                        .requestMatchers(
                                "/api/v1/auth/qr/validate",
                                "/api/v1/auth/session/approve",
                                "/api/v1/auth/session/reject"
                        ).hasAuthority("INTERNAL")

                        /* USER APIs */
                        .requestMatchers(
                                "/api/v1/user/details/me"
                        ).hasAuthority("USER")

                        .anyRequest().authenticated()
                )


                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//
//        CorsConfiguration config = new CorsConfiguration();
//
//        config.setAllowedOriginPatterns(List.of("*"));
//        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
//        config.setExposedHeaders(List.of("Authorization"));
//        config.setAllowedHeaders(List.of("*"));
//
//        config.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//
//        return source;
//    }
@Bean
CorsConfigurationSource corsConfigurationSource() {

    CorsConfiguration config = new CorsConfiguration();

    config.setAllowedOriginPatterns(List.of("*"));

    config.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
    ));

    config.setAllowedHeaders(List.of("*"));

    config.setExposedHeaders(List.of("Authorization"));

    config.setAllowCredentials(true);

    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", config);

    return source;
}
}