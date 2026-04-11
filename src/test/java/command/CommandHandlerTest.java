package command;

import exception.InvalidFilterException;
import exception.InvalidIndexException;
import exception.MissingArgumentException;
import exception.MultipleFilterException;
import exception.SKUNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sku.Location;
import sku.SKU;
import sku.SKUList;
import skutask.Priority;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

//@@author omcodedthis
class CommandHandlerTest {

    private SKUList skuList;
    private SKUCommandHandler skuHandler;
    private TaskCommandHandler taskHandler;
    private ViewCommandHandler viewHandler;

    @BeforeEach
    public void setUp() {
        skuList = new SKUList();
        skuHandler = new SKUCommandHandler(skuList);
        taskHandler = new TaskCommandHandler(skuList);
        viewHandler = new ViewCommandHandler(skuList);
    }

    @Test
    public void handleAddSku_validCommand_addsToSkuList() throws Exception {
        ParsedCommand cmd = new ParsedCommand("addsku", Map.of("n", "WIDGET-1",
                "l", "A1"));
        skuHandler.handleAddSku(cmd);

        assertNotNull(skuList.findByID("WIDGET-1"));
        assertEquals(Location.A1, skuList.findByID("WIDGET-1").getSKULocation());
    }

    @Test
    public void handleAddSku_missingArgs_throwsMissingArgumentException() {
        ParsedCommand cmd = new ParsedCommand("addsku", Map.of("n", "WIDGET-1"));
        assertThrows(MissingArgumentException.class, () -> skuHandler.handleAddSku(cmd));
    }

    @Test
    public void handleEditSku_validMove_updatesLocation() throws Exception {
        skuList.addSKU("MOVE-ME", Location.A1);
        ParsedCommand cmd = new ParsedCommand("editsku", Map.of("n", "MOVE-ME",
                "l", "C3"));

        skuHandler.handleEditSku(cmd);
        assertEquals(Location.C3, skuList.findByID("MOVE-ME").getSKULocation());
    }

    @Test
    public void handleDeleteSku_existingSku_removesFromList() throws Exception {
        skuList.addSKU("DELETE-ME", Location.B2);
        ParsedCommand cmd = new ParsedCommand("deletesku", Map.of("n", "DELETE-ME"));

        skuHandler.handleDeleteSku(cmd);
        assertEquals(0, skuList.getSize());
    }

    @Test
    public void handleDeleteSku_nonExistentSku_throwsSKUNotFoundException() {
        ParsedCommand cmd = new ParsedCommand("deletesku", Map.of("n", "GHOST-SKU"));
        assertThrows(SKUNotFoundException.class, () -> skuHandler.handleDeleteSku(cmd));
    }

    @Test
    public void handleAddSkuTask_validCommand_addsTaskToSku() throws Exception {
        skuList.addSKU("TASK-SKU", Location.C1);
        ParsedCommand cmd = new ParsedCommand("addskutask",
                Map.of("n", "TASK-SKU", "d", "2026-12-12", "p", "high", "t",
                        "Urgent Task"));

        taskHandler.handleAddSkuTask(cmd);

        SKU sku = skuList.findByID("TASK-SKU");
        assertEquals(1, sku.getSKUTaskList().getSize());
        assertEquals(Priority.HIGH, sku.getSKUTaskList().getSKUTaskList().get(0).getSKUTaskPriority());
    }

    @Test
    public void handleAddSkuTask_invalidDate_abortsTaskAddition() throws Exception {
        skuList.addSKU("DATE-TEST", Location.C1);
        ParsedCommand cmd = new ParsedCommand("addskutask", Map.of("n", "DATE-TEST",
                "d", "2026-15-40"));

        assertDoesNotThrow(() -> taskHandler.handleAddSkuTask(cmd));
        assertEquals(0, skuList.findByID("DATE-TEST").getSKUTaskList().getSize());
    }

    @Test
    public void handleEditTask_validPropertyUpdate_changesTaskDetails() throws Exception {
        skuList.addSKU("EDIT-TASK", Location.A1);
        SKU sku = skuList.findByID("EDIT-TASK");
        sku.getSKUTaskList().addSKUTask("EDIT-TASK", Priority.LOW, "2026-01-01", "Old Desc");

        ParsedCommand cmd = new ParsedCommand("edittask",
                Map.of("n", "EDIT-TASK", "i", "1", "p", "high", "t", "New Desc"));

        taskHandler.handleEditTask(cmd);

        assertEquals(Priority.HIGH, sku.getSKUTaskList().getSKUTaskList().get(0).getSKUTaskPriority());
        assertEquals("New Desc", sku.getSKUTaskList().getSKUTaskList().get(0).getSKUTaskDescription());
        assertEquals("2026-01-01", sku.getSKUTaskList().getSKUTaskList().get(0).getSKUTaskDueDate());
    }

    @Test
    public void handleDeleteTask_outOfBounds_throwsInvalidIndexException() throws Exception {
        skuList.addSKU("BOUND-TEST", Location.A1);
        skuList.findByID("BOUND-TEST").getSKUTaskList().addSKUTask("BOUND-TEST", Priority.LOW,
                "2026-01-01", "T");

        ParsedCommand cmdHigh = new ParsedCommand("deletetask", Map.of("n", "BOUND-TEST",
                "i", "2"));
        assertThrows(InvalidIndexException.class, () -> taskHandler.handleDeleteTask(cmdHigh));
    }

    @Test
    public void handleMarkTask_validIndex_updatesStatus() throws Exception {
        skuList.addSKU("MARK-TEST", Location.B1);
        skuList.findByID("MARK-TEST").getSKUTaskList().addSKUTask("MARK-TEST", Priority.MEDIUM,
                "2026-05-05", "Test");

        ParsedCommand cmd = new ParsedCommand("marktask", Map.of("n", "MARK-TEST",
                "i", "1"));
        taskHandler.handleMarkTask(cmd);

        assertTrue(skuList.findByID("MARK-TEST").getSKUTaskList().getSKUTaskList().get(0).isDone());
    }

    @Test
    public void handleUnmarkTask_validIndex_updatesStatus() throws Exception {
        skuList.addSKU("UNMARK-TEST", Location.B1);
        skuList.findByID("UNMARK-TEST").getSKUTaskList().addSKUTask("UNMARK-TEST", Priority.MEDIUM,
                "2026-05-05", "Test");

        skuList.findByID("UNMARK-TEST").getSKUTaskList().markTask(1);

        ParsedCommand cmd = new ParsedCommand("unmarktask", Map.of("n", "UNMARK-TEST",
                "i", "1"));
        taskHandler.handleUnmarkTask(cmd);

        assertFalse(skuList.findByID("UNMARK-TEST").getSKUTaskList().getSKUTaskList().get(0).isDone());
    }

    @Test
    public void handleSortTask_byPriority_executesWithoutCrashing() throws Exception {
        skuList.addSKU("SORT-SKU", Location.A1);
        skuList.findByID("SORT-SKU").getSKUTaskList().addSKUTask("SORT-SKU", Priority.LOW,
                "2026-01-01", "Low Prio");

        ParsedCommand cmd = new ParsedCommand("sorttasks", Map.of("n", "SORT-SKU",
                "s", "priority", "o", "desc"));
        assertDoesNotThrow(() -> taskHandler.handleSortTask(cmd));
    }

    @Test
    public void handleListTasks_noFilters_executesWithoutError() {
        ParsedCommand cmd = new ParsedCommand("listtasks", Map.of());
        assertDoesNotThrow(() -> viewHandler.handleListTasks(cmd));
    }

    @Test
    public void handleListTasks_multipleFilters_throwsMultipleFilterException() {
        ParsedCommand cmd = new ParsedCommand("listtasks", Map.of("n", "WIDGET", "p", "HIGH"));
        assertThrows(MultipleFilterException.class, () -> viewHandler.handleListTasks(cmd));
    }

    @Test
    public void handleListTasks_invalidFilter_throwsInvalidFilterException() {
        ParsedCommand cmd = new ParsedCommand("listtasks", Map.of("z", "UNKNOWN"));
        assertThrows(InvalidFilterException.class, () -> viewHandler.handleListTasks(cmd));
    }

    @Test
    public void handleFind_validSearch_executesWithoutError() {
        skuList.addSKU("FIND-SKU", Location.A1);
        skuList.findByID("FIND-SKU").getSKUTaskList().addSKUTask("FIND-SKU", Priority.HIGH, "2026-01-01", "search me");

        ParsedCommand cmd = new ParsedCommand("find", Map.of("t", "search"));
        assertDoesNotThrow(() -> viewHandler.handleFind(cmd));
    }

    @Test
    public void handleFind_missingAllArgs_throwsMissingArgumentException() {
        ParsedCommand cmd = new ParsedCommand("find", Map.of());
        assertThrows(MissingArgumentException.class, () -> viewHandler.handleFind(cmd));
    }

    @Test
    public void handleDeleteTask_negativeIndex_throwsInvalidIndexException() throws Exception {
        skuList.addSKU("NEG-TEST", Location.A1);
        skuList.findByID("NEG-TEST").getSKUTaskList().addSKUTask("NEG-TEST", Priority.LOW,
                "2026-01-01", "T");

        ParsedCommand cmd = new ParsedCommand("deletetask", Map.of("n", "NEG-TEST",
                "i", "-1"));
        assertThrows(InvalidIndexException.class, () -> taskHandler.handleDeleteTask(cmd));
    }

