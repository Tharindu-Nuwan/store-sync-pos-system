package lk.ijse.dep11.app.controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.dep11.app.db.OrderDataAccess;
import lk.ijse.dep11.app.tm.Order;

import java.io.IOException;
import java.sql.SQLException;

public class SearchOrdersFormController {
    public AnchorPane root;
    public TextField txtSearch;
    public TableView<Order> tblOrders;

    public void initialize() throws IOException {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), root);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        String[] colNames = {"orderId", "orderDate", "customerId", "customerName", "orderTotal"};
        for (int i = 0; i < colNames.length; i++) {
            tblOrders.getColumns().get(i)
                    .setCellValueFactory(new PropertyValueFactory<>(colNames[i]));
        }
        try {
            tblOrders.getItems().addAll(OrderDataAccess.findOrders(""));
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load orders, try later!").show();
            navigateToHome(null);
        }

        txtSearch.textProperty().addListener((ov, prev, cur)-> {
            tblOrders.getItems().clear();
            try {
                tblOrders.getItems().addAll(OrderDataAccess.findOrders(cur));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void navigateToHome(MouseEvent mouseEvent) throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardView.fxml"))));
        stage.centerOnScreen();
        stage.setTitle("Home View");
        stage.setResizable(false);
        stage.show();
        Stage searchForm = (Stage) root.getScene().getWindow();
        searchForm.close();
    }
}
