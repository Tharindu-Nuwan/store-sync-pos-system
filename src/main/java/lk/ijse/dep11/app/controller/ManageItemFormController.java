package lk.ijse.dep11.app.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import lk.ijse.dep11.app.db.ItemDataAccess;
import lk.ijse.dep11.app.db.OrderDataAccess;
import lk.ijse.dep11.app.tm.Item;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

public class ManageItemFormController {
    public AnchorPane root;
    public JFXButton btnAddNew;
    public JFXTextField txtCode;
    public JFXTextField txtDescription;
    public JFXTextField txtQtyOnHand;
    public JFXButton btnSave;
    public JFXButton btnDelete;
    public TableView<Item> tblItems;
    public JFXTextField txtUnitPrice;

    public void initialize() {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), root);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        tblItems.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("code"));
        tblItems.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("description"));
        tblItems.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("qty"));
        tblItems.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        btnDelete.setDisable(true);
        btnSave.setDefaultButton(true);

        try {
            tblItems.getItems().addAll(ItemDataAccess.getAllItems());
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Failed to load items, try again later!").show();
        }
        Platform.runLater(txtCode::requestFocus);

        tblItems.getSelectionModel().selectedItemProperty().addListener((ov, prev, cur) -> {
            if (cur == null) {
                btnSave.setText("SAVE");
                btnDelete.setDisable(true);
                txtCode.setDisable(false);
            } else {
                btnSave.setText("UPDATE");
                btnDelete.setDisable(false);
                txtCode.setText(cur.getCode());
                txtCode.setDisable(true);
                txtDescription.setText(cur.getDescription());
                txtQtyOnHand.setText(cur.getQty() + "");
                txtUnitPrice.setText(cur.getUnitPrice() + "");
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
        Stage itemForm = (Stage) root.getScene().getWindow();
        itemForm.close();
    }

    public void btnAddNew_OnAction(ActionEvent actionEvent) {
        for (TextField textField : new TextField[]{txtCode, txtDescription, txtUnitPrice, txtQtyOnHand}) {
            textField.clear();
        }
        txtCode.requestFocus();
        tblItems.getSelectionModel().clearSelection();
    }

    public void btnSave_OnAction(ActionEvent actionEvent) {
        if (!isDataValid()) return;
        try {
            Item item = new Item(txtCode.getText().strip(), txtDescription.getText().strip(),
                    Integer.parseInt(txtQtyOnHand.getText()), new BigDecimal(txtUnitPrice.getText()).setScale(2));

            if (btnSave.getText().equals("SAVE")) {
                if (ItemDataAccess.existItem(item.getCode())) {
                    new Alert(Alert.AlertType.ERROR, "Item code already exist!").show();
                    txtCode.requestFocus();
                    txtCode.selectAll();
                    return;
                }
                ItemDataAccess.saveItem(item);
                tblItems.getItems().add(item);
                new Alert(Alert.AlertType.INFORMATION, "Item saved successfully!").show();
            }else {
                ItemDataAccess.updateItem(item);
                ObservableList<Item> itemsList = tblItems.getItems();
                Item selectedItem = tblItems.getSelectionModel().getSelectedItem();
                itemsList.set(itemsList.indexOf(selectedItem), item);
                new Alert(Alert.AlertType.INFORMATION, "Item updated successfully!").show();
                tblItems.refresh();
            }
            btnAddNew.fire();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to save the item, try again!").show();
        }
    }

    public void btnDelete_OnAction(ActionEvent actionEvent) {
        Item selectedItem = tblItems.getSelectionModel().getSelectedItem();
        try {
            if (OrderDataAccess.existOrderByItemCode(selectedItem.getCode())) {
                new Alert(Alert.AlertType.ERROR, "Failed to delete, the item already associated with an order!")
                        .show();
            } else {
                ItemDataAccess.deleteItem(selectedItem.getCode());
                tblItems.getItems().remove(selectedItem);
                if (tblItems.getItems().isEmpty()) btnAddNew.fire();
                new Alert(Alert.AlertType.INFORMATION, "Item deleted successfully!").show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to delete the item, try again!").show();
        }
    }

    private boolean isDataValid() {
        String code = txtCode.getText().strip();
        String description = txtDescription.getText().strip();
        String qty = txtQtyOnHand.getText().strip();
        String unitPrice = txtUnitPrice.getText().strip();

        if (!code.matches("\\d{4,}")) {
            txtCode.requestFocus();
            txtCode.selectAll();
            return false;
        } else if (!description.matches("^[A-Za-z0-9 ]{4,}$")) {
            txtDescription.requestFocus();
            txtDescription.selectAll();
            return false;
        } else if (!qty.matches("\\d+") || Integer.parseInt(qty) <= 0) {
            txtQtyOnHand.requestFocus();
            txtQtyOnHand.selectAll();
            return false;
        } else if (!isPrice(unitPrice)) {
            txtUnitPrice.requestFocus();
            txtUnitPrice.selectAll();
            return false;
        }
        return true;
    }

    private boolean isPrice(String unitPrice) {
        try {
            double price = Double.parseDouble(unitPrice);
            return price > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
