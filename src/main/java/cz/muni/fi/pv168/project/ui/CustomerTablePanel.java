package cz.muni.fi.pv168.project.ui;

import cz.muni.fi.pv168.project.data.CustomerDao;
import cz.muni.fi.pv168.project.model.Customer;
import cz.muni.fi.pv168.project.ui.action.GlobalActions;
import cz.muni.fi.pv168.project.ui.dialog.CustomerDialogFactory;
import cz.muni.fi.pv168.project.ui.i18n.I18N;
import cz.muni.fi.pv168.project.ui.model.CustomerTableModel;
import cz.muni.fi.pv168.project.ui.operation.*;

import javax.swing.*;
import java.awt.*;

public class CustomerTablePanel extends OperationProviderPanel {

    private static final I18N I18N = new I18N(CustomerTablePanel.class);

    public CustomerTablePanel(CustomerTableModel customerTableModel, GlobalActions globalActions, CustomerDao customerDao) {
        var table = new JTable(customerTableModel);
        var editSupport = new TableEditSupport<>(table, customerTableModel, new CustomerDialogFactory());

        configureOperations(editSupport);
        table.setComponentPopupMenu(createTablePopupMenu(globalActions));

        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    protected void configureOperations(EditSupport<Customer> editSupport) {
        addOperation(new AddOperationImpl<>(editSupport, I18N.getString("AddOperation.description")));
        addOperation(new CustomerDeleteOperationImpl(editSupport, I18N.getString("DeleteOperation.description")));
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
