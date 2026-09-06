# Maven Build Failure Troubleshooting

## Overview

Maven build failures can occur during dependency resolution, compilation, testing, packaging, or plugin execution.

## Compilation Failures

Common errors:

```text
COMPILATION ERROR
cannot find symbol
```

Usually indicates that the Java compiler cannot locate a referenced class, method, variable, or package.

Common causes:

* Missing import.
* Missing dependency.
* Incorrect class name.
* Incorrect method signature.
* Dependency version mismatch.
* Source code references a class that was removed or renamed.

## Dependency Resolution Failures

Common errors:

```text
Could not resolve dependencies
```

Possible causes:

* Dependency does not exist.
* Incorrect dependency version.
* Repository unavailable.
* Network connectivity issue.
* Authentication failure with a private repository.

## Test Failures

Typical output:

```text
Tests run: 10, Failures: 1, Errors: 0
```

A test failure means the code compiled successfully but one or more tests did not produce the expected result.

The first step is to identify the failing test and inspect its assertion or exception.

## Plugin Failures

Maven plugins can fail because of:

* Incorrect plugin configuration.
* Incompatible Java version.
* Missing plugin dependency.
* Invalid command-line parameters.
* Plugin execution errors.

## Troubleshooting Strategy

1. Identify the Maven lifecycle phase that failed.
2. Locate the first meaningful error.
3. Ignore repeated downstream errors until the root failure is identified.
4. If compilation failed, inspect the source file and line number.
5. If dependency resolution failed, inspect dependency coordinates and repository configuration.
6. If tests failed, inspect the failing test and assertion.
7. If a plugin failed, inspect plugin configuration and Java compatibility.

## Important Principle

The first meaningful error in a Maven build log is usually more useful than the final generic `BUILD FAILURE` message.

Example:

```text
[ERROR] COMPILATION ERROR
[ERROR] PaymentService.java:[47,25] cannot find symbol
```

The actionable failure is the compilation error at `PaymentService.java:47`, not the final `BUILD FAILURE` line.
