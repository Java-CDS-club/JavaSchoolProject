package cz.muni.fi.pv168.project.data.validation;

public class TaskTypeValidator {
    public static boolean validate(
        String description,
        String rate
    ) {
        if (description.isEmpty())
            return false;
        if (rate.isEmpty() || !rate.matches("\\d+.?\\d*") || Double.parseDouble(rate) < 0)
            return false;
        return true;
    }
}
