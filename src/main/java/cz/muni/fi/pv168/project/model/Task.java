package cz.muni.fi.pv168.project.model;

import java.time.LocalDate;
import java.util.Objects;

public class Task {

    private Long id;
    private String description;
    private LocalDate date;
    private int workTime;
    private Customer customer;
    private String taskTypeDescription;
    private double taskTypeHourlyRate;
    private static final String currency = "CZK";
    private TaskType taskType;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Task(String description, LocalDate date, int workTime, Customer customer, TaskType taskType) {
        setTaskType(taskType);
        setDescription(description);
        setDate(date);
        setWorkTime(workTime);
        setCustomer(customer);
        setTaskTypeDescription(taskType != null ? taskType.getDescription() : "");
        setTaskTypeHourlyRate(taskType != null ? taskType.getHourlyRate() : 0.0);
    }

    public static Task create() {
        return new Task(
                "", LocalDate.now(), 1, null, null
        );
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = Objects.requireNonNull(description, "firstName must not be null");
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getWorkTime() {
        return workTime;
    }

    public void setWorkTime(int workTime) {
        this.workTime = workTime;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getTaskTypeDescription() {
        return taskTypeDescription;
    }

    public void setTaskTypeDescription(String taskTypeDescription) {
        this.taskTypeDescription = taskTypeDescription;
    }

    public double getTaskTypeHourlyRate() {
        return taskTypeHourlyRate;
    }

    public void setTaskTypeHourlyRate(double taskTypeHourlyRate) {
        this.taskTypeHourlyRate = taskTypeHourlyRate;
    }

    public String getHourlyRateWithCurrency() {
        return String.format("%.0f %s", getTaskTypeHourlyRate(), currency);
    }

    public static String getCurrency() {
        return currency;
    }

    public double getPrice() {
        return workTime * taskTypeHourlyRate;
    }

    @Override
    public String toString() {
        return description + ' ' + customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task that = (Task) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
