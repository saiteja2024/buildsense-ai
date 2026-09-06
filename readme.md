# BuildSense AI

**AI-powered CI/CD failure analysis and remediation platform**

BuildSense AI analyzes CI/CD build logs, identifies failure patterns, combines deterministic rules with LLM reasoning, and uses **RAG-based engineering knowledge retrieval** to produce structured root-cause analysis and remediation recommendations.

The long-term goal is to evolve BuildSense from a build-log analyzer into an **agentic engineering system capable of investigating failures, understanding repository code, proposing fixes, and validating those fixes through CI.**

---

## Architecture

### Current Architecture

```text
                    CI/CD Build
                         |
                         v
                    Build Log
                         |
                         v
               +--------------------+
               | Log Preprocessor   |
               +---------+----------+
                         |
              +----------+----------+
              |                     |
              v                     v
     Rule-Based Analyzer      AI Analyzer
              |                     |
              |                LLM Reasoning
              |                Root Cause
              |                Recommendation
              |                Confidence
              |                     |
              +----------+----------+
                         |
                         v
                Final Build Analysis
                         |
                         v
                      REST API
```

### RAG Architecture

```text
                 Knowledge Documents
                         |
                         v
                  Document Loader
                         |
                         v
                     Chunking
                         |
                         v
                Ollama Embeddings
               (nomic-embed-text)
                         |
                         v
                    PGVector
                         |
                         |
Build Failure --------->| Semantic Search
                         |
                         v
                 Relevant Knowledge
                         |
                         v
                    LLM Reasoning
                         |
                         v
                 Build Diagnosis
```

---

## Current Capabilities

### Build Analysis

* Compilation failure detection
* Maven dependency failure detection
* Automated test failure detection
* Runtime failure detection
* `NullPointerException` analysis
* `IllegalArgumentException` analysis
* Failed component extraction
* Source-code location extraction
* Stack-trace extraction
* Stack-trace-based component identification
* Structured LLM responses
* Confidence-based AI recommendations
* ANSI log cleanup and preprocessing

### RAG

* Local Ollama embedding model
* `nomic-embed-text` embeddings
* 768-dimensional embeddings
* Markdown knowledge-base ingestion
* Document chunking
* Embedding storage in PostgreSQL + PGVector
* Semantic similarity search
* Retrieval of relevant troubleshooting knowledge

---

## Example

### Input

```text
BUILD FAILURE

java.lang.NullPointerException: Cannot invoke "PaymentValidator.validate()" because "validator" is null
    at com.example.payment.PaymentService.process(PaymentService.java:47)
```

### Deterministic Analysis

```json
{
  "status": "FAILED",
  "errorType": "RUNTIME_ERROR",
  "component": "PaymentValidator",
  "sourceLocation": "PaymentService.java:47",
  "stackTrace": "at com.example.payment.PaymentService.process(PaymentService.java:47)"
}
```

### Retrieved Knowledge

```text
A NullPointerException occurs when application code attempts
to use an object reference that contains a null value.

For Spring Boot applications, a common cause is a missing
or incorrectly configured dependency.

PaymentValidator should be properly initialized and injected
into PaymentService using constructor injection.
```

### AI Diagnosis

```json
{
  "status": "FAILED",
  "errorType": "RUNTIME_ERROR",
  "component": "PaymentValidator",
  "rootCause": "PaymentValidator was not initialized or injected into PaymentService.",
  "recommendation": "Verify that PaymentValidator is properly registered as a Spring component and injected into PaymentService.",
  "confidence": "HIGH",
  "sourceLocation": "PaymentService.java:47"
}
```

---

## Technology Stack

* **Language:** Java 21
* **Framework:** Spring Boot
* **AI Framework:** LangChain4j
* **LLM:** Ollama / Llama 3.2
* **Embedding Model:** Ollama / nomic-embed-text
* **Vector Database:** PostgreSQL + PGVector
* **API:** REST
* **Build:** Maven
* **CI/CD:** Jenkins
* **Containerization:** Docker
* **Version Control:** Git

### Planned

* Repository analysis
* Source-code retrieval
* AI agents
* Git/Jenkins tools
* Automated remediation
* CI-based fix validation

---

## RAG Knowledge Base

BuildSense currently uses engineering troubleshooting documents as its initial knowledge base.

```text
src/main/resources/
└── knowledge/
    ├── null-pointer-exceptions.md
    ├── illegal-argument-exceptions.md
    ├── spring-dependency-injection.md
    ├── database-connection-failures.md
    ├── kafka-failures.md
    └── maven-build-failures.md
```

