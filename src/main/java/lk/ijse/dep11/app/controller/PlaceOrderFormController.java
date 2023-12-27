package lk.ijse.dep11.app.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.dep11.app.db.CustomerDataAccess;
import lk.ijse.dep11.app.db.ItemDataAccess;
import lk.ijse.dep11.app.db.OrderDataAccess;
import lk.ijse.dep11.app.tm.Customer;
import lk.ijse.dep11.app.tm.Item;
import lk.ijse.dep11.app.tm.OrderItem;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PlaceOrderFormController {
    public AnchorPane root;
    public JFXTextField txtCustomerName;
    public JFXTextField txtDescription;
    public JFXTextField txtQtyOnHand;
    public JFXButton btnSave;
    public TableView<OrderItem> tblOrderDetails;
    public JFXComboBox<Customer> cmbCustomerId;
    public JFXComboBox<Item> cmbItemCode;
    public JFXTextField txtUnitPrice;
    public JFXTextField txtQty;
    public Label lblId;
    public Label lblDate;
    public Label lblTotal;
    public JFXButton btnPlaceOrder;

    public void initialize() throws IOException {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), root);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        String[] cols = {"code", "description", "qty", "unitPrice", "total", "btnDelete"};
        for (int i = 0; i < cols.length; i++) {
            tblOrderDetails.getColumns().get(i)
                    .setCellValueFactory(new PropertyValueFactory<>(cols[i]));
        }

        lblDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        newOrder();

        cmbCustomerId.getSelectionModel().selectedItemProperty().addListener((ov, prev, cur)-> {
            enablePlaceOrderButton();
            if (cur != null) {
                txtCustomerName.setText(cur.getName());
                txtCustomerName.setDisable(false);
                txtCustomerName.setEditable(false);
            } else {
                txtCustomerName.clear();
                txtCustomerName.setDisable(true);
            }
        });

        cmbItemCode.getSelectionModel().selectedItemProperty().addListener((ov, prev, cur)-> {
            if (cur != null) {
                txtDescription.setText(cur.getDescription());
                txtQtyOnHand.setText(cur.getQty() + "");
                txtUnitPrice.setText(cur.getUnitPrice().toString());

                for (TextField textField : new TextField[]{txtDescription, txtQtyOnHand, txtUnitPrice}) {
                    textField.setDisable(false);
                    textField.setEditable(false);
                }
                txtQty.setDisable(cur.getQty() == 0);
            } else {
                for (TextField textField : new TextField[]{txtDescription, txtQtyOnHand, txtUnitPrice, txtQty}) {
                    textField.setDisable(true);
                    textField.clear();
                }
            }
        });
        txtQty.textProperty().addListener((ov, prev, cur)-> {
            Item selectedItem = cmbItemCode.getSelectionModel().getSelectedItem();
            btnSave.setDisable(true);
            if (cur.matches("\\d+")) {
                if (Integer.parseInt(cur) <= selectedItem.getQty() && Integer.parseInt(cur) > 0) {
                    btnSave.setDisable(false);
                }
            }
        });
    }

    private void enablePlaceOrderButton() {
        Customer selectedCustomer = cmbCustomerId.getSelectionModel().getSelectedItem();
        btnPlaceOrder.setDisable(!(selectedCustomer != null && !tblOrderDetails.getItems().isEmpty()));
    }

    private void newOrder() throws IOException {
        for (TextField textField : new TextField[]{txtCustomerName, txtDescription, txtQtyOnHand, txtUnitPrice, txtQty}) {
            textField.setDisable(true);
            textField.clear();
        }
        tblOrderDetails.getItems().clear();
        lblTotal.setText("00.00");
        btnSave.setDisable(true);
        btnPlaceOrder.setDisable(true);
        cmbCustomerId.getSelectionModel().clearSelection();
        cmbItemCode.getSelectionModel().clearSelection();
        try {
            cmbCustomerId.getItems().clear();
            cmbCustomerId.getItems().addAll(CustomerDataAccess.getAllCustomers());
            cmbItemCode.getItems().clear();
            cmbItemCode.getItems().addAll(ItemDataAccess.getAllItems());
            String lastOrderId = OrderDataAccess.getLastOrderId();
            if (lastOrderId == null) {
                lblId.setText("OD001");
            } else {
                int newOrderId = Integer.parseInt(lastOrderId.substring(2)) + 1;
                lblId.setText(String.format("OD%03d", newOrderId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to establish database connection, try later!")
                    .show();
            navigateToHome(null);
        }
        Platform.runLater(cmbCustomerId::requestFocus);
    }

    public void btnAdd_OnAction(ActionEvent actionEvent) {
        Item selectedItem = cmbItemCode.getSelectionModel().getSelectedItem();
        Optional<OrderItem> optOrderItem = tblOrderDetails.getItems().stream()
                .filter(item -> selectedItem.getCode().equals(item.getCode())).findFirst();

        if (optOrderItem.isEmpty()) {
            JFXButton btnDelete = new JFXButton("Delete");
            OrderItem newOrderItem = new OrderItem(selectedItem.getCode(), selectedItem.getDescription(),
                    Integer.parseInt(txtQty.getText()), selectedItem.getUnitPrice(), btnDelete);
            tblOrderDetails.getItems().add(newOrderItem);
            btnDelete.setOnAction(e -> {
                tblOrderDetails.getItems().remove(newOrderItem);
                selectedItem.setQty(selectedItem.getQty() + newOrderItem.getQty());
                calculateOrderTotal();
                enablePlaceOrderButton();
            });
            selectedItem.setQty(selectedItem.getQty() - newOrderItem.getQty());
        } else {
            OrderItem orderItem = optOrderItem.get();
            orderItem.setQty(orderItem.getQty() + Integer.parseInt(txtQty.getText()));
            tblOrderDetails.refresh();
            selectedItem.setQty(selectedItem.getQty() - Integer.parseInt(txtQty.getText()));
        }
        cmbItemCode.getSelectionModel().clearSelection();
        cmbItemCode.requestFocus();
        calculateOrderTotal();
        enablePlaceOrderButton();
    }

    private void calculateOrderTotal() {
        Optional<BigDecimal> orderTotal = tblOrderDetails.getItems().stream()
                .map(oi -> oi.getTotal())
                .reduce((prev, cur)-> prev.add(cur));
        lblTotal.setText(orderTotal.orElseGet(()-> BigDecimal.ZERO).setScale(2) + "");
    }


    public void btnPlaceOrder_OnAction(ActionEvent actionEvent) throws IOException {
        try {
            OrderDataAccess.saveOrder(lblId.getText().strip(),
                    Date.valueOf(lblDate.getText()),
                    cmbCustomerId.getValue().getId(),
                    tblOrderDetails.getItems());
            printBill();
            newOrder();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to save the order, try later!")
                    .show();
        }
    }

    private void printBill() {
        try {
            JasperDesign jasperDesign = JRXmlLoader
                    .load(getClass().getResourceAsStream("/print/storesync_pos-bill.jrxml"));
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

            Map<String, Object> reportParams = new HashMap<>();
            reportParams.put("id", lblId.getText().strip());
            reportParams.put("date", lblDate.getText());
            reportParams.put("customer-id", cmbCustomerId.getValue().getId());
            reportParams.put("customer-name", cmbCustomerId.getValue().getName());
            reportParams.put("total", lblTotal.getText());

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, reportParams,
                    new JRBeanCollectionDataSource(tblOrderDetails.getItems()));

            JasperViewer.viewReport(jasperPrint, false);
//            JasperPrintManager.printReport(jasperPrint, false);
        } catch (JRException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to print the bill!")
                    .show();
        }
    }

    public void navigateToHome(MouseEvent mouseEvent) throws IOException {
        Stage stage = new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardView.fxml"))));
        stage.centerOnScreen();
        stage.setTitle("Home View");
        stage.setResizable(false);
        stage.show();
        Stage orderForm = (Stage) root.getScene().getWindow();
        orderForm.close();
    }
}
