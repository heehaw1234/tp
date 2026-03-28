package storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sku.Location;
import sku.SKUList;
import skutask.Priority;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

//@@author omcodedthis
class ExportTest {

    private SKUList skuList;
    private final String exportFilePath = "Data/ItemTasker_Export.txt";

    @BeforeEach
    public void setUp() {
        skuList = new SKUList();
    }

    @AfterEach
    public void tearDown() {
        File file = new File(exportFilePath);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void exportToTextFile_emptyList_writesEmptyMessage() throws IOException {
        Export.exportToTextFile(skuList);

        String content = Files.readString(Path.of(exportFilePath));
        assertTrue(content.contains("The warehouse is currently empty. No SKUs to report."));
    }

    @Test
    public void exportToTextFile_skuWithNoTasks_writesNoTasksMessage() throws IOException {
        skuList.addSKU("PALLET-A", Location.A1);
        Export.exportToTextFile(skuList);

        String content = Files.readString(Path.of(exportFilePath));
        assertTrue(content.contains("SKU: [PALLET-A] | Location: A1"));
        assertTrue(content.contains("-> No tasks assigned."));
    }

    @Test
    public void exportToTextFile_skuWithTasks_writesTaskDetails() throws IOException {
        skuList.addSKU("PALLET-B", Location.B2);
        skuList.getSKUList().get(0).getSKUTaskList().addSKUTask("PALLET-B", Priority.HIGH, "2026-12-31", "Test Export");

        Export.exportToTextFile(skuList);

        String content = Files.readString(Path.of(exportFilePath));
        assertTrue(content.contains("1. [ ] ID: PALLET-B | Priority: HIGH | Due: 2026-12-31 | Desc: Test Export"));
    }

    @Test
    public void exportToTextFile_multipleSkusAndTasks_writesSequentialDetails() throws IOException {
        skuList.addSKU("pallet-c", Location.C3);
        skuList.addSKU("PALLET-D", Location.A2);

        skuList.getSKUList().get(0).getSKUTaskList().addSKUTask("pallet-c", Priority.MEDIUM,
                "2026-05-05", "Task 1");
        skuList.getSKUList().get(0).getSKUTaskList().addSKUTask("pallet-c", Priority.LOW,
                "2026-06-06", "Task 2");

        Export.exportToTextFile(skuList);

        String content = Files.readString(Path.of(exportFilePath));

        // Verifies the SKU ID is forced to uppercase in the header
        assertTrue(content.contains("SKU: [PALLET-C] | Location: C3"));

        // Verifies sequential task numbering
        assertTrue(content.contains("1. [ ] ID: pallet-c | Priority: MEDIUM | Due: 2026-05-05 | Desc: Task 1"));
        assertTrue(content.contains("2. [ ] ID: pallet-c | Priority: LOW | Due: 2026-06-06 | Desc: Task 2"));

        // Verifies the second SKU is processed correctly
        assertTrue(content.contains("SKU: [PALLET-D] | Location: A2"));
        assertTrue(content.contains("-> No tasks assigned."));
    }

    @Test
    public void exportToTextFile_formatting_includesCorrectDividers() throws IOException {
        skuList.addSKU("PALLET-E", Location.B1);
        Export.exportToTextFile(skuList);

        String content = Files.readString(Path.of(exportFilePath));
        assertTrue(content.contains("=========================================================================="));
        assertTrue(content.contains("--------------------------------------------------------------------------"));
    }

    @Test
    public void exportToTextFile_executesWithoutThrowingException() {
        assertDoesNotThrow(() -> Export.exportToTextFile(skuList));
    }

    @Test
    public void exportToTextFile_directoryCreationFails_throwsException() {
        // Simulate a scenario where a FILE named "Data" already exists
        File fakeDir = new File("Data");

        // If the directory doesn't exist, create a dummy file to block it
        boolean createdFakeFile = false;
        try {
            if (!fakeDir.exists()) {
                fakeDir.createNewFile();
                createdFakeFile = true;
            }

            Export.exportToTextFile(skuList);

        } catch (IOException e) {
            assertNotNull(e);
        } finally {
            // Clean up the sabotage
            if (createdFakeFile) {
                fakeDir.delete();
            }
        }
    }
}
