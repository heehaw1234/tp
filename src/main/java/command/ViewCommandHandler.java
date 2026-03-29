package command;

import exception.EmptyListException;
import exception.InvalidCommandException;
import exception.InvalidIndexException;
import exception.MissingArgumentException;
import exception.SKUNotFoundException;

import sku.Location;
import sku.SKU;
import sku.SKUList;
import skutask.SKUTask;
import skutask.ViewSKUTask;
import ui.Ui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles all view and search commands: listing tasks with optional filters
 * and finding tasks by combinable search criteria.
 * Each complex operation is broken into single-purpose sub-methods (SLAP).
 */

//@@author dorndorn54
public class ViewCommandHandler {

    private static final Logger logger = Logger.getLogger(ViewCommandHandler.class.getName());
    private final SKUList skuList;

    public ViewCommandHandler(SKUList skuList) {
        this.skuList = skuList;
    }

    /**
     * Dispatches to the appropriate listing sub-method based on which filter is provided.
     *
     * @param cmd The parsed command containing optional filter arguments.
     * @throws InvalidCommandException If the command format is fundamentally invalid.
     * @throws EmptyListException If the system is queried but currently tracks no tasks.
     * @throws SKUNotFoundException If a specific SKU filter is applied but the SKU does not exist.
     */
    public void handleListTasks(ParsedCommand cmd) throws InvalidCommandException, EmptyListException,
            SKUNotFoundException {
        String skuFilter = cmd.getArg("n");
        String priorityFilter = cmd.getArg("p");
        String locationFilter = cmd.getArg("l");

        logger.log(Level.INFO, "Listing tasks. Filters -> SKU: {0}, Priority: {1}, Location: {2}",
                new Object[]{skuFilter, priorityFilter, locationFilter});
        if (skuFilter != null) {
            listTasksForSku(skuFilter);
        } else if (priorityFilter != null) {
            listTasksByPriority(priorityFilter);
        } else if (locationFilter != null) {
            listTasksByDistance(locationFilter);
        } else {
            logger.log(Level.FINE, "No filters provided, listing all tasks.");
            Ui.printAllTasks(this.skuList);
        }
    }

    /**
     * Lists all tasks belonging to a specific SKU.
     *
     * @param skuId The SKU identifier to filter by.
     */
    private void listTasksForSku(String skuId) {
        SKU targetSku = skuList.findByID(skuId);
        if (targetSku == null || targetSku.getSKUTaskList().isEmpty()) {
            logger.log(Level.WARNING, "Lookup failed: SKU {0} not found or empty.", skuId);
            Ui.printInfo("No tasks found for SKU: " + skuId.toUpperCase());
            return;
        }

        ViewSKUTask viewer = new ViewSKUTask();
        viewer.setSkuFilter(skuId);
        List<SKUTask> results = viewer.listTasks(this.skuList);
        Ui.printTasksForSku(skuId, results);
    }

    /**
     * Lists all tasks matching a given priority level.
     *
     * @param priorityStr The priority level string to filter by.
     */
    private void listTasksByPriority(String priorityStr) {
        if (CommandHelper.parsePriority(priorityStr) == null) {
            logger.log(Level.WARNING, "Invalid priority level provided: {0}", priorityStr);
            return;
        }

        ViewSKUTask viewer = new ViewSKUTask();
        viewer.setPriorityFilter(priorityStr);
        List<SKUTask> results = viewer.listTasks(this.skuList);
        Ui.printTasksByPriority(priorityStr, results);
    }

    /**
     * Lists all tasks sorted by distance from a given warehouse location.
     *
     * @param locationStr The reference location string to sort by distance from.
     */
    private void listTasksByDistance(String locationStr) {
        Location from = CommandHelper.parseLocation(locationStr);
        if (from == null) {
            logger.log(Level.WARNING, "Invalid location for distance filter: {0}", locationStr);
            return;
        }

        ViewSKUTask viewer = new ViewSKUTask();
        viewer.setLocationFilter(locationStr);
        List<SKUTask> results = viewer.listTasks(this.skuList);

        List<String> formattedEntries = formatDistanceEntries(results, locationStr);
        Ui.printTasksByDistance(from.name(), formattedEntries);
    }

    /**
     * Formats task entries with their distance from a reference location.
     *
     * @param tasks The sorted list of tasks.
     * @param locationStr The reference location string for distance calculation.
     * @return A list of pre-formatted distance entry strings.
     */
    private List<String> formatDistanceEntries(List<SKUTask> tasks, String locationStr) {
        ViewSKUTask viewer = new ViewSKUTask();
        List<String> entries = new ArrayList<>();
        for (SKUTask t : tasks) {
            SKU skuObj = skuList.findByID(t.getSKUTaskID());
            if (skuObj == null) {
                logger.log(Level.SEVERE, "Data Integrity Error: Task {0} has no parent SKU.", t.getSKUTaskID());
                continue;
            }
            int dist = viewer.calculateDistance(t, locationStr, this.skuList);
            entries.add(String.format("  [SKU: %-25s | dist=%-2d] %s", t.getSKUTaskID(), dist, t));
        }
        return entries;
    }

    // ========== find — validate, search, display (SLAP) ==========

