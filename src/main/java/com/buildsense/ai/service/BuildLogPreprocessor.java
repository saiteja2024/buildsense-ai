package com.buildsense.ai.service;

import org.springframework.stereotype.Service;

@Service
public class BuildLogPreprocessor {

    public String preprocess(String buildLog) {

        if (buildLog == null || buildLog.isBlank()) {
            return "";
        }

        return buildLog
                .replaceAll("\u001B\\[[;\\d]*m", "")
                .trim();
    }
}