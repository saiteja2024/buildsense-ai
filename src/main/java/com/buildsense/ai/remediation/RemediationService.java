package com.buildsense.ai.remediation;

import com.buildsense.ai.model.FinalBuildAnalysis;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Service
public class RemediationService {

    private final PatchGeneratorService patchGeneratorService;
    private final Path repositoryPath;

    public RemediationService(
            PatchGeneratorService patchGeneratorService,
            @Value("${buildsense.repository.path}") String repositoryPath) {
        this.patchGeneratorService = patchGeneratorService;
        this.repositoryPath = Path.of(repositoryPath);
    }

    public RemediationResult attemptAutoFix(FinalBuildAnalysis analysis) {
        // 1. Generate patch
        String patchContent = patchGeneratorService.generatePatch(analysis);
        if (patchContent == null || patchContent.isBlank()) {
            return new RemediationResult(false, patchContent, "Failed to generate patch from analysis.");
        }

        File patchFile = repositoryPath.resolve("fix.patch").toFile();

        try {
            // Write patch to temporary file in repo root
            try (FileWriter writer = new FileWriter(patchFile)) {
                writer.write(patchContent);
            }

            // 2. Validate patch with `git apply --check` first
            ProcessResult checkResult = executeProcessWithResult("git", "apply", "--check", "fix.patch");
            if (checkResult.exitCode() != 0) {
                rollbackPatch(patchFile);
                return new RemediationResult(
                        false,
                        patchContent,
                        "Git patch validation failed (--check exit code " + checkResult.exitCode() + "): " + checkResult.output()
                );
            }

            // Apply patch for real
            ProcessResult applyResult = executeProcessWithResult("git", "apply", "fix.patch");
            if (applyResult.exitCode() != 0) {
                rollbackPatch(patchFile);
                return new RemediationResult(
                        false,
                        patchContent,
                        "Failed to apply patch: " + applyResult.output()
                );
            }

            // 3. Verify fix by executing Maven tests
            boolean testSuccess = executeMavenTest();

            if (testSuccess) {
                // 4. Commit changes to fix branch
                commitFix(analysis);
                return new RemediationResult(true, patchContent, "Patch applied successfully, `mvn test` passed, and changes committed to Git!");
            } else {
                rollbackPatch(patchFile);
                return new RemediationResult(false, patchContent, "Patch applied but `mvn test` failed validation. Changes rolled back.");
            }

        } catch (Exception e) {
            rollbackPatch(patchFile);
            return new RemediationResult(false, patchContent, "Remediation error: " + e.getMessage());
        } finally {
            if (patchFile.exists()) {
                patchFile.delete();
            }
        }
    }

    private boolean executeMavenTest() {
        String isWindows = System.getProperty("os.name").toLowerCase().contains("win") ? "mvn.cmd" : "mvn";
        ProcessResult result = executeProcessWithResult(isWindows, "test");
        return result.exitCode() == 0 && result.output().contains("BUILD SUCCESS");
    }

    private void commitFix(FinalBuildAnalysis analysis) {
        String branchName = "fix/buildsense-" + System.currentTimeMillis();
        executeProcessWithResult("git", "checkout", "-b", branchName);
        executeProcessWithResult("git", "add", "-A");
        executeProcessWithResult("git", "commit", "-m", "fix: " + analysis.rootCause());
    }

    private void rollbackPatch(File patchFile) {
        executeProcessWithResult("git", "checkout", ".");
        executeProcessWithResult("git", "clean", "-fd");
        if (patchFile.exists()) {
            patchFile.delete();
        }
    }

    private ProcessResult executeProcessWithResult(String... command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(repositoryPath.toFile());
            pb.redirectErrorStream(true); // Merge stderr into stdout
            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            boolean finished = process.waitFor(60, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return new ProcessResult(-1, "Process timed out after 60 seconds.");
            }

            return new ProcessResult(process.exitValue(), output.toString().trim());
        } catch (Exception e) {
            return new ProcessResult(-1, "Process execution failed: " + e.getMessage());
        }
    }

    private record ProcessResult(int exitCode, String output) {}

    public record RemediationResult(
            boolean success,
            String generatedPatch,
            String message
    ) {}
}