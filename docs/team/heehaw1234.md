# heehaw1234 - Project Portfolio Page

## Project: ItemTasker

ItemTasker is a desktop application used for managing stock-keeping units (SKUs) and their associated tasks efficiently. The user interacts with it using a CLI (Command Line Interface). It is written in Java and helps users streamline their inventory and task management workflows.

### Overview
I was primarily responsible for the core task data model (`SKUTask`, `SKUTaskList`, `Priority`), the `find` command search logic, and the SKUTask component documentation in the Developer Guide. I also contributed to project infrastructure such as logging, assertions, and repository hygiene.

Given below are my contributions to the project.

### Summary of Contributions

* **New Feature**: Added the core `SKUTask` and `SKUTaskList` models.
  * **What it does**: Establishes the foundational data structures for the application, representing individual stock keeping unit tasks and managing the central collection of all tasks. 
  * **Justification**: This feature forms the backbone of the application's data layer, enabling the creation, retrieval, and tracking of tasks and their associated priority states.
  * **Highlights**: This required careful consideration of the fields, priority states, and overall class responsibility to ensure other components (like storage and UI) could seamlessly interact with the task data.

* **New Feature**: Implemented the `find` command data-matching logic in `ViewCommandHandler`.
  * **What it does**: Provides a robust search mechanism that allows users to filter tasks by SKU IDs, descriptions, and specific task indices. It parses multi-parameter search conditions and safely iterates over the inventory.
  * **Justification**: Finding specific tasks is crucial for inventory scaling. The function was specifically designed with the Single Level of Abstraction Principle (SLAP) in mind, breaking down complex filtering validations across `handleFind`, `searchTasks`, and nested SKU traversal methods.
  * **Highlights**: Navigating edge cases—like preventing out-of-bounds exceptions when searching arbitrary indices across differently sized SKU inventories—required careful loop tracking and error handling. It elegantly isolates reading/filtering logic cleanly within the `ViewCommandHandler`.

* **New Feature**: Co-Implemented `ViewCommandHandler`.
  * **What it does**: Handles commands related to reading, sorting, and displaying tasks to the user. It isolates view-specific logic away from execution logic.
  * **Justification**: Increases the cohesiveness of the system. By splitting the logic from a monolithic runner into specialized handlers, the code obeys the Single Responsibility Principle, making view modifications isolated and sustainable.

* **Code contributed**: [RepoSense link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=heehaw1234&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2026-02-20T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&filteredFileName=)

* **Enhancements implemented**:
  * Set up logging infrastructure via `ItemTaskerLogger`, which suppresses red `stderr` log output in the CLI by redirecting logs to a file handler — improving user experience when running the `.jar`.
  * Wrote comprehensive JUnit tests for `SKUTaskTest`, `SKUTaskListTest`, and `FindCommandTest` to verify core operations and edge cases in parsing and data storage.
  * Related PRs: [#45](https://github.com/AY2526S2-CS2113-W09-1/tp/pull/45), [#65](https://github.com/AY2526S2-CS2113-W09-1/tp/pull/65), [#96](https://github.com/AY2526S2-CS2113-W09-1/tp/pull/96), [#112](https://github.com/AY2526S2-CS2113-W09-1/tp/pull/112), [#126](https://github.com/AY2526S2-CS2113-W09-1/tp/pull/126), [#132](https://github.com/AY2526S2-CS2113-W09-1/tp/pull/132)
  * Bugs reported and fixed: [#54](https://github.com/AY2526S2-CS2113-W09-1/tp/issues/54), [#55](https://github.com/AY2526S2-CS2113-W09-1/tp/issues/55), [#92](https://github.com/AY2526S2-CS2113-W09-1/tp/issues/92), [#125](https://github.com/AY2526S2-CS2113-W09-1/tp/issues/125), [#131](https://github.com/AY2526S2-CS2113-W09-1/tp/issues/131)
  * Features & enhancements tracked via issues: [#22](https://github.com/AY2526S2-CS2113-W09-1/tp/issues/22), [#23](https://github.com/AY2526S2-CS2113-W09-1/tp/issues/23), [#24](https://github.com/AY2526S2-CS2113-W09-1/tp/issues/24), [#53](https://github.com/AY2526S2-CS2113-W09-1/tp/issues/53), [#64](https://github.com/AY2526S2-CS2113-W09-1/tp/issues/64), [#78](https://github.com/AY2526S2-CS2113-W09-1/tp/issues/78), [#95](https://github.com/AY2526S2-CS2113-W09-1/tp/issues/95), [#110](https://github.com/AY2526S2-CS2113-W09-1/tp/issues/110), [#111](https://github.com/AY2526S2-CS2113-W09-1/tp/issues/111), [#116](https://github.com/AY2526S2-CS2113-W09-1/tp/issues/116), [#133](https://github.com/AY2526S2-CS2113-W09-1/tp/issues/133), [#149](https://github.com/AY2526S2-CS2113-W09-1/tp/issues/149)

* **Contributions to the UG**:
  * Added FAQ entries addressing application logging locations for bug reporting (`itemtasker.log`) and explaining the software's scalability constraints regarding SKU/task capacity limits.
  * Assisted in aligning documentation to accurately reflect actual Command Line commands for `ItemTasker` following the project's refactoring from boilerplate AddressBook commands.
  * Related PRs: [#148](https://github.com/AY2526S2-CS2113-W09-1/tp/pull/148)

* **Contributions to the DG**:
  * Authored the implementation details and specifications for the `SKUTask` component, ensuring it cleanly documented the system's class structures and logic flows using AddressBook-Level 3 formatting.
  * Authored **Appendix B (Target User Profile)** and **Appendix C (Non-Functional Requirements)**, establishing the intended user constraints, system performance expectations, environment boundaries, and data management scoping.
  * Created and integrated multiple PlantUML diagrams (`skutask-architecture.puml`, `addTaskSequence.puml`, `deleteTaskSequence.puml`, `settersSequence.puml`, `gettersSequence.puml`) to visually represent the interaction between the `Ui`, `TaskCommandHandler`, and `CommandHelper`.
  * Kept architectural diagrams up to date by tracking the transition of logic from `CommandRunner` to `TaskCommandHandler` & `ViewCommandHandler`.
  * Related PRs: [#77](https://github.com/AY2526S2-CS2113-W09-1/tp/pull/77), [#86](https://github.com/AY2526S2-CS2113-W09-1/tp/pull/86), [#93](https://github.com/AY2526S2-CS2113-W09-1/tp/pull/93), [#115](https://github.com/AY2526S2-CS2113-W09-1/tp/pull/115), [#119](https://github.com/AY2526S2-CS2113-W09-1/tp/pull/119), [#150](https://github.com/AY2526S2-CS2113-W09-1/tp/pull/150)

* **Contributions to team-based tasks**:
  * Added assertion flags (`-ea`) to `build.gradle` for both `run` and `test` configurations to enforce defensive programming across the project.
  * Maintained `.gitignore` to exclude `Data/storage.json`, `*.log`, and `*.lck` files, preventing local runtime artifacts from polluting the repository.
  * Managed `storage.json` tracking issues (removing it from version control and clearing committed copies).
  * Managed GitHub pull request alignments and merged documentation refactors to ensure the repository remains functional.
  * Related PRs: [#16](https://github.com/AY2526S2-CS2113-W09-1/tp/pull/16), [#25](https://github.com/AY2526S2-CS2113-W09-1/tp/pull/25), [#26](https://github.com/AY2526S2-CS2113-W09-1/tp/pull/26), [#56](https://github.com/AY2526S2-CS2113-W09-1/tp/pull/56), [#113](https://github.com/AY2526S2-CS2113-W09-1/tp/pull/113), [#114](https://github.com/AY2526S2-CS2113-W09-1/tp/pull/114), [#136](https://github.com/AY2526S2-CS2113-W09-1/tp/pull/136)

* **Review/mentoring contributions**:
  * Helped track and resolve a bug related to distance calculation due to case sensitivity ([#92](https://github.com/AY2526S2-CS2113-W09-1/tp/issues/92)).