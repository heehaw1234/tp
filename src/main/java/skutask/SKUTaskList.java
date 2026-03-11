package skutask;

import java.util.ArrayList;

public class SKUTaskList {
    private final ArrayList<SKUTask> skuTaskList; // makes reference to taskList immutable

    public SKUTaskList() {
        this.skuTaskList = new ArrayList<SKUTask>();
    }

    public int getSize() {
        return skuTaskList.size();
    }

    public boolean isEmpty() {
        return skuTaskList.isEmpty();
    }

    public void addSKUTask(String skuID, Priority priority, String dueDate) {
        SKUTask newTask = new SKUTask(skuID, priority, dueDate);
        skuTaskList.add(newTask);
    }

    public void addSKUTask(String skuID, String dueDate) {
        SKUTask newSkuTask = new SKUTask(skuID, dueDate);
        skuTaskList.add(newSkuTask);
    }

    private int getIndexOfSKUTask(String skuID) {
        int size = getSize();
        for (int i = 0; i < size; i++) {
            if (skuTaskList.get(i).getSKUTaskID().equals(skuID)) {
                return i;
            }
        }
        return -1;
    }

    public void deleteSKUTask(String skuIDToDelete) {
        int idxToDelete = getIndexOfSKUTask(skuIDToDelete);
        if (idxToDelete != -1) {
            skuTaskList.remove(idxToDelete);
        }
    }

    public ArrayList<SKUTask> getSKUTaskList() {
        return skuTaskList;
    }

    public void printSKUTaskList() {
        int i = 1;
        for (SKUTask currSkuTask : skuTaskList) {
            System.out.println(i + ". " + currSkuTask);
            i++;
        }
    }

    public void markTask(int taskIndex) {
        skuTaskList.get(taskIndex - 1).mark();
    }

    public void unmarkTask(int taskIndex) {
        skuTaskList.get(taskIndex - 1).unmark();
    }
}
