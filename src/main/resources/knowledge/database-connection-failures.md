# Database Connection Failures

## Overview

Database connection failures occur when an application cannot establish or maintain a connection to the database.

## Common Causes

* Database server unavailable.
* Incorrect database hostname.
* Incorrect port.
* Invalid username or password.
* Database connection pool exhaustion.
* Network connectivity problems.
* Firewall restrictions.
* SSL configuration problems.
* Database connection timeout.
* Incorrect Spring datasource configuration.

## Common Errors

```text
Connection refused
```

Usually indicates that the database endpoint cannot be reached.

```text
Connection timed out
```

Usually indicates a network connectivity or firewall problem.

```text
password authentication failed
```

Usually indicates invalid database credentials.

```text
HikariPool - Connection is not available
```

May indicate connection pool exhaustion or a database availability problem.

## Troubleshooting Steps

1. Identify the database host and port.
2. Verify that the database server is running.
3. Verify network connectivity.
4. Check database credentials.
5. Check datasource configuration.
6. Inspect connection pool configuration.
7. Look for connection leaks.
8. Check database server capacity and active connections.

## Spring Boot Configuration

Typical configuration:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/payment
spring.datasource.username=payment_user
spring.datasource.password=*****
```

Verify that the hostname, port, database name, and credentials are correct.

## Diagnostic Priority

If the error contains `Connection refused`, investigate database availability and network connectivity first.

If the error contains `password authentication failed`, investigate credentials first.

If the error indicates connection pool exhaustion, investigate connection leaks, long-running transactions, and pool sizing.