    @Test
    public void handleDeleteTask_zeroIndex_throwsInvalidIndexException() throws Exception {
        skuList.addSKU("ZERO-TEST", Location.A1);
        skuList.findByID("ZERO-TEST").getSKUTaskList().addSKUTask("ZERO-TEST", Priority.LOW,
                "2026-01-01", "T");

        ParsedCommand cmd = new ParsedCommand("deletetask", Map.of("n", "ZERO-TEST",
                "i", "0"));
        assertThrows(InvalidIndexException.class, () -> taskHandler.handleDeleteTask(cmd));
    }

    @Test
    public void handleEditTask_negativeIndex_throwsInvalidIndexException() throws Exception {
        skuList.addSKU("EDIT-NEG", Location.A1);
        skuList.findByID("EDIT-NEG").getSKUTaskList().addSKUTask("EDIT-NEG", Priority.LOW,
                "2026-01-01", "T");

        ParsedCommand cmd = new ParsedCommand("edittask",
                Map.of("n", "EDIT-NEG", "i", "-1", "t", "new desc"));
        assertThrows(InvalidIndexException.class, () -> taskHandler.handleEditTask(cmd));
    }

    @Test
    public void handleMarkTask_negativeIndex_throwsInvalidIndexException() throws Exception {
        skuList.addSKU("MARK-NEG", Location.A1);
        skuList.findByID("MARK-NEG").getSKUTaskList().addSKUTask("MARK-NEG", Priority.LOW,
                "2026-01-01", "T");

        ParsedCommand cmd = new ParsedCommand("marktask", Map.of("n", "MARK-NEG",
                "i", "-1"));
        assertThrows(InvalidIndexException.class, () -> taskHandler.handleMarkTask(cmd));
    }

    @Test
    public void handleUnmarkTask_negativeIndex_throwsInvalidIndexException() throws Exception {
        skuList.addSKU("UNMARK-NEG", Location.A1);
        skuList.findByID("UNMARK-NEG").getSKUTaskList().addSKUTask("UNMARK-NEG", Priority.LOW,
                "2026-01-01", "T");
        skuList.findByID("UNMARK-NEG").getSKUTaskList().markTask(1);

        ParsedCommand cmd = new ParsedCommand("unmarktask", Map.of("n", "UNMARK-NEG",
                "i", "-1"));
        assertThrows(InvalidIndexException.class, () -> taskHandler.handleUnmarkTask(cmd));
    }

    @Test
    public void handleFind_negativeIndex_throwsInvalidIndexException() {
        skuList.addSKU("FIND-NEG", Location.A1);
        skuList.findByID("FIND-NEG").getSKUTaskList().addSKUTask("FIND-NEG", Priority.HIGH,
                "2026-01-01", "test");

        ParsedCommand cmd = new ParsedCommand("find", Map.of("n", "FIND-NEG", "i", "-1"));
        assertThrows(InvalidIndexException.class, () -> viewHandler.handleFind(cmd));
    }

    @Test
    public void handleStatus_allSkus_executesWithoutError() {
        ParsedCommand cmd = new ParsedCommand("status", Map.of());
        assertDoesNotThrow(() -> viewHandler.handleStatus(cmd));
    }

    @Test
    public void handleStatus_specificSku_executesWithoutError() {
        skuList.addSKU("STAT-SKU", Location.C2);
        ParsedCommand cmd = new ParsedCommand("status", Map.of("n", "STAT-SKU"));
        assertDoesNotThrow(() -> viewHandler.handleStatus(cmd));
    }

    @Test
    public void handleAddSku_unknownFlag_throwsInvalidFilterException() {
        ParsedCommand cmd = new ParsedCommand("addsku", Map.of("n", "FLAG-SKU", "l", "A1", "x", "BAD"));
        assertThrows(InvalidFilterException.class, () -> skuHandler.handleAddSku(cmd));
    }

    @Test
    public void handleAddSkuTask_unknownFlag_throwsInvalidFilterException() throws Exception {
        skuList.addSKU("FLAG-TEST", Location.A1);
        ParsedCommand cmd = new ParsedCommand("addskutask",
                Map.of("n", "FLAG-TEST", "d", "2026-06-15", "x", "HIGH"));
        assertThrows(InvalidFilterException.class, () -> taskHandler.handleAddSkuTask(cmd));
    }

