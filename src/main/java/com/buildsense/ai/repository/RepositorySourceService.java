package com.buildsense.ai.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@Service
public class RepositorySourceService {

    private final Path repositoryPath;

    public RepositorySourceService(
            @Value("${buildsense.repository.path}") String repositoryPath) {
        this.repositoryPath = Path.of(repositoryPath);
    }

    public String getSourceFile(String fileName) {
        Path sourceFile = findFile(fileName);
        if (sourceFile == null) {
            return null;
        }

        try {
            return Files.readString(sourceFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read repository source file: " + fileName, e);
        }
    }

    /**
     * Extracts a windowed snippet around a specific line number.
     * @param fileName File name (e.g., PaymentValidator.java)
     * @param targetLine Failing line number (e.g., 12)
     * @param windowSize Radius of lines to include above and below target line
     */
    public String getSourceSnippet(String fileName, int targetLine, int windowSize) {
        Path sourceFile = findFile(fileName);
        if (sourceFile == null) {
            return null;
        }

        try {
            List<String> lines = Files.readAllLines(sourceFile);
            if (lines.isEmpty()) {
                return "";
            }

            int zeroBasedTarget = targetLine - 1;
            int startLine = Math.max(0, zeroBasedTarget - windowSize);
            int endLine = Math.min(lines.size() - 1, zeroBasedTarget + windowSize);

            StringBuilder snippet = new StringBuilder();
            snippet.append("// File: ").append(fileName)
                    .append(" (Showing lines ").append(startLine + 1)
                    .append(" to ").append(endLine + 1)
                    .append(", Target line: ").append(targetLine).append(")\n\n");

            for (int i = startLine; i <= endLine; i++) {
                String prefix = (i == zeroBasedTarget) ? " -> " : "    ";
                snippet.append(String.format("%s%4d | %s\n", prefix, i + 1, lines.get(i)));
            }

            return snippet.toString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to read snippet for: " + fileName, e);
        }
    }

    private Path findFile(String fileName) {
        try (Stream<Path> paths = Files.walk(repositoryPath)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().equals(fileName))
                    .findFirst()
                    .orElse(null);
        } catch (IOException e) {
            throw new RuntimeException("Error searching repository for file: " + fileName, e);
        }
    }

    /**
     * Reads the root Maven POM file.
     */
    public String getPomXml() {
        Path pomPath = repositoryPath.resolve("pom.xml");
        if (Files.exists(pomPath)) {
            try {
                return Files.readString(pomPath);
            } catch (IOException e) {
                return "// Failed to read pom.xml: " + e.getMessage();
            }
        }
        return null;
    }

    /**
     * Reads application configuration from standard Maven location: src/main/resources
     */
    public String getApplicationConfig() {
        Path resourceDir = repositoryPath.resolve("src/main/resources");
        if (!Files.exists(resourceDir)) {
            return null;
        }

        Path appYaml = resourceDir.resolve("application.yml");
        if (Files.exists(appYaml)) {
            return readPathSafe(appYaml);
        }

        Path appProperties = resourceDir.resolve("application.properties");
        if (Files.exists(appProperties)) {
            return readPathSafe(appProperties);
        }

        return null;
    }

    private String readPathSafe(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            return "// Error reading config file at " + path.getFileName();
        }
    }
}