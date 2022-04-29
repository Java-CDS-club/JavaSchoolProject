package cz.muni.fi.pv168.project.data;

import javax.sql.DataSource;
import java.sql.SQLException;

public abstract class CommonDao {
    private static final String schemaName = "APP";

    private final DataSource dataSource;

    public CommonDao(DataSource dataSource) {
        this.dataSource = dataSource;
        initTable();
    }

    public void dropTable() {
        try (var connection = getDataSource().getConnection();
             var st = connection.createStatement()) {

            st.executeUpdate("DROP TABLE " + schemaName + "." + getTableName());
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to drop " + getTableName() + " table", ex);
        }
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

    protected abstract String getTableName();

    protected abstract String getTableSchema();

    private void initTable() {
        if (!tableExists()) {
            createTable();
        }
    }

    private boolean tableExists() {
        try (var connection = dataSource.getConnection();
             var rs = connection.getMetaData().getTables(null, schemaName, getTableName(), null)) {
            return rs.next();
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to detect if the table " + schemaName + "." + getTableName() + " exist", ex);
        }
    }

    private void createTable() {
        try (var connection = dataSource.getConnection();
             var st = connection.createStatement()) {

            st.executeUpdate(getTableSchema());
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to create CUSTOMER table", ex);
        }
    }
}
