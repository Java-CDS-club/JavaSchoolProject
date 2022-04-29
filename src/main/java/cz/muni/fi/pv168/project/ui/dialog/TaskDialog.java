package cz.muni.fi.pv168.project.ui.dialog;

import cz.muni.fi.pv168.project.data.validation.TaskValidator;
import cz.muni.fi.pv168.project.model.Customer;
import cz.muni.fi.pv168.project.model.Task;
import cz.muni.fi.pv168.project.model.TaskType;
import cz.muni.fi.pv168.project.ui.i18n.I18N;
import cz.muni.fi.pv168.project.ui.model.ComboBoxModelAdapter;
import cz.muni.fi.pv168.project.ui.model.LocalDateModel;
import org.jdatepicker.DateModel;
import org.jdatepicker.JDatePicker;


import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.time.LocalDate;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public final class TaskDialog extends EntityDialog<Task> {

    private static final I18N I18N = new I18N(TaskDialog.class);

    private final DateModel<LocalDate> date = new LocalDateModel();
    private final JTextField workTime = new JTextField(20);
    private final ComboBoxModel<Customer> customerModel;
    private final JComboBox<Customer> customerComboBox;
    private final ComboBoxModel<TaskType> taskTypeModel;
    private final JComboBox<TaskType> taskTypeComboBox;
    private final JTextField taskHourlyRate = new JTextField(20);
    private final JTextField description = new JTextField(20);

    private final Task task;

    public TaskDialog(Task task, ListModel<Customer> customerModel, ListModel<TaskType> taskTypeModel, String title) {
        super(title);
        this.task = task;
        this.customerModel = new ComboBoxModelAdapter<>(customerModel);
        this.taskTypeModel = new ComboBoxModelAdapter<>(taskTypeModel);

        taskTypeComboBox = new JComboBox<>(this.taskTypeModel);
        taskTypeComboBox.addItemListener(e -> {
            if ((e.getStateChange() == ItemEvent.SELECTED)) {
                var taskType = (TaskType) this.taskTypeModel.getSelectedItem();
                taskHourlyRate.setText(Double.toString(taskType.getHourlyRate()));
            }
        });

        customerComboBox =  new JComboBox<>(this.customerModel);

        setValues();
        addFields();
    }

    private void setValues() {
        date.setValue(task.getDate());
        workTime.setText(Integer.toString(task.getWorkTime()));
        description.setText(task.getDescription());
        customerModel.setSelectedItem(task.getCustomer());
        taskTypeModel.setSelectedItem(task.getTaskType());
        taskHourlyRate.setText(Double.toString(task.getTaskTypeHourlyRate()));
    }

    private void addFields() {
        add(I18N.getString("date"), new JDatePicker(date), true);
        add(I18N.getString("workTime"), workTime, true);
        add(I18N.getString("customer"), customerComboBox);
        add(I18N.getString("taskType"), taskTypeComboBox);
        add(I18N.getString("taskTypeHourlyRate"), taskHourlyRate, true);
        add(I18N.getString("description"), description);
    }

    @Override
    Task getEntity() {
        task.setDate(date.getValue());
        task.setWorkTime(Integer.parseInt(workTime.getText()));
        task.setCustomer((Customer) customerModel.getSelectedItem());
        var taskType = (TaskType) taskTypeModel.getSelectedItem();
        task.setTaskTypeDescription(taskType.getDescription());
        task.setDescription(description.getText());
        task.setTaskType(taskType);
        if (taskHourlyRate.getText().equals("")) {
            task.setTaskTypeHourlyRate(taskType.getHourlyRate());
        } else {
            task.setTaskTypeHourlyRate(Double.parseDouble(taskHourlyRate.getText()));
        }
        return task;
    }

    @Override
    void fieldsListener(JButton ok) {
        ok.setEnabled(isValid());
        DocumentListener docListener = new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkForText();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                checkForText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkForText();
            }

            private void checkForText() {
                ok.setEnabled(isValid());
            }
        };
        ActionListener actListener = e -> {
            ok.setEnabled(isValid());
        };
        taskHourlyRate.getDocument().addDocumentListener(docListener);
        customerComboBox.addActionListener(actListener);
        taskTypeComboBox.addActionListener(actListener);

    }

    private boolean isValid() {
        return TaskValidator.validate(
                (Customer) customerModel.getSelectedItem(),
                (TaskType) taskTypeModel.getSelectedItem(),
                taskHourlyRate.getText());
    }
}
