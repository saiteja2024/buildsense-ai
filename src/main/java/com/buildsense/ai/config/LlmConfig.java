package com.buildsense.ai.config;


import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import dev.langchain4j.model.chat.ChatModel;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class LlmConfig {

    @Bean
    public ChatModel chatLanguageModel(){

        return OllamaChatModel.builder()
                .baseUrl("http://127.0.0.1:11434")
                .modelName("llama3.2")
                .responseFormat(ResponseFormat.JSON)
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
