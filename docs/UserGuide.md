# User Guide

## Introduction
ItemTasker is a CLI-based Stock Keeping Unit (SKU) Ticketing System. A localized command-line tool designed to handle inventory specific actions required for individual item SKUs such as damage checks, expiry reviews & quality control. Unlike standard commercial inventory software that tracks quantity, ItemTracker tracks accountability & actions, allowing managers to attach specific tasks with priorities to individual SKUs.

## Quick Start

{Give steps to get started quickly}

1. Ensure that you have Java 17 or above installed.
1. Down the latest version of `Duke` from [here](http://link.to/duke).

## Features 

{Give detailed description of each feature}

### Adding a todo: `todo`
Adds a new item to the list of todo items.

Format: `todo n/TODO_NAME d/DEADLINE`

* The `DEADLINE` can be in a natural language format.
* The `TODO_NAME` cannot contain punctuation.  

Example of usage: 

`todo n/Write the rest of the User Guide d/next week`

`todo n/Refactor the User Guide to remove passive voice d/13/04/2020`

## FAQ

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

**Q**: Where can I find the application logs if I need to report a bug?  
**A**: ItemTasker automatically generates an `itemtasker.log` file in the same directory as the application containing diagnostic information. You can review or attach this file when reporting any unexpected behavior.

**Q**: Is there a limit to how many SKUs or tasks I can add?  
**A**: There is no hardcoded limit to the number of SKUs or tasks you can manage. However, performance might begin to slow down if you are managing tens of thousands of tasks simultaneously on older hardware.

## Command Summary

| Action | Format | Example |
|--------|--------|---------|
| Add SKU | `addsku n/SKU_ID l/LOCATION` | `addsku n/PALLET-A l/B2` |
| Edit SKU | `editsku n/SKU_ID l/NEW_LOCATION` | `editsku n/PALLET-A l/C3` |
| Delete SKU | `deletesku n/SKU_ID` | `deletesku n/PALLET-A` |
| Add task | `addskutask n/SKU_ID d/DUE_DATE [p/PRIORITY] [t/DESCRIPTION]` | `addskutask n/PALLET-A d/2026-06-15 p/HIGH t/Check stock` |
| Edit task | `edittask n/SKU_ID i/TASK_INDEX [d/DUE_DATE] [p/PRIORITY] [t/DESCRIPTION]` | `edittask n/PALLET-A i/1 d/2026-12-31 p/LOW` |
| Delete task | `deletetask n/SKU_ID i/TASK_INDEX` | `deletetask n/PALLET-A i/2` |
| Mark task | `marktask n/SKU_ID i/TASK_INDEX` | `marktask n/PALLET-A i/1` |
| Unmark task | `unmarktask n/SKU_ID i/TASK_INDEX` | `unmarktask n/PALLET-A i/1` |
| Sort tasks | `sorttasks n/SKU_ID s/SORT_FIELD [o/ORDER]` | `sorttasks n/PALLET-A s/priority o/descending` |
| List all tasks | `listtasks` | `listtasks` |
| List by SKU | `listtasks n/SKU_ID` | `listtasks n/PALLET-A` |
| List by priority | `listtasks p/PRIORITY` | `listtasks p/HIGH` |
| List by distance | `listtasks l/LOCATION` | `listtasks l/B2` |
| Find tasks | `find [n/SKU_ID] [t/DESCRIPTION] [i/TASK_INDEX]` | `find n/PALLET-A t/Check` |
| View warehouse map | `viewmap` | `viewmap` |
| Export inventory | `export` | `export` |
| Help | `help` | `help` |
| Exit | `bye` / `exit` | `bye` |

*Locations: A1 A2 A3 \| B1 B2 B3 \| C1 C2 C3 (3x3 warehouse grid). Priorities: HIGH, MEDIUM, LOW. Sort fields: date, priority, status.*
