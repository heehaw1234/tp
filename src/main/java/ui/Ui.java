package ui;

import sku.SKU;
import sku.SKUList;
import skutask.SKUTask;

import java.util.List;
import java.util.Scanner;

/**
 * Handles all console input and output for ItemTasker.
 * Centralises printing so that formatting changes only need to be made here.
 */
public class Ui {

    private static final String DIVIDER = "__________________________________________________________________________";
    private static final String LOGO = "  ___  _                   _____            _                 \n"
            + " |_ _|| |_  ___  _ __ ___  |_   _|__ _  ___ | | __ ___  _ __  \n"
            + "  | | | __|/ _ \\| '_ ` _ \\   | | / _` |/ __|| |/ // _ \\| '__| \n"
            + "  | | | |_|  __/| | | | | |  | || (_| |\\__ \\|   <|  __/| |    \n"
            + " |___| \\__|\\___||_| |_| |_|  |_| \\__,_||___/|_|\\_\\\\___||_|    \n";

    private final Scanner scanner;

    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reads and returns the next line of user input, trimmed.
     *
     * @return User input string.
     */
    public String readInput() {
        System.out.print("> ");
        return scanner.nextLine().trim();
    }

    /** Prints the welcome banner on application startup. */
    public static void printWelcome() {
        System.out.println(DIVIDER);
        System.out.println(LOGO);
        System.out.println(" ItemTasker - CLI SKU Ticketing System");
        System.out.println(" Type a command to get started. Type 'help' for a list of commands.");
        System.out.println(DIVIDER);
    }

    /** Prints the goodbye message on application exit. */
    public static void printGoodbye() {
        System.out.println(DIVIDER);
        System.out.println(" Goodbye! All tasks have been saved.");
        System.out.println(DIVIDER);
    }

    /**
     * Prints a success message prefixed with [OK].
     *
     * @param message Success detail message.
     */
    public static void printSuccess(String message) {
        System.out.println(" [OK] " + message);
    }

    /**
     * Prints an error message prefixed with [ERROR].
     *
     * @param message Error detail message.
     */
    public static void printError(String message) {
        System.out.println(" [ERROR] " + message);
    }

    /**
     * Prints an informational message prefixed with [INFO].
     *
     * @param message Informational detail message.
     */
    public static void printInfo(String message) {
        System.out.println(" [INFO] " + message);
    }

    /**
     * Prints a message when the command entered is not recognised.
     *
     * @param command The unrecognised command word.
     */
    public static void printUnknownCommand(String command) {
        System.out.println(" [ERROR] Unknown command: '" + command + "'. Type 'help' for valid commands.");
    }

    /** Prints the help reference listing all valid commands and their formats. */
    public static void printHelp() {
        System.out.println(DIVIDER);
        System.out.println(" ITEMTASKER - COMMAND REFERENCE");
        System.out.println(DIVIDER);
        System.out.println(" SKU MANAGEMENT");
        System.out.println("   addsku n/SKU_ID l/LOCATION          Add a SKU to the warehouse.");
        System.out.println("   editsku n/SKU_ID l/NEW_LOCATION     Move a SKU to a new warehouse location.");
        System.out.println("   deletesku n/SKU_ID                  Remove a SKU and all its tasks.");
        System.out.println();
        System.out.println(" TASK MANAGEMENT");
        System.out.println("   addskutask n/SKU_ID d/DUE_DATE      Add a HIGH priority task (default).");
        System.out.println("         [p/PRIORITY] [t/DESC]         Optional: priority and description.");
        System.out.println("   edittask n/SKU_ID i/TASK_INDEX      Edit a task's due date, priority, or description.");
        System.out.println("    [d/DUE_DATE] [p/PRIORITY] [t/DESC] Note: At least one field required.");
        System.out.println("   deletetask n/SKU_ID i/TASK_INDEX    Delete task at given index.");
        System.out.println("   marktask n/SKU_ID i/TASK_INDEX      Mark a task as completed.");
        System.out.println("   unmarktask n/SKU_ID i/TASK_INDEX    Unmark a completed task.");
        System.out.println();
        System.out.println(" VIEWING");
        System.out.println("   listtasks                           List all tasks.");
        System.out.println("   listtasks n/SKU_ID                  List tasks for a specific SKU.");
        System.out.println("   listtasks p/PRIORITY                List tasks filtered by priority.");
        System.out.println("   listtasks l/LOCATION                List tasks sorted by distance.");
        System.out.println("   find [n/SKU_ID] [t/DESC] [i/INDEX]  Search tasks by SKU, description, index.");
        System.out.println("   viewmap                             Show warehouse map.");
        System.out.println();
        System.out.println(" OTHER");
        System.out.println("   export                             Export inventory to a readable text file.");
        System.out.println("   help                               Show this help message.");
        System.out.println("   bye / exit                         Exit ItemTasker.");
        System.out.println(DIVIDER);
        System.out.println(" Locations: A1 A2 A3 | B1 B2 B3 | C1 C2 C3  (3x3 warehouse grid)");
        System.out.println(DIVIDER);
    }

