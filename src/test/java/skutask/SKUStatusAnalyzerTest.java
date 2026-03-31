package skutask;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sku.Location;
import sku.SKU;
import sku.SKUList;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@@author SeanTLY23
public class SKUStatusAnalyzerTest {

    private SKUStatusAnalyzer analyzer;
    private SKUList skuList;

    @BeforeEach
    public void setUp() {
        analyzer = new SKUStatusAnalyzer();
        skuList = new SKUList();
    }

    @Test
    public void analyze_emptyTaskList_returnsAllZeros() {
        skuList.addSKU("EMPTY-SKU", Location.A1);
        SKU sku = skuList.getSKUList().get(0);

        SKUStatusAnalyzer.StatusResult result = analyzer.analyze(sku);

        assertEquals(0, result.getTotalTasks());
        assertEquals(0, result.getCompletedTasks());
        assertEquals(0, result.getPendingTasks());
        assertEquals(0, result.getCompletionPercent());
        assertEquals(0, result.getPendingHighPriority());
        assertEquals(0, result.getOverdueCount());
    }

    @Test
    public void analyze_allTasksDone_returnsFullCompletion() {
        skuList.addSKU("SKU-DONE", Location.A1);
        SKU sku = skuList.getSKUList().get(0);
        sku.getSKUTaskList().addSKUTask("SKU-DONE", Priority.HIGH, "2099-12-31", "task1");
        sku.getSKUTaskList().addSKUTask("SKU-DONE", Priority.LOW, "2099-12-31", "task2");
        sku.getSKUTaskList().getSKUTaskList().get(0).mark();
        sku.getSKUTaskList().getSKUTaskList().get(1).mark();

        SKUStatusAnalyzer.StatusResult result = analyzer.analyze(sku);

        assertEquals(2, result.getTotalTasks());
        assertEquals(2, result.getCompletedTasks());
        assertEquals(0, result.getPendingTasks());
        assertEquals(100, result.getCompletionPercent());
        assertEquals(0, result.getPendingHighPriority());
    }

    @Test
    public void analyze_mixedTasks_returnsCorrectCounts() {
        skuList.addSKU("SKU-MIX", Location.B2);
        SKU sku = skuList.getSKUList().get(0);
        sku.getSKUTaskList().addSKUTask("SKU-MIX", Priority.HIGH, "2099-12-31", "high pending");
        sku.getSKUTaskList().addSKUTask("SKU-MIX", Priority.MEDIUM, "2099-12-31", "med pending");
        sku.getSKUTaskList().addSKUTask("SKU-MIX", Priority.HIGH, "2099-12-31", "high done");
        sku.getSKUTaskList().getSKUTaskList().get(2).mark();

        SKUStatusAnalyzer.StatusResult result = analyzer.analyze(sku);

        assertEquals(3, result.getTotalTasks());
        assertEquals(1, result.getCompletedTasks());
        assertEquals(2, result.getPendingTasks());
        assertEquals(33, result.getCompletionPercent());
        assertEquals(1, result.getPendingHighPriority());
    }

    @Test
    public void analyze_overdueTask_countedCorrectly() {
        skuList.addSKU("SKU-OD", Location.C1);
        SKU sku = skuList.getSKUList().get(0);
        sku.getSKUTaskList().addSKUTask("SKU-OD", Priority.HIGH, "2020-01-01", "overdue");
        sku.getSKUTaskList().addSKUTask("SKU-OD", Priority.LOW, "2099-12-31", "not overdue");

        SKUStatusAnalyzer.StatusResult result = analyzer.analyze(sku);

        assertEquals(2, result.getTotalTasks());
        assertEquals(1, result.getOverdueCount());
    }

    @Test
    public void analyze_doneTaskWithPastDate_notCountedAsOverdue() {
        skuList.addSKU("SKU-DONE-OD", Location.A2);
        SKU sku = skuList.getSKUList().get(0);
        sku.getSKUTaskList().addSKUTask("SKU-DONE-OD", Priority.HIGH, "2020-01-01", "done old");
        sku.getSKUTaskList().getSKUTaskList().get(0).mark();

        SKUStatusAnalyzer.StatusResult result = analyzer.analyze(sku);

        assertEquals(0, result.getOverdueCount());
    }

