# Spring Dependency Injection Failures

## Overview

Spring dependency injection failures occur when the Spring container cannot create, locate, or inject a required bean.

## Common Causes

* Missing `@Component`, `@Service`, `@Repository`, or `@Controller`.
* Incorrect component scanning.
* Missing `@Bean` configuration.
* Multiple beans of the same type without a qualifier.
* Circular dependencies.
* Incorrect package structure.
* Dependency not included in the application context.
* Incorrect profile or configuration.

## Common Errors

```text
NoSuchBeanDefinitionException
```

Usually means Spring could not find a bean of the required type.

```text
NoUniqueBeanDefinitionException
```

Usually means multiple beans match the required dependency.

```text
UnsatisfiedDependencyException
```

Usually means Spring could not satisfy a dependency required to construct a bean.

## Troubleshooting Steps

1. Identify the missing dependency from the exception message.
2. Verify that the implementation is annotated with `@Component` or `@Service`.
3. Verify that the package is included in component scanning.
4. Check whether the dependency requires explicit `@Bean` configuration.
5. If multiple implementations exist, use `@Qualifier`.
6. Check active Spring profiles.
7. Check for circular dependencies.

## Recommended Pattern

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

This makes required dependencies explicit and prevents partially initialized service objects.
