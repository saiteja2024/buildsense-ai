package com.buildsense.ai.service;

import com.buildsense.ai.model.AiBuildAnalysis;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class AiBuildAnalyzer {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public AiBuildAnalyzer(
            ChatClient.Builder chatClientBuilder,
            ObjectMapper objectMapper) {

        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public AiBuildAnalysis analyzeBuildLog(String buildLog) {

        String response = chatClient
                .prompt()
                .system("""
                        You are BuildSense AI, an expert CI/CD build failure
                        diagnosis assistant.
                        
                        Analyze the supplied build log.
                        
                        Return ONLY valid JSON.
                        Do not use markdown.
                        Do not include ```json.
                        
                        Use exactly this structure:
                        {
                          "failureType": "...",
                          "rootCause": "...",
                          "recommendation": "...",
                          "additionalChecks": "...",
                          "confidence": "HIGH|MEDIUM|LOW"
                        }
                        
                        Only state facts that are directly supported by the build log.
                        
                        When the log does not contain enough information to determine the root cause,
                        say "Insufficient information in build log" rather than guessing.
                        
                        For test failures, distinguish between:
                        - test failures/assertion failures
                        - test errors/exceptions
                        - insufficient test coverage
                        
                        Do not classify a failure as insufficient test coverage unless the log explicitly
                        mentions coverage or a coverage threshold failure..
                        """)
                .user(buildLog)
                .call()
                .content();

        try {
            return objectMapper.readValue(response, AiBuildAnalysis.class);
        } catch (Exception e) {

            String repairedResponse = response.trim();

            if (repairedResponse.startsWith("{")
                    && !repairedResponse.endsWith("}")) {
                repairedResponse = repairedResponse + "}";
            }

            try {
                return objectMapper.readValue(
                        repairedResponse,
                        AiBuildAnalysis.class
                );
            } catch (Exception retryException) {
                throw new IllegalStateException(
                        "Unable to parse AI response as JSON: " + response,
                        retryException
                );
            }
        }
    }
}