package com.buildsense.ai.rag;

import com.buildsense.ai.model.BuildAnalysis;
import com.buildsense.ai.model.FinalBuildAnalysis;
import com.buildsense.ai.repository.GitService;
import com.buildsense.ai.repository.MavenContextService;
import com.buildsense.ai.repository.RepositorySourceService;
import com.buildsense.ai.service.BuildLogAnalyzer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagDiagnosisService {

    private final RagSearchService ragSearchService;
    private final ChatModel chatModel;
    private final BuildLogAnalyzer buildLogAnalyzer;
    private final RepositorySourceService repositorySourceService;
    private final MavenContextService mavenContextService;
    private final GitService gitService;
    private final ObjectMapper objectMapper;

    public RagDiagnosisService(
            RagSearchService ragSearchService,
            ChatModel chatModel,
            BuildLogAnalyzer buildLogAnalyzer,
            RepositorySourceService repositorySourceService,
            MavenContextService mavenContextService,
            GitService gitService,
            ObjectMapper objectMapper) {

        this.ragSearchService = ragSearchService;
        this.chatModel = chatModel;
        this.buildLogAnalyzer = buildLogAnalyzer;
        this.repositorySourceService = repositorySourceService;
        this.mavenContextService = mavenContextService;
        this.gitService = gitService;
        this.objectMapper = objectMapper;
    }

    public FinalBuildAnalysis diagnose(String buildLog) {

        // 1. Deterministic analysis
        BuildAnalysis analysis = buildLogAnalyzer.analyze(buildLog);

        // 2. Targeted RAG retrieval
        List<EmbeddingMatch<TextSegment>> matches =
                ragSearchService.search(
                        analysis.errorType(),
                        analysis.errorMessage(),
                        analysis.component(),
                        analysis.sourceLocation()
                );

        // 3. Build retrieved knowledge
        StringBuilder knowledge = new StringBuilder();
        for (var match : matches) {
            knowledge.append(match.embedded().text())
                    .append("\n\n");
        }

        // 4. Extract repository source code snippet
        String sourceCodeContext = extractSourceCodeContext(analysis.sourceLocation());

        // 5. Extract Maven pom.xml / properties context
        String mavenContext = mavenContextService.getMavenContext(analysis.errorType());

        // 6. Extract recent Git commits / diffs
        String gitContext = gitService.getRecentCommit();

        // 7. Ask Llama for structured JSON
        String prompt = """
                You are BuildSense AI, an expert Java and Maven build failure analyzer.

                Analyze the build failure using the provided repository source code, Maven context, Git history, and engineering knowledge.

                IMPORTANT:
                - The deterministic analyzer has already identified the error type.
                - Do not change the detected error type.
                - Use the stack trace, source location, and source code snippet as primary evidence.
                - Use Maven descriptors or Git context if relevant to the failure.
                - Use the retrieved knowledge as supporting context.
                - Return ONLY valid JSON matching the requested structure.
                - Do not attempt to invoke functions or tools.
                - Do not use markdown.
                - Do not include ```json or ```.

                DETECTED ERROR TYPE:
                %s

                DETECTED ERROR MESSAGE:
                %s

                DETECTED COMPONENT:
                %s

                SOURCE LOCATION:
                %s

                SOURCE CODE SNIPPET:
                %s

                MAVEN & CONFIGURATION CONTEXT:
                %s

                GIT HISTORY & DIFF:
                %s

                STACK TRACE:
                %s

                BUILD LOG:
                %s

                RETRIEVED ENGINEERING KNOWLEDGE:
                %s

                Return exactly this JSON structure:

                {
                  "rootCause": "concise root cause referencing source line or configuration if applicable",
                  "recommendation": "concise recommended fix",
                  "confidence": "HIGH"
                }

                Confidence must be one of:
                HIGH
                MEDIUM
                LOW
                """.formatted(
                analysis.errorType(),
                analysis.errorMessage(),
                analysis.component(),
                analysis.sourceLocation(),
                sourceCodeContext,
                mavenContext,
                gitContext,
                analysis.stackTrace(),
                buildLog,
                knowledge
        );

        String response = chatModel.chat(prompt);

        try {

            String json = extractJson(response);

            LlmDiagnosis diagnosis =
                    objectMapper.readValue(
                            json,
                            LlmDiagnosis.class
                    );

            // 8. Combine deterministic + Repository + Maven + Git + RAG + LLM results
            return new FinalBuildAnalysis(
                    analysis.status(),
                    analysis.errorType(),
                    analysis.component(),
                    diagnosis.rootCause(),
                    diagnosis.recommendation(),
                    diagnosis.confidence(),
                    analysis.sourceLocation(),
                    analysis.stackTrace()
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to parse LLM response: " + response,
                    e
            );
        }
    }

    private String extractSourceCodeContext(String sourceLocation) {
        if (sourceLocation == null || sourceLocation.isBlank() || !sourceLocation.contains(":")) {
            return "No source code available.";
        }

        try {
            String[] parts = sourceLocation.split(":");
            String fileName = parts[0].trim();
            int lineNumber = Integer.parseInt(parts[1].trim());

            String snippet = repositorySourceService.getSourceSnippet(fileName, lineNumber, 15);

            if (snippet != null && !snippet.isBlank()) {
                return snippet;
            }
        } catch (Exception e) {
            // Graceful fallback
        }

        return "Source file not found in local repository.";
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record LlmDiagnosis(
            String rootCause,
            String recommendation,
            String confidence
    ) {
    }

    private String extractJson(String response) {

        int start = response.indexOf("{");
        int end = response.lastIndexOf("}");

        if (start < 0 || end < start) {
            throw new RuntimeException(
                    "LLM did not return valid JSON: " + response
            );
        }

        return response.substring(start, end + 1);
    }
}