package cz.muni.fi.pv168.project.ui.dialog;

import cz.muni.fi.pv168.project.data.validation.TaskTypeValidator;
import cz.muni.fi.pv168.project.model.TaskType;
import cz.muni.fi.pv168.project.ui.i18n.I18N;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public final class TaskTypeDialog extends EntityDialog<TaskType> {

    private static final I18N I18N = new I18N(TaskTypeDialog.class);

    private final JTextField description = new JTextField(20);
    private final JTextField hourlyRate = new JTextField(20);

    private final TaskType taskType;

    public TaskTypeDialog(TaskType taskType, String title) {
        super(title);
        this.taskType = taskType;
        setValues();
        addFields();
    }

    private void setValues() {
        description.setText((taskType.getDescription()));
        hourlyRate.setText(String.valueOf(taskType.getHourlyRate()));
    }

    private void addFields() {
        add(I18N.getString("description"), description, true);
        add(I18N.getString("hourlyRate"), hourlyRate, true);
    }

    @Override
    TaskType getEntity() {
        taskType.setDescription(description.getText());
        taskType.setHourlyRate(Double.parseDouble(hourlyRate.getText()));
        return taskType;
    }

    @Override
    void fieldsListener(JButton ok) {
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
                ok.setEnabled(TaskTypeValidator.validate(
                    description.getText(),
                    hourlyRate.getText()
                ));
            }
        };
        description.getDocument().addDocumentListener(docListener);
        hourlyRate.getDocument().addDocumentListener(docListener);
    }
}
