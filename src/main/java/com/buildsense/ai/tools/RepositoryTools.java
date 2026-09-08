package com.buildsense.ai.tools;

import com.buildsense.ai.repository.RepositorySourceService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class RepositoryTools {

    private final RepositorySourceService repositorySourceService;

    public RepositoryTools(RepositorySourceService repositorySourceService) {
        this.repositorySourceService = repositorySourceService;
    }

    @Tool("Reads a source code window around a given line number in a file")
    public String getSourceCodeSnippet(
            @P("Relative or base file name (e.g., PaymentValidator.java)") String fileName,
            @P("Target error line number") int lineNumber,
            @P("Window radius around line number") int windowSize) {
        return repositorySourceService.getSourceSnippet(fileName, lineNumber, windowSize);
    }
}