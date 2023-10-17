package lk.ijse.dep11.app.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ManageItemFormController {
    public AnchorPane root;
    public JFXButton btnAddNew;
    public JFXTextField txtCode;
    public JFXTextField txtDescription;
    public JFXTextField txtQtyOnHand;
    public JFXButton btnSave;
    public JFXButton btnDelete;
    public TableView tblItems;
    public JFXTextField txtUnitPrice;

    public void navigateToHome(MouseEvent mouseEvent) throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardView.fxml"))));
        stage.centerOnScreen();
        stage.setTitle("Home View");
        stage.setResizable(false);
        stage.show();
        Stage itemForm = (Stage) root.getScene().getWindow();
        itemForm.close();
    }

    public void btnAddNew_OnAction(ActionEvent actionEvent) {
    }

    public void btnSave_OnAction(ActionEvent actionEvent) {
    }

    public void btnDelete_OnAction(ActionEvent actionEvent) {
    }
}
