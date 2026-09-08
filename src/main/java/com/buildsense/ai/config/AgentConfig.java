package com.buildsense.ai.config;


import com.buildsense.ai.agent.BuildSenseAgent;
import com.buildsense.ai.tools.GitTools;
import com.buildsense.ai.tools.MavenTools;
import com.buildsense.ai.tools.RepositoryTools;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfig {

    @Bean
    public BuildSenseAgent buildSenseAgent(
            ChatModel chatModel,
            RepositoryTools repositoryTools,
            MavenTools mavenTools,
            GitTools gitTools) {

        return AiServices.builder(BuildSenseAgent.class)
                .chatModel(chatModel)
                .tools(repositoryTools, mavenTools, gitTools)
                .build();
    }
}