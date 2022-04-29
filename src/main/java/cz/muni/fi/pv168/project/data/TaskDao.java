package cz.muni.fi.pv168.project.data;

import cz.muni.fi.pv168.project.model.Customer;
import cz.muni.fi.pv168.project.model.Task;
import cz.muni.fi.pv168.project.model.TaskType;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static java.sql.Statement.RETURN_GENERATED_KEYS;


public final class TaskDao extends CommonDao implements DataAccessObject<Task> {

    public TaskDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void create(Task task) throws DataAccessException {
        if (task.getId() != null) {
            throw new IllegalArgumentException("Task already has ID: " + task);
        }
        try (var connection = getDataSource().getConnection();
             var st = connection.prepareStatement(
                     "INSERT INTO TASK (" +
                             "DESCRIPTION, " +
                             "DATE, " +
                             "WORK_TIME, " +
                             "CUSTOMER_ID, " +
                             "TASK_TYPE_ID, " +
                             "TASK_TYPE_DESCRIPTION, " +
                             "TASK_TYPE_HOURLY_RATE" +
                             ") VALUES (?, ?, ?, ?, ?, ?, ?)",
                     RETURN_GENERATED_KEYS)) {
            st.setString(1, task.getDescription());
            st.setTimestamp(2, java.sql.Timestamp.valueOf(task.getDate().atStartOfDay()));
            st.setInt(3, task.getWorkTime());
            if (task.getCustomer() == null) {
                st.setNull(4, Types.BIGINT);
            } else {
                st.setLong(4, task.getCustomer().getId());
            }
            if (task.getTaskType() == null) {
                st.setNull(5, Types.BIGINT);
            } else {
                st.setLong(5, task.getTaskType().getId());
            }
            st.setString(6, task.getTaskTypeDescription());
            st.setDouble(7, task.getTaskTypeHourlyRate());
            st.executeUpdate();
            try (var rs = st.getGeneratedKeys()) {
                if (rs.getMetaData().getColumnCount() != 1) {
                    throw new DataAccessException("Failed to fetch generated key: compound key returned for task: " + task);
                }
                if (rs.next()) {
                    task.setId(rs.getLong(1));
                } else {
                    throw new DataAccessException("Failed to fetch generated key: no key returned for task: " + task);
                }
                if (rs.next()) {
                    throw new DataAccessException("Failed to fetch generated key: multiple keys returned for task: " + task);
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to store task " + task, ex);
        }
    }

    private TaskType resolveTaskType(ResultSet rs) throws SQLException {
        var taskType = new TaskType(
                rs.getString("TT_DESCRIPTION"),
                rs.getDouble("HOURLY_RATE"));
        taskType.setId(rs.getLong("TASK_TYPE_ID"));
        return taskType;
    }

    private Customer resolveCustomer(ResultSet rs) throws SQLException {
        var customer = new Customer(
                rs.getString("NAME"),
                rs.getString("DIC"),
                rs.getString("PHONE"),
                rs.getString("MAIL"));
        customer.setId(rs.getLong("CUSTOMER_ID"));
        return customer;
    }

    @Override
    public Collection<Task> findAll() throws DataAccessException {
        try (var connection = getDataSource().getConnection();
             var st = connection.prepareStatement("SELECT " +
                     "TASK.ID AS TASK_ID, " +
                     "TASK.DESCRIPTION AS T_DESCRIPTION, " +
                     "DATE, " +
                     "WORK_TIME, " +
                     "CUSTOMER_ID, " +
                     "TASK_TYPE_ID, " +
                     "TASK_TYPE_DESCRIPTION, " +
                     "TASK_TYPE_HOURLY_RATE, " +
                     "C.ID AS CUSTOMER_ID, " +
                     "NAME, " +
                     "DIC, " +
                     "PHONE, " +
                     "MAIL, " +
                     "T.ID AS TASK_TYPE_ID, " +
                     "HOURLY_RATE, " +
                     "T.DESCRIPTION AS TT_DESCRIPTION " +
                     "FROM TASK " +
                     "INNER JOIN TASK_TYPE T on TASK.TASK_TYPE_ID = T.ID " +
                     "LEFT OUTER JOIN CUSTOMER C on TASK.CUSTOMER_ID = C.ID "

             )) {
            List<Task> tasks = new ArrayList<>();
            try (var rs = st.executeQuery()) {
                while (rs.next()) {
                    var task = new Task(
                            rs.getString("T_DESCRIPTION"),
                            rs.getTimestamp("DATE").toLocalDateTime().toLocalDate(),
                            rs.getInt("WORK_TIME"),
                            resolveCustomer(rs),
                            resolveTaskType(rs)
                        );

                    task.setTaskTypeHourlyRate(rs.getDouble("TASK_TYPE_HOURLY_RATE"));
                    task.setTaskTypeDescription(rs.getString("TASK_TYPE_DESCRIPTION"));
                    task.setId(rs.getLong("TASK_ID"));
                    tasks.add(task);
                }
            }
            return tasks;
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to load all employees", ex);
        }
    }

    @Override
    public void update(Task task) throws DataAccessException {
        if (task.getId() == null) {
            throw new IllegalArgumentException("Task has null ID");
        }

        try (var connection = getDataSource().getConnection();
             var st = connection.prepareStatement(
                     "UPDATE TASK SET " +
                             "DESCRIPTION = ?, " +
                             "DATE = ?, " +
                             "WORK_TIME = ?, " +
                             "CUSTOMER_ID = ?, " +
                             "TASK_TYPE_ID = ?, " +
                             "TASK_TYPE_HOURLY_RATE = ? " +
                             "WHERE ID = ?",
                     RETURN_GENERATED_KEYS)) {
            st.setString(1, task.getDescription());
            st.setTimestamp(2, java.sql.Timestamp.valueOf(task.getDate().atStartOfDay()));
            st.setInt(3, task.getWorkTime());
            if (task.getCustomer() == null) {
                st.setNull(4, Types.BIGINT);
            } else {
                st.setLong(4, task.getCustomer().getId());
            }
            if (task.getTaskType() == null) {
                st.setNull(5, Types.BIGINT);
            } else {
                st.setLong(5, task.getTaskType().getId());
            }
            st.setDouble(6, task.getTaskTypeHourlyRate());
            st.setLong(7, task.getId());
            int rowsUpdated = st.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DataAccessException("Failed to update non-existing task: " + task);
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to update task " + task, ex);
        }
    }

    @Override
    public void delete(Task task) throws DataAccessException {
        if (task.getId() == null) {
            throw new IllegalArgumentException("Task has null ID: " + task);
        }
        try (var connection = getDataSource().getConnection();
             var st = connection.prepareStatement("DELETE FROM TASK WHERE ID = ?")) {
            st.setLong(1, task.getId());
            int rowsDeleted = st.executeUpdate();
            if (rowsDeleted == 0) {
                throw new DataAccessException("Failed to delete non-existing task: " + task);
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to delete task " + task, ex);
        }
    }

    @Override
    protected String getTableName() {
        return "TASK";
    }

    @Override
    protected String getTableSchema() {
        return  "CREATE TABLE APP.TASK (" +
                "ID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0)," +
                "DESCRIPTION VARCHAR(100) NOT NULL," +
                "DATE TIMESTAMP NOT NULL," +
                "WORK_TIME INT NOT NULL," +
                "CUSTOMER_ID BIGINT REFERENCES CUSTOMER(ID), " +
                "TASK_TYPE_ID BIGINT REFERENCES TASK_TYPE(ID)," +
                "TASK_TYPE_DESCRIPTION VARCHAR(100) NOT NULL," +
                "TASK_TYPE_HOURLY_RATE DECIMAL(15, 2) NOT NULL" +
                ")";
    }
}
