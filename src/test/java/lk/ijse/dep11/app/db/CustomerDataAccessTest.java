package lk.ijse.dep11.app.db;

import lk.ijse.dep11.app.tm.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerDataAccessTest {
    @BeforeEach
    void setUp() throws SQLException {
        SingleConnectionDataSource.getInstance().getConnection().setAutoCommit(false);
    }

    @AfterEach
    void tearDown() throws SQLException {
        SingleConnectionDataSource.getInstance().getConnection().rollback();
        SingleConnectionDataSource.getInstance().getConnection().setAutoCommit(true);
    }

    @Test
    void sqlSyntax() {
        assertDoesNotThrow(()-> Class.forName("lk.ijse.dep11.app.db.CustomerDataAccess"));
    }

    @Test
    void saveCustomer() {
        assertDoesNotThrow(()-> {
            CustomerDataAccess.saveCustomer(new Customer("C001", "Kasun", "Galle"));
            CustomerDataAccess.saveCustomer(new Customer("C002", "Nuwan", "Colombo"));
        });
        assertThrows(SQLException.class, ()-> {
            CustomerDataAccess.saveCustomer(new Customer("C001", "Kasun", "Galle"));
        });
    }

    @Test
    void updateCustomer() throws SQLException {
        CustomerDataAccess.saveCustomer(new Customer("C001", "Kasun", "Galle"));
        assertDoesNotThrow(()-> {
            CustomerDataAccess.updateCustomer(new Customer("C001", "Ruwan", "Kaluthara"));
        });
    }

    @Test
    void deleteCustomer() throws SQLException {
        CustomerDataAccess.saveCustomer(new Customer("C001", "Kasun", "Galle"));
        int numOfCustomers = CustomerDataAccess.getAllCustomers().size();
        assertDoesNotThrow(()-> {
            CustomerDataAccess.deleteCustomer("C001");
            assertEquals(numOfCustomers - 1, CustomerDataAccess.getAllCustomers().size());
        });
    }

    @Test
    void getAllCustomers() throws SQLException {
        CustomerDataAccess.saveCustomer(new Customer("C001", "Kasun", "Galle"));
        CustomerDataAccess.saveCustomer(new Customer("C002", "Nuwan", "Colombo"));
        assertDoesNotThrow(()-> {
            List<Customer> customerList = CustomerDataAccess.getAllCustomers();
            assertTrue(customerList.size() >= 2);
        });
    }

    @Test
    void getLastCustomerId() throws SQLException {
        String lastCustomerId = CustomerDataAccess.getLastCustomerId();
        if (CustomerDataAccess.getAllCustomers().isEmpty()) assertNull(lastCustomerId);
        else {
            CustomerDataAccess.saveCustomer(new Customer("C001", "Kasun", "Galle"));
            lastCustomerId = CustomerDataAccess.getLastCustomerId();
            assertNotNull(lastCustomerId);
        }
    }
}