package cz.muni.fi.pv168.project.ui.operation;

import cz.muni.fi.pv168.project.ui.error.ErrorDialog;
import cz.muni.fi.pv168.project.ui.i18n.I18N;

import javax.swing.*;
import java.util.Comparator;
import java.util.List;

public class DeleteOperationImpl<E> extends AbstractOperation implements DeleteOperation {

    private static final I18N I18N = new I18N(DeleteOperationImpl.class);

    private final EditSupport<E> editSupport;

    public DeleteOperationImpl(EditSupport<E> editSupport, String description) {
        super(description);
        this.editSupport = editSupport;
        this.editSupport.addListSelectionListener(e -> fireActionEnabledChange());
    }

    @Override
    public boolean isEnabled() {
        return editSupport.getSelectedRowCount() > 0;
    }

    @Override
    public void delete() {
        List<Integer> selectedRows = editSupport.getSelectedRows();
        int confirmationOption = JOptionPane.showConfirmDialog(editSupport.getParentComponent(),
                I18N.getFormattedMessage("confirmationDialog.text", selectedRows.size()),
                I18N.getFormattedMessage("confirmationDialog.title"),
                JOptionPane.YES_NO_OPTION);
        if (confirmationOption == JOptionPane.YES_OPTION) {
            editSupport.getEditableModel().deleteRows(selectedRows);
        }
    }

}
