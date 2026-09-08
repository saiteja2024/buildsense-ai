package com.buildsense.ai.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Service
public class RepositorySourceService {

    private final Path repositoryPath;

    public RepositorySourceService(
            @Value("${buildsense.repository.path}") String repositoryPath) {

        this.repositoryPath = Path.of(repositoryPath);
    }

    public String getSourceFile(String fileName) {

        try (Stream<Path> paths = Files.walk(repositoryPath)) {

            Path sourceFile = paths
                    .filter(Files::isRegularFile)
                    .filter(path ->
                            path.getFileName()
                                    .toString()
                                    .equals(fileName))
                    .findFirst()
                    .orElse(null);

            if (sourceFile == null) {
                return null;
            }

            return Files.readString(sourceFile);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to read repository source file: " + fileName,
                    e
            );
        }
    }
}