package com.buildsense.ai.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Service
public class GitService {

    private final Path repositoryPath;

    public GitService(@Value("${buildsense.repository.path}") String repositoryPath) {
        this.repositoryPath = Path.of(repositoryPath);
    }

    public String getRecentCommit() {
        return executeGitCommand("git", "log", "-1", "--stat");
    }

    public String getRecentDiff() {
        String diff = executeGitCommand("git", "diff", "HEAD~1");
        if (diff == null || diff.isBlank()) {
            diff = executeGitCommand("git", "diff");
        }
        return (diff != null && !diff.isBlank()) ? diff : "No recent uncommitted Git changes.";
    }

    private String executeGitCommand(String... command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(repositoryPath.toFile());
            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            boolean finished = process.waitFor(5, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return "Git command timed out.";
            }

            return output.toString().trim();
        } catch (Exception e) {
            return "Failed to execute Git command: " + e.getMessage();
        }
    }
}