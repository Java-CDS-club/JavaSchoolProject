package cz.muni.fi.pv168.project.ui.model;

import cz.muni.fi.pv168.project.data.TaskDao;
import cz.muni.fi.pv168.project.data.TaskEventEmitter;
import cz.muni.fi.pv168.project.model.Customer;
import cz.muni.fi.pv168.project.model.Task;
import cz.muni.fi.pv168.project.ui.error.ErrorDialog;
import cz.muni.fi.pv168.project.ui.i18n.I18N;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class TaskTableModel extends EditableEntityTableModel<Task> {

    private static final cz.muni.fi.pv168.project.ui.i18n.I18N I18N = new I18N(TaskTableModel.class);

    private static final List<Column<Task, ?>> COLUMNS = List.of(
            Column.editable(I18N.getString("customer"), Customer.class, Task::getCustomer, Task::setCustomer),
            Column.readOnly(I18N.getString("date"), LocalDate.class, Task::getDate),
            Column.readOnly(I18N.getString("workTime"), String.class, (task -> formatNumbers(task.getWorkTime(), "hrs"))),
            Column.readOnly(I18N.getString("taskType"), String.class, Task::getTaskTypeDescription),
            Column.readOnly(I18N.getString("wage"), String.class, Task::getHourlyRateWithCurrency),
            Column.editable(I18N.getString("description"), String.class, Task::getDescription, Task::setDescription),
            Column.readOnly(I18N.getString("price"), String.class, (task -> formatNumbers(task.getPrice(), Task.getCurrency())))
    );
    private final TaskEventEmitter taskEventEmitter;

    public TaskTableModel(TaskDao taskDao, TaskEventEmitter taskEventEmitter) {
        super(COLUMNS, taskDao);
        this.taskEventEmitter = taskEventEmitter;
    }

    public static String formatNumbers(double number, String string) {
        return String.format("%.2f %s", number, string);
    }

    @Override
    public void addRow(Task entity) {
        int newRowIndex = rows.size();
        SwingWorker<Void, Void> swingWorker = new SwingWorker<>() {

            @Override
            protected Void doInBackground() {
                dataAccessObject.create(entity);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    rows.add(entity);
                    taskEventEmitter.emit(entity, TaskEventEmitter.Event.CREATED);
                    fireTableRowsInserted(newRowIndex, newRowIndex);
                } catch (Exception ex) {
                    ErrorDialog.show(I18N.getFormattedMessage("addRowError"));
                }
            }
        };
        swingWorker.execute();
    }

    @Override
    public void deleteRows(List<Integer> rowIndexes) {
        var sorted = rowIndexes
                .stream()
                .sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        SwingWorker<Void, Void> swingWorker = new SwingWorker<>() {

            @Override
            protected Void doInBackground() {
                for (var rowIndex:
                        sorted) {
                    var task = rows.get(rowIndex);
                    dataAccessObject.delete(task);
                    taskEventEmitter.emit(task, TaskEventEmitter.Event.DELETED);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    updateRows(sorted);
                } catch (Exception ex) {
                    ErrorDialog.show(I18N.getFormattedMessage("deleteRowError"));
                }
            }
        };
        swingWorker.execute();
    }
}
