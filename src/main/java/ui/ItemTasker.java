package ui;

import command.CommandRunner;
import command.ParsedCommand;
import exception.ItemTaskerException;
import sku.SKUList;

import java.io.IOException;

/**
 * Main entry-point for the ItemTasker application.
 */

//@@author omcodedthis
public class ItemTasker {

    /**
     * Executes the main application loop and serves as the entry point for ItemTasker.
     * @param args Command line arguments.
     * @throws ItemTaskerException If a top-level error occurs during execution.
     * @throws IOException If an error occurs during file loading or saving.
     */
    public static void main(String[] args) throws ItemTaskerException, IOException {
        ItemTaskerLogger.setup();
        
        SKUList skuList = new SKUList();
        Ui ui = new Ui();
        CommandRunner runner = new CommandRunner(skuList);

        Ui.printWelcome();

        while (runner.isRunning()) {
            String input = ui.readInput();
            try {
                ParsedCommand cmd = Parser.parse(input);
                runner.run(cmd);
            } catch (ItemTaskerException e) {
                Ui.printError(e.getMessage());
            }
        }
    }
}
