package lk.ijse.dep11.app.db;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SingleConnectionDataSourceTest {
    @Test
    void getConnection() {
        assertDoesNotThrow(SingleConnectionDataSource.getInstance()::getConnection);
    }
    @Test
    void getInstance() {
        SingleConnectionDataSource instance1 = SingleConnectionDataSource.getInstance();
        SingleConnectionDataSource instance2 = SingleConnectionDataSource.getInstance();
        SingleConnectionDataSource instance3 = SingleConnectionDataSource.getInstance();
        assertEquals(instance1, instance2);
        assertEquals(instance2, instance3);
    }

    @Test
    void generateSchema() {
        assertDoesNotThrow(()-> {
            SingleConnectionDataSource.getInstance().getConnection().createStatement()
                    .executeQuery("SELECT * FROM customer, item, \"order\", order_item");
        });
    }
}
