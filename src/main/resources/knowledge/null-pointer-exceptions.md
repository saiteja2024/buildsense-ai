# NullPointerException Troubleshooting

## Overview

A NullPointerException occurs when application code attempts to use an object reference that has not been initialized or contains a null value.

## Common Causes

* An object was never initialized.
* A dependency was not injected.
* A method returned null unexpectedly.
* A required configuration value is missing.
* An object was conditionally initialized but the condition was not satisfied.
* A database or external service returned an unexpected null value.

## Spring Boot Dependency Injection

For Spring Boot applications, a common cause is a missing or incorrectly configured dependency.

Example:

```java
@Service
public class PaymentService {

    private PaymentValidator validator;

    public void process(Payment payment) {
        validator.validate(payment);
    }
}
```

If `validator` is not injected, calling `validator.validate()` causes a NullPointerException.

Constructor injection is preferred:

```java
@Service
public class PaymentService {

    private final PaymentValidator validator;

    public PaymentService(PaymentValidator validator) {
        this.validator = validator;
    }
}
```

## Troubleshooting Steps

1. Identify the first application frame in the stack trace.
2. Inspect the source line identified by the stack trace.
3. Determine which object is null.
4. Check whether the object was initialized.
5. For Spring dependencies, verify component scanning and dependency injection.
6. Verify that required configuration values are present.
7. Check whether upstream methods can return null.

## Example

Error:

```text
java.lang.NullPointerException: Cannot invoke "PaymentValidator.validate()" because "validator" is null
    at com.example.payment.PaymentService.process(PaymentService.java:47)
```

Likely root cause:

`PaymentValidator` was not initialized or injected into `PaymentService`.

Recommended action:

Inspect the `PaymentService` dependency injection configuration and ensure that `PaymentValidator` is properly registered and injected.