    @Test
    public void analyze_doneHighPriority_notCountedAsPendingHigh() {
        skuList.addSKU("SKU-DH", Location.B1);
        SKU sku = skuList.getSKUList().get(0);
        sku.getSKUTaskList().addSKUTask("SKU-DH", Priority.HIGH, "2099-12-31", "done high");
        sku.getSKUTaskList().getSKUTaskList().get(0).mark();

        SKUStatusAnalyzer.StatusResult result = analyzer.analyze(sku);

        assertEquals(0, result.getPendingHighPriority());
    }

    @Test
    public void analyze_skuIdPreservedInResult() {
        skuList.addSKU("MY-SKU", Location.A1);
        SKU sku = skuList.getSKUList().get(0);

        SKUStatusAnalyzer.StatusResult result = analyzer.analyze(sku);

        assertEquals("MY-SKU", result.getSkuId());
    }

    @Test
    public void analyzeAll_multipleSKUs_returnsAllResults() {
        skuList.addSKU("SKU-1", Location.A1);
        skuList.addSKU("SKU-2", Location.B2);
        skuList.addSKU("SKU-3", Location.C3);

        List<SKUStatusAnalyzer.StatusResult> results = analyzer.analyzeAll(skuList);

        assertEquals(3, results.size());
        assertEquals("SKU-1", results.get(0).getSkuId());
        assertEquals("SKU-2", results.get(1).getSkuId());
        assertEquals("SKU-3", results.get(2).getSkuId());
    }

    @Test
    public void analyzeAll_emptyWarehouse_returnsEmptyList() {
        List<SKUStatusAnalyzer.StatusResult> results = analyzer.analyzeAll(skuList);

        assertTrue(results.isEmpty());
    }

    @Test
    public void analyze_onlyLowAndMediumPending_pendingHighIsZero() {
        skuList.addSKU("SKU-LM", Location.A3);
        SKU sku = skuList.getSKUList().get(0);
        sku.getSKUTaskList().addSKUTask("SKU-LM", Priority.LOW, "2099-12-31", "low");
        sku.getSKUTaskList().addSKUTask("SKU-LM", Priority.MEDIUM, "2099-12-31", "med");

        SKUStatusAnalyzer.StatusResult result = analyzer.analyze(sku);

        assertEquals(0, result.getPendingHighPriority());
        assertEquals(2, result.getPendingTasks());
    }

    @Test
    public void analyze_singleTaskDone_fiftyPercentWhenTwoTasks() {
        skuList.addSKU("SKU-HALF", Location.B3);
        SKU sku = skuList.getSKUList().get(0);
        sku.getSKUTaskList().addSKUTask("SKU-HALF", Priority.HIGH, "2099-12-31", "done");
        sku.getSKUTaskList().addSKUTask("SKU-HALF", Priority.HIGH, "2099-12-31", "pending");
        sku.getSKUTaskList().getSKUTaskList().get(0).mark();

        SKUStatusAnalyzer.StatusResult result = analyzer.analyze(sku);

        assertEquals(50, result.getCompletionPercent());
    }

    @Test
    public void analyze_allOverdue_allCounted() {
        skuList.addSKU("SKU-ALL-OD", Location.C2);
        SKU sku = skuList.getSKUList().get(0);
        sku.getSKUTaskList().addSKUTask("SKU-ALL-OD", Priority.HIGH, "2020-01-01", "od1");
        sku.getSKUTaskList().addSKUTask("SKU-ALL-OD", Priority.LOW, "2020-06-15", "od2");
        sku.getSKUTaskList().addSKUTask("SKU-ALL-OD", Priority.MEDIUM, "2020-12-31", "od3");

        SKUStatusAnalyzer.StatusResult result = analyzer.analyze(sku);

        assertEquals(3, result.getOverdueCount());
    }
}

