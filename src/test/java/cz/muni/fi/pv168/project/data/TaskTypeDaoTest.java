package cz.muni.fi.pv168.project.data;

import cz.muni.fi.pv168.project.model.TaskType;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

final class TaskTypeDaoTest {

    private static EmbeddedDataSource dataSource;
    private TaskTypeDao taskTypeDao;

    @BeforeAll
    static void initTestDataSource(){
        dataSource = new EmbeddedDataSource();
        dataSource.setDatabaseName("memory:project-test");
        dataSource.setCreateDatabase("create");
    }

    @BeforeEach
    void createTaskTypeDao() throws SQLException {

        taskTypeDao = new TaskTypeDao(dataSource);
        try (var connection = dataSource.getConnection(); var st = connection.createStatement()) {
            st.executeUpdate("DELETE FROM APP.TASK_TYPE");
        }
    }

    @AfterEach
    void taskTypeDaoCleanUp() {
        taskTypeDao.dropTable();
    }

    @Test
    void createTaskType() {
        var programming = new TaskType("Programming", 250);
        taskTypeDao.create(programming);

        assertThat(programming.getId())
                .isNotNull();
        assertThat(taskTypeDao.findAll())
                .usingFieldByFieldElementComparator()
                .containsExactly(programming);
    }

    @Test
    void createTaskTypeWithExistingId() {
        var programming = new TaskType("Programming", 250);
        programming.setId(123L);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> taskTypeDao.create(programming))
                .withMessage("Task type already has ID: %s", programming);
    }

    @Test
    void findAllEmpty() {
        assertThat(taskTypeDao.findAll())
                .isEmpty();
    }

    @Test
    void findAll() {
        var grace = new TaskType("Programming", 250);
        var frederic = new TaskType("Design", 200);
        var james = new TaskType("Research", 500);
        var joshua = new TaskType("HR", 100);
        var martin = new TaskType("Pretending to work", 1000);

        taskTypeDao.create(grace);
        taskTypeDao.create(frederic);
        taskTypeDao.create(james);
        taskTypeDao.create(joshua);
        taskTypeDao.create(martin);

        assertThat(taskTypeDao.findAll())
                .usingFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(grace, frederic, james, joshua, martin);
    }

    @Test
    void delete() {
        var john1 = new TaskType("Programming", 250);
        var john2 = new TaskType("Programming", 250);

        taskTypeDao.create(john1);
        taskTypeDao.create(john2);

        taskTypeDao.delete(john1);

        assertThat(taskTypeDao.findAll())
                .usingFieldByFieldElementComparator()
                .containsExactly(john2);
    }

    @Test
    void deleteWithNullId() {
        var bill = new TaskType("Programming", 250);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> taskTypeDao.delete(bill))
                .withMessage("Task type has null ID: %s", bill);
    }

    @Test
    void deleteNonExisting() {
        var bill = new TaskType("Programming", 250);
        bill.setId(123L);

        assertThatExceptionOfType(DataAccessException.class)
                .isThrownBy(() -> taskTypeDao.delete(bill))
                .withMessage("Failed to delete non-existing task type: %s", bill);
    }

    @Test
    void update() {
        var john1 = new TaskType("Programming", 250);
        var john2 = new TaskType("Programming", 250);

        taskTypeDao.create(john1);
        taskTypeDao.create(john2);

        john1.setDescription("Design");
        john1.setHourlyRate(200);

        taskTypeDao.update(john1);

        assertThat(findById(john1.getId()))
                .isEqualToComparingFieldByField(john1);
        assertThat(findById(john2.getId()))
                .isEqualToComparingFieldByField(john2);
    }

    @Test
    void updateWithNullId() {
        var bill = new TaskType("Programming", 250);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> taskTypeDao.update(bill))
                .withMessage("Task type has null ID");
    }

    @Test
    void updateNonExisting() {
        var bill = new TaskType("Programming", 250);
        bill.setId(123L);

        assertThatExceptionOfType(DataAccessException.class)
                .isThrownBy(() -> taskTypeDao.update(bill))
                .withMessage("Failed to update non-existing task type: %s", bill);
    }

    private TaskType findById(long id) {
        return taskTypeDao.findAll().stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No task with id " + id + " found"));
    }
}