package cz.muni.fi.pv168.project.ui.operation;

import cz.muni.fi.pv168.project.model.Customer;

public class CustomerDeleteOperationImpl extends DeleteOperationImpl<Customer>{
    private final EditSupport<Customer> editSupport;

    public CustomerDeleteOperationImpl(EditSupport<Customer> editSupport, String description) {
        super(editSupport, description);
        this.editSupport = editSupport;
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() &&
                editSupport.getSelectedRows()
                        .stream()
                        .map(editSupport.getEditableModel()::getEntity)
                        .noneMatch(customer -> customer.getTaskCount() > 0);
    }


}
