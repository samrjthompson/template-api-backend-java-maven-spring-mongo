package org.example.config;

import static org.example.Main.NAMESPACE;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.text.SimpleDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);

    private final AppProperties appProperties;
    private final String basePath;

    public AppConfig(AppProperties appProperties, @Value("${api.base-path}") String basePath) {
        this.appProperties = appProperties;
        this.basePath = basePath;

        MDC.clear();
        MDC.put("context_id", appProperties.contextId());

        LOGGER.info("Context ID: {}", appProperties.contextId());
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"))
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl(basePath).build();
    }
}
