# Test-First Verification Skill

## Purpose

You are responsible for ensuring that every code change is verified by automated tests.

After any implementation, refactor, bug fix, feature addition, configuration change, or modification to existing code, you must validate the change through tests.

## Mandatory Rules

### Rule 1: Never Finish Without Verification

A task is NOT complete when the code compiles.

A task is complete only when:

1. Relevant tests exist.
2. Tests cover the changed behavior.
3. Tests execute successfully.
4. No existing tests are broken.

### Rule 2: Detect Missing Test Coverage

For every code change:

* Identify affected functionality.
* Determine whether automated tests already cover it.
* Review existing unit, integration, and end-to-end tests.

If coverage is missing or insufficient:

* Create new tests.
* Extend existing tests.
* Add edge-case tests when appropriate.

Never assume manual testing is sufficient.

### Rule 3: Prefer the Smallest Appropriate Test

Testing priority:

1. Unit Tests
2. Integration Tests
3. End-to-End Tests

Use the lowest testing level capable of verifying the behavior.

Examples:

* Utility method → Unit test
* Service logic → Unit test
* Database interaction → Integration test
* User workflow → End-to-end test

### Rule 4: Validate Both Success and Failure Paths

When creating tests, cover:

* Happy path
* Validation failures
* Error handling
* Edge cases
* Boundary conditions

Example:

For a user registration feature:

* Valid registration
* Duplicate email
* Invalid email format
* Missing required fields
* Password policy violations

### Rule 5: Regression Protection

For bug fixes:

1. Create a failing test that reproduces the bug.
2. Implement the fix.
3. Verify the new test passes.
4. Verify existing tests continue to pass.

Never fix a bug without a regression test.

### Rule 6: Existing Tests Take Priority

Before creating new tests:

* Search for relevant test suites.
* Extend existing tests when possible.
* Follow existing naming conventions.
* Follow existing testing patterns.

Avoid unnecessary duplication.

## Spring Boot Specific Guidance

### Controllers

Verify:

* HTTP status codes
* Request validation
* Response payloads
* Security constraints

Preferred tools:

* MockMvc
* WebMvcTest

### Services

Verify:

* Business logic
* Branch conditions
* Error handling
* Transactions

Preferred tools:

* JUnit 5
* Mockito

### Repositories

Verify:

* Queries
* Mappings
* Persistence behavior

Preferred tools:

* DataJpaTest
* Testcontainers when applicable

### Thymeleaf Pages

Verify:

* Model attributes
* View resolution
* Conditional rendering logic

Preferred tools:

* MockMvc
* Integration tests

## Required Workflow

For every task:

### Step 1

Implement the change.

### Step 2

Identify affected components.

### Step 3

Locate existing tests.

### Step 4

Create or update tests.

### Step 5

Execute relevant tests.

### Step 6

Review failures.

### Step 7

Fix failures.

### Step 8

Re-run tests until successful.

### Step 9

Report results.

## Required Completion Report

After every implementation provide:

```text
Implementation Summary
---------------------
- Files modified:
- Behavior changed:

Test Coverage
-------------
- Existing tests updated:
- New tests created:
- Scenarios covered:

Verification
------------
- Unit tests: PASSED
- Integration tests: PASSED
- E2E tests: PASSED (if applicable)

Outstanding Risks
-----------------
- None
```

## Forbidden Behaviors

Do not:

* Skip test creation.
* Assume coverage exists without checking.
* Mark work complete before running tests.
* Ignore failing tests.
* Remove tests to make builds pass.
* Reduce coverage without justification.

## Success Criteria

Every code change must leave the codebase with:

* Equal or better test coverage.
* Automated verification.
* Regression protection.
* Passing test suite.
* Documented test results.

No implementation is considered complete until these criteria are met.