# Om Tirodkar - Project Portfolio Page

## Overview
ItemTasker is a CLI-based Stock Keeping Unit (SKU) Ticketing System. A localized command-line tool designed to handle inventory specific actions required for individual item SKUs such as damage checks, expiry reviews & quality control. Unlike standard commercial inventory software that tracks quantity, ItemTracker tracks accountability & actions, allowing managers to attach specific tasks with priorities to individual SKUs.

### Summary of Contributions

#### Code Contributed
[RepoSense link of my profile.](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=omcodedthis&breakdown=true)

#### Enhancements Implemented

##### Add / Delete SKU
* **Objective of Enhancement:** To establish the core inventory management capability by allowing users to add master Stock Keeping Unit (SKU) records bound to physical warehouse locations, and to securely delete them along with their nested task data.
* **Complexity:** This feature required designing the `SKUList` class and enforcing strict domain invariants. Adding an SKU involved validating `Location` enums and preventing duplicate SKU IDs. Deleting an SKU was complex because removing the parent SKU inherently drops its encapsulated `SKUTaskList` from the system.
* **Completeness:** The implementation is robust. It utilizes case-insensitive ID matching (`equalsIgnoreCase`) to prevent duplicate entry bugs. Comprehensive unit testing ensures boundary conditions and invalid inputs (null/empty IDs) are rejected appropriately.
* **Implementation Difficulty:** Moderate. The primary challenge was safely modifying the underlying `ArrayList` during deletion. This is to safely target and remove specific objects, ensuring memory-safe list management.

##### Export
* **Objective of Enhancement:** To provide a data extraction feature that compiles the warehouse state into a formatted, human-readable text file for external auditing and reporting.
* **Complexity:** This enhancement required traversing a complex object hierarchy (`SKUList` → `SKU` → `SKUTaskList` → `SKUTask`) and safely interacting with the local file system. It involved dynamic string formatting to sequentially number tasks and visually divide SKUs.
* **Completeness:** The feature is self-sufficient. It includes autonomous directory generation (`mkdirs()`) if the `Data/` folder is missing. It guards against empty warehouse states by printing a specific empty report, and utilizes a `try-with-resources` block (`FileWriter`) to guarantee that system I/O streams are safely closed even if an error occurs during the write process.
* **Implementation Difficulty:** High. The most difficult aspect was handling edge-case file system states and ensuring it was OS-agnostic. For instance, I had to implement defensive logic to check if "Data" already existed (without an extension) and was blocking the creation of the required directory. Handling this via an `IOException` rather than allowing the app to crash required careful foresight.

##### Command Object Instantiation
* **Objective of Enhancement:** To decouple raw user string input from application logic by parsing commands into a structured, immutable, and easily queryable `ParsedCommand` object.
* **Complexity:** CLI inputs can be highly unpredictable. The parser had to reliably isolate the main command word from an arbitrary number of optional flag-value pairs (e.g., `n/`, `p/`, `d/`), package them into a `HashMap`, and normalize keys for case-insensitive querying.
* **Completeness:** The parser safely handles leading/trailing whitespace, missing arguments, and empty strings, while the resulting `ParsedCommand` object returns an `Collections.unmodifiableMap` to ensure the command parameters cannot be altered downstream, strictly adhering to functional programming principles.
* **Implementation Difficulty:** Moderate. The primary challenge was ensuring strict data integrity and immutability once the raw string was converted into an object. I engineered the `ParsedCommand` class to aggressively sanitize, trimming and lowercasing all command words and flag keys upon instantiation. Furthermore, I wrapped the internal arguments map in a `Collections.unmodifiableMap`. This defensive design guarantees that downstream execution handlers cannot accidentally mutate the above, strictly enforcing a safe, read-only data flow across the application.

##### Entry-Loop, Exceptions & Testing
* **Objective of Enhancement:** To design the main application lifecycle, prevent fatal crashes via a custom exception hierarchy, and ensure system reliability through foundational unit testing.
* **Complexity:** Required setting up the continuous `while (runner.isRunning())` loop in `ItemTasker.java` and routing inputs through the parser and execution layers. Furthermore, I created an xception hierarchy inheriting from `ItemTaskerException` (e.g., `MissingArgumentException`, `SKUNotFoundException`, `InvalidCommandException`).
* **Completeness:** By catching `ItemTaskerException` at the highest level of the application loop, the system translates deeply nested errors into user-friendly UI messages. This prevents raw Java stack traces from leaking to the user and guarantees the app remains functional.
* **Implementation Difficulty:** Moderate. The challenge lay in software architecture design rather than algorithm complexity. Building the custom exception classes required ensuring that the Controller layer (`CommandRunner`) only threw domain-specific exceptions, enforcing a clean separation of concerns and making the codebase highly testable (as evidenced by the extensive `CommandHanlderTest`, `ExportTest`, `SKUTest`, `SKUListTest`. An average of 68.6% of line coverage was achieved.

#### Contributions to the User Guide (UG)

##### FAQ Section
Authored FAQ section Q&A segments on saving, data-transfer and on implementation, translating into actionable steps for both technical and non-technical users alike.

#### Contributions to the Developer Guide (DG)

##### SKU Component
Created all the UML diagrams and explanation for the SKU component, under Design.

##### Storage Component
Created all the UML diagrams and explanation for the Storage component, under Design.

##### Add / Delete SKU Enhancement
Created all the UML diagrams and explanation for the Add / Delete enhancement, under Implementation.

##### Appendix
Wrote the:
1. **Appendix A: Product Scope**
2. **Appendix D: Glossary**
3. **Appendix E: Manual Testing**

sections to cater to both technical and non-technical audiences alike.

#### Contributions to Team-Based Tasks
1. **Repository Setup & Organization:** I established the initial GitHub organization and repository infrastructure for the team, configuring access controls, branch protection rules, and team onboarding to ensure a secure and collaborative development environment.
2. **Release Management:** Tagged, and deployed major application versions of v1.0 and v2.0 milestone releases.
3. **Workflow Standardization & Issue Tracking:** Standardized our GitHub workflow. This included a custom issue template, adding labels to tag statuses of Tasks and Bugs correctly.
4. **Documentation Coordination:** Coordinated and drafted the non-feature-specific sections of our project documentation via Google Docs, inclusive of setting up the user stores page. This also included tracking of target user profile, outlining the product scope, and ensuring a consistent tone across the manuals.
5. **Project Management & Task Delegation:** Facilitated feature discussions, guiding the team toward consensus on the final product scope. Furthermore, this involved managing project timelines, incorporating buffer periods to mitigate the impact of unforeseen isses.

#### Review and Mentoring Contributions
Faciliated and provided suggestions for better, more cohesive architecture, and the delegation of tasks to match member's strengths and preferences, through the use of a [central Google Docs](https://docs.google.com/document/d/e/2PACX-1vQohHhSMz69R5UO6f5hfYUJkco6Apk47ItuhdlcX0ttFVttmwhCqM7oTatOpOYOT16jKZL-DuIsKyZv/pub) file to keep track of tasks and scope.

#### Contributions Beyond the Project Team
To be added.

### Contributions to the Developer Guide (Extracts)
Below is an extract of the **Add / Delete SKU Section** from Implementation:

### Add / Delete SKU Feature

#### Implementation Details
The Add and Delete SKU mechanism is facilitated by the `SKUCommandHandler` component, which is dispatched by the `CommandRunner`. It manages the application's core state through a single primary data structure: the `SKUList`. Following object-oriented encapsulation principles, there are no external maps; each `SKU` manages its own `SKUTaskList`.

The operations are exposed and handled internally via the following methods:

* `SKUCommandHandler#handleAddSku(ParsedCommand)` — Validates arguments (ensuring they are not null or empty), checks for duplicates, and delegates to `SKUList` to instantiate a new `SKU` (which automatically initializes its own internal task list).
* `SKUCommandHandler#handleDeleteSku(ParsedCommand)` — Validates the input, ensures the target SKU exists, and removes the `SKU` from the inventory, which deletes purges all tasks associated with it.

Given below is an example usage scenario demonstrating how the Add SKU mechanism behaves at each step.

**Step 1.** The user executes `addsku n/PALLET-A l/A1`. The `Ui` reads the input, and the `Parser` extracts the command word and maps the arguments `n/` to `PALLET-A` and `l/` to `A1` into a `ParsedCommand` object.

**Step 2.** The `CommandRunner#run()` method receives this `ParsedCommand`. Recognizing the `addsku` command word, it routes execution to the dedicated `SKUCommandHandler#handleAddSku()`.

**Step 3.** `handleAddSku()` performs validations, checking for missing or empty arguments. It calls `CommandHelper.parseLocation("A1")` to resolve the `Location` enum. It then calls `skuList.findByID("PALLET-A")` to iterate through the `SKUList`. If no duplicates are found, it proceeds with the insertion.

![Steps 1 to 3](plantUML/add-delete-sku/add-sku-step1-3.png)

**Step 4.** The `SKUList#addSKU()` method is invoked. This method acts as a secondary defensive barrier, checking inputs before calling the `SKU` constructor. During instantiation, the `SKU` normalizes its ID (trimming whitespace and forcing uppercase) and automatically generates an empty `SKUTaskList` for itself. The `SKU` is then appended to the internal `ArrayList`.

![Step 4](plantUML/add-delete-sku/add-sku-step4.png)

**Step 5.** Back in `handleAddSku()`, execution completes successfully. Control returns to the `Ui` to print the success message. The system's memory state now contains the new `SKU`, fully equipped to accept tasks without requiring any external mapping.

![Step 5](plantUML/add-delete-sku/add-sku-step5.png)

*Note: The `deletesku` command operates by routing to `SKUCommandHandler#handleDeleteSku()`, which validates the input and throws a `SKUNotFoundException` if the target does not exist. It then calls `SKUList#deleteSKU()` to perform a case-insensitive removal from the array. Due to encapsulation, dropping the `SKU` object automatically garbage-collects its associated `SKUTaskList`, preventing memory leaks.*

The following sequence diagram shows the flow of adding a SKU:

![Step 5](plantUML/add-delete-sku/add-sku-sequence.png)

The following class diagram shows the architecture:

![Step 5](plantUML/add-delete-sku/add-sku-architecture.png)

#### Design Considerations

**Aspect: How SKU tasks are stored and mapped to their parent SKU:**

* **Current Implementation:** Require all task operations to access the `SKUTaskList` directly through the `SKU` object residing in the `SKUList`.
    * *Pros:* High cohesion and strict encapsulation. A SKU is solely responsible for its own tasks. Memory overhead is reduced, and state mutations are safer as there is no need to synchronize deletions across multiple data structures.
    * *Cons:* Slightly slower lookup times, as finding a task requires iterating through the `SKUList` to locate the parent SKU first (O(n) complexity).
* **Alternative:** Maintain a `HashMap<String, SKUTaskList>` inside the command handlers or `CommandRunner` to map SKU IDs to their tasks.
    * *Pros:* Fast, O(1) time complexity when looking up tasks for a specific SKU during filtering or task addition.
    * *Cons:* Severe data duplication and poor encapsulation. This requires the handlers to juggle references and manually synchronize deletions across two separate data structures, leading to an architecture prone to orphaned tasks if not correctly synced.

### Contributions to the User Guide (Extracts)
Below is an extract of the **FAQ Section**:
**Q**: How do I transfer my warehouse data to another computer?  
**A**: Install the application on the other computer and run it once to generate the default folders. Then, simply overwrite the `Data/storage.json` file it creates with the `storage.json` file from your previous computer.

**Q**: Do I need to manually save my tasks before closing the application?  
**A**: No. ItemTasker automatically saves your entire inventory and task list to the hard disk whenever you close the application using the `bye` or `exit` commands. Just ensure you exit the app properly instead of force-closing the terminal!

**Q**: Can I use my own custom location names like "Loading-Dock" or "Aisle-12"?  
**A**: Currently, ItemTasker strictly uses a standardized 3x3 grid system (A1 through C3) to ensure spatial sorting and distance calculations work instantly. You must assign SKUs to one of the 9 predefined sectors.

**Q**: How does the `listtasks l/LOCATION` command calculate distance?  
**A**: It calculates the "Manhattan Distance" across the warehouse grid. It measures the physical grid steps required to move from your specified location to the SKU's location, bringing the closest tasks to the top of your list so you can clear them efficiently.

**Q**: What happens if I manually edit the `storage.json` file and make a mistake?  
**A**: If the JSON format becomes invalid, outdated, or corrupted due to manual edits, ItemTasker will print a warning on startup and begin with an empty warehouse to prevent system crashes. It is highly recommended to make a copy of your `storage.json` file before doing any manual tweaking.