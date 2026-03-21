package ui;

import exception.ItemTaskerException;
import sku.SKUList;

import java.io.IOException;

/**
 * Main entry-point for the java.duke.ItemTasker application.
 */
public class ItemTasker {

    /**
     * The main execution method for the application.
     * @param args Command line arguments.
     * @throws ItemTaskerException If a top-level error occurs during execution.
     */
    public static void main(String[] args) throws ItemTaskerException, IOException {
        SKUList skuList = new SKUList();
        Ui ui = new Ui();
        CommandRunner runner = new CommandRunner(skuList);

        Ui.printWelcome();

        while (runner.isRunning()) {
            String input = ui.readInput();
            ParsedCommand cmd = Parser.parse(input);
            runner.run(cmd);
        }
    }
}
