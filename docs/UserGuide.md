# User Guide

## Introduction

{Give a product intro}

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

**Q**: How do I transfer my data to another computer? 

**A**: {your answer here}

## command Summary

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
