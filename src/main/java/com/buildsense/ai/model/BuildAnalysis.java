package com.buildsense.ai.model;

public record BuildAnalysis(
        String status,
        String errorType,
        String errorMessage,
        String component,
        String recommendation,
        String sourceLocation,
        String stackTrace
) {
}