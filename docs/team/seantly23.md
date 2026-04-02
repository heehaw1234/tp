# SeanTLY23 - Project Portfolio Page

## Project: ItemTasker

ItemTasker is a desktop application used for managing stock-keeping units (SKUs) and their associated tasks efficiently. The user interacts with it using a CLI (Command Line Interface). It is written in Java and helps users streamline their inventory and task management workflows.

### Overview
I was primarily responsible for the **User Interface (Ui)** component, the physical warehouse visualization logic (**`viewmap`**), and the SKU health-tracking features (**`status`**). I also played a key role in refactoring the command handling architecture and maintaining the project's documentation standards.

Given below are my contributions to the project.

### Summary of Contributions

* **New Feature**: Implemented the **`viewmap`** command.
    * **What it does**: Generates a visual 3x3 grid in the terminal representing the physical warehouse layout. It maps SKU IDs to their specific grid coordinates (e.g., A1 to C3).
    * **Justification**: Warehouse managers need a spatial mental model of their inventory. This feature allows users to quickly identify occupied versus vacant storage slots without scrolling through text lists.
    * **Highlights**: Required implementing a mapping logic between the `Location` enum and a coordinate-based grid display, ensuring the CLI output remained aligned regardless of SKU ID length.

* **New Feature**: Implemented the **`status`** command.
    * **What it does**: Provides a summary of a specific SKU's "health," showing its physical location and a completion ratio of its attached tasks (e.g., "5/10 tasks completed").
    * **Justification**: While other commands show granular task details, `status` offers a high-level overview for quick decision-making and progress tracking.
    * **Highlights**: Integrated the UI display logic with the Model layer to dynamically calculate completion percentages and aggregate task states.

* **New Feature**: Designed and implemented the **`Ui` component**.
    * **What it does**: Acts as the primary interface for the application, handling all user-facing output, including standardized success messages, error alerts, and the warehouse grid.
    * **Justification**: A consistent UI is vital for a CLI tool to ensure that users can distinguish between different types of feedback at a glance.
    * **Highlights**: Developed a centralized messaging system that follows a consistent format (dividers, headers, and indentation), improving the overall professional feel of the application.

* **Code contributed**: [RepoSense link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=seant&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2026-02-20T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&filteredFileName=)

* **Enhancements implemented**:
    * Refactored the monolithic `CommandRunner` into specialized handlers (specifically co-implementing `ViewCommandHandler`), improving adherence to the **Single Responsibility Principle**.
    * Standardized all command output formats across the team to ensure a cohesive user experience.
    * Fixed UI rendering bugs where special characters or long SKU names caused the `viewmap` grid to misalign.

* **Contributions to the UG**:
    * Added documentation and examples for the `viewmap` and `status` commands.
    * Authored a new **FAQ entry** explaining how to use `viewmap` to identify available warehouse space before adding new SKUs.
    * Conducted a final review of the Command Summary table to ensure all 16 commands were accurately represented.

* **Contributions to the DG**:
    * Authored the **UI Component Design** section, documenting the boundary pattern used to separate internal logic from terminal output.
    * Created implementation walkthroughs for the `status` feature, including a sequence diagram showing the interaction between `ViewCommandHandler`, `SKU`, and `Ui`.
    * Updated the **Architecture Diagram** to reflect the transition from a single `CommandRunner` to a multi-handler architecture.
    * Created and integrated PlantUML diagrams for the UI layout and the mapping logic used in the warehouse grid display.

* **Contributions to team-based tasks**:
    * Acted as the primary reviewer for UI-related Pull Requests to ensure visual consistency.
    * Assisted in refactoring `ViewCommandHandler` to separate read-only logic from data-mutation logic.