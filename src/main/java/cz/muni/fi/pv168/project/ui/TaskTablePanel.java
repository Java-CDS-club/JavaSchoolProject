package cz.muni.fi.pv168.project.ui;

import cz.muni.fi.pv168.project.model.Customer;
import cz.muni.fi.pv168.project.model.Task;
import cz.muni.fi.pv168.project.model.TaskType;
import cz.muni.fi.pv168.project.ui.action.GlobalActions;
import cz.muni.fi.pv168.project.ui.dialog.TaskDialogFactory;
import cz.muni.fi.pv168.project.ui.i18n.I18N;
import cz.muni.fi.pv168.project.ui.model.CheckboxListCellRenderer;
import cz.muni.fi.pv168.project.ui.model.ComboBoxModelAdapter;
import cz.muni.fi.pv168.project.ui.model.TaskTableModel;
import cz.muni.fi.pv168.project.ui.operation.AddOperationImpl;
import cz.muni.fi.pv168.project.ui.operation.DeleteOperationImpl;
import cz.muni.fi.pv168.project.ui.operation.EditOperationImpl;
import cz.muni.fi.pv168.project.ui.operation.EditSupport;
import cz.muni.fi.pv168.project.ui.operation.TableEditSupport;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;

class TaskTablePanel extends OperationProviderPanel {

    private static final I18N I18N = new I18N(TaskTablePanel.class);
    private static final JButton selectAllButton = new JButton(I18N.getString("selectButtonSelect"));

    public TaskTablePanel(TaskTableModel taskTableModel, ListModel<Customer> customerListModel,
                          ListModel<TaskType> taskTypeListModel, GlobalActions globalActions) {
        var table = new JTable(taskTableModel);
        var customerList = new JList<>(customerListModel);
        var editSupport = new TableEditSupport<>(table, taskTableModel, new TaskDialogFactory(customerListModel, taskTypeListModel));

        configureTableEditors(customerListModel, taskTypeListModel, table);
        configureOperations(editSupport);
        configureCustomerFilter(taskTableModel, table, customerList);
        table.setComponentPopupMenu(createTablePopupMenu(globalActions));

        selectAllButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int customerListSize = customerList.getModel().getSize();
                if (customerList.getSelectedIndices().length != customerListSize) {
                    customerList.addSelectionInterval(0, customerListSize - 1);
                } else {
                    customerList.removeSelectionInterval(0, customerListSize - 1);
                }
            }
        });

        customerList.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                if(super.isSelectedIndex(index0)) {
                    super.removeSelectionInterval(index0, index1);
                }
                else {
                    super.addSelectionInterval(index0, index1);
                }
            }
        });

        customerList.addListSelectionListener(listSelectionEvent -> renameSelectButton(customerList));

        customerList.setCellRenderer(new CheckboxListCellRenderer());
        setLayout(new BorderLayout());
        var customerListWrapper = new JPanel(new BorderLayout());
        customerListWrapper.add(selectAllButton, BorderLayout.NORTH);
        customerListWrapper.add(new JScrollPane(customerList), BorderLayout.CENTER);
        var splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                customerListWrapper,
                new JScrollPane(table));
        splitPane.setEnabled(false);
        add(splitPane, BorderLayout.CENTER);

    }

    private void configureTableEditors(ListModel<Customer> customerListModel, ListModel<TaskType> taskTypeListModel, JTable table) {
        var customerComboBox = new JComboBox<>(new ComboBoxModelAdapter<>(customerListModel));
        table.setDefaultEditor(Customer.class, new DefaultCellEditor(customerComboBox));
        var customerJComboBox = new JComboBox<>(new ComboBoxModelAdapter<>(customerListModel));
        table.setDefaultEditor(Customer.class, new DefaultCellEditor(customerJComboBox));
        var taskTypeJComboBox = new JComboBox<>(new ComboBoxModelAdapter<>(taskTypeListModel));
        table.setDefaultEditor(TaskType.class, new DefaultCellEditor(taskTypeJComboBox));
    }

    protected void configureOperations(EditSupport<Task> editSupport) {
        addOperation(new AddOperationImpl<>(editSupport, I18N.getString("AddOperation.description")));
        addOperation(new DeleteOperationImpl<>(editSupport, I18N.getString("DeleteOperation.description")));
        addOperation(new EditOperationImpl<>(editSupport, I18N.getString("EditOperation.description")));
    }

    private void configureCustomerFilter(TaskTableModel taskTableModel, JTable table, JList<Customer> customerList) {
        var rowSorter = new TableRowSorter<>(taskTableModel);
        var customerFilter = new CustomerFilter(rowSorter);
        table.setRowSorter(rowSorter);
        customerList.addListSelectionListener(e -> customerFilter.filter(customerList.getSelectedValuesList()));
    }

    protected JPopupMenu createTablePopupMenu(GlobalActions globalActions) {
        var menu = new JPopupMenu();
        menu.add(globalActions.getAddAction());
        menu.add(globalActions.getDeleteAction());
        menu.add(globalActions.getEditAction());
        return menu;
    }


    private void renameSelectButton(JList<Customer> customerList) {
        if (customerList.getSelectedIndices().length != customerList.getModel().getSize()) {
            selectAllButton.setText(I18N.getString("selectButtonSelect"));
        } else {
            selectAllButton.setText(I18N.getString("selectButtonDeselect"));
        }
    }
}