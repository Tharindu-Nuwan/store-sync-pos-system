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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import lk.ijse.dep11.app.db.CustomerDataAccess;
import lk.ijse.dep11.app.db.OrderDataAccess;
import lk.ijse.dep11.app.tm.Customer;

import java.io.IOException;
import java.sql.SQLException;

public class ManageCustomerFormController {
    public AnchorPane root;
    public JFXButton btnAddNew;
    public JFXTextField txtCustomerId;
    public JFXTextField txtCustomerName;
    public JFXTextField txtCustomerAddress;
    public JFXButton btnSave;
    public JFXButton btnDelete;
    public TableView<Customer> tblCustomers;
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

        tblCustomers.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblCustomers.getColumns().get(0).setStyle("-fx-alignment: center;");
        tblCustomers.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblCustomers.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));
        txtCustomerId.setEditable(false);
        btnDelete.setDisable(true);
        btnSave.setDefaultButton(true);
        btnAddNew.fire();

        try {
            tblCustomers.getItems().addAll(CustomerDataAccess.getAllCustomers());
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load customers, try again later!").show();
        }

        tblCustomers.getSelectionModel().selectedItemProperty().addListener((ov, prev, cur) -> {
            if (cur != null) {
                btnSave.setText("UPDATE");
                btnDelete.setDisable(false);
                txtCustomerId.setText(cur.getId());
                txtCustomerName.setText(cur.getName());
                txtCustomerAddress.setText(cur.getAddress());
            } else {
                btnSave.setText("SAVE");
                btnDelete.setDisable(true);
            }
        });
        Platform.runLater(txtCustomerName::requestFocus);
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

    public void btnAddNew_OnAction(ActionEvent actionEvent) throws IOException {
        for (TextField txtField : new TextField[]{txtCustomerId, txtCustomerName, txtCustomerAddress}) {
            txtField.clear();
        }
        tblCustomers.getSelectionModel().clearSelection();
        txtCustomerName.requestFocus();
        try {
            String lastCustomerId = CustomerDataAccess.getLastCustomerId();
            if (lastCustomerId == null) {
                txtCustomerId.setText("C001");
            } else {
                int newId = Integer.parseInt(lastCustomerId.substring(1)) + 1;
                txtCustomerId.setText(String.format("C%03d", newId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to establish the database connection, try again").show();
            navigateToHome(null);
        }
    }

    public void btnSave_OnAction(ActionEvent actionEvent) {
        if (!isDataValid()) return;
        Customer customer = new Customer(txtCustomerId.getText(),
                txtCustomerName.getText().strip(),
                txtCustomerAddress.getText().strip());

        try {
            if (btnSave.getText().equals("SAVE")) {
                CustomerDataAccess.saveCustomer(customer);
                tblCustomers.getItems().add(customer);
                new Alert(Alert.AlertType.INFORMATION, "Customer saved successfully!").show();
            } else {
                CustomerDataAccess.updateCustomer(customer);
                ObservableList<Customer> customerList = tblCustomers.getItems();
                Customer selectedCustomer = tblCustomers.getSelectionModel().getSelectedItem();
                customerList.set(customerList.indexOf(selectedCustomer), customer);
                tblCustomers.refresh();
                new Alert(Alert.AlertType.INFORMATION, "Customer updated successfully!").show();
            }
            btnAddNew.fire();
        } catch(SQLException e){
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to save the customer, try again!").show();
        }
    }

    public void btnDelete_OnAction(ActionEvent actionEvent) {
        try {
            if (OrderDataAccess.existOrderByCustomerId(txtCustomerId.getText())) {
                new Alert(Alert.AlertType.ERROR,
                        "Unable to delete this customer, already associated with an order").show();
            } else {
                CustomerDataAccess.deleteCustomer(txtCustomerId.getText());
                ObservableList<Customer> customersList = tblCustomers.getItems();
                Customer selectedCustomer = tblCustomers.getSelectionModel().getSelectedItem();
                customersList.remove(selectedCustomer);
                if (customersList.isEmpty()) btnAddNew.fire();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isDataValid() {
        String name = txtCustomerName.getText().strip();
        String address = txtCustomerAddress.getText().strip();

        if (!name.matches("^[A-Za-z ]{2,}$")) {
            txtCustomerName.requestFocus();
            txtCustomerName.selectAll();
            return false;
        } else if (address.length() < 3) {
            txtCustomerAddress.requestFocus();
            txtCustomerAddress.selectAll();
            return false;
        }
        return true;
    }
}
