# Om Tirodkar - Project Portfolio Page

## Overview

ItemTasker is a CLI-based Stock Keeping Unit (SKU) Ticketing System. A localized command-line tool designed to handle inventory specific actions required for individual item SKUs.

## Summary of Contributions

### Code Contributed

[RepoSense link of my profile.](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=omcodedthis&breakdown=true)

### Enhancements Implemented

#### Add / Delete SKU

Designed `SKUList.java`, `SKU.java`, `SKUTaskList.java`, and `SKUCommandHandler.java` for core inventory management of encapsulated `SKUTaskList`s (Moderate Difficulty). Ensures memory-safe `ArrayList` modification and robustly prevents duplicate entries via case-insensitive ID matching, fully validated by `SKUListTest.java` and `SKUTest.java`.

#### Export

Implemented data extraction in `ExportCommand.java` to compile the warehouse state into an OS-agnostic, human-readable text file for auditing (High Difficulty). Built defensive checks against edge-case file states (e.g., existing "Data" files blocking directory creation), and autonomously generates directories, rigorously tested in `ExportTest.java`.

#### Command Object Instantiation

Designed `ParsedCommand.java` and restructured `Parser.java` to decouple raw CLI input from application logic into a structured, queryable object (Moderate Difficulty). Normalizes inputs and utilizes `Collections.unmodifiableMap` to strictly enforce a read-only data flow.

#### Entry-Loop, Exceptions & Testing

Designed the `while (runner.isRunning())` lifecycle in `ItemTasker.java` and established the `ItemTaskerException.java` hierarchy (Moderate Difficulty). Enforced clean separation of concerns in `CommandRunner.java` by translating nested domain errors into user-friendly UI messages without leaking stack traces, supported by extensive tests like `CommandHandlerTest.java`, enabling 68.6% test coverage.

### Contributions to the User Guide (UG)

#### FAQ Section

Authored Q&A segments on saving, data-transfer, and implementation, translating technical mechanics into actionable steps for end-users.

### Contributions to the Developer Guide (DG)

#### SKU & Storage Components

Created all UML diagrams and architectural explanations for the SKU & Storage component under Design.

#### Add / Delete SKU Enhancement

Created all memory-state UML diagrams and explanations for the Add/Delete feature under Implementation.

#### Appendix

Wrote **Appendix A: Product Scope**, **Appendix D: Glossary**, and **Appendix E: Manual Testing** to cater to both technical and non-technical audiences.

### Contributions to Team-Based Tasks

1. **Repository & Releases:** Established GitHub organization, branch protection rules, and deployed v1.0/v2.0/v2.1 milestone releases.
2. **Workflow:** Standardized issue templates and labeling conventions across the team for Tasks/Bugs. Delegated PE-D bugs amongst the team for quicker turnaround for submission.
3. **Project Management:** Coordinated non-feature documentation (Product Scope, User Profile) and managed project timelines with safety buffers for the team.

### Review and Mentoring Contributions

Facilitated cohesive architecture discussions and task delegation matching members' strengths via a [central Google Docs](https://docs.google.com/document/d/e/2PACX-1vQohHhSMz69R5UO6f5hfYUJkco6Apk47ItuhdlcX0ttFVttmwhCqM7oTatOpOYOT16jKZL-DuIsKyZv/pub) tracker.

### Contributions Beyond the Project Team

* **Peer Quality Assurance (PE-D):** Systematically tested a peer team's application during the Practical Exam Dry Run, identifying and documenting critical functionality flaws and documentation bugs to assist their final v2.1 release.