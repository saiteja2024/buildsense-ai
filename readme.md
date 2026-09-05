# BuildSense AI

**AI-powered CI/CD failure analysis and remediation platform**

BuildSense AI analyzes CI/CD build logs, identifies failure patterns, combines deterministic rules with LLM reasoning, and produces structured root-cause analysis and remediation recommendations.

The long-term goal is to evolve BuildSense from a build-log analyzer into an **agentic engineering system capable of investigating failures, understanding repository code, proposing fixes, and validating those fixes through CI.**

## Architecture

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
             |              LLM Root Cause
             |              Recommendation
             |              Confidence
             |                     |
             +----------+----------+
                        |
                        v
               Final Build Analysis
                        |
                        v
                    REST API
```

## Current Capabilities

* Compilation failure detection
* Maven dependency failure detection
* Automated test failure detection
* Runtime failure detection
* `NullPointerException` analysis
* Failed component extraction
* Source-code location extraction
* Stack-trace analysis
* Structured LLM responses
* Confidence-based AI recommendations
* ANSI log cleanup and preprocessing

## Example

### Input

```text
BUILD FAILURE

java.lang.NullPointerException:
Cannot invoke "PaymentValidator.validate()"
because "validator" is null

at com.example.payment.PaymentService.process(PaymentService.java:47)
```

### Output

```json
{
  "status": "FAILED",
  "errorType": "RUNTIME_ERROR",
  "component": "PaymentValidator",
  "rootCause": "null reference to PaymentValidator instance",
  "recommendation": "Verify that PaymentValidator instance is properly initialized and injected into PaymentService before using it.",
  "confidence": "HIGH",
  "sourceLocation": "PaymentService.java:47"
}
```

## Technology Stack

* **Language:** Java 21
* **Framework:** Spring Boot
* **AI:** Spring AI + Ollama
* **API:** REST
* **Build:** Maven
* **CI/CD:** Jenkins
* **Containerization:** Docker
* **Version Control:** Git
* **Planned:** RAG, vector database, repository analysis, AI agents

## Design Philosophy

BuildSense intentionally combines **deterministic engineering logic with AI reasoning**.

```text
Deterministic Analysis
        +
LLM Reasoning
        =
Reliable Build Diagnosis
```

The rule-based analyzer handles facts that can be reliably extracted from logs, while the LLM provides higher-level reasoning and recommendations.

This approach helps reduce hallucinations and makes the system easier to validate and extend.

## Roadmap

### Phase 1 — Build Analysis

* [x] Build-log preprocessing
* [x] Compilation error detection
* [x] Dependency error detection
* [x] Test failure detection
* [x] Runtime error detection
* [x] Component extraction
* [x] Source-location extraction
* [ ] Stack-trace extraction
* [ ] Expanded error classifications

### Phase 2 — RAG

* [ ] Historical build-failure knowledge base
* [ ] Store previous failures and successful fixes
* [ ] Semantic search
* [ ] Retrieve similar historical failures
* [ ] Include retrieved context in AI diagnosis

### Phase 3 — Repository Intelligence

* [ ] Repository inspection
* [ ] Source-code retrieval
* [ ] Stack-trace-to-source mapping
* [ ] Dependency/configuration analysis
* [ ] Relevant-code retrieval

### Phase 4 — AI Agent

* [ ] Git tools
* [ ] Jenkins tools
* [ ] Repository tools
* [ ] RAG search tools
* [ ] Build/test execution tools

### Phase 5 — Automated Remediation

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

## Why BuildSense?

Traditional CI systems tell developers:

> "The build failed."

BuildSense aims to answer:

> **"Why did it fail, where is the problem, what should be changed, and can the proposed fix be validated automatically?"**

## Project Status

🚧 **Actively under development**

BuildSense is being developed incrementally, starting with deterministic build-log analysis and LLM-assisted diagnosis before progressing toward RAG, repository intelligence, agentic workflows, and automated remediation.
