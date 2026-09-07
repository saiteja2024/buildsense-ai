# BuildSense AI

**AI-powered CI/CD failure analysis and remediation platform**

BuildSense AI analyzes CI/CD build logs, identifies failure patterns, combines deterministic analysis with LLM reasoning, and uses **RAG-based engineering knowledge retrieval** to produce structured root-cause analysis and remediation recommendations.

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
                         v
               +--------------------+
               | Rule-Based Analyzer|
               +---------+----------+
                         |
              +----------+----------+
              |          |           |
              v          v           v
          Error Type  Component  Source Location
              |          |           |
              +----------+-----------+
                         |
                         v
                  Targeted RAG Query
                         |
                         v
               +--------------------+
               |   PGVector Search  |
               +---------+----------+
                         |
                         v
              Relevant Engineering
                    Knowledge
                         |
                         v
               +--------------------+
               |    LLM Reasoning   |
               |    Ollama/Llama    |
               +---------+----------+
                         |
              +----------+----------+
              |          |           |
              v          v           v
          Root Cause Recommendation Confidence
              |          |           |
              +----------+-----------+
                         |
                         v
                Final Build Analysis
                         |
                         v
                      REST API
```

### RAG Pipeline

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
                         ^
                         |
Build Failure -----------+
                         |
                  Semantic Search
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
* ANSI log cleanup and preprocessing
* Deterministic error classification

### AI Diagnosis

* Local LLM inference using Ollama
* Llama 3.2 integration
* Context-aware root-cause analysis
* AI-generated remediation recommendations
* Confidence scoring
* Structured JSON output
* Deterministic error classification preserved during LLM reasoning
* RAG context provided to the LLM during diagnosis

### RAG

* Local Ollama embedding model
* `nomic-embed-text` embeddings
* 768-dimensional embeddings
* Markdown knowledge-base ingestion
* Document chunking
* Embedding storage in PostgreSQL + PGVector
* Semantic similarity search
* Targeted retrieval based on detected error information
* Relevant troubleshooting knowledge provided to the LLM
* RAG-enhanced AI diagnosis

---

## Example

### Input

```text
BUILD FAILURE

java.lang.IllegalArgumentException: Invalid payment amount: -100

at com.example.payment.PaymentValidator.validate(PaymentValidator.java:28)
at com.example.payment.PaymentService.process(PaymentService.java:47)
at com.example.payment.PaymentController.process(PaymentController.java:32)
```

### Deterministic Analysis

```json
{
  "status": "FAILED",
  "errorType": "ILLEGAL_ARGUMENT",
  "component": "PaymentValidator",
  "sourceLocation": "PaymentValidator.java:28",
  "stackTrace": "at com.example.payment.PaymentValidator.validate(PaymentValidator.java:28)\nat com.example.payment.PaymentService.process(PaymentService.java:47)\nat com.example.payment.PaymentController.process(PaymentController.java:32)"
}
```

### RAG Retrieval

BuildSense converts the detected failure information into a targeted semantic query:

```text
Error Type: ILLEGAL_ARGUMENT
Error Message: IllegalArgumentException: Invalid payment amount: -100
Component: PaymentValidator
Source Location: PaymentValidator.java:28
```

The query is embedded using `nomic-embed-text` and searched against the PostgreSQL/PGVector knowledge base.

Relevant engineering knowledge is retrieved and provided to the LLM as contextual evidence.

### AI Diagnosis

```json
{
  "status": "FAILED",
  "errorType": "ILLEGAL_ARGUMENT",
  "component": "PaymentValidator",
  "rootCause": "The payment validation logic received a negative payment amount.",
  "recommendation": "Validate the payment amount before processing and reject values outside the accepted range.",
  "confidence": "HIGH",
  "sourceLocation": "PaymentValidator.java:28",
  "stackTrace": "at com.example.payment.PaymentValidator.validate(PaymentValidator.java:28)\nat com.example.payment.PaymentService.process(PaymentService.java:47)\nat com.example.payment.PaymentController.process(PaymentController.java:32)"
}
```

---

## Technology Stack

* **Language:** Java 21
* **Framework:** Spring Boot
* **AI Framework:** LangChain4j
* **LLM Runtime:** Ollama
* **LLM:** Llama 3.2
* **Embedding Model:** nomic-embed-text
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

### Ingestion Pipeline

```text
Markdown Documents
        |
        v
Document Loader
        |
        v
Recursive Chunking
        |
        v
nomic-embed-text
        |
        v
768-dimensional Embeddings
        |
        v
PostgreSQL + PGVector
```

### Retrieval + Diagnosis Pipeline

```text
Build Failure
      |
      v
Deterministic Analysis
      |
      v
Structured Semantic Query
      |
      v
nomic-embed-text
      |
      v
PGVector Similarity Search
      |
      v
Relevant Troubleshooting Knowledge
      |
      v
LLM Context
      |
      v
Root Cause + Recommendation + Confidence
```

---

## Design Philosophy

BuildSense intentionally combines **deterministic engineering logic, retrieved domain knowledge, and AI reasoning**.

```text
Deterministic Analysis
          +
   RAG Knowledge
          +
    LLM Reasoning
          =
Context-Aware Build Diagnosis
```

The rule-based analyzer handles facts that can be reliably extracted from the build log.

RAG provides relevant engineering knowledge without requiring the LLM to rely entirely on its internal knowledge.

The LLM then reasons over the **actual build failure + retrieved engineering context** to produce the root cause, recommendation, and confidence.

This hybrid architecture helps reduce hallucinations, provides contextual evidence for AI reasoning, and makes the system easier to validate and extend.

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

## Phase 2 — RAG + AI Diagnosis

* [x] Engineering troubleshooting knowledge base
* [x] Markdown document ingestion
* [x] Document chunking
* [x] Ollama embedding generation
* [x] PostgreSQL + PGVector setup
* [x] Store document embeddings
* [x] Semantic similarity search
* [x] Retrieve relevant troubleshooting knowledge
* [x] Targeted retrieval using deterministic analysis
* [x] Integrate retrieved context into LLM diagnosis
* [x] Root-cause generation
* [x] Remediation recommendations
* [x] Confidence scoring
* [x] Structured JSON output
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
* [ ] Identify likely source-code changes

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
* [ ] Tool selection and orchestration

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
      |                       |
      |                       v
      |                  Generate Fix
      |
      +---- Passed ----> Successful Fix
```

* [ ] Generate code/configuration fix
* [ ] Create Git branch
* [ ] Create pull request
* [ ] Trigger CI validation
* [ ] Analyze validation results
* [ ] Iterate based on CI feedback
* [ ] Learn from successful fixes

---

## Why BuildSense?

Traditional CI systems tell developers:

> "The build failed."

BuildSense aims to answer:

> **"Why did it fail, where is the problem, what engineering knowledge applies, what should be changed, and can the proposed fix be validated automatically?"**

---

## Project Status

🚧 **Actively under development**

BuildSense has progressed from deterministic build-log analysis to a **working RAG-enhanced AI diagnosis pipeline** using local Ollama models and PostgreSQL/PGVector.

The current production flow is:

```text
Actual Build Failure
        +
Deterministic Analysis
        +
Targeted RAG Retrieval
        +
Retrieved Engineering Knowledge
        +
LLM Reasoning
        ↓
Context-Aware AI Diagnosis
        ↓
Root Cause
Recommendation
Confidence
```

The primary API is:

```text
POST /api/builds/analyze
```

This endpoint now orchestrates the complete analysis flow from build log to AI-generated diagnosis.

### Current Milestone

**Phase 1 — Build Analysis:** ✅ Complete

**Phase 2 — RAG + AI Diagnosis:** ✅ Core implementation complete

**Phase 3 — Repository Intelligence:** 🚧 Next

The next major milestone is **Repository Intelligence** — allowing BuildSense to move beyond troubleshooting documentation and retrieve and analyze the actual source code associated with a build failure.

From there, the project will evolve toward **AI agents, automated remediation, pull-request generation, and CI-based fix validation.**
