package cz.muni.fi.pv168.project.ui.operation;

import cz.muni.fi.pv168.project.model.TaskType;

public class TaskTypeDeleteOperationImpl extends DeleteOperationImpl<TaskType>{
    private final EditSupport<TaskType> editSupport;

    public TaskTypeDeleteOperationImpl(EditSupport<TaskType> editSupport, String description) {
        super(editSupport, description);
        this.editSupport = editSupport;
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() &&
                editSupport.getSelectedRows()
                        .stream()
                        .map(editSupport.getEditableModel()::getEntity)
                        .noneMatch(taskType -> taskType.getTaskCount() > 0);
    }
}
