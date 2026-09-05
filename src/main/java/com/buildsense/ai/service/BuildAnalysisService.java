package com.buildsense.ai.service;

import com.buildsense.ai.model.AiBuildAnalysis;
import com.buildsense.ai.model.BuildAnalysis;
import com.buildsense.ai.model.FinalBuildAnalysis;
import org.springframework.stereotype.Service;

@Service
public class BuildAnalysisService {

    private final BuildLogAnalyzer buildLogAnalyzer;
    private final AiBuildAnalyzer aiBuildAnalyzer;
    private final BuildLogPreprocessor buildLogPreprocessor;

    public BuildAnalysisService(
            BuildLogAnalyzer buildLogAnalyzer,
            AiBuildAnalyzer aiBuildAnalyzer,
            BuildLogPreprocessor buildLogPreprocessor) {

        this.buildLogAnalyzer = buildLogAnalyzer;
        this.aiBuildAnalyzer = aiBuildAnalyzer;
        this.buildLogPreprocessor = buildLogPreprocessor;
    }

    public FinalBuildAnalysis analyze(String buildLog) {

        String processedLog =
                buildLogPreprocessor.preprocess(buildLog);

        BuildAnalysis ruleAnalysis =
                buildLogAnalyzer.analyze(processedLog);

        AiBuildAnalysis aiAnalysis =
                aiBuildAnalyzer.analyzeBuildLog(processedLog);

        return new FinalBuildAnalysis(
                ruleAnalysis.status(),
                ruleAnalysis.errorType(),
                ruleAnalysis.component(),
                aiAnalysis.rootCause(),
                aiAnalysis.recommendation(),
                aiAnalysis.confidence(),
                ruleAnalysis.sourceLocation()
        );
    }
}