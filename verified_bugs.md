# Verified Bug Report Evaluation

## ❌ **Filtered / Not Problematic**
These bugs were reported as flaws but actually strictly follow the current user documentation or are non-contradictory.
*   **Bug 2:** `find` command headers print before errors. *(Not explicitly restricted by documentation)*
*   **Bug 3:** `editsku` sets location to itself. *(Technically correct "update", conforms to UG)*
*   **Bug 8:** Priority sort ascending is HIGH -> MEDIUM. *(Explicitly defined & promised this way in the UG)*
*   **Bug 12:** Export doesn't specify exact path in UG. *(Minor omission, not a strict contradiction/flaw)*

---

## 🚩 **Problematic Bugs & Discrepancies**

### 1. `Parser` Constraints Contradicting the User Guide
**Associated Bugs:** #1, #7, #11
**Description:** The UG outlines that `t/` descriptions accept free-text formats. However, the codebase aggressively tokenizes any string using the regex `(?<=\S)\s+(?=[a-zA-Z]/)`. Users typing common expressions like "w/o" or "a/c" will have their task description violently truncated and their command rejected. 
**Assigned to:** `@@author dorndorn54` *(Author of `src/main/java/ui/Parser.java`)*

### 2. Inconsistent Command Arguments & Validation
**Associated Bug:** #4
**Description:** `addskutask` passes empty `t/` arguments directly into a blank task. `edittask` explicitly blocks the exact same syntax and throws an error. The UG implies these act the same.
**Assigned to:** `@@author omcodedthis` *(handleAddSkuTask)* & `@@author AkshayPranav19` *(handleEditTask)* in `src/main/java/command/TaskCommandHandler.java`

### 3. Missing `EOF` Pipeline Defenses
**Associated Bug:** #5
**Description:** App drops a harsh `NoSuchElementException` stack trace if a piped text file input exhausts standard input before explicitly typing `bye` to exit.
**Assigned to:** `@@author dorndorn54` *(Author of `src/main/java/ui/Ui.java` input scanner loops)*

### 4. Secret Duplicate Task Policies
**Associated Bugs:** #6, #10
**Description:** The application strictly enforces a case-insensitive task duplication rule which completely blocks the addition of identically named tasks. There is absolutely zero mention of this constraint anywhere in the UG, DG, or Javadocs.
**Assigned to:** `@@author heehaw1234` *(Author of `src/main/java/skutask/SKUTaskList.java`)*



## 🚩 **Documentation vs Documentation (DG Discrepancies)**

### 5. Contradictions in System Exceptions
**Associated Bug:** #16
**Description:** The DG claims `InvalidIndexException` operates with a "dual constructor." The Javadocs within the file and the code block itself explicitly outline three separate constructors to cover out-of-bounds, parsed-fails, and system integer-overflows.
**Assigned to:** `@@author omcodedthis` *(Author of the exception package)*

### 6. Incorrect User Stories
**Associated Bugs:** #18, #20
*   **(Bug 18):** The DG claims v1.0 attaches Locations to *Tasks*. Code/Javadocs enforce tying valid Locations strictly to *SKUs*.
*   **(Bug 20):** The DG claims the application exports an underlying `CSV` spreadsheet. Code/UG outlines plain-text read-file formatting.
**Assigned to:** `@@author omcodedthis` *(Author of `Storage/Export.java` & the `SKU` layer)*

### 7. Architectural Sequence Mismatches
**Associated Bugs:** #22, #23, #24
*   **(Bug 22):** The DG diagram `command-architecture.puml` states `SKUCommandHandler` leans on `DateValidator`. It does not.
*   **(Bug 23):** Sequence diagrams show command executions resolving SKUs universally via `CommandHelper.findSkuOrError()`, but the code loops dynamically locally through `skuList.findByID()`.
*   **(Bug 24):** The DG diagram for the `parserSequence.puml` outlines that standalone flagged expressions (ones without trailing slashes) should organically be ignored. Code strictly throws an `InvalidCommandException` for them.
**Assigned to:** `@@author omcodedthis` *(Handlers/System Design)* & `@@author dorndorn54` *(Parser sequence)*