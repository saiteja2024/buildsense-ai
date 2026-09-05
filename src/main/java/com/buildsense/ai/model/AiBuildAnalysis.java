package com.buildsense.ai.model;

public record AiBuildAnalysis(
        String failureType,
        String rootCause,
        String recommendation,
        String additionalChecks,
        String confidence
) {
}