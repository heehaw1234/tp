# Om Tirodkar - Project Portfolio Page

## Overview

ItemTasker is a CLI-based Stock Keeping Unit (SKU) Ticketing System. A localized command-line tool designed to handle
inventory specific actions required for individual item SKUs.

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

Below is a 1:1 extract pulled from the User Guide, note that only a portion is made available here due to page limits.

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

Below is are 1:1 diagrams pulled from the Developer Guide, note that only a portion is made available here due to page limits.

### SKU Component Diagram

![Diagram](../diagrams/component-sku/component-sku-diagram.png)

<div style="page-break-after: always;"></div>

### Storage Component Diagram

![Diagram](../diagrams/component-storage/component-storage-diagram.png)

### Add / Delete SKU Feature Sequence Diagram

![Sequence Diagram](../diagrams/add-delete-sku/add-sku-sequence.png)

<div style="page-break-after: always;"></div>

### Add / Delete SKU Feature Architecture Diagram

![Class Diagram](../diagrams/add-delete-sku/add-sku-architecture.png)


