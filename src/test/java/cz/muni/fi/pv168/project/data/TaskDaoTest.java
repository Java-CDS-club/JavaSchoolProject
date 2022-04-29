package cz.muni.fi.pv168.project.data;

import cz.muni.fi.pv168.project.model.Task;
import cz.muni.fi.pv168.project.model.TaskType;
import cz.muni.fi.pv168.project.model.Customer;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

final class TaskDaoTest {

    private static EmbeddedDataSource dataSource;
    private static CustomerDao customerDao;
    private static TaskTypeDao taskTypeDao;
    private TaskDao taskDao;

    private static final Customer GOOGLE = new Customer("Google", "", "", "");
    private static final Customer FACEBOOK = new Customer("Facebook", "", "", "");
    private static final Customer AMAZON = new Customer("Amazon", "", "", "");

    private static final TaskType PROGRAMMING = new TaskType("Programming", 250);
    private static final TaskType DESIGN = new TaskType("Design", 200);
    private static final TaskType RESEARCH = new TaskType("Research", 500);

    private static final LocalDate DATE = LocalDate.now();
    private static final int WORK_TIME = 8;


    @BeforeAll
    static void initTestDataSource() throws SQLException {
        dataSource = new EmbeddedDataSource();
        dataSource.setDatabaseName("memory:project-test");
        dataSource.setCreateDatabase("create");
        customerDao = new CustomerDao(dataSource);
        taskTypeDao = new TaskTypeDao(dataSource);
        try (var connection = dataSource.getConnection(); var st = connection.createStatement()) {
            st.executeUpdate("DELETE FROM APP.CUSTOMER");
            st.executeUpdate("DELETE FROM APP.TASK_TYPE");
        }
        customerDao.create(GOOGLE);
        customerDao.create(FACEBOOK);
        customerDao.create(AMAZON);

        taskTypeDao.create(PROGRAMMING);
        taskTypeDao.create(DESIGN);
        taskTypeDao.create(RESEARCH);
    }

    @BeforeEach
    void createTaskDao() throws SQLException {

        taskDao = new TaskDao(dataSource);
        try (var connection = dataSource.getConnection(); var st = connection.createStatement()) {
            st.executeUpdate("DELETE FROM APP.TASK");
        }
    }

    @AfterEach
    void taskDaoCleanUp() {
        taskDao.dropTable();
    }

    @AfterAll
    static void customerDaoCleanUp() {
        customerDao.dropTable();
    }

    @AfterAll
    static void taskTypeDaoCleanUp() {
        taskTypeDao.dropTable();
    }

    @Test
    void createTask() {
        var programming = new Task(PROGRAMMING.getDescription(), DATE, WORK_TIME, GOOGLE, PROGRAMMING);
        taskDao.create(programming);

        assertThat(programming.getId())
                .isNotNull();
        assertThat(taskDao.findAll())
                .usingFieldByFieldElementComparator()
                .containsExactly(programming);
    }

    @Test
    void createTaskWithExistingId() {
        var programming = new Task(PROGRAMMING.getDescription(), DATE, WORK_TIME, GOOGLE, PROGRAMMING);
        programming.setId(123L);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> taskDao.create(programming))
                .withMessage("Task already has ID: %s", programming);
    }

    @Test
    void findAllEmpty() {
        assertThat(taskDao.findAll())
                .isEmpty();
    }

    @Test
    void findAll() {
        var grace = new Task(PROGRAMMING.getDescription(), DATE, WORK_TIME, GOOGLE, PROGRAMMING);
        var frederic = new Task(PROGRAMMING.getDescription(), DATE, WORK_TIME, FACEBOOK, PROGRAMMING);
        var james = new Task(PROGRAMMING.getDescription(), DATE, WORK_TIME, AMAZON, PROGRAMMING);
        var joshua = new Task(DESIGN.getDescription(), DATE, WORK_TIME, GOOGLE, DESIGN);
        var martin = new Task(RESEARCH.getDescription(), DATE, WORK_TIME, GOOGLE, RESEARCH);

        taskDao.create(grace);
        taskDao.create(frederic);
        taskDao.create(james);
        taskDao.create(joshua);
        taskDao.create(martin);

        assertThat(taskDao.findAll())
                .usingFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(grace, frederic, james, joshua, martin);
    }

    @Test
    void delete() {
        var john1 = new Task(PROGRAMMING.getDescription(), DATE, WORK_TIME, GOOGLE, PROGRAMMING);
        var john2 = new Task(PROGRAMMING.getDescription(), DATE, WORK_TIME, GOOGLE, PROGRAMMING);

        taskDao.create(john1);
        taskDao.create(john2);

        taskDao.delete(john1);

        assertThat(taskDao.findAll())
                .usingFieldByFieldElementComparator()
                .containsExactly(john2);
    }

    @Test
    void deleteWithNullId() {
        var bill = new Task(PROGRAMMING.getDescription(), DATE, WORK_TIME, GOOGLE, PROGRAMMING);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> taskDao.delete(bill))
                .withMessage("Task has null ID: %s", bill);
    }

    @Test
    void deleteNonExisting() {
        var bill = new Task(PROGRAMMING.getDescription(), DATE, WORK_TIME, GOOGLE, PROGRAMMING);
        bill.setId(123L);

        assertThatExceptionOfType(DataAccessException.class)
                .isThrownBy(() -> taskDao.delete(bill))
                .withMessage("Failed to delete non-existing task: %s", bill);
    }

    @Test
    void update() {
        var john1 = new Task(PROGRAMMING.getDescription(), DATE, WORK_TIME, GOOGLE, PROGRAMMING);
        var john2 = new Task(PROGRAMMING.getDescription(), DATE, WORK_TIME, GOOGLE, PROGRAMMING);

        taskDao.create(john1);
        taskDao.create(john2);

        john1.setDescription(DESIGN.getDescription());
        john1.setDate(DATE.minusDays(1));
        john1.setWorkTime(WORK_TIME - 1);
        john1.setCustomer(AMAZON);
        john1.setTaskType(DESIGN);

        taskDao.update(john1);

        assertThat(findById(john1.getId()))
                .isEqualToComparingFieldByField(john1);
        assertThat(findById(john2.getId()))
                .isEqualToComparingFieldByField(john2);
    }

    @Test
    void updateWithNullId() {
        var bill = new Task(PROGRAMMING.getDescription(), DATE, WORK_TIME, GOOGLE, PROGRAMMING);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> taskDao.update(bill))
                .withMessage("Task has null ID");
    }

    @Test
    void updateNonExisting() {
        var bill = new Task(PROGRAMMING.getDescription(), DATE, WORK_TIME, GOOGLE, PROGRAMMING);
        bill.setId(123L);

        assertThatExceptionOfType(DataAccessException.class)
                .isThrownBy(() -> taskDao.update(bill))
                .withMessage("Failed to update non-existing task: %s", bill);
    }

    private Task findById(long id) {
        return taskDao.findAll().stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No task with id " + id + " found"));
    }
}