package com.buildsense.ai.service;

import com.buildsense.ai.model.BuildAnalysis;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BuildLogAnalyzer {

    public BuildAnalysis analyze(String buildLog) {

        if (buildLog.contains("Cannot find symbol")) {

            String component = extractComponent(buildLog);

            return new BuildAnalysis(
                    "FAILED",
                    "COMPILATION_ERROR",
                    "Cannot find symbol",
                    component,
                    "Check whether the class exists, is imported correctly, or is available as a project dependency.",
                    extractSourceLocation(buildLog)
            );
        }
        if (buildLog.contains("Could not resolve dependencies")
                || buildLog.contains("Could not find artifact")
                || buildLog.contains("DependencyResolutionException")) {

            return new BuildAnalysis(
                    "FAILED",
                    "DEPENDENCY_ERROR",
                    "Maven dependency resolution failed",
                    null,
                    "Check the dependency version, repository configuration, and Maven connectivity.",
                    extractSourceLocation(buildLog)
            );
        }
        if (buildLog.contains("Failures:")
                || buildLog.contains("There are test failures")
                || buildLog.contains("maven-surefire-plugin")
                || buildLog.contains("AssertionError")) {

            return new BuildAnalysis(
                    "FAILED",
                    "TEST_FAILURE",
                    "Automated tests failed",
                    null,
                    "Review the failing test cases, assertion errors, and test reports.",
                    extractSourceLocation(buildLog)
            );
        }
        if (buildLog.contains("NullPointerException")) {

            String errorMessage = extractRuntimeError(buildLog);
            String component = extractNpeComponent(buildLog);

            return new BuildAnalysis(
                    "FAILED",
                    "RUNTIME_ERROR",
                    errorMessage,
                    component,
                    "Identify the object that is null and check its initialization, dependency injection, or null-handling logic.",
                    extractSourceLocation(buildLog)
            );
        }
        return new BuildAnalysis(
                "FAILED",
                "UNKNOWN",
                "Unable to identify the specific error.",
                null,
                "Review the build log for the root cause.",
                extractSourceLocation(buildLog)
        );
    }

    private String extractComponent(String buildLog) {

        String marker = "Cannot find symbol";

        int index = buildLog.indexOf(marker);

        if (index >= 0) {
            String remaining = buildLog.substring(index + marker.length()).trim();

            if (!remaining.isEmpty()) {
                return remaining;
            }
        }

        return "Unknown";
    }

    private String extractRuntimeError(String buildLog) {

        int index = buildLog.indexOf("NullPointerException");

        if (index >= 0) {
            String remaining = buildLog.substring(index).trim();

            if (!remaining.isEmpty()) {
                return remaining;
            }
        }

        return "NullPointerException";
    }

    private String extractNpeComponent(String buildLog) {

        String marker = "Cannot invoke \"";

        int start = buildLog.indexOf(marker);

        if (start >= 0) {
            start += marker.length();

            int end = buildLog.indexOf(".", start);

            if (end > start) {
                return buildLog.substring(start, end);
            }
        }

        return null;
    }

    private String extractSourceLocation(String buildLog) {

        Pattern pattern = Pattern.compile("([\\w$.-]+\\.java:\\d+)");

        Matcher matcher = pattern.matcher(buildLog);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }
}


//Open AI key: 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