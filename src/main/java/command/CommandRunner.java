package command;

import exception.ItemTaskerException;

import sku.SKUList;
import storage.Storage;
import ui.Ui;
import ui.ViewMap;

import java.io.IOException;

/**
 * Dispatches parsed commands to the appropriate handler and manages
 * the application's running state. Each command category is delegated
 * to a dedicated handler class (SRP).
 */
//@@author dorndorn54
public class CommandRunner {

    private boolean isRunning;

    private final SKUList skuList;
    private final SkuCommandHandler skuHandler;
    private final TaskCommandHandler taskHandler;
    private final ViewCommandHandler viewHandler;

    /**
     * Constructs a CommandRunner backed by the given SKU data store.
     *
     * @param skuList The shared SKU data store for the application.
     */
    public CommandRunner(SKUList skuList) {
        this.skuList = skuList;
        this.isRunning = true;
        this.skuHandler = new SkuCommandHandler(skuList);
        this.taskHandler = new TaskCommandHandler(skuList);
        this.viewHandler = new ViewCommandHandler(skuList);
        Storage.loadState(skuList);
    }

    /**
     * Returns the current running state of the application.
     *
     * @return True if the application should continue running, false otherwise.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Dispatches a parsed command to the appropriate handler based on its command word.
     *
     * @param cmd The parsed command object containing the command word and extracted arguments.
     * @throws ItemTaskerException If a domain-specific error occurs during execution.
     * @throws IOException         If an error occurs during state saving upon exit.
     */
    public void run(ParsedCommand cmd) throws ItemTaskerException, IOException {
        assert cmd != null : "ParsedCommand should not be null";

        switch (cmd.getCommandWord()) {
        case "addsku":
            skuHandler.handleAddSku(cmd);
            break;
        case "deletesku":
            skuHandler.handleDeleteSku(cmd);
            break;
        case "editsku":
            skuHandler.handleEditSku(cmd);
            break;
        case "addskutask":
            taskHandler.handleAddSkuTask(cmd);
            break;
        case "edittask":
            taskHandler.handleEditTask(cmd);
            break;
        case "deletetask":
            taskHandler.handleDeleteTask(cmd);
            break;
        case "marktask":
            taskHandler.handleMarkTask(cmd);
            break;
        case "unmarktask":
            taskHandler.handleUnmarkTask(cmd);
            break;
        case "sorttasks":
            taskHandler.handleSortTask(cmd);
            break;
        case "listtasks":
            try {
                viewHandler.handleListTasks(cmd);
            } catch (exception.MultipleFilterException | exception.InvalidFilterException e) {
                Ui.printError(e.getMessage());
            }
            break;
        case "find":
            viewHandler.handleFind(cmd);
            break;
        case "export":
            handleExport();
            break;
        case "help":
            Ui.printHelp();
            break;
        case "viewmap":
            new ViewMap().printTaskMap(this.skuList);
            break;
        case "bye":
        case "exit":
            try {
                Storage.saveState(this.skuList);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Ui.printGoodbye();
            isRunning = false;
            break;
        case "":
            break;
        default:
            Ui.printUnknownCommand(cmd.getCommandWord());
        }
    }

    /**
     * Initiates the export of the system's inventory to a readable text file.
     */
    private void handleExport() {
        try {
            storage.Export.exportToTextFile(this.skuList);
            Ui.printSuccess("Warehouse state successfully exported to Data/ItemTasker_Export.txt");
        } catch (IOException e) {
            Ui.printError("Failed to export data: " + e.getMessage());
        }
    }
}
