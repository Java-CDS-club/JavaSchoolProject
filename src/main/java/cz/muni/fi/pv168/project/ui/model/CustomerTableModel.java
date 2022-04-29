package cz.muni.fi.pv168.project.ui.model;

import cz.muni.fi.pv168.project.data.CustomerDao;
import cz.muni.fi.pv168.project.data.TaskEventEmitter;
import cz.muni.fi.pv168.project.model.Customer;
import cz.muni.fi.pv168.project.model.Task;
import cz.muni.fi.pv168.project.ui.i18n.I18N;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class CustomerTableModel extends EditableEntityTableModel<Customer> {

    private static final I18N I18N = new I18N(CustomerTableModel.class);

    private static final List<Column<Customer, ?>> COLUMNS = List.of(
            Column.readOnly(I18N.getString("name"), String.class, Customer::getName),
            Column.readOnly(I18N.getString("dic"), String.class, Customer::getDic),
            Column.readOnly(I18N.getString("phone"), String.class, Customer::getPhone),
            Column.readOnly(I18N.getString("mail"), String.class, Customer::getMail)
    );

    public CustomerTableModel(CustomerDao customerDao, TaskEventEmitter taskEventEmitter) {
        super(COLUMNS, customerDao);

        taskEventEmitter.subscribe(this::taskCreated, TaskEventEmitter.Event.CREATED);
        taskEventEmitter.subscribe(this::taskDeleted, TaskEventEmitter.Event.DELETED);
    }

    public void taskCreated(Task task) {
        for (var customer:
             rows) {
            if (Objects.equals(customer.getId(), task.getCustomer().getId())) {
                customer.setTaskCount(customer.getTaskCount() + 1);
            }
        }
    }

    public void taskDeleted(Task task) {
        for (var customer:
                rows) {
            if (Objects.equals(customer.getId(), task.getCustomer().getId())) {
                customer.setTaskCount(customer.getTaskCount() - 1);
            }
        }
    }
}
