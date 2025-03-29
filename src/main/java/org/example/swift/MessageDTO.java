package org.example.swift;

import lombok.Builder;

@Builder
public record MessageDTO(
        String message
) {
}
