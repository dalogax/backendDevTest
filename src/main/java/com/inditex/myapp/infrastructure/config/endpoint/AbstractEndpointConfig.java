package com.inditex.myapp.infrastructure.config.endpoint;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public abstract class AbstractEndpointConfig {

    @NotBlank
    private String url;
}
