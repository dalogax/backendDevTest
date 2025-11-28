package dev.molaya.tests.application.out.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ProductCommand(@NotNull String id) {}
