package com.buildsense.ai.service;

import com.buildsense.ai.model.FinalBuildAnalysis;
import com.buildsense.ai.rag.RagDiagnosisService;
import org.springframework.stereotype.Service;

@Service
public class BuildAnalysisService {

    private final RagDiagnosisService ragDiagnosisService;

    public BuildAnalysisService(
            RagDiagnosisService ragDiagnosisService) {

        this.ragDiagnosisService = ragDiagnosisService;
    }

    public FinalBuildAnalysis analyze(String buildLog) {

        return ragDiagnosisService.diagnose(buildLog);
    }
}