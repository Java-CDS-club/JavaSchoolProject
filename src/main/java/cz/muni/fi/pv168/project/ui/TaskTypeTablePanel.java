package cz.muni.fi.pv168.project.ui;

import cz.muni.fi.pv168.project.model.TaskType;
import cz.muni.fi.pv168.project.ui.action.GlobalActions;
import cz.muni.fi.pv168.project.ui.dialog.TaskTypeDialogFactory;
import cz.muni.fi.pv168.project.ui.i18n.I18N;
import cz.muni.fi.pv168.project.ui.model.TaskTypeTableModel;
import cz.muni.fi.pv168.project.ui.operation.*;

import javax.swing.*;
import java.awt.*;

public class TaskTypeTablePanel extends OperationProviderPanel {

    private static final I18N I18N = new I18N(TaskTypeTablePanel.class);

    public TaskTypeTablePanel(TaskTypeTableModel taskTypeTableModel, GlobalActions globalActions) {
        var table = new JTable(taskTypeTableModel);
        var editSupport = new TableEditSupport<>(table, taskTypeTableModel, new TaskTypeDialogFactory());

        configureOperations(editSupport);
        table.setComponentPopupMenu(createTablePopupMenu(globalActions));

        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    protected void configureOperations(EditSupport<TaskType> editSupport) {
        addOperation(new AddOperationImpl<>(editSupport, I18N.getString("AddOperation.description")));
        addOperation(new TaskTypeDeleteOperationImpl(editSupport, I18N.getString("DeleteOperation.description")));
        addOperation(new EditOperationImpl<>(editSupport, I18N.getString("EditOperation.description")));
    }

    protected JPopupMenu createTablePopupMenu(GlobalActions globalActions) {
        var menu = new JPopupMenu();
        menu.add(globalActions.getAddAction());
        menu.add(globalActions.getDeleteAction());
        menu.add(globalActions.getEditAction());
        return menu;
    }
}
