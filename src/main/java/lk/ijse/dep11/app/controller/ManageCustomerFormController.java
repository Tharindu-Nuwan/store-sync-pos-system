package lk.ijse.dep11.app.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.IOException;

public class ManageCustomerFormController {
    public AnchorPane root;
    public JFXButton btnAddNew;
    public JFXTextField txtCustomerId;
    public JFXTextField txtCustomerName;
    public JFXTextField txtCustomerAddress;
    public JFXButton btnSave;
    public JFXButton btnDelete;
    public TableView tblCustomers;
    public TableColumn colId;
    public TableColumn colName;
    public TableColumn colAddress;

    public void initialize() {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), root);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        colId.prefWidthProperty().bind(tblCustomers.widthProperty().multiply(0.15));
        colName.prefWidthProperty().bind(tblCustomers.widthProperty().multiply(0.35));
        colAddress.prefWidthProperty().bind(tblCustomers.widthProperty().multiply(0.50));
    }
    public void navigateToHome(MouseEvent mouseEvent) throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardView.fxml"))));
        stage.centerOnScreen();
        stage.setTitle("Home View");
        stage.setResizable(false);
        stage.show();
        Stage customerForm = (Stage) root.getScene().getWindow();
        customerForm.close();
    }

    public void btnAddNew_OnAction(ActionEvent actionEvent) {
    }

    public void btnSave_OnAction(ActionEvent actionEvent) {
    }

    public void btnDelete_OnAction(ActionEvent actionEvent) {
    }
}
