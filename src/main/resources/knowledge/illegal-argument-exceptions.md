# IllegalArgumentException Troubleshooting

## Overview

IllegalArgumentException occurs when a method receives an argument that is invalid or outside the expected range.

## Common Causes

* Negative values where only positive values are allowed.
* Null values passed to methods that require non-null values.
* Invalid enum values.
* Incorrect date or time ranges.
* Strings that do not match the expected format.
* Values outside configured limits.

## Example

```java
public void validatePaymentAmount(double amount) {

    if (amount < 0) {
        throw new IllegalArgumentException("Payment amount cannot be negative");
    }
}
```

Calling:

```java
validatePaymentAmount(-100);
```

produces:

```text
java.lang.IllegalArgumentException: Payment amount cannot be negative
```

## Troubleshooting Steps

1. Locate the first application frame in the stack trace.
2. Identify the method that threw the exception.
3. Inspect the arguments passed to that method.
4. Compare the actual value with the method's expected range or format.
5. Check upstream validation.
6. Verify whether external input is being validated before reaching the method.

## Example Build Failure

```text
java.lang.IllegalArgumentException: Invalid payment amount: -100
    at com.example.payment.PaymentValidator.validate(PaymentValidator.java:28)
```

Likely root cause:

The payment validation logic received a negative payment amount.

Recommended action:

Validate the payment amount before processing and reject values outside the accepted range.
