package cz.muni.fi.pv168.project.ui.action;

import cz.muni.fi.pv168.project.ui.operation.Operation;
import cz.muni.fi.pv168.project.ui.operation.OperationProvider;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.util.Optional;

abstract class AbstractEntityAction<T extends Operation> extends AbstractAction {

    private final Class<T> operationType;
    private final JTabbedPane tabbedPane;
    private final ChangeListener operationEnabledListener = e -> updateEnabledStatus();
    private T currentOperation;

    public AbstractEntityAction(Class<T> operationType, JTabbedPane tabbedPane) {
        this.operationType = operationType;
        this.tabbedPane = tabbedPane;
        this.tabbedPane.addChangeListener(e -> updateSelectedTab());
        updateSelectedTab();
    }

    protected Optional<T> getOperation() {
        var selectedComponent = tabbedPane.getSelectedComponent();
        if (selectedComponent instanceof OperationProvider) {
            var operationProvider = (OperationProvider) selectedComponent;
            return operationProvider.getOperation(operationType);
        } else {
            return Optional.empty();
        }
    }

    private void updateSelectedTab() {
        if (currentOperation != null) {
            currentOperation.removeOperationEnabledListener(operationEnabledListener);
        }
        currentOperation = getOperation().orElse(null);
        if (currentOperation != null) {
            putValue(SHORT_DESCRIPTION, currentOperation.getDescription());
            currentOperation.addOperationEnabledListener(operationEnabledListener);
        } else {
            putValue(SHORT_DESCRIPTION, "");
        }
        updateEnabledStatus();
    }

    private void updateEnabledStatus() {
        var operation = getOperation();
        if (operation.isPresent()) {
            setEnabled(operation.get().isEnabled());
        } else {
            setEnabled(false);
        }
    }
}
