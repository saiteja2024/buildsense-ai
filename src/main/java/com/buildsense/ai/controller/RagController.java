package com.buildsense.ai.controller;


import com.buildsense.ai.rag.KnowledgeIngestionService;
import com.buildsense.ai.rag.RagSearchService;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.data.segment.TextSegment;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rag")
public class RagController {

    private final KnowledgeIngestionService ingestionService;
    private final RagSearchService ragSearchService;

    public RagController(
            KnowledgeIngestionService ingestionService,
            RagSearchService ragSearchService) {

        this.ingestionService = ingestionService;
        this.ragSearchService = ragSearchService;
    }

    @PostMapping("/ingest")
    public String ingest() {

        ingestionService.ingest("null-pointer-exceptions.md");

        return "Knowledge document ingested successfully";
    }

    @GetMapping("/search")
    public String search(@RequestParam String query) {

        var matches = ragSearchService.search(query);

        if (matches.isEmpty()) {
            return "No matches found";
        }

        StringBuilder response = new StringBuilder();

        for (var match : matches) {
            response.append("Score: ")
                    .append(match.score())
                    .append("\n");

            response.append("Text: ")
                    .append(match.embedded().text())
                    .append("\n\n");
        }

        return response.toString();
    }
}
