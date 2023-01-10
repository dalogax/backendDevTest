package com.inditex.myapp.infrastructure.config.endpoint;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public abstract class AbstractEndpointConfig {

    @NotBlank
    private String basePath;

    @NotNull
    private Integer port;
}
