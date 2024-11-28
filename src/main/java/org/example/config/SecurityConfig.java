package org.example.config;

import org.example.security.RequestValidationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final RequestValidationFilter requestValidationFilter;
    private final AuthenticationProvider customAuthenticationProvider;

    public SecurityConfig(RequestValidationFilter requestValidationFilter,
                          AuthenticationProvider customAuthenticationProvider) {
        this.requestValidationFilter = requestValidationFilter;
        this.customAuthenticationProvider = customAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(requestValidationFilter, UsernamePasswordAuthenticationFilter.class);

        http.httpBasic(Customizer.withDefaults());
        http.authenticationProvider(customAuthenticationProvider);
        http.authorizeHttpRequests(c -> c.anyRequest().authenticated());

        return http.build();
    }
}
