package cz.muni.fi.pv168.project.data;

import cz.muni.fi.pv168.project.model.TaskType;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;


public final class TaskTypeDao extends CommonDao implements DataAccessObject<TaskType> {

    private final DataSource dataSource;

    public TaskTypeDao(DataSource dataSource) {
        super(dataSource);
        this.dataSource = dataSource;
    }

    @Override
    public void create(TaskType taskType) throws DataAccessException {
        if (taskType.getId() != null) {
            throw new IllegalArgumentException("Task type already has ID: " + taskType);
        }
        try (var connection = dataSource.getConnection();
             var st = connection.prepareStatement(
                     "INSERT INTO TASK_TYPE (HOURLY_RATE, DESCRIPTION) VALUES (?, ?)",
                     RETURN_GENERATED_KEYS)) {
            st.setDouble(1, taskType.getHourlyRate());
            st.setString(2, taskType.getDescription());
            st.executeUpdate();
            try (var rs = st.getGeneratedKeys()) {
                if (rs.getMetaData().getColumnCount() != 1) {
                    throw new DataAccessException("Failed to fetch generated key: compound key returned for task type: " + taskType);
                }
                if (rs.next()) {
                    taskType.setId(rs.getLong(1));
                } else {
                    throw new DataAccessException("Failed to fetch generated key: no key returned for task type: " + taskType);
                }
                if (rs.next()) {
                    throw new DataAccessException("Failed to fetch generated key: multiple keys returned for task type: " + taskType);
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to store task type " + taskType, ex);
        }
    }

    @Override
    public Collection<TaskType> findAll() throws DataAccessException {
        try (var connection = dataSource.getConnection();
             var st = connection.prepareStatement("SELECT " +
                     "TASK_TYPE.ID AS TASK_TYPE_ID, " +
                     "HOURLY_RATE, " +
                     "TASK_TYPE.DESCRIPTION AS TT_DESCRIPTION, " +
                     "count(T.ID) AS TASK_COUNT " +
                     "FROM TASK_TYPE " +
                     "LEFT JOIN TASK T on TASK_TYPE.ID = T.TASK_TYPE_ID " +
                     "GROUP BY " +
                     "TASK_TYPE.ID, " +
                     "HOURLY_RATE, " +
                     "TASK_TYPE.DESCRIPTION "
             )) {

            List<TaskType> taskTypes = new ArrayList<>();
            try (var rs = st.executeQuery()) {
                while (rs.next()) {
                    var taskType = new TaskType(
                            rs.getString("TT_DESCRIPTION"),
                            rs.getDouble("HOURLY_RATE"),
                            rs.getInt("TASK_COUNT"));
                    taskType.setId(rs.getLong("TASK_TYPE_ID"));
                    taskTypes.add(taskType);
                }
            }
            return taskTypes;
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to load all task types", ex);
        }
    }

    @Override
    public void update(TaskType taskType) throws DataAccessException {
        if (taskType.getId() == null) {
            throw new IllegalArgumentException("Task type has null ID");
        }
        try (var connection = dataSource.getConnection();
             var st = connection.prepareStatement(
                     "UPDATE TASK_TYPE SET HOURLY_RATE = ?, DESCRIPTION = ? WHERE ID = ?")) {
            st.setDouble(1, taskType.getHourlyRate());
            st.setString(2, taskType.getDescription());
            st.setLong(3, taskType.getId());
            int rowsUpdated = st.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DataAccessException("Failed to update non-existing task type: " + taskType);
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to update task type " + taskType, ex);
        }
    }

    @Override
    public void delete(TaskType taskType) throws DataAccessException {
        if (taskType.getId() == null) {
            throw new IllegalArgumentException("Task type has null ID: " + taskType);
        }
        try (var connection = dataSource.getConnection();
             var st = connection.prepareStatement("DELETE FROM TASK_TYPE WHERE ID = ?")) {
            st.setLong(1, taskType.getId());
            int rowsDeleted = st.executeUpdate();
            if (rowsDeleted == 0) {
                throw new DataAccessException("Failed to delete non-existing task type: " + taskType);
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to delete task type " + taskType, ex);
        }
    }

    @Override
    protected String getTableName() {
        return "TASK_TYPE";
    }

    @Override
    protected String getTableSchema() {
        return  "CREATE TABLE APP.TASK_TYPE (" +
                "ID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0)," +
                "HOURLY_RATE DECIMAL(15, 2) NOT NULL," +
                "DESCRIPTION VARCHAR(100) NOT NULL" +
                ")";
    }
}
