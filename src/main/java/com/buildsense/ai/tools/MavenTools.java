package com.buildsense.ai.tools;

import com.buildsense.ai.repository.MavenContextService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class MavenTools {

    private final MavenContextService mavenContextService;

    public MavenTools(MavenContextService mavenContextService) {
        this.mavenContextService = mavenContextService;
    }

    @Tool("Fetches Maven pom.xml and application configuration files for a specified error context")
    public String getMavenDescriptors(
            @P("The categorized error type (e.g., DEPENDENCY_ERROR, COMPILATION_ERROR)") String errorType) {
        return mavenContextService.getMavenContext(errorType);
    }
}