package com.buildsense.ai.controller;

import com.buildsense.ai.model.FinalBuildAnalysis;
import com.buildsense.ai.rag.RagDiagnosisService;
import com.buildsense.ai.remediation.RemediationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/remediation")
public class RemediationController {

    private final RagDiagnosisService ragDiagnosisService;
    private final RemediationService remediationService;

    public RemediationController(RagDiagnosisService ragDiagnosisService, RemediationService remediationService) {
        this.ragDiagnosisService = ragDiagnosisService;
        this.remediationService = remediationService;
    }

    @PostMapping("/fix")
    public RemediationService.RemediationResult fixBuildFailure(@RequestBody String buildLog) {
        // Step 1: Diagnose
        FinalBuildAnalysis analysis = ragDiagnosisService.diagnose(buildLog);

        // Step 2: Attempt Patch & Verification
        return remediationService.attemptAutoFix(analysis);
    }
}