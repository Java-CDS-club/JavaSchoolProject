package cz.muni.fi.pv168.project.data.validation;

import cz.muni.fi.pv168.project.model.Customer;
import cz.muni.fi.pv168.project.model.TaskType;

public class TaskValidator {
    public static boolean validate(
            Customer selectedCustomer,
            TaskType selectedTaskType,
            String rate
    ) {
        if (rate.isEmpty() || !rate.matches("\\d+.?\\d*") || Double.parseDouble(rate) < 0)
            return false;
        if (selectedTaskType == null)
            return false;
        if (selectedCustomer == null)
            return false;
        return true;
    }
}
