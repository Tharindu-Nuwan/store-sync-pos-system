package lk.ijse.dep11.app.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardViewController {
    public AnchorPane root;
    public ImageView imgCustomer;
    public ImageView imgItem;
    public ImageView imgOrder;
    public ImageView imgSearch;
    public Label lblTitle;
    public Label lblDescription;

    public void imgCustomerOnMouseClicked(MouseEvent mouseEvent) throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/ManageCustomerForm.fxml"))));
        stage.centerOnScreen();
        stage.setTitle("Manage Customers");
        stage.show();
        Stage home = (Stage) root.getScene().getWindow();
        home.close();
    }

    public void imgItemOnMouseClicked(MouseEvent mouseEvent) {
    }

    public void imgOrderOnMouseClicked(MouseEvent mouseEvent) {
    }

    public void imgSearchOnMouseClicked(MouseEvent mouseEvent) {
    }

    public void playMouseEnterAnimation(MouseEvent mouseEvent) {
    }

    public void playMouseExitAnimation(MouseEvent mouseEvent) {
    }
}
