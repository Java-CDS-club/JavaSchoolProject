package cz.muni.fi.pv168.project.model;

import java.util.Objects;

public class TaskType {

    private Long id;
    private String description;
    private double hourlyRate;
    private int taskCount;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TaskType(String description, double hourlyRate, int taskCount) {
        this.taskCount = taskCount;
        setDescription(description);
        setHourlyRate(hourlyRate);
    }

    public TaskType(String description, double hourlyRate) {
        this(description, hourlyRate, 0);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = Objects.requireNonNull(description, "description must not be null");
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    @Override
    public String toString() {
        return hourlyRate + ":" + description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskType)) return false;
        TaskType that = (TaskType) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }
}
