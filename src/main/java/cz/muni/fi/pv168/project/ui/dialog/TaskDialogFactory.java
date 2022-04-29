package cz.muni.fi.pv168.project.ui.dialog;

import cz.muni.fi.pv168.project.model.Customer;
import cz.muni.fi.pv168.project.model.Task;
import cz.muni.fi.pv168.project.model.TaskType;
import cz.muni.fi.pv168.project.ui.i18n.I18N;

import javax.swing.*;

public class TaskDialogFactory implements EntityDialogFactory<Task> {

    private static final I18N I18N = new I18N(TaskDialogFactory.class);

    private final ListModel<Customer> customerListModel;
    private final ListModel<TaskType> taskTypeListModel;

    public TaskDialogFactory(ListModel<Customer> customerListModel, ListModel<TaskType> taskTypeListModel) {
        this.customerListModel = customerListModel;
        this.taskTypeListModel = taskTypeListModel;
    }

    @Override
    public EntityDialog<Task> newEditDialog(Task entity) {
        return new TaskDialog(entity, customerListModel, taskTypeListModel, I18N.getString("title.edit"));
    }

    @Override
    public EntityDialog<Task> newAddDialog() {
        var task = Task.create();
        preSelect(task);
        return new TaskDialog(task, customerListModel, taskTypeListModel, I18N.getString("title.add"));
    }

    private void preSelect(Task task) {
        if (taskTypeListModel.getSize() > 0) {
            var taskType = taskTypeListModel.getElementAt(0);
            task.setTaskType(taskType);
            task.setTaskTypeHourlyRate(taskType.getHourlyRate());
        }
        if (customerListModel.getSize() > 0)
            task.setCustomer(customerListModel.getElementAt(0));
    }
}
