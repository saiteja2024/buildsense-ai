package com.buildsense.ai.tools;

import com.buildsense.ai.repository.GitService;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class GitTools {

    private final GitService gitService;

    public GitTools(GitService gitService) {
        this.gitService = gitService;
    }

    @Tool("Retrieves the most recent Git commit log and file changes")
    public String getRecentCommitHistory() {
        return gitService.getRecentCommit();
    }

    @Tool("Retrieves the active Git diff for uncommitted or recent changes")
    public String getRecentDiff() {
        return gitService.getRecentDiff();
    }
}