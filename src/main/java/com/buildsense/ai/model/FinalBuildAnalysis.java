package com.buildsense.ai.model;

public record FinalBuildAnalysis(
        String status,
        String errorType,
        String component,
        String rootCause,
        String recommendation,
        String confidence,
        String sourceLocation
) {
}