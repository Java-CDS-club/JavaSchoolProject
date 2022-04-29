package cz.muni.fi.pv168.project.ui.dialog;

import cz.muni.fi.pv168.project.model.Customer;
import cz.muni.fi.pv168.project.ui.i18n.I18N;

public class CustomerDialogFactory implements EntityDialogFactory<Customer>{

    private static final I18N I18N = new I18N(CustomerDialogFactory.class);

    @Override
    public EntityDialog<Customer> newEditDialog(Customer entity) {
        return new CustomerDialog(entity, I18N.getString("title.edit"));
    }

    @Override
    public EntityDialog<Customer> newAddDialog() {
        return new CustomerDialog(Customer.create(), I18N.getString("title.add"));
    }
}
