package cz.muni.fi.pv168.project.ui.model;

import cz.muni.fi.pv168.project.data.TaskEventEmitter;
import cz.muni.fi.pv168.project.data.TaskTypeDao;
import cz.muni.fi.pv168.project.model.Task;
import cz.muni.fi.pv168.project.model.TaskType;
import cz.muni.fi.pv168.project.ui.i18n.I18N;

import java.util.List;
import java.util.Objects;

public class TaskTypeTableModel extends EditableEntityTableModel<TaskType>  {

    private static final cz.muni.fi.pv168.project.ui.i18n.I18N I18N = new I18N(TaskTypeTableModel.class);

    private static final List<Column<TaskType, ?>> COLUMNS = List.of(
            Column.readOnly(I18N.getString("description"), String.class, TaskType::getDescription),
            Column.readOnly(I18N.getString("wage"), String.class, (taskType -> Double.toString(taskType.getHourlyRate())))
    );

    public TaskTypeTableModel(TaskTypeDao taskTypeDao, TaskEventEmitter taskEventEmitter) {
        super(COLUMNS, taskTypeDao);

        taskEventEmitter.subscribe(this::taskCreated, TaskEventEmitter.Event.CREATED);
        taskEventEmitter.subscribe(this::taskDeleted, TaskEventEmitter.Event.DELETED);
    }

    public void taskCreated(Task task) {
        for (var taskType:
                rows) {
            if (Objects.equals(taskType.getId(), task.getTaskType().getId())) {
                taskType.setTaskCount(taskType.getTaskCount() + 1);
            }
        }
    }

    public void taskDeleted(Task task) {
        for (var taskType:
                rows) {
            if (Objects.equals(taskType.getId(), task.getTaskType().getId())) {
                taskType.setTaskCount(taskType.getTaskCount() - 1);
            }
        }
    }
}