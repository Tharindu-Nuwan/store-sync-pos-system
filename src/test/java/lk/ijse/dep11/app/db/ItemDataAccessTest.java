package lk.ijse.dep11.app.db;

import lk.ijse.dep11.app.tm.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ItemDataAccessTest {

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
    void saveItem() {
        assertDoesNotThrow(()-> {
            ItemDataAccess.saveItem(new Item("1234", "Key Board", 10, new BigDecimal("12500.00")));
            ItemDataAccess.saveItem(new Item("4321", "USB Mouse", 20, new BigDecimal("1850.00")));
        });
        assertThrows(SQLException.class, ()-> {
            ItemDataAccess.saveItem(new Item("1234", "Key Board", 10, new BigDecimal("12500.00")));
        });
    }

    @Test
    void updateItem() throws SQLException {
        ItemDataAccess.saveItem(new Item("1234", "Key Board", 10, new BigDecimal("12500.00")));
        assertDoesNotThrow(()-> {
            ItemDataAccess.updateItem(new Item("1234", "USB Key Board", 8, new BigDecimal("12800.50")));
        });
    }

    @Test
    void deleteItem() throws SQLException {
        ItemDataAccess.saveItem(new Item("1234", "Key Board", 10, new BigDecimal("12500.00")));
        int count = ItemDataAccess.getAllItems().size();
        assertDoesNotThrow(()-> ItemDataAccess.deleteItem("1234"));
        assertEquals(count - 1, ItemDataAccess.getAllItems().size());
    }

    @Test
    void getAllItems() throws SQLException {
        ItemDataAccess.saveItem(new Item("1234", "Key Board", 10, new BigDecimal("12500.00")));
        ItemDataAccess.saveItem(new Item("4321", "USB Mouse", 20, new BigDecimal("1850.00")));
        assertTrue(ItemDataAccess.getAllItems().size() >= 2);
    }

    @Test
    void existItem() throws SQLException {
        assertDoesNotThrow(()-> assertFalse(ItemDataAccess.existItem("Crazy Item")));
        ItemDataAccess.saveItem(new Item("1234", "Key Board", 10, new BigDecimal("12500.00")));
        assertTrue(ItemDataAccess.existItem("1234"));

    }
}