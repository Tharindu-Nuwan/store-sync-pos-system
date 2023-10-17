package lk.ijse.dep11.app.controller;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class DashboardViewController {
    public AnchorPane root;
    public ImageView imgCustomer;
    public ImageView imgItem;
    public ImageView imgOrder;
    public ImageView imgSearch;
    public Label lblTitle;
    public Label lblDescription;

    public void initialize() {
        applyFadeInTransition(1000, root);
    }
    public void imgCustomerOnMouseClicked(MouseEvent mouseEvent) {
        loadSceneOnClick("Manage Customers", "/view/ManageCustomerForm.fxml");
    }

    public void imgItemOnMouseClicked(MouseEvent mouseEvent) {
        loadSceneOnClick("Manage Items", "/view/ManageItemForm.fxml");
    }

    public void imgOrderOnMouseClicked(MouseEvent mouseEvent) {
        loadSceneOnClick("Place an Order", "/view/PlaceOrderForm.fxml");
    }

    public void imgSearchOnMouseClicked(MouseEvent mouseEvent) {
        loadSceneOnClick("Search Orders", "/view/SearchOrdersForm.fxml");
    }

    public void playMouseExitAnimation(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) mouseEvent.getSource();
            ScaleTransition scaleT = new ScaleTransition(Duration.millis(200), icon);
            scaleT.setToX(1);
            scaleT.setToY(1);
            scaleT.play();

            applyFadeInTransition(200, lblTitle);
            applyFadeInTransition(200, lblDescription);

            icon.setEffect(null);
            lblTitle.setText("WELCOME!");
            lblDescription.setText("Please select any of above operations to proceed.");
        }
    }

    public void playMouseEnterAnimation(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) mouseEvent.getSource();

            switch (icon.getId()) {
                case "imgCustomer":
                    lblTitle.setText("Manage Customers");
                    lblDescription.setText("Click to add, edit, delete, search or view customers");
                    break;
                case "imgItem":
                    lblTitle.setText("Manage Items");
                    lblDescription.setText("Click to add, edit, delete, search or view items");
                    break;
                case "imgOrder":
                    lblTitle.setText("Place Orders");
                    lblDescription.setText("Click here if you want to place a new order");
                    break;
                case "imgSearch":
                    lblTitle.setText("Search Orders");
                    lblDescription.setText("Click if you want to search orders");
                    break;
            }

            ScaleTransition scaleT = new ScaleTransition(Duration.millis(200), icon);
            scaleT.setToX(1.2);
            scaleT.setToY(1.2);
            scaleT.play();

            applyFadeInTransition(200, lblTitle);
            applyFadeInTransition(200, lblDescription);

            DropShadow glow = new DropShadow();
            glow.setColor(Color.CORNFLOWERBLUE);
            glow.setWidth(20);
            glow.setHeight(20);
            glow.setRadius(20);
            icon.setEffect(glow);
        }
    }

    private void loadSceneOnClick(String title, String fxmlPath) {
        try {
            Stage stage = new Stage();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource(fxmlPath))));
            stage.centerOnScreen();
            stage.setTitle(title);
            stage.show();
            Stage home = (Stage) root.getScene().getWindow();
            home.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void applyFadeInTransition(double millis, Node node) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(millis), node);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }
}
