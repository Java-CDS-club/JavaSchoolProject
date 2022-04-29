package cz.muni.fi.pv168.project.data;

import cz.muni.fi.pv168.project.model.Customer;
import org.apache.derby.jdbc.EmbeddedDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

final class CustomerDaoTest {

    private static EmbeddedDataSource dataSource;
    private CustomerDao customerDao;

    @BeforeAll
    static void initTestDataSource(){
        dataSource = new EmbeddedDataSource();
        dataSource.setDatabaseName("memory:project-test");
        dataSource.setCreateDatabase("create");
    }

    @BeforeEach
    void createCustomerDao() throws SQLException {

        customerDao = new CustomerDao(dataSource);
        try (var connection = dataSource.getConnection(); var st = connection.createStatement()) {
            st.executeUpdate("DELETE FROM APP.CUSTOMER");
        }
    }

    @AfterEach
    void customerDaoCleanUp() {
        customerDao.dropTable();
    }

    @Test
    void createCustomer() {
        var google = new Customer("Google", "", "", "");
        customerDao.create(google);

        assertThat(google.getId())
                .isNotNull();
        assertThat(customerDao.findAll())
                .usingFieldByFieldElementComparator()
                .containsExactly(google);
    }

    @Test
    void findAllEmpty() {
        assertThat(customerDao.findAll())
                .isEmpty();
    }

    @Test
    void findAll() {
        var grace = new Customer("Google", "", "", "");
        var frederic = new Customer("Facebook", "", "", "");
        var james = new Customer("Amazon", "", "", "");
        var joshua = new Customer("Tesla", "", "", "");
        var martin = new Customer("Hasbro", "", "", "");

        customerDao.create(grace);
        customerDao.create(frederic);
        customerDao.create(james);
        customerDao.create(joshua);
        customerDao.create(martin);

        assertThat(customerDao.findAll())
                .usingFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(grace, frederic, james, joshua, martin);
    }

    @Test
    void delete() {
        var john1 = new Customer("Google", "", "", "");
        var john2 = new Customer("Google", "", "", "");

        customerDao.create(john1);
        customerDao.create(john2);

        customerDao.delete(john1);

        assertThat(customerDao.findAll())
                .usingFieldByFieldElementComparator()
                .containsExactly(john2);
    }

    @Test
    void deleteNonExisting() {
        var bill = new Customer("Google", "", "", "");
        bill.setId(123L);

        assertThatExceptionOfType(DataAccessException.class)
                .isThrownBy(() -> customerDao.delete(bill))
                .withMessage("Failed to delete non-existing customer: %s", bill);
    }

    @Test
    void update() {
        var john1 = new Customer("Google", "", "", "");
        var john2 = new Customer("Google", "", "", "");

        customerDao.create(john1);
        customerDao.create(john2);

        john1.setName("Facebook");

        customerDao.update(john1);

        assertThat(findById(john1.getId()))
                .isEqualToComparingFieldByField(john1);
        assertThat(findById(john2.getId()))
                .isEqualToComparingFieldByField(john2);
    }

    @Test
    void updateWithNullId() {
        var bill = new Customer("Google", "", "", "");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customerDao.update(bill))
                .withMessage("Customer has null ID");
    }

    @Test
    void updateNonExisting() {
        var bill = new Customer("Google", "", "", "");
        bill.setId(123L);

        assertThatExceptionOfType(DataAccessException.class)
                .isThrownBy(() -> customerDao.update(bill))
                .withMessage("Failed to update non-existing customer: %s", bill);
    }

    private Customer findById(long id) {
        return customerDao.findAll().stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No customer with id " + id + " found"));
    }
}