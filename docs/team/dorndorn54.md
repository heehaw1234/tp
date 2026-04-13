# Project Portfolio Page — ItemTasker

## Overview

ItemTasker is a desktop CLI application for warehouse SKU (Stock Keeping Unit) management.
Warehouse staff can track SKUs across a 3×3 grid of storage locations, attach prioritised tasks
with due dates to each SKU, and monitor completion status — all through a fast, typed command
interface. It is written in Java with approximately 3–4 kLoC and uses Gson for JSON-based
persistent storage.

---

## Summary of Contributions

### Code Contributed

[RepoSense link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=dorndorn54&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2026-02-20T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&filteredFileName=)

---

### Features & Enhancements Implemented

#### 1. Storage system (`Storage.java`)

- Designed and implemented full persistent storage using Gson, serialising and deserialising the
  entire `SKUList → SKU → SKUTaskList → SKUTask` object hierarchy to `Data/storage.json`.
- Handles first-run scenarios (no file exists), auto-creates the `Data/` directory, and gracefully
  recovers from corrupted or outdated JSON with a clear error message rather than a crash.
- Integrated `loadState` into `CommandRunner`'s constructor so state is transparently restored at
  startup, and wired `saveState` into the `bye`/`exit` command path.
- **Highlights:** Getting Gson to recursively serialise the nested domain model without custom
  adapters required careful attention to field naming and the immutability of inner lists.
  Verifying that round-trips (save → load → save) were lossless required thorough integration
  testing.

#### 2. Command dispatching (`CommandRunner.java`)

- Implemented the central `run(ParsedCommand)` dispatcher using a `switch` statement that routes
  every command word to the appropriate dedicated handler (SRP: `SKUCommandHandler`,
  `TaskCommandHandler`, `ViewCommandHandler`).
- Manages the application's `isRunning` lifecycle flag and wires together the three handler
  objects, the storage layer, and the UI in one cohesive class.
- Added `handleExport()` as a private helper delegating to `storage.Export`, keeping the
  dispatcher clean.
- **Highlights:** Choosing to pass a shared `SKUList` reference to all three handlers (rather than
  coupling handlers to each other) was a deliberate design decision that keeps the handlers
  independently testable.

#### 3. Shared command helpers (`CommandHelper.java`)

- Extracted six reusable static parsing/validation methods (`findSkuOrError`, `parseLocation`,
  `parsePriority`, `parsePriorityOrDefault`, `parseIndex`, `matchesDescription`) that are called
  across all three handler classes.
- Standardised the validate-and-return-null pattern so that every handler has a consistent, DRY
  way to handle bad input and print errors via `Ui`.
- **Highlights:** Without this class, the same `try/catch IllegalArgumentException` blocks and
  null-checks would appear at least eight times across the codebase. Centralising them also made
  it much easier to update the error messages for `parseLocation` and `parsePriority`
  consistently.

#### 4. Console UI layer (`Ui.java`)

- Designed and implemented the full console I/O interface: welcome banner (with ASCII logo),
  goodbye message, `printSuccess` / `printError` / `printInfo` / `printUnknownCommand` prefix
  conventions, and the full `printHelp` command reference.
- Implemented task display methods: `printTasksForSku`, `printTasksByPriority`,
  `printTasksByDistance`, `printAllTasks`, `printSearchHeader` / `printSearchFooter`, and
  `printDivider`.
- All output is routed through static `Ui` methods, meaning formatting can be changed in one
  place without touching any handler or domain class.

---

### Contributions to the User Guide

- Wrote the **Storage** section explaining auto-save/load behaviour and what to do if
  `storage.json` is corrupted.
- Wrote the **Command Summary** table covering all 15+ commands.
- Added usage notes for `bye`/`exit` and `export`.

---

### Contributions to the Developer Guide

- Added the **Architecture** section with the high-level component diagram (`Ui`, `CommandRunner`,
  `CommandHelper`, `Storage`, `SKUList`).
- Wrote the **Storage component** section with the sequence diagram for `loadState` and
  `saveState`.
- Wrote the **CommandRunner** section with the sequence diagram showing command dispatch flow from
  `main` → `CommandRunner.run()` → handler.
- Added implementation notes for `CommandHelper` explaining the validate-and-return-null design
  pattern.

---

### Contributions to Team-Based Tasks

- Set up the Gradle build configuration and `build.gradle` dependencies (Gson, JUnit 5).
- Maintained the shared `exceptions` package (`ItemTaskerException` hierarchy) used by all
  handlers.
- Coordinated the v1.0 and v2.0 releases on GitHub (jar packaging, release notes).

---

### Review / Mentoring Contributions

- Reviewed PRs for `ViewCommandHandler` and `DateValidator` with non-trivial comments on
  edge-case handling (e.g., empty task lists, null filter strings).
- Identified and flagged a JUnit 4 vs. JUnit 5 annotation mismatch in the test suite that was
  silently causing tests to be skipped.
