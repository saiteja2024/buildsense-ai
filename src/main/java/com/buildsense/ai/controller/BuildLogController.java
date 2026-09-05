package com.buildsense.ai.controller;

import com.buildsense.ai.model.FinalBuildAnalysis;
import com.buildsense.ai.service.BuildAnalysisService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/builds")
public class BuildLogController {

    private final BuildAnalysisService buildAnalysisService;

    public BuildLogController(BuildAnalysisService buildAnalysisService) {
        this.buildAnalysisService = buildAnalysisService;
    }

    @PostMapping("/analyze")
    public FinalBuildAnalysis analyzeBuild(@RequestBody String buildLog) {
        return buildAnalysisService.analyze(buildLog);
    }
}