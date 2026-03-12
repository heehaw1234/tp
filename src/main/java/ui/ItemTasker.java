package ui;

import SKU.SKUList;

public class ItemTasker {
    /**
     * Main entry-point for the java.duke.ItemTasker application.
     */
    public static void main(String[] args) {
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
