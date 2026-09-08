package com.buildsense.ai.repository;

import org.springframework.stereotype.Service;

@Service
public class MavenContextService {

    private final RepositorySourceService repositorySourceService;

    public MavenContextService(RepositorySourceService repositorySourceService) {
        this.repositorySourceService = repositorySourceService;
    }

    public String getMavenContext(String errorType) {
        // Automatically fetch pom.xml or application context for build/dependency issues
        if ("COMPILATION_ERROR".equals(errorType) ||
                "DEPENDENCY_ERROR".equals(errorType) ||
                "BUILD_FAILURE".equals(errorType)) {

            StringBuilder context = new StringBuilder();

            String pom = repositorySourceService.getPomXml();
            if (pom != null && !pom.isBlank()) {
                context.append("--- MAVEN POM.XML ---\n")
                        .append(pom)
                        .append("\n\n");
            }

            String appConfig = repositorySourceService.getApplicationConfig();
            if (appConfig != null && !appConfig.isBlank()) {
                context.append("--- APPLICATION CONFIG (src/main/resources) ---\n")
                        .append(appConfig)
                        .append("\n\n");
            }

            return context.length() > 0 ? context.toString() : "No Maven configuration found.";
        }

        return "Maven descriptor not required for runtime error type: " + errorType;
    }
}