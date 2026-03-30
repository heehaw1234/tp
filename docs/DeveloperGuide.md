# Developer Guide

## Acknowledgements

This project is a derivative of the [SE-EDU initiative's](https://se-education.org) AddressBook-Level3 (AB3). We have customized its foundational architecture, parser logic, and command execution flow for our specific use case.

We also acknowledge the following for their contributions to our development process:
- [PlantUML](https://plantuml.com) for UML diagram generation.
- The NUS CS2113 teaching team for their instructional guidance and code templates.

## Design

### Architecture

ItemTasker follows a layered architecture with clear separation of concerns:

![Architecture Diagram](plantUML/command/command-runner-architecture.png)

**Component Relationships:**

The diagram shows the main components and their relationships:

- **Ui** handles all user interface interactions
- **Parser** transforms raw input into structured `ParsedCommand` objects
- **CommandRunner** dispatches commands to the appropriate handler
- **SkuCommandHandler** manages SKU-level commands (`addsku`, `editsku`, `deletesku`)
- **TaskCommandHandler** manages task-level commands (`addskutask`, `edittask`, `deletetask`, `marktask`, `unmarktask`, `sorttasks`)
- **ViewCommandHandler** manages read-only commands (`listtasks`, `find`)
- **CommandHelper** and **DateValidator** provide shared validation utilities
- **TaskSorter** sorts tasks by date, priority, or completion status
- **SKUList** contains multiple **SKU** instances (1-to-many relationship)
- **SKU** contains one **SKUTaskList** which holds multiple **SKUTask** instances (1-to-many relationship)
- **Storage** handles JSON persistence of the warehouse state
- **Export** writes a human-readable inventory snapshot to a text file

*Note: The multiplicity notation "1" → "\*" indicates a one-to-many relationship in UML. For example, one SKUList can contain zero or more SKUs, and one SKUTaskList can contain zero or more SKUTasks.*

**Key Design Principles:**

- **Single Responsibility**: Each handler class owns exactly one category of commands
- **Command Delegation**: `CommandRunner` acts purely as a dispatcher with no business logic
- **Encapsulation**: Each `SKU` owns its own `SKUTaskList` — no external maps or redundant data structures
- **Layered Architecture**: UI → Logic → Model → Storage separation
## Implementation

### Add / Delete SKU Feature

#### Implementation Details

The Add and Delete SKU mechanism is facilitated by the `CommandRunner` component, which manages the application's core state through a single primary data structure: the `SKUList`. Following strict Object-Oriented encapsulation, there are no external maps; each `SKU` inherently manages its own `SKUTaskList`.

The operations are exposed and handled internally via the following methods:

* `CommandRunner#handleAddSku(ParsedCommand)` — Validates constraints and delegates to `SKUList` to instantiate a new `SKU` (which automatically initializes its own internal task list).
* `CommandRunner#handleDeleteSku(ParsedCommand)` — Removes the `SKU` from the inventory, which inherently purges all tasks associated with it.

Given below is an example usage scenario demonstrating how the Add SKU mechanism behaves at each step.

**Step 1.** The user executes `addsku n/PALLET-A l/A1`. The `Ui` reads the input, and the `Parser` extracts the command word and maps the arguments `n/` to `PALLET-A` and `l/` to `A1` into a `ParsedCommand` object.

**Step 2.** The `CommandRunner#run()` method receives this `ParsedCommand`. Recognizing the `addsku` command word, it routes execution to `CommandRunner#handleAddSku()`.

**Step 3.** `handleAddSku()` parses the location string into a `Location` enum. It then calls `findSku("PALLET-A")` to iterate through the `SKUList`. Finding no duplicates, it proceeds with the insertion.

![System Memory State Steps 1 to 3](plantUML/add-delete-sku/add-sku-step1-3.png)

**Step 4.** The `SKUList#addSKU()` method is invoked. This method calls the `SKU` constructor, instantiating a new `SKU` object. During instantiation, the `SKU` automatically generates an empty `SKUTaskList` for itself. The `SKU` is then appended to the internal `ArrayList`.

![System Memory State Step 4](plantUML/add-delete-sku/add-sku-step4.png)

**Step 5.** Back in `handleAddSku()`, execution completes successfully. Control returns to the `Ui` to print the success message. The system's memory state now contains the new `SKU`, fully equipped to accept tasks without requiring any external mapping.

![System Memory State Step 5](plantUML/add-delete-sku/add-sku-step5.png)

*Note: The `deletesku` command operates by simply calling `SKUList#deleteSKU()` to remove the object from the array. Due to encapsulation, dropping the `SKU` object automatically garbage-collects its associated `SKUTaskList`, preventing memory leaks.*

The following sequence diagram shows the flow of adding a SKU:

![Add SKU Sequence Diagram](plantUML/add-delete-sku/add-sku-sequence.png)

The following class diagram shows the architecture:

![Add SKU Class Diagram](plantUML/add-delete-sku/add-sku-architecture.png)

#### Design Considerations

**Aspect: How SKU tasks are stored and mapped to their parent SKU:**

* **Current Implementation:** Require all task operations to access the `SKUTaskList` directly through the `SKU` object residing in the `SKUList`.
    * *Pros:* High cohesion and strict encapsulation. A SKU is solely responsible for its own tasks. Memory overhead is reduced, and state mutations are safer as there is no need to synchronize deletions across multiple data structures.
    * *Cons:* Slightly slower lookup times, as finding a task requires iterating through the `SKUList` to locate the parent SKU first (O(n) complexity).
* **Alternative:** Maintain a `HashMap<String, SKUTaskList>` inside the `CommandRunner` to map SKU IDs to their tasks.
    * *Pros:* Fast, O(1) time complexity when looking up tasks for a specific SKU during filtering or task addition.
    * *Cons:* Severe data duplication and poor encapsulation. This requires the `CommandRunner` to juggle references and manually synchronize deletions across two separate data structures, leading to an architecture prone to orphaned tasks if not correctly synced.

### Add / Delete SKU Task Feature

#### Implementation Details

The Add and Delete SKU Task operations are facilitated by the `CommandRunner` component, which routes execution to the specific SKU identified by the user, and subsequently down to that SKU's `SKUTaskList`. The `SKUTaskList` internally manages an `ArrayList<SKUTask>` and delegates data updates to the underlying `SKUTask` instances. The properties for a task include its ID, due date, completion status, and importantly, its `Priority` (an enum of HIGH, MEDIUM, or LOW). The `SKUTaskList` provides internal wrapper methods for additions, deletions, and modifications to enforce safe encapsulation.

The operations are handled internally via the following flow:

* `CommandRunner#handleAddSkuTask(ParsedCommand)` — Extracts the targeted SKU ID and the task properties (including `Priority`). It calls `CommandRunner#findSku()` to validate the SKU's existence and delegates to `SKUTaskList#addSKUTask()` to instantiate a new `SKUTask`.
* `CommandRunner#handleDeleteTask(ParsedCommand)` — Calls `CommandRunner#findSku()` to locate the SKU, validates the target task index, and instructs `SKUTaskList#deleteSKUTaskByIndex()` to remove the task from the internal array.

Given below is an example usage scenario demonstrating how the Add SKU Task mechanism behaves step-by-step.

**Step 1.** The user executes `addskutask n/P-A d/2026-10-10 p/HIGH`. The `Ui` reads the input, and the `Parser` extracts the command word and maps the arguments `n/` to `P-A`, `d/` to `2026-10-10`, and `p/` to `HIGH` into a `ParsedCommand` object.

**Step 2.** The `CommandRunner#run()` method receives this `ParsedCommand`. Recognizing the `addskutask` command word, it routes execution to `CommandRunner#handleAddSkuTask()`.

**Step 3.** `handleAddSkuTask()` processes the properties (parsing `HIGH` into the `Priority` enum). It calls `findSku("P-A")` to locate the target `SKU`. Upon finding it, it retrieves the SKU's internal `SKUTaskList`.

**Step 4.** The `SKUTaskList#addSKUTask()` method is invoked. This method instantiates a new `SKUTask` object with the extracted properties (including the `Priority` enum state). The task is appended to the internal `ArrayList`.

**Step 5.** Execution completes successfully, and control returns to the `Ui` to print the success message.

*Note: The delete operation follows a nearly identical traversal, except `CommandRunner#handleDeleteTask()` parses the target index instead of properties, and delegates to `SKUTaskList#deleteSKUTaskByIndex()`.*

The following sequence diagram shows the end-to-end flow of adding a SKU Task:

![Add SKU Task Sequence Diagram](plantUML/skutask-operations/addTaskSequence.png)

The following sequence diagram shows the end-to-end flow of deleting a SKU Task:

![Delete SKU Task Sequence Diagram](plantUML/skutask-operations/deleteTaskSequence.png)

### Task Property Access (Setters & Getters)

#### Implementation Details

Updating or retrieving a task's state passes entirely through `CommandRunner`, down to `SKUTaskList`, and finally to individual `SKUTask` objects. When a user executes `edittask n/P-A i/1 p/LOW`, `CommandRunner#handleEditTask()` locates the SKU and invokes `SKUTaskList#editSKUTask()`. `SKUTaskList` identifies the proper `SKUTask` at the index and modifies its state exclusively, reinforcing the abstraction.

The following sequence diagram shows the holistic flow of setting properties (e.g., due date, priority, and description via `t/DESC`):

![Setters Sequence Diagram](plantUML/skutask-operations/settersSequence.png)

The following sequence diagram illustrates reading properties from the objects for listing (e.g., executing `listtasks n/P-A`). Note that task output is produced by `toString()`, which internally includes the description if non-empty:

![Getters Sequence Diagram](plantUML/skutask-operations/gettersSequence.png)

The following class diagram shows the architecture connecting the `CommandRunner` down to the `SKUTask` instances:

![SKU Task Architecture Class Diagram](plantUML/skutask-operations/skutask-architecture.png)

#### Design Considerations

**Aspect: Managing task modifications via `SKUTaskList` wrappers versus returning internal objects:**

* **Current Implementation:** `SKUTaskList` handles modification duties in place (e.g., reading indices inside `editSKUTask` and mapping updates, working directly with enums like `Priority`).
    * *Pros:* Strong encapsulation. `SKUTaskList` dictates precisely how a task is safely modified, without leaking mutable object references back to caller-components.
    * *Cons:* Requires additional boilerplate wrapper methods inside `SKUTaskList` just to pass down simple enum updates (`Priority`) or strings to the internal tasks.
* **Alternative:** Expose `getTask(index)` method from `SKUTaskList`, letting callers (e.g., `CommandRunner`) modify the returned `SKUTask` object directly.
    * *Pros:* Simpler logic to write, heavily reducing the number of pass-through methods in `SKUTaskList`.
    * *Cons:* Weakens data coupling boundaries. A caller command might hold onto a `SKUTask` and accidentally modify it asynchronously outside of the defined safe access points, compromising system stability.

### Mark / Unmark SKU Task Feature

#### Implementation Details

The Mark and Unmark operations allow users to toggle the completion state of a `SKUTask`. Both operations are facilitated by the `CommandRunner` component, which routes execution through the SKU's `SKUTaskList` down to the individual `SKUTask`.

The operations are handled internally via the following methods:

* `CommandRunner#handleMarkTask(ParsedCommand)` — Locates the target SKU and task, validates that the task is not already marked, and delegates to `SKUTaskList#markTask()`.
* `CommandRunner#handleUnmarkTask(ParsedCommand)` — Locates the target SKU and task, validates that the task is not already unmarked, and delegates to `SKUTaskList#unmarkTask()`.

Given below is an example usage scenario for the Mark SKU Task mechanism.

**Step 1.** The user executes `marktask n/P-A i/1`. The `Ui` reads the input, and the `Parser` maps the arguments into a `ParsedCommand` object.

**Step 2.** The `CommandRunner#run()` method routes execution to `CommandRunner#handleMarkTask()`.

**Step 3.** `handleMarkTask()` calls `findSku("P-A")` to locate the target `SKU`. It then retrieves the `SKUTaskList` and the internal `ArrayList<SKUTask>` to validate the index.

**Step 4.** The task's `isDone()` state is checked. If already marked, an info message is returned. Otherwise, `SKUTaskList#markTask(1)` is called, which delegates to `SKUTask#mark()` to set `isDone = true`.

**Step 5.** Execution completes and a success message is displayed.

*Note: `unmarktask` follows the same traversal in reverse — it validates the task is currently marked before calling `SKUTaskList#unmarkTask()`, which delegates to `SKUTask#unmark()`.*

The following sequence diagram shows the flow of marking a task:

![Mark Task Sequence Diagram](plantUML/mark-unmark-task/markTaskSequence.png)

The following sequence diagram shows the flow of unmarking a task:

![Unmark Task Sequence Diagram](plantUML/mark-unmark-task/unmarkTaskSequence.png)

The following class diagram shows the architecture:

![Mark/Unmark Architecture Class Diagram](plantUML/mark-unmark-task/mark-unmark-architecture.png)

#### Design Considerations

**Aspect: Pre-condition check before toggling state:**

* **Current Implementation:** `handleMarkTask()` and `handleUnmarkTask()` check `isDone()` on the task before delegating, rejecting redundant operations with an info message.
    * *Pros:* Prevents silent no-ops that could confuse users (e.g., marking an already-done task with no feedback).
    * *Cons:* Requires an extra read call to `isDone()` before the write, slightly increasing coupling between `CommandRunner` and `SKUTask` state.
* **Alternative:** Delegate the guard check into `SKUTaskList` or `SKUTask` itself, throwing an exception on invalid toggle.
    * *Pros:* Encapsulates the guard closer to the data.
    * *Cons:* Requires exception propagation for a non-exceptional condition, which adds overhead and complicates the call chain.

### View SKU Task Feature

#### Implementation Details

The View SKU Task mechanism allows users to retrieve filtered or sorted views of the warehouse tasks without modifying the underlying data. This is facilitated by the `ViewSKUTask` logic processor, which decouples the filtering and sorting algorithms from the core `CommandRunner` and `Model` components.

The feature supports three primary modes:
1.  **SKU Filtering (`n/`):** Isolates tasks belonging to a specific SKU ID.
2.  **Priority Filtering (`p/`):** Streams all tasks and filters by the `Priority` enum (HIGH, MEDIUM, LOW).
3.  **Spatial Sorting (`l/`):** Sorts all system tasks based on the distance from a specified warehouse `Location`.

The operations are handled internally via the following flow:

* **command Routing:** `CommandRunner#handleListTasks(ParsedCommand)` extracts the arguments, instantiates a `ViewSKUTask` object, and sets the respective filter strings.
* **Data Aggregation:** `ViewSKUTask#listTasks(SKUList)` performs a full traversal of the `SKUList` to gather all available `SKUTask` objects into a "flattened" master list.
* **Filtering & Sorting Logic:**
  * For **SKU ID** and **Priority**, the viewer applies a Java Stream filter directly on the task list. Since these properties are encapsulated within the `SKUTask` object itself, no further interaction with the Model is required.
  * For **Distance**, the viewer uses `calculateDistance()` within a comparator. This method performs a **parent-lookup** for each task to retrieve the physical coordinates from its associated `parentSku`.

The Distance formula used for spatial sorting is:
$$\text{Distance} = |x_1 - x_2| + |y_1 - y_2|$$

#### Usage Scenarios and Sequence Diagrams

**Scenario 1: Spatial Sorting (`listtasks l/B2`)** This scenario demonstrates the parent-lookup loop required to resolve coordinates. The viewer iterates through each `SKU` to collect tasks and then references the `parentSku` location during the sort.

![View SKU Task (Distance) Sequence Diagram](plantUML/viewSKUTask-operations/viewSKUTask-distanceSequence.png)

**Scenario 2: Priority Filtering (`listtasks p/HIGH`)** This illustrates a simplified flow. Because `Priority` is stored internally within the `SKUTask`, the viewer gathers the tasks once and filters them internally without further Model interaction.

![View SKU Task (Priority) Sequence Diagram](plantUML/viewSKUTask-operations/viewSKUTask-prioritySequence.png)

**Scenario 3: SKU ID Filtering (`listtasks n/A123`)** Similar to Priority filtering, the SKU ID is an internal property of the `SKUTask`. The viewer gathers the list and filters it without needing a secondary lookup to the `parentSku`.

![View SKU Task (SKU ID) Sequence Diagram](plantUML/viewSKUTask-operations/viewSKUTask-SKUSequence.png)

#### Architecture

The following class diagram shows how the `ViewSKUTask` logic component interacts with the Model:

![View SKU Task Architecture](plantUML/viewSKUTask-operations/viewSKUTask-architecture.png)

#### Design Considerations

**Aspect: Data "Flattening" for Global Views:**

* **Current Implementation:** `ViewSKUTask` manually iterates through the nested `SKU -> SKUTaskList` hierarchy to build a temporary list for filtering/sorting.
  * *Pros:* Maintains strict encapsulation. Neither `CommandRunner` nor `ViewSKUTask` needs to maintain a redundant global map of tasks, ensuring the `SKU` remains the single source of truth for its tasks.
  * *Cons:* Performance cost of $O(N)$ where $N$ is the total number of SKUs, as every list must be visited to gather tasks for a global view.
* **Alternative:** Maintain a `MasterTaskList` in `SKUList` that updates whenever a task is added/deleted.
  * *Pros:* Sorting and filtering are faster ($O(1)$ to retrieve the base list).
  * *Cons:* Higher risk of data inconsistency. Deleting an SKU would require purging its specific tasks from the master list, increasing complexity in the `Delete SKU` feature.

## Appendix A: Product Scope

### Target user profile

{Describe the target user profile}

### Value proposition

{Describe the value proposition: what problem does it solve?}

## Appendix B: User Stories

| Version | As a ... | I want to ... | So that I can ... |
|---------|----------|---------------|-------------------|
| v1.0 | Inventory Manager | register a new SKU | begin tracking accountability tasks for this particular SKU |
| v1.0 | Inventory Manager | add a task to a specific SKU | ensure necessary inspections are conducted |
| v1.0 | Inventory Manager | assign a priority level to each task | clear tasks that need to be performed first |
| v1.0 | Inventory Manager | mark a task as completed for a specific SKU | track the tasks completed in a given day |
| v1.0 | Inventory Manager | set due dates for tasks assigned to a SKU | quicly locate specific tasks without browsing through the entire list |
| v1.0 | Inventory Manager | input my current location and sort tasks in terms of distance to me | clear tasks in the warehouse starting with the closest task (in terms of distance) to me |
| v1.0 | Inventory Manager | delete a task for a specific SKU | not unnecessarily track it if it needs to be dropped |
| v1.0 | Inventory Manager | view all my tasks in a single dashboard ordered by SKU | know what needs to be completed & plan accordingly |
| v1.0 | Inventory Manager | set a default priority for all new tasks | speed up the task creation process |
| v1.0 | Inventory Manager | attach a "Location" to a task | not waste time looking for the item that needs work |
| v2.0 | Inventory Manager | sort my tasks by priority | complete only high-priority tasks in the event of limited time |
| v2.0 | Inventory Manager | pull up the tasks only for a specific SKU | complete all the tasks for a given SKU if required |
| v2.0 | Inventory Manager | search for tasks using keywords | quickly locate specific tasks without browsing through the entire list |
| v2.0 | Inventory Manager | view a help guide of available commands | learn how to use the CLI without external documentation |
| v2.0 | Inventory Manager | generate a view of the amount of tasks in each sector of the warehouse | have a birds-eye view of the location of each task |
| v2.0 | Inventory Manager | add notes or comments to individual task | document observations or special conditions during task execution |
| v2.0 | Inventory Manager | register a new SKU identifier into a localDB | track specific tasks for an item separately |
| v2.0 | Inventory Manager | attach an action item to a registered SKU | the condition + quality of the item is monitored |
| v2.0 | Inventory Manager | assign priority level when creating a task | ensure urgent issues such as expiring goods are attended to in time |
| v2.0 | Inventory Manager | execute a command to view a dashboard summary of all active SKUs & pending tasks | get instantly get a view of all SKUs without using a complicated GUI |
| v2.0 | Inventory Manager | mark a task as resolved | clear the queue of outstanding tasks by priority |
| v2.0 | Inventory Manager | export a list of all high priority tasks to a readable format, e.g CSV | print a physical checklist for warehouse associates who do not have access to the CLI |
| v2.0 | Inventory Manager | search for a specific SKU id | quickly audit all pending actions for a specific product that might be under inspection / recall |
| v2.0 | Inventory Manager | edit the description of an existing task | update information efficiently |

## Appendix C: Non-Functional Requirements

1. **Environment Requirements:** The system should work on any mainstream OS (Windows, Linux, macOS) as long as it has Java `17` or above installed.
2. **Data Requirements:** Data should be stored locally in a single `Data/storage.json` file without requiring a standalone database management system. The size of the text file should be kept minimal.
3. **Performance Requirements:** The system should execute all commands (e.g. adding, deleting, listing) and display the output within two seconds on a standard modern PC.
4. **Usability (Quality) Requirements:** A user with above-average typing speed for regular English text (i.e., not code, not system admin commands) should be able to accomplish most of the tasks faster using CLI than using a GUI-based application.
5. **Quality (Robustness) Requirements:** The application should be able to handle invalid user input gracefully without crashing.
6. **Process Requirements:** The project should follow the milestones set for CS2113 and utilize GitHub Actions for Continuous Integration (CI) and automated testing.
7. **Maintainability Requirements:** Code should be structured in a modular fashion using Object-Oriented principles to ensure extensibility for future features. Strict Checkstyle rule configurations must be adhered to.

## Appendix D: Glossary

* *glossary item* - Definition

## Appendix E: Instructions for Manual Testing

{Give instructions on how to do a manual product testing e.g., how to load sample data to be used for testing}