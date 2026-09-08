package com.buildsense.ai.controller;

import com.buildsense.ai.model.FinalBuildAnalysis;
import com.buildsense.ai.rag.KnowledgeIngestionService;
import com.buildsense.ai.rag.RagDiagnosisService;
import com.buildsense.ai.rag.RagSearchService;
import com.buildsense.ai.repository.RepositorySourceService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rag")
public class RagController {

    private final KnowledgeIngestionService ingestionService;
    private final RagSearchService ragSearchService;
    private final RagDiagnosisService ragDiagnosisService;
    private final RepositorySourceService repositorySourceService;

    public RagController(
            KnowledgeIngestionService ingestionService,
            RagSearchService ragSearchService,
            RagDiagnosisService ragDiagnosisService,
            RepositorySourceService repositorySourceService) {

        this.ingestionService = ingestionService;
        this.ragSearchService = ragSearchService;
        this.ragDiagnosisService = ragDiagnosisService;
        this.repositorySourceService = repositorySourceService;
    }
    @PostMapping("/ingest")
    public String ingest() {

        ingestionService.ingest("null-pointer-exceptions.md");
        ingestionService.ingest("illegal-argument-exceptions.md");

        return "Knowledge document ingested successfully";
    }

    @PostMapping("/diagnose")
    public FinalBuildAnalysis diagnose(@RequestBody String buildLog) {
        return ragDiagnosisService.diagnose(buildLog);
    }

    @GetMapping("/repository/source")
    public String getSource(@RequestParam String fileName) {

        String source = repositorySourceService.getSourceFile(fileName);

        if (source == null) {
            return "Source file not found: " + fileName;
        }

        return source;
    }
}