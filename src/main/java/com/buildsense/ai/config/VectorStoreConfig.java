package com.buildsense.ai.config;

import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import dev.langchain4j.data.segment.TextSegment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;


@Configuration
public class VectorStoreConfig {

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore(
            EmbeddingModel embeddingModel) {

        return PgVectorEmbeddingStore.builder()
                .host("localhost")
                .port(5432)
                .database("buildsense")
                .user("postgres")
                .password("postgres")
                .table("buildsense_embeddings")
                .dimension(embeddingModel.dimension())
                .createTable(true)
                .build();
    }

}
