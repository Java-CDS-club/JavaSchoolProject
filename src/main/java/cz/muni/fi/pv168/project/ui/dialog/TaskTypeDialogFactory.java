package cz.muni.fi.pv168.project.ui.dialog;

import cz.muni.fi.pv168.project.model.TaskType;
import cz.muni.fi.pv168.project.ui.i18n.I18N;

public class TaskTypeDialogFactory implements EntityDialogFactory<TaskType>{

    private static final I18N I18N = new I18N(TaskTypeDialogFactory.class);

    @Override
    public EntityDialog<TaskType> newEditDialog(TaskType entity) {
        return new TaskTypeDialog(entity, I18N.getString("title.edit"));
    }

    @Override
    public EntityDialog<TaskType> newAddDialog() {
        return new TaskTypeDialog(new TaskType("", 0), I18N.getString("title.add"));
    }
}
