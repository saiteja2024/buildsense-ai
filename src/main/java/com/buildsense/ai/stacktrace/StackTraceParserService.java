package com.buildsense.ai.stacktrace;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class StackTraceParserService {

    // Matches patterns like: at com.buildsense_test_repo.PaymentValidator.validate(PaymentValidator.java:12)
    private static final Pattern STACK_FRAME_PATTERN =
            Pattern.compile("at\\s+[\\w\\.]+\\(([A-Za-z0-9_]+\\.java):(\\d+)\\)");

    public StackFrameInfo parseFirstFrame(String buildLog) {
        if (buildLog == null || buildLog.isBlank()) {
            return null;
        }

        Matcher matcher = STACK_FRAME_PATTERN.matcher(buildLog);
        if (matcher.find()) {
            String fileName = matcher.group(1);
            int lineNumber = Integer.parseInt(matcher.group(2));
            return new StackFrameInfo(fileName, lineNumber);
        }

        return null;
    }

    public record StackFrameInfo(String fileName, int lineNumber) {}
}