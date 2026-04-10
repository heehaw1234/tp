# Om Tirodkar - Project Portfolio Page

## Overview

ItemTasker is a CLI-based Stock Keeping Unit (SKU) Ticketing System. A localized command-line tool designed to handle
inventory specific actions required for individual item SKUs such as damage checks, expiry reviews & quality control.

## Summary of Contributions

### Code Contributed

[RepoSense link of my profile.](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=omcodedthis&breakdown=true)

### Enhancements Implemented

#### Add / Delete SKU

Created `SKUList.java`, `SKU.java`, `SKUTaskList.java`, and `SKUCommandHandler.java` for core inventory management of
encapsulated `SKUTaskList`s (Moderate Difficulty). Ensures memory-safe `ArrayList` modification and robustly prevents
duplicate entries via case-insensitive ID matching, fully validated by `SKUListTest.java` and `SKUTest.java`.

#### Export

Implemented data extraction in `ExportCommand.java` to compile the warehouse state into an OS-agnostic, human-readable
text file for auditing (High Difficulty). Built defensive checks against edge-case file states (e.g., existing "Data"
files blocking directory creation), and autonomously generates directories, rigorously tested in `ExportTest.java`.

#### Command Object Instantiation

Designed `ParsedCommand.java` and restructured `Parser.java` to decouple raw CLI input from application logic into a
structured, queryable object (Moderate Difficulty). Normalizes inputs and utilizes `Collections.unmodifiableMap` to
strictly enforce a read-only data flow.

#### Entry-Loop, Exceptions & Testing

Designed the `while (runner.isRunning())` lifecycle in `ItemTasker.java` and established the `ItemTaskerException.java`
hierarchy (Moderate Difficulty). Enforced clean separation of concerns in `CommandRunner.java` by translating nested
domain errors into user-friendly UI messages without leaking stack traces, supported by extensive tests
like `CommandHandlerTest.java`, achieving an average of 68.6% line coverage.

### Contributions to the User Guide (UG)

#### FAQ Section

Authored Q&A segments on saving, data-transfer, and implementation, translating technical mechanics into actionable
steps for end-users.

### Contributions to the Developer Guide (DG)

#### SKU & Storage Components and Add / Delete SKU Enhancement

Created all UML diagrams and architectural explanations for the SKU & Storage component under Design and Add/Delete
feature under Implementation.

#### Appendix

Wrote **Appendix A: Product Scope**, **Appendix D: Glossary**, and **Appendix E: Manual Testing** to cater to both
technical and non-technical audiences.

### Contributions to Team-Based Tasks

1. **Repository & Releases:** Established GitHub organization, branch protection rules, and deployed v1.0/v2.0/v2.1
   milestone releases.
2. **Workflow:** Standardized issue templates and labeling conventions across the team for Tasks/Bugs. Delegated PE-D
   bugs amongst the team for quicker turnaround for submission.
3. **Project Management:** Coordinated non-feature documentation (Product Scope, User Profile) and managed project
   timelines with safety buffers for the team.

### Review and Mentoring Contributions

Facilitated cohesive architecture discussions and task delegation matching members' strengths via
a [central Google Docs](https://docs.google.com/document/d/e/2PACX-1vQohHhSMz69R5UO6f5hfYUJkco6Apk47ItuhdlcX0ttFVttmwhCqM7oTatOpOYOT16jKZL-DuIsKyZv/pub)
tracker.

### Contributions Beyond the Project Team

* **Peer Quality Assurance (PE-D):** Systematically tested a peer team's application during the Practical Exam Dry Run,
  identifying and documenting critical functionality flaws and documentation bugs to assist their final v2.1 release.

<div style="page-break-after: always;"></div>

## Contributions to the User Guide (Extracts)

## FAQ

**Q**: How do I transfer my warehouse data to another computer?  
**A**: Install the application on the other computer and run it once to generate the default folders. Then, simply
overwrite the `Data/storage.json` file it creates with the `storage.json` file from your previous computer.

**Q**: Do I need to manually save my tasks before closing the application?  
**A**: No. ItemTasker automatically saves your entire inventory and task list to the hard disk whenever you close the
application using the `bye` or `exit` commands. Just ensure you exit the app properly instead of force-closing the
terminal!

**Q**: Can I use my own custom location names like "Loading-Dock" or "Aisle-12"?  
**A**: Currently, ItemTasker strictly uses a standardized 3x3 grid system (A1 through C3) to ensure spatial sorting and
distance calculations work instantly. You must assign SKUs to one of the 9 predefined sectors.

**Q**: How does the `listtasks l/LOCATION` command calculate distance?  
**A**: It calculates the "Manhattan Distance" across the warehouse grid. It measures the physical grid steps required to
move from your specified location to the SKU's location, bringing the closest tasks to the top of your list so you can
clear them efficiently.

**Q**: What happens if I manually edit the `storage.json` file and make a mistake?  
**A**: If the JSON format becomes invalid, outdated, or corrupted due to manual edits, ItemTasker will print a warning
on startup and begin with an empty warehouse to prevent system crashes. It is highly recommended to make a copy of
your `storage.json` file before doing any manual tweaking.

<div style="page-break-after: always;"></div>

## Contributions to the Developer Guide (Extracts)

### SKU component

**API** : `SKUList.java`, `SKU.java`, `Location.java`.

![Diagram](../diagrams/component-sku/component-sku-diagram.png)

The `SKU` component,

* stores the data i.e., all `SKU` objects (which are contained in a central `SKUList` object).
* enforces data integrity at the domain level. The `SKUList` ensures that no duplicate SKU IDs exist within the
  warehouse, and the `SKU` constructor normalizes all IDs (trimming whitespace and converting to uppercase) while
  guaranteeing a mandatory valid `Location` is assigned.
* encapsulates task management by having each `SKU` object independently own and automatically initialize its
  own `SKUTaskList` upon creation. This establishes a strict object-oriented boundary where a SKU is solely responsible
  for its associated tasks.
* does not depend on any of the other main components (such as **`UI`**, **`Logic`**, or **`Storage`**). As the `SKU`
  component represents the core data entities of the domain, it makes sense on its own without depending on external
  execution or presentation layers.

### Storage component

**API** : `Storage.java` and `Export.java`.

![Diagram](../diagrams/component-storage/component-storage-diagram.png)

The `Storage` component,

* can save the warehouse inventory data (the entire `SKUList` hierarchy, including SKUs, SKUTaskLists, and SKUTasks) in
  JSON format to the hard disk, and read it back into the corresponding objects.
* can export the current warehouse state into a formatted, human-readable text file (`ItemTasker_Export.txt`) for
  reporting purposes.
* gracefully handles missing directories by automatically creating the required `Data/` folder upon saving or exporting.
* guards against corrupted or outdated data files during the loading sequence to prevent application crashes.
* depends on classes in the `sku` and `skutask` components (because the Storage component's job is to save, retrieve,
  and parse objects that represent the data of the App in memory).
* utilizes the external Gson library for all JSON serialization and deserialization processes.

<div style="page-break-after: always;"></div>

### Add / Delete SKU Feature

#### Implementation Details

The Add and Delete SKU mechanism is facilitated by the `SKUCommandHandler` component, which is dispatched by
the `CommandRunner`. It manages the application's core state through a single primary data structure: the `SKUList`.
Following object-oriented encapsulation principles, there are no external maps; each `SKU` manages its
own `SKUTaskList`.

The operations are exposed and handled internally via the following methods:

* `SKUCommandHandler#handleAddSku(ParsedCommand)` — Validates arguments (ensuring they are not null or empty), checks
  for duplicates, and delegates to `SKUList` to instantiate a new `SKU` (which automatically initializes its own
  internal task list).
* `SKUCommandHandler#handleDeleteSku(ParsedCommand)` — Validates the input, ensures the target SKU exists, and removes
  the `SKU` from the inventory, which deletes purges all tasks associated with it.

Given below is an example usage scenario demonstrating how the Add SKU mechanism behaves at each step.

**Step 1.** The user executes `addsku n/PALLET-A l/A1`. The `Ui` reads the input, and the `Parser` extracts the command
word and maps the arguments `n/` to `PALLET-A` and `l/` to `A1` into a `ParsedCommand` object.

**Step 2.** The `CommandRunner#run()` method receives this `ParsedCommand`. Recognizing the `addsku` command word, it
routes execution to the dedicated `SKUCommandHandler#handleAddSku()`.

**Step 3.** `handleAddSku()` performs validations, checking for missing or empty arguments. It
calls `CommandHelper.parseLocation("A1")` to resolve the `Location` enum. It then calls `skuList.findByID("PALLET-A")`
to iterate through the `SKUList`. If no duplicates are found, it proceeds with the insertion.

![Steps 1 to 3](../diagrams/add-delete-sku/add-sku-step1-3.png)

**Step 4.** The `SKUList#addSKU()` method is invoked. This method acts as a secondary defensive barrier, checking inputs
before calling the `SKU` constructor. During instantiation, the `SKU` normalizes its ID (trimming whitespace and forcing
uppercase) and automatically generates an empty `SKUTaskList` for itself. The `SKU` is then appended to the
internal `ArrayList`.

![Step 4](../diagrams/add-delete-sku/add-sku-step4.png)

**Step 5.** Back in `handleAddSku()`, execution completes successfully. Control returns to the `Ui` to print the success
message. The system's memory state now contains the new `SKU`, fully equipped to accept tasks without requiring any
external mapping.

![Step 5](../diagrams/add-delete-sku/add-sku-step5.png)

*Note: The `deletesku` command operates by routing to `SKUCommandHandler#handleDeleteSku()`, which validates the input
and throws a `SKUNotFoundException` if the target does not exist. It then calls `SKUList#deleteSKU()` to perform a
case-insensitive removal from the array. Due to encapsulation, dropping the `SKU` object automatically garbage-collects
its associated `SKUTaskList`, preventing memory leaks.*

The following sequence diagram shows the flow of adding a SKU:

![Step 5](../diagrams/add-delete-sku/add-sku-sequence.png)

The following class diagram shows the architecture:

![Step 5](../diagrams/add-delete-sku/add-sku-architecture.png)

#### Design Considerations

**Aspect: How SKU tasks are stored and mapped to their parent SKU:**

* **Current Implementation:** Require all task operations to access the `SKUTaskList` directly through the `SKU` object
  residing in the `SKUList`.
    * *Pros:* High cohesion and strict encapsulation. A SKU is solely responsible for its own tasks. Memory overhead is
      reduced, and state mutations are safer as there is no need to synchronize deletions across multiple data
      structures.
    * *Cons:* Slightly slower lookup times, as finding a task requires iterating through the `SKUList` to locate the
      parent SKU first (O(n) complexity).
* **Alternative:** Maintain a `HashMap<String, SKUTaskList>` inside the command handlers or `CommandRunner` to map SKU
  IDs to their tasks.
    * *Pros:* Fast, O(1) time complexity when looking up tasks for a specific SKU during filtering or task addition.
    * *Cons:* Severe data duplication and poor encapsulation. This requires the handlers to juggle references and
      manually synchronize deletions across two separate data structures, leading to an architecture prone to orphaned
      tasks if not correctly synced.