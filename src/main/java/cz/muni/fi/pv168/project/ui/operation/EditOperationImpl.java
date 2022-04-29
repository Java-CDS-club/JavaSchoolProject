package cz.muni.fi.pv168.project.ui.operation;

import cz.muni.fi.pv168.project.ui.error.ErrorDialog;
import cz.muni.fi.pv168.project.ui.i18n.I18N;

import javax.swing.*;

public class EditOperationImpl<E> extends AbstractOperation implements EditOperation {

    private final EditSupport<E> editSupport;

    private static final I18N I18N = new I18N(EditOperationImpl.class);


    public EditOperationImpl(EditSupport<E> editSupport, String description) {
        super(description);
        this.editSupport = editSupport;
        this.editSupport.addListSelectionListener(e -> fireActionEnabledChange());
    }

    @Override
    public boolean isEnabled() {
        return editSupport.getSelectedRowCount() == 1;
    }

    @Override
    public void edit() {
        var selectedRows = editSupport.getSelectedRows();
        if (selectedRows.size() != 1) {
            throw new IllegalStateException("Invalid selected rows count (must be 1): " + selectedRows.size());
        }
        var entity = editSupport.getEditableModel().getEntity(selectedRows.get(0));

        editSupport.getDialogFactory().newEditDialog(entity)
                .show(editSupport.getParentComponent())
                .ifPresent(editSupport.getEditableModel()::updateRow);
    }
}