    /** Prints the horizontal divider line. */
    public static void printDivider() {
        System.out.println(DIVIDER);
    }

    /**
     * Prints a numbered list of tasks belonging to a specific SKU.
     *
     * @param skuId The SKU identifier for the header.
     * @param tasks The list of tasks to display.
     */
    public static void printTasksForSku(String skuId, List<SKUTask> tasks) {
        System.out.println(" Tasks for SKU [" + skuId.toUpperCase() + "]:");
        printDivider();
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
        }
        printDivider();
    }

    /**
     * Prints tasks filtered by a specific priority level.
     *
     * @param priority The priority level used as a filter.
     * @param tasks    The filtered list of tasks to display.
     */
    public static void printTasksByPriority(String priority, List<SKUTask> tasks) {
        System.out.println(" Tasks with priority [" + priority.toUpperCase() + "]:");
        printDivider();
        if (tasks.isEmpty()) {
            printInfo("No tasks found with priority: " + priority.toUpperCase());
        } else {
            for (SKUTask t : tasks) {
                System.out.println("  [SKU: " + t.getSKUTaskID() + "] " + t);
            }
        }
        printDivider();
    }

    /**
     * Prints tasks sorted by distance from a given location.
     *
     * @param fromLocation     The reference location name.
     * @param formattedEntries Pre-formatted distance entry strings.
     */
    public static void printTasksByDistance(String fromLocation, List<String> formattedEntries) {
        System.out.println(" Tasks sorted by distance from [" + fromLocation + "]:");
        printDivider();
        if (formattedEntries.isEmpty()) {
            printInfo("No tasks found.");
        } else {
            for (String entry : formattedEntries) {
                System.out.println(entry);
            }
        }
        printDivider();
    }

    /**
     * Prints all tasks grouped by their parent SKU.
     *
     * @param skuList The master list of all SKUs in the system.
     */
    public static void printAllTasks(SKUList skuList) {
        System.out.println(" All tasks:");
        printDivider();
        if (skuList.isEmpty()) {
            printInfo("No SKUs registered yet.");
        }
        boolean anyTasks = false;
        for (SKU sku : skuList.getSKUList()) {
            System.out.println(" SKU [" + sku.getSKUID().toUpperCase() + "]:");
            if (sku.getSKUTaskList().isEmpty()) {
                System.out.println("   No tasks for this SKU.");
            } else {
                sku.getSKUTaskList().printSKUTaskList();
                anyTasks = true;
            }
        }
        if (!skuList.isEmpty() && !anyTasks) {
            printInfo("No tasks in the system yet.");
        }
        printDivider();
    }

    /**
     * Prints the search results header before the search executes,
     * so it appears even if an exception interrupts the search.
     */
    public static void printSearchHeader() {
        System.out.println(" Search results:");
        printDivider();
    }

    /**
     * Prints the search result entries and closing divider.
     *
     * @param results Pre-formatted result strings. Empty list shows "no match" message.
     */
    public static void printSearchFooter(List<String> results) {
        if (results.isEmpty()) {
            printInfo("No matching tasks found.");
        } else {
            for (String result : results) {
                System.out.println(result);
            }
        }
        printDivider();
    }
}

