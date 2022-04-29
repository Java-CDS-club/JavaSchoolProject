package cz.muni.fi.pv168.project.ui;

import cz.muni.fi.pv168.project.model.Customer;
import cz.muni.fi.pv168.project.model.Task;
import cz.muni.fi.pv168.project.ui.model.TaskTableModel;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.util.List;

public class CustomerFilter {
    private final TableRowSorter<TaskTableModel> rowSorter;

    CustomerFilter(TableRowSorter<TaskTableModel> rowSorter) {
        this.rowSorter = rowSorter;
    }

    void filter(List<Customer> selectedCustomers) {
        if (selectedCustomers.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(new CustomerRowFilter(selectedCustomers));
        }
    }

    private static class CustomerRowFilter extends RowFilter<TaskTableModel, Integer> {

        private final List<Customer> selectedCustomers;

        private CustomerRowFilter(List<Customer> selectedCustomers) {
            this.selectedCustomers = selectedCustomers;
        }

        @Override
        public boolean include(Entry<? extends TaskTableModel, ? extends Integer> entry) {
            TaskTableModel tableModel = entry.getModel();
            int rowIndex = entry.getIdentifier();
            Task task = tableModel.getEntity(rowIndex);
            return selectedCustomers.contains(task.getCustomer());
        }
    }
}
