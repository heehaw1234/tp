package ui;

import sku.Location;
import sku.SKU;
import sku.SKUList;

import skutask.Priority;
import skutask.SKUTask;
import skutask.SKUTaskList;
import skutask.ViewSKUTask;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