    /**
     * Handles the 'find' command by validating inputs, searching tasks, and displaying results.
     * The search header is printed before searching so it appears even if an exception
     * interrupts the search (preserving original output behaviour).
     *
     * @param cmd The parsed command containing the filter flags.
     * @throws MissingArgumentException If no filter flags are provided.
     * @throws SKUNotFoundException If the specified SKU does not exist in the warehouse.
     * @throws InvalidIndexException If the task index is not a valid number or is out of range.
     */
    public void handleFind(ParsedCommand cmd) throws MissingArgumentException, SKUNotFoundException,
            InvalidIndexException {
        String skuFilter = cmd.getArg("n");
        String descFilter = cmd.getArg("t");
        String indexStr = cmd.getArg("i");

        validateFindArgs(skuFilter, descFilter, indexStr);

        int taskIndex = -1;
        if (indexStr != null) {
            taskIndex = CommandHelper.parseIndex(indexStr);
            if (taskIndex == -1) {
                return;
            }
        }

        Ui.printSearchHeader();
        List<String> results = searchTasks(skuFilter, descFilter, taskIndex);
        Ui.printSearchFooter(results);
    }

    /**
     * Validates that at least one filter is provided and that the SKU filter (if given) exists.
     *
     * @param skuFilter The SKU filter, or null.
     * @param descFilter The description filter, or null.
     * @param indexStr The index filter string, or null.
     * @throws MissingArgumentException If all filters are null.
     * @throws SKUNotFoundException If the specified SKU does not exist.
     */
    private void validateFindArgs(String skuFilter, String descFilter, String indexStr)
            throws MissingArgumentException, SKUNotFoundException {
        if (skuFilter == null && descFilter == null && indexStr == null) {
            throw new MissingArgumentException("Usage: find [n/SKU_ID] [t/DESCRIPTION] [i/TASK_INDEX]");
        }
        if (skuFilter != null && skuList.findByID(skuFilter) == null) {
            throw new SKUNotFoundException(skuFilter);
        }
    }

    /**
     * Searches all matching tasks across SKUs based on the provided filters.
     *
     * @param skuFilter The SKU ID filter, or null to search all SKUs.
     * @param descFilter The description keyword filter, or null to match all.
     * @param taskIndex The 1-based task index filter, or -1 to search all indices.
     * @return A list of pre-formatted result strings for matching tasks.
     * @throws InvalidIndexException If the index is out of range for a filtered SKU.
     */
    private List<String> searchTasks(String skuFilter, String descFilter, int taskIndex)
            throws InvalidIndexException {
        List<String> results = new ArrayList<>();
        for (SKU sku : skuList.getSKUList()) {
            if (skuFilter != null && !sku.getSKUID().equalsIgnoreCase(skuFilter)) {
                continue;
            }
            searchTasksInSku(sku, descFilter, taskIndex, skuFilter != null, results);
        }
        return results;
    }

    /**
     * Searches tasks within a single SKU and appends formatted results to the list.
     *
     * @param sku The SKU to search within.
     * @param descFilter The description keyword filter, or null to match all.
     * @param taskIndex The 1-based task index filter, or -1 to search all indices.
     * @param hasSkuFilter Whether the user specified a SKU filter (affects error behaviour).
     * @param results The accumulator list for formatted result strings.
     * @throws InvalidIndexException If the index is out of range and a SKU filter was specified.
     */
    private void searchTasksInSku(SKU sku, String descFilter, int taskIndex,
                                  boolean hasSkuFilter, List<String> results) throws InvalidIndexException {
        ArrayList<SKUTask> tasks = sku.getSKUTaskList().getSKUTaskList();

        if (taskIndex > 0) {
            searchByIndex(sku, tasks, descFilter, taskIndex, hasSkuFilter, results);
        } else {
            searchAllTasks(sku, tasks, descFilter, results);
        }
    }

    /**
     * Searches for a task at a specific index within a SKU.
     *
     * @param sku The SKU being searched.
     * @param tasks The task list of the SKU.
     * @param descFilter The description keyword filter, or null to match all.
     * @param taskIndex The 1-based task index to look up.
     * @param hasSkuFilter Whether the user specified a SKU filter.
     * @param results The accumulator list for formatted result strings.
     * @throws InvalidIndexException If the index is out of range and a SKU filter was specified.
     */
    private void searchByIndex(SKU sku, ArrayList<SKUTask> tasks, String descFilter,
                               int taskIndex, boolean hasSkuFilter, List<String> results)
            throws InvalidIndexException {
        if (taskIndex > tasks.size()) {
            if (hasSkuFilter) {
                throw new InvalidIndexException(taskIndex, sku.getSKUID());
            }
            return;
        }
        SKUTask task = tasks.get(taskIndex - 1);
        if (CommandHelper.matchesDescription(task, descFilter)) {
            results.add(formatSearchResult(sku.getSKUID(), taskIndex, task));
        }
    }

    /**
     * Searches all tasks within a SKU for description matches.
     *
     * @param sku The SKU being searched.
     * @param tasks The task list of the SKU.
     * @param descFilter The description keyword filter, or null to match all.
     * @param results The accumulator list for formatted result strings.
     */
    private void searchAllTasks(SKU sku, ArrayList<SKUTask> tasks, String descFilter, List<String> results) {
        for (int i = 0; i < tasks.size(); i++) {
            SKUTask task = tasks.get(i);
            if (CommandHelper.matchesDescription(task, descFilter)) {
                results.add(formatSearchResult(sku.getSKUID(), i + 1, task));
            }
        }
    }

    /**
     * Formats a single search result entry for display.
     *
     * @param skuId The SKU identifier.
     * @param index The 1-based task index.
     * @param task The matching task.
     * @return A formatted result string.
     */
    private String formatSearchResult(String skuId, int index, SKUTask task) {
        return "  [SKU: " + skuId.toUpperCase() + "] #" + index + ". " + task;
    }
}