    @Test
    public void handleEditTask_unknownFlag_throwsInvalidFilterException() throws Exception {
        skuList.addSKU("EDIT-FLAG", Location.A1);
        skuList.findByID("EDIT-FLAG").getSKUTaskList().addSKUTask("EDIT-FLAG", Priority.LOW,
                "2026-01-01", "T");
        ParsedCommand cmd = new ParsedCommand("edittask",
                Map.of("n", "EDIT-FLAG", "i", "1", "x", "LOW"));
        assertThrows(InvalidFilterException.class, () -> taskHandler.handleEditTask(cmd));
    }

    @Test
    public void handleDeleteTask_unknownFlag_throwsInvalidFilterException() throws Exception {
        skuList.addSKU("DEL-FLAG", Location.A1);
        skuList.findByID("DEL-FLAG").getSKUTaskList().addSKUTask("DEL-FLAG", Priority.LOW,
                "2026-01-01", "T");
        ParsedCommand cmd = new ParsedCommand("deletetask",
                Map.of("n", "DEL-FLAG", "i", "1", "x", "BAD"));
        assertThrows(InvalidFilterException.class, () -> taskHandler.handleDeleteTask(cmd));
    }

    @Test
    public void handleMarkTask_unknownFlag_throwsInvalidFilterException() throws Exception {
        skuList.addSKU("MARK-FLAG", Location.A1);
        skuList.findByID("MARK-FLAG").getSKUTaskList().addSKUTask("MARK-FLAG", Priority.LOW,
                "2026-01-01", "T");
        ParsedCommand cmd = new ParsedCommand("marktask",
                Map.of("n", "MARK-FLAG", "i", "1", "x", "BAD"));
        assertThrows(InvalidFilterException.class, () -> taskHandler.handleMarkTask(cmd));
    }

    @Test
    public void handleUnmarkTask_unknownFlag_throwsInvalidFilterException() throws Exception {
        skuList.addSKU("UMARK-FLAG", Location.A1);
        skuList.findByID("UMARK-FLAG").getSKUTaskList().addSKUTask("UMARK-FLAG", Priority.LOW,
                "2026-01-01", "T");
        ParsedCommand cmd = new ParsedCommand("unmarktask",
                Map.of("n", "UMARK-FLAG", "i", "1", "x", "BAD"));
        assertThrows(InvalidFilterException.class, () -> taskHandler.handleUnmarkTask(cmd));
    }

    @Test
    public void handleFind_unknownFlag_throwsInvalidFilterException() {
        skuList.addSKU("FIND-FLAG", Location.A1);
        skuList.findByID("FIND-FLAG").getSKUTaskList().addSKUTask("FIND-FLAG", Priority.HIGH,
                "2026-01-01", "test");
        ParsedCommand cmd = new ParsedCommand("find", Map.of("n", "FIND-FLAG", "x", "BAD"));
        assertThrows(InvalidFilterException.class, () -> viewHandler.handleFind(cmd));
    }

    @Test
    public void handleEditSku_unknownFlag_throwsInvalidFilterException() throws Exception {
        skuList.addSKU("EDITS-FLAG", Location.A1);
        ParsedCommand cmd = new ParsedCommand("editsku", Map.of("n", "EDITS-FLAG", "l", "B2", "x", "BAD"));
        assertThrows(InvalidFilterException.class, () -> skuHandler.handleEditSku(cmd));
    }

    @Test
    public void handleDeleteSku_unknownFlag_throwsInvalidFilterException() throws Exception {
        skuList.addSKU("DELSKU-FLAG", Location.A1);
        ParsedCommand cmd = new ParsedCommand("deletesku", Map.of("n", "DELSKU-FLAG", "x", "BAD"));
        assertThrows(InvalidFilterException.class, () -> skuHandler.handleDeleteSku(cmd));
    }

    @Test
    public void handleSortTask_unknownFlag_throwsInvalidFilterException() throws Exception {
        skuList.addSKU("SORT-FLAG", Location.A1);
        ParsedCommand cmd = new ParsedCommand("sorttasks", Map.of("n", "SORT-FLAG", "x", "BAD"));
        assertThrows(InvalidFilterException.class, () -> taskHandler.handleSortTask(cmd));
    }

    @Test
    public void handleListTasks_nonExistentSku_throwsSKUNotFoundException() {
        ParsedCommand cmd = new ParsedCommand("listtasks", Map.of("n", "GHOST-SKU"));
        assertThrows(SKUNotFoundException.class, () -> viewHandler.handleListTasks(cmd));
    }

    @Test
    public void parseIndex_overflowLargeIndex_throwsInvalidIndexException() {
        // Technically testing through marktask handle
        ParsedCommand cmd = new ParsedCommand("marktask", Map.of("n", "PALLET-A", "i", "2147483648"));
        InvalidIndexException thrown = assertThrows(InvalidIndexException.class, () -> taskHandler.handleMarkTask(cmd));
        assertTrue(thrown.getMessage().contains("Task index is too large"));
    }
}