The documents are:

```text
Markdown
   |
   v
Document Loader
   |
   v
Chunking
   |
   v
nomic-embed-text
   |
   v
768-dimensional vectors
   |
   v
PostgreSQL + PGVector
```

At query time:

```text
Build Failure
      |
      v
Create Semantic Query
      |
      v
nomic-embed-text
      |
      v
PGVector Similarity Search
      |
      v
Relevant Troubleshooting Knowledge
```

---

## Design Philosophy

BuildSense intentionally combines **deterministic engineering logic with AI reasoning and retrieved domain knowledge**.

```text
Deterministic Analysis
          +
   RAG Knowledge
          +
    LLM Reasoning
          =
Reliable Build Diagnosis
```

The rule-based analyzer handles facts that can be reliably extracted from logs.

RAG provides relevant engineering knowledge without requiring the LLM to rely entirely on its internal knowledge.

The LLM then reasons over the **actual build failure + retrieved context** to produce the diagnosis and recommendation.

This architecture helps reduce hallucinations and makes the system easier to validate and extend.

---

# Roadmap

## Phase 1 — Build Analysis

* [x] Build-log preprocessing
* [x] ANSI log cleanup
* [x] Compilation error detection
* [x] Dependency error detection
* [x] Test failure detection
* [x] Runtime error detection
* [x] `NullPointerException` analysis
* [x] `IllegalArgumentException` analysis
* [x] Component extraction
* [x] Source-location extraction
* [x] Stack-trace extraction
* [x] Stack-trace-based component identification
* [ ] Expanded error classifications

---

## Phase 2 — RAG

* [x] Engineering troubleshooting knowledge base
* [x] Markdown document ingestion
* [x] Document chunking
* [x] Ollama embedding generation
* [x] PostgreSQL + PGVector setup
* [x] Store document embeddings
* [x] Semantic similarity search
* [x] Retrieve relevant troubleshooting knowledge
* [ ] Integrate retrieved context into AI diagnosis
* [ ] Store historical build failures
* [ ] Store successful fixes
* [ ] Retrieve similar historical failures

---

## Phase 3 — Repository Intelligence

* [ ] Repository inspection
* [ ] Source-code retrieval
* [ ] Stack-trace-to-source mapping
* [ ] Dependency analysis
* [ ] Configuration analysis
* [ ] Relevant-code retrieval
* [ ] Combine repository context with RAG

---

## Phase 4 — AI Agent

```text
                Build Failure
                      |
                      v
                 AI Agent
                      |
          +-----------+-----------+
          |           |           |
          v           v           v
       Git Tool    RAG Tool   Repository Tool
          |           |           |
          +-----------+-----------+
                      |
                      v
                Build/Test Tool
```

* [ ] Git tools
* [ ] Jenkins tools
* [ ] Repository tools
* [ ] RAG search tools
* [ ] Build/test execution tools
* [ ] Agentic investigation workflow

---

## Phase 5 — Automated Remediation

```text
Build Failure
      |
      v
   Diagnosis
      |
      v
Repository Analysis
      |
      v
 Generate Fix
      |
      v
Create Git Branch
      |
      v
Create Pull Request
      |
      v
   Run CI
      |
      +---- Failed ----> Investigate Again
      |
      +---- Passed ----> Successful Fix
```

* [ ] Generate code/configuration fix
* [ ] Create Git branch
* [ ] Create pull request
* [ ] Trigger CI validation
* [ ] Analyze validation results
* [ ] Learn from successful fixes

---

## Why BuildSense?

Traditional CI systems tell developers:

> "The build failed."

BuildSense aims to answer:

> **"Why did it fail, where is the problem, what should be changed, what engineering knowledge applies, and can the proposed fix be validated automatically?"**

---

## Project Status

🚧 **Actively under development**

BuildSense is being developed incrementally.

The project has progressed from deterministic build-log analysis to **LLM-assisted diagnosis and working RAG retrieval using local Ollama embeddings and PostgreSQL/PGVector**.

The next milestone is to connect the retrieved knowledge to the LLM so that BuildSense can generate diagnoses using:

```text
Actual Build Failure
        +
Retrieved Engineering Knowledge
        +
LLM Reasoning
        =
Context-Aware AI Diagnosis
```

The project will then progress toward repository intelligence, AI agents, automated remediation, and CI-based validation.
