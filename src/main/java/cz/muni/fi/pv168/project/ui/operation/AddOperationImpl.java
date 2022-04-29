package cz.muni.fi.pv168.project.ui.operation;

public class AddOperationImpl<E> extends AbstractOperation implements AddOperation {

    private final EditSupport<E> editSupport;

    public AddOperationImpl(EditSupport<E> editSupport, String description) {
        super(description);
        this.editSupport = editSupport;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void add() {
        editSupport.getDialogFactory().newAddDialog()
                .show(editSupport.getParentComponent())
                .ifPresent(editSupport.getEditableModel()::addRow);
    }
}
