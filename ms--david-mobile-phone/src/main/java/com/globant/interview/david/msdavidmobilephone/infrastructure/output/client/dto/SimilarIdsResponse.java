package com.globant.interview.david.msdavidmobilephone.infrastructure.output.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SimilarIdsResponse(List<String> ids) {

    public SimilarIdsResponse {
        if (ids == null) {
            ids = List.of();
        }
    }
}
