package com.buildsense.ai.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface BuildSenseAgent {

    record DiagnosisResult(
            String rootCause,
            String recommendation,
            String confidence
    ) {}

    @SystemMessage("""
            You are an autonomous Build Failure Resolution Agent for Maven Java applications.
            Use available tools (`getSourceCodeSnippet`, `getMavenDescriptors`, `getRecentCommitHistory`, `getRecentDiff`) to inspect files, POM dependencies, and Git context.
            Return the final analysis strictly matching the requested schema.
            """)
    @UserMessage("""
            Analyze this build failure:

            DETECTED ERROR TYPE: {{errorType}}
            DETECTED ERROR MESSAGE: {{errorMessage}}
            DETECTED COMPONENT: {{component}}
            SOURCE LOCATION: {{sourceLocation}}

            STACK TRACE:
            {{stackTrace}}

            BUILD LOG:
            {{buildLog}}
            """)
    DiagnosisResult diagnose(@V("errorType") String errorType,
                             @V("errorMessage") String errorMessage,
                             @V("component") String component,
                             @V("sourceLocation") String sourceLocation,
                             @V("stackTrace") String stackTrace,
                             @V("buildLog") String buildLog);
}