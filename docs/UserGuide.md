# User Guide

## Introduction

ItemTasker is a desktop Command Line Interface
(CLI) application designed for warehouse inventory management.
It lets warehouse operators efficiently track Stock Keeping 
Units (SKUs) and their associated maintenance
or operational tasks — all from the keyboard, with no GUI overhead.
Built with Java 17, ItemTasker follows a clean layered 
architecture (UI → Logic → Model → Storage) and models a 3×3 warehouse grid. 
Each SKU maps to a physical grid location (A1–C3) and carries its own task list,
where every task can be assigned a due date, a priority level 
(HIGH / MEDIUM / LOW), and a description.
The application persists all state to JSON automatically on exit
and reloads it seamlessly on the next launch.

## Quick Start

1. Ensure you have Java `17` or above installed on your computer.

2. Download the latest `.jar` file from [here](http://link.to/itemtasker).

3. Copy the file to the folder you want to use as the home folder for ItemTasker.

4. Open a command terminal, `cd` into the folder you put the jar file in, and use the following command to run the application:
```
   java -jar itemtasker.jar
```
If the setup is correct, you should see the welcome message appear in a few seconds.

5. Type a command and press Enter to execute it. e.g. typing `help` and pressing Enter will display all available commands. Some example commands you can try:
    * `listtasks` : Lists all tasks across all SKUs.
    * `addsku n/PALLET-A l/B2` : Adds a new SKU named `PALLET-A` at warehouse location `B2`.
    * `addskutask n/PALLET-A d/2026-06-15 p/HIGH t/Check stock` : Adds a high-priority task to `PALLET-A` due on 15 June 2026.
    * `marktask n/PALLET-A i/1` : Marks the 1st task of `PALLET-A` as completed.
    * `deletesku n/PALLET-A` : Deletes `PALLET-A` and all its associated tasks.
    * `viewmap` : Displays the 3×3 warehouse grid with SKU locations.
    * `bye` : Exits the application.

6. Refer to the Features section below for the full details of each command.

## Features

### Adding a SKU: `addsku`
Adds a new SKU to the warehouse at the specified grid location.

Format: `addsku n/SKU_ID l/LOCATION`

* `SKU_ID` is case-insensitive and will be stored in uppercase.
* `LOCATION` must be a valid warehouse grid cell: `A1`, `A2`, `A3`, `B1`, `B2`, `B3`, `C1`, `C2`, or `C3`.
* Duplicate SKU IDs are not allowed.

Example of usage:

`addsku n/PALLET-A l/B2`

`addsku n/WIDGET-X1 l/A3`

---

### Editing a SKU: `editsku`
Updates the warehouse location of an existing SKU.

Format: `editsku n/SKU_ID l/NEW_LOCATION`

* `SKU_ID` must already exist in the warehouse.
* `NEW_LOCATION` must be a valid warehouse grid cell.

Example of usage:

`editsku n/PALLET-A l/C3`

`editsku n/WIDGET-X1 l/A1`

---

### Deleting a SKU: `deletesku`
Deletes an existing SKU and all its associated tasks from the warehouse.

Format: `deletesku n/SKU_ID`

* This action is irreversible — all tasks tied to the SKU will also be removed.

Example of usage:

`deletesku n/PALLET-A`

---

### Adding a task: `addskutask`
Adds a new task to an existing SKU.

Format: `addskutask n/SKU_ID d/DUE_DATE [p/PRIORITY] [t/DESCRIPTION]`

* `SKU_ID` must already exist. Use `addsku` to register it first.
* `DUE_DATE` must be in `YYYY-MM-DD` format. e.g. `2026-06-15`.
* `PRIORITY` is optional and must be `HIGH`, `MEDIUM`, or `LOW`. Defaults to `MEDIUM` if not provided.
* `DESCRIPTION` is optional. Leave it out to create a task with no description.

Example of usage:

`addskutask n/PALLET-A d/2026-06-15 p/HIGH t/Check stock levels`

`addskutask n/WIDGET-X1 d/2026-12-01`

---

### Editing a task: `edittask`
Updates one or more fields of an existing task.

Format: `edittask n/SKU_ID i/TASK_INDEX [d/DUE_DATE] [p/PRIORITY] [t/DESCRIPTION]`

* At least one of `d/`, `p/`, or `t/` must be provided.
* `TASK_INDEX` refers to the task's position in the SKU's task list (starting from 1).
* `DUE_DATE` must be in `YYYY-MM-DD` format if provided.
* `PRIORITY` must be `HIGH`, `MEDIUM`, or `LOW` if provided.

Example of usage:

`edittask n/PALLET-A i/1 d/2026-12-31 p/LOW`

`edittask n/WIDGET-X1 i/2 t/Updated description`

---

### Deleting a task: `deletetask`
Deletes a specific task from a SKU's task list.

Format: `deletetask n/SKU_ID i/TASK_INDEX`

* `TASK_INDEX` refers to the task's position in the SKU's task list (starting from 1).
* This action is irreversible.

Example of usage:

`deletetask n/PALLET-A i/2`

---

### Marking a task: `marktask`
Marks a specific task as completed.

Format: `marktask n/SKU_ID i/TASK_INDEX`

* `TASK_INDEX` refers to the task's position in the SKU's task list (starting from 1).
* If the task is already marked as done, the application will notify you.

Example of usage:

`marktask n/PALLET-A i/1`

---

### Unmarking a task: `unmarktask`
Marks a previously completed task as not done.

Format: `unmarktask n/SKU_ID i/TASK_INDEX`

* `TASK_INDEX` refers to the task's position in the SKU's task list (starting from 1).
* If the task is already unmarked, the application will notify you.

Example of usage:

`unmarktask n/PALLET-A i/1`

---

### Sorting tasks: `sorttasks`
Sorts a SKU's task list by a specified field.

Format: `sorttasks n/SKU_ID s/SORT_FIELD [o/ORDER]`

* `SORT_FIELD` must be one of: `date`, `priority`, or `status`.
* `ORDER` is optional and must be `ascending` or `descending`. Defaults to `ascending` if not provided.

Example of usage:

`sorttasks n/PALLET-A s/priority o/descending`

`sorttasks n/WIDGET-X1 s/date`

---

### Listing tasks: `listtasks`
Lists tasks across the warehouse with optional filters.

Format: `listtasks [n/SKU_ID] [p/PRIORITY] [l/LOCATION]`

* Only one filter may be used at a time.
* Using `n/` lists all tasks for a specific SKU.
* Using `p/` filters tasks by priority level (`HIGH`, `MEDIUM`, or `LOW`).
* Using `l/` lists all tasks sorted by distance from the specified grid location.
* Using no filter lists all tasks across all SKUs.

Example of usage:

`listtasks`

`listtasks n/PALLET-A`

`listtasks p/HIGH`

`listtasks l/B2`

---

### Finding tasks: `find`
Searches for tasks matching one or more criteria.

Format: `find [n/SKU_ID] [t/DESCRIPTION] [i/TASK_INDEX]`

* At least one filter must be provided.
* Filters can be combined. e.g. `find n/PALLET-A t/stock` returns tasks in `PALLET-A` whose description contains "stock".
* `DESCRIPTION` is a keyword match and is case-insensitive.
* `TASK_INDEX` searches for a task at that position across matching SKUs.

Example of usage:

`find n/PALLET-A t/stock`

`find t/check`

`find n/PALLET-A i/1`

---

### Viewing warehouse map: `viewmap`
Displays a visual representation of the 3×3 warehouse grid showing which locations are occupied.

Format: `viewmap`

Example of usage:

`viewmap`

---

### Checking status: `status`
Displays completion statistics for one or all SKUs.

Format: `status [n/SKU_ID]`

* Using no filter shows completion statistics for all SKUs in the warehouse.
* Using `n/` shows statistics for a specific SKU only.

Example of usage:

`status`

`status n/PALLET-A`

---

### Exporting inventory: `export`
Saves a human-readable snapshot of the full warehouse inventory to a text file.

Format: `export`

Example of usage:

`export`

---

### Viewing help: `help`
Displays a summary of all available commands.

Format: `help`

Example of usage:

`help`

---

### Exiting the application: `bye`
Exits ItemTasker. All data is automatically saved before the application closes.

Format: `bye`

Example of usage:

`bye`

## FAQ

**Q**: How do I transfer my data to another computer? 

**A**: {your answer here}

## command Summary

{Give a 'cheat sheet' of commands here}

* Add todo `todo n/TODO_NAME d/DEADLINE`
