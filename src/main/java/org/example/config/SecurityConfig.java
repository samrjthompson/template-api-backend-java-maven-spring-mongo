package org.example.config;

import org.example.security.RequestValidationFilter;
import org.example.security.UserAuthorities;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private static final String WRITE_AUTHORITY = UserAuthorities.fromValue(UserAuthorities.WRITE);
    private static final String READ_AUTHORITY = UserAuthorities.fromValue(UserAuthorities.READ);

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
        http.authorizeHttpRequests(
                c -> c.requestMatchers(HttpMethod.GET, "/hello").hasAnyAuthority(WRITE_AUTHORITY, READ_AUTHORITY)
                        .requestMatchers(HttpMethod.GET, "/hello/private").hasAnyAuthority(WRITE_AUTHORITY)
                        .anyRequest().authenticated() // All other requests required authentication
        );

        return http.build();
    }
}
