package org.example.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.text.ParseException;
import java.util.Base64;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

@Configuration
public class SecurityConfig {

    private final String encodedRsaKey;
    private final String loginUrl;

    public SecurityConfig(@Value("${rsa.key}") String encodedRsaKey, @Value("${login.url}") String loginUrl) {
        this.encodedRsaKey = encodedRsaKey;
        this.loginUrl = loginUrl;
    }

    @Bean
    @Order(0)
    public SecurityFilterChain asFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer oAuth2AuthorizationServerConfigurer =
                OAuth2AuthorizationServerConfigurer
                        .authorizationServer()
                        .oidc(Customizer.withDefaults());

        http.securityMatcher(oAuth2AuthorizationServerConfigurer.getEndpointsMatcher());
        http.with(oAuth2AuthorizationServerConfigurer, Customizer.withDefaults());
        http.authorizeHttpRequests(
                c -> c.anyRequest().authenticated()
        );

        http.exceptionHandling(e ->
                e.authenticationEntryPoint(
                        new LoginUrlAuthenticationEntryPoint(loginUrl))
        );

        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
        http.formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails adminUser = User.withUsername("bill")
                .password("password")
                .authorities("admin")
                .build();

        UserDetails readUser = User.withUsername("john")
                .password("password")
                .authorities("read")
                .build();

        return new InMemoryUserDetailsManager(adminUser, readUser);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient registeredClient =
                RegisteredClient.withId(UUID.randomUUID().toString())
                        .clientId("client") // Client's username
                        .clientSecret("secret") // Client's password
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .redirectUri("https://www.manning.com/authorized") // This is a redirect back to the client
                        .scope(OidcScopes.OPENID)
                        .build();

        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws ParseException {
        String json = new String(Base64.getDecoder().decode(encodedRsaKey));
        RSAKey rsaKey = RSAKey.parse(json);

//        JWKSet jwkSet = new JWKSet(RSAKeyUtils.rsaKeyGenerator());
        JWKSet jwkSet = new JWKSet(rsaKey);

        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            JwtClaimsSet.Builder claims = context.getClaims();
            claims.claim("authorities",
                    context.getPrincipal().getAuthorities()); // Gets authorities assigned to the User logged in
        };
    }
}
