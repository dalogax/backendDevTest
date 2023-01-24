package com.inditex.myapp.infrastructure.config.endpoint;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@NoArgsConstructor
@Validated
@Configuration
@ConfigurationProperties(prefix = "endpoints")
public class EndpointsConfig {

    private ExistingApiEndpointConfig existingApi;
}
