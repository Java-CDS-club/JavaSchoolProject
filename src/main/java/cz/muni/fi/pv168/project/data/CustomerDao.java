package cz.muni.fi.pv168.project.data;

import cz.muni.fi.pv168.project.model.Customer;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;


public final class CustomerDao extends CommonDao implements DataAccessObject<Customer> {

    public CustomerDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void create(Customer customer) throws DataAccessException {
        try (var connection = getDataSource().getConnection();
             var st = connection.prepareStatement(
                     "INSERT INTO CUSTOMER (" +
                             "NAME, " +
                             "DIC, " +
                             "PHONE, " +
                             "MAIL) " +
                             "VALUES (?, ?, ?, ?)",
                     RETURN_GENERATED_KEYS)) {
            st.setString(1, customer.getName());
            st.setString(2, customer.getDic());
            st.setString(3, customer.getPhone());
            st.setString(4, customer.getMail());
            st.executeUpdate();
            try (var rs = st.getGeneratedKeys()) {
                if (rs.getMetaData().getColumnCount() != 1) {
                    throw new DataAccessException("Failed to fetch generated key: compound key returned for customer: " + customer);
                }
                if (rs.next()) {
                    customer.setId(rs.getLong(1));
                } else {
                    throw new DataAccessException("Failed to fetch generated key: no key returned for customer: " + customer);
                }
                if (rs.next()) {
                    throw new DataAccessException("Failed to fetch generated key: multiple keys returned for customer: " + customer);
                }
            }

        } catch (SQLException ex) {
            throw new DataAccessException("Failed to store department " + customer, ex);
        }
    }

    @Override
    public Collection<Customer> findAll() throws DataAccessException {
        try (var connection = getDataSource().getConnection();
             var st = connection.prepareStatement("SELECT " +
                     "CUSTOMER.ID AS CUSTOMER_ID, " +
                     "NAME, " +
                     "DIC, " +
                     "PHONE, " +
                     "MAIL, " +
                     "count(T.ID) AS TASK_COUNT " +
                     "FROM CUSTOMER " +
                     "LEFT JOIN TASK T on CUSTOMER.ID = T.CUSTOMER_ID " +
                     "GROUP BY " +
                     "CUSTOMER.ID, " +
                     "NAME, " +
                     "DIC, " +
                     "PHONE, " +
                     "MAIL "
             )) {

            List<Customer> customers = new ArrayList<>();
            try (var rs = st.executeQuery()) {
                while (rs.next()) {
                    var customer = new Customer(
                            rs.getString("NAME"),
                            rs.getString("DIC"),
                            rs.getString("PHONE"),
                            rs.getString("MAIL"),
                            rs.getInt("TASK_COUNT")
                    );
                    customer.setId(rs.getLong("CUSTOMER_ID"));
                    customers.add(customer);
                }
            }
            return customers;
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to load all customers", ex);
        }
    }

    @Override
    public void update(Customer customer) throws DataAccessException {
        if (customer.getId() == null) {
            throw new IllegalArgumentException("Customer has null ID");
        }
        try (var connection = getDataSource().getConnection();
             var st = connection.prepareStatement(
                     "UPDATE CUSTOMER SET " +
                             "NAME = ?, " +
                             "DIC = ?, " +
                             "PHONE = ?, " +
                             "MAIL = ? " +
                             "WHERE ID = ?"
             )) {
            st.setString(1, customer.getName());
            st.setString(2, customer.getDic());
            st.setString(3, customer.getPhone());
            st.setString(4, customer.getMail());
            st.setLong(5, customer.getId());
            int rowsUpdated = st.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DataAccessException("Failed to update non-existing customer: " + customer);
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to update customer " + customer, ex);
        }
    }

    @Override
    public void delete(Customer customer) throws DataAccessException {
        if (customer.getId() == null) {
            throw new IllegalArgumentException("Employee has null ID: " + customer);
        }
        try (var connection = getDataSource().getConnection();
             var st = connection.prepareStatement("DELETE FROM CUSTOMER WHERE ID = ?")) {
            st.setLong(1, customer.getId());
            int rowsDeleted = st.executeUpdate();
            if (rowsDeleted == 0) {
                throw new DataAccessException("Failed to delete non-existing customer: " + customer);
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to delete customer " + customer, ex);
        }
    }

    @Override
    protected String getTableName() {
        return "CUSTOMER";
    }

    @Override
    protected String getTableSchema() {
        return "CREATE TABLE APP.CUSTOMER (" +
                "ID BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0)," +
                "NAME VARCHAR(100) NOT NULL," +
                "DIC VARCHAR(100) NOT NULL," +
                "PHONE VARCHAR(100) NOT NULL," +
                "MAIL VARCHAR(100) NOT NULL" +
                ")";
    }
}
