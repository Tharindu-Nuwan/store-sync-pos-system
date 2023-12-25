package lk.ijse.dep11.app.db;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SingleConnectionDataSource {
    private static SingleConnectionDataSource instance;
    private final Connection connection;
    private SingleConnectionDataSource() {
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getResourceAsStream("/application.properties"));
            String username = properties.getProperty("app.datasource.username");
            String password = properties.getProperty("app.datasource.password");
            String url = properties.getProperty("app.datasource.url");
            connection = DriverManager.getConnection(url, username, password);
            generateSchema();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateSchema() {
        URL url = getClass().getResource("/schema.sql");
        try {
            Path path = Paths.get(url.toURI());
            String dbScript = Files.readAllLines(path).stream()
                    .reduce((prevLine, currentLine) -> prevLine + currentLine).get();
            connection.createStatement().execute(dbScript);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static SingleConnectionDataSource getInstance() {
        return (instance == null) ? (instance = new SingleConnectionDataSource()) : instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
