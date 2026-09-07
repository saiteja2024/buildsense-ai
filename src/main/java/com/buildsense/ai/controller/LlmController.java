package com.buildsense.ai.controller;

import com.buildsense.ai.llm.LlmTestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/llm")
public class LlmController {

    private final LlmTestService llmTestService;

    public LlmController(LlmTestService llmTestService) {
        this.llmTestService = llmTestService;
    }

    @GetMapping("/ask")
    public String ask(@RequestParam String prompt) {
        return llmTestService.ask(prompt);
    }
}
