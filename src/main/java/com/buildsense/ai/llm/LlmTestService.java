package com.buildsense.ai.llm;

import dev.langchain4j.model.chat.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class LlmTestService {

    private final ChatModel chatModel;

    public LlmTestService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String ask(String prompt) {
        return chatModel.chat(prompt);
    }

}
