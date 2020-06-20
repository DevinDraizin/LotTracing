package ShipItems;

import Commons.UIComponents.popUps.Notifier;
import NewOrder.purchaseOrder;
import com.jfoenix.controls.*;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.jensd.fx.glyphs.octicons.OctIcon;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Main;
import screensframework.ControlledScreen;
import screensframework.ScreensController;



/*
The primary goal of the ship items page is to facilitate shipping ordered parts from
one or more purchase orders. This means creating a shipment object containing the
information in Fulfillment_Table and linking it to all of its ordered parts
(which each contain a reference back to the purchase order it belongs to) in the
fulfillment_Ordered_Part_Bridge. Additionally, we have to update the purchase orders
'complete' field to check if the current shipment will fulfill the entire PO and if so
we need to update the PO in the database and the UI to complete.
*/
public class shipItemsController implements ControlledScreen
{


    @FXML
    JFXTextField searchField, ShipIDField;

    @FXML
    JFXTabPane tabLayout;

    @FXML
    Label selectedPOLabel;

    @FXML
    JFXTreeTableView<purchaseOrder> POTable;

    @FXML
    JFXTreeTableView<orderedPartShipment> partsTable;

    ScreensController myController;



    private ObservableList<purchaseOrder> POTableData;
    private ObservableList<orderedPartShipment> orderedPartShipmentsData = FXCollections.observableArrayList();

    @Override
    public void setScreenParent(ScreensController screenPage) {
        myController = screenPage;
    }

    @Override
    public void update()
    {
        tabLayout.getSelectionModel().select(0);
        initPOTable();
    }

    public void initialize()
    {
        initPOTable();
        initPartTable();
    }

    private void initializePOTableData()
    {
        POTableData = FXCollections.observableArrayList();
        DAL.newOrderDAO.getPurchaseOrderList(POTableData);

        //Remove all complete purchase orders
        POTableData.removeIf(elem -> elem.complete);
    }

    private void setPartsTable(String PO)
    {
        DAL.ShipItemsDAO.getOrderedPartShipments(PO,orderedPartShipmentsData);
    }


    private void updateShipQty(String newVal)
    {
        int qty = Commons.utilities.getPositiveInt(newVal);

        if(qty == -1)
        {
            Notifier.getWarningNotification("Invalid Input", "Quantity must be a positive number");
            return;
        }

        partsTable.getSelectionModel().getSelectedItem().getValue().amountToShip.set(qty);
        partsTable.refresh();

        Notifier.getSuccessNotification("Success!", "Updated Shipping Table");
    }

    private void initPartTable()
    {
        partsTable.setEditable(true);

        JFXTreeTableColumn<orderedPartShipment,String> PartNumberCol = new JFXTreeTableColumn<>("Part Number");
        PartNumberCol.setCellValueFactory(param -> param.getValue().getValue().orderedPart.partNumber);

        JFXTreeTableColumn<orderedPartShipment,String> QtyOrderedCol = new JFXTreeTableColumn<>("Quantity Ordered");
        QtyOrderedCol.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue().getValue().orderedPart.QtyOrdered.get())));

        JFXTreeTableColumn<orderedPartShipment,String> QtyToShipCol = new JFXTreeTableColumn<>("Amount To Ship");
        QtyToShipCol.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue().getValue().amountToShip.get())));

        //These lines make it so you can update the table by double clicking
        QtyToShipCol.setCellFactory((TreeTableColumn<orderedPartShipment, String> param) ->
                new GenericEditableTreeTableCell<>(new TextFieldEditorBuilder()));

        QtyToShipCol.setOnEditCommit((TreeTableColumn.CellEditEvent<orderedPartShipment, String> t) -> updateShipQty(t.getNewValue()));



        if(!POTableData.isEmpty())
        {
            setPartsTable(POTableData.get(0).PONumber.get());
        }
        else
        {
            orderedPartShipmentsData = FXCollections.observableArrayList();
        }

        final TreeItem<orderedPartShipment> root = new RecursiveTreeItem<>(orderedPartShipmentsData, RecursiveTreeObject::getChildren);
        partsTable.setRoot(root);
        partsTable.setShowRoot(false);
        partsTable.getColumns().setAll(PartNumberCol,QtyOrderedCol, QtyToShipCol);


        ContextMenu menu = new ContextMenu();
        MenuItem shipItem = new MenuItem("    Add To Shipment    ");

        menu.getItems().addAll(shipItem);
        partsTable.setContextMenu(menu);

        shipItem.setOnAction(e -> setQty());
    }

    //Responsible for creating small window to get quantity
    //from the user to update the quantity to ship field
    //in the parts table. This includes all the data val
    //and logic for updating the table
    private void setQty()
    {
        Stage window = new Stage();
        window.setHeight(300);
        window.setWidth(500);
        window.setTitle("Quantity");

        VBox mainLayout = new VBox(60);
        mainLayout.getStylesheets().add("CSS/buyers.css");
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(10,10,10,10));

        Label header = new Label("Quantity to ship:");
        header.setStyle("-fx-text-fill: #052b7e; -fx-font-size: 22");

        JFXTextField inputField = new JFXTextField();
        inputField.setPromptText("Quantity");
        inputField.setLabelFloat(true);
        inputField.setMaxWidth(200);


        HBox buttonBox = new HBox(28);
        buttonBox.setAlignment(Pos.CENTER);

        JFXButton submitButton = Commons.staticLookupCommons.createButton("Submit", OctIcon.CHECK);
        JFXButton cancelButton = Commons.staticLookupCommons.createButton("Cancel", OctIcon.CIRCLE_SLASH);

        buttonBox.getChildren().addAll(submitButton,cancelButton);

        mainLayout.getChildren().addAll(header,inputField,buttonBox);

        cancelButton.setOnAction(e -> window.close());

        //Check that qty is >= 0 and is a real num then update the table
        //else, display error and dont close window
        submitButton.setOnAction(e -> {

            int qty = Commons.utilities.getPositiveInt(inputField.getText());

            if(qty == -1)
            {
                Notifier.getWarningNotification("Invalid Input", "Quantity must be a positive number");
                inputField.clear();
            }
            else
            {
                //Update table then refresh to display the change
                partsTable.getSelectionModel().getSelectedItem().getValue().amountToShip.set(qty);
                partsTable.refresh();
                window.close();
                Notifier.getSuccessNotification("Success!", "Updated Shipping Table");
            }
        });

        Scene scene = new Scene(mainLayout);
        window.setScene(scene);
        inputField.requestFocus();
        window.show();

    }

    private void initPOTable()
    {
        JFXTreeTableColumn<purchaseOrder,String> PONumberCol = new JFXTreeTableColumn<>("Purchase Order #");
        PONumberCol.setCellValueFactory(param -> param.getValue().getValue().PONumber);

        JFXTreeTableColumn<purchaseOrder,String> SONumberCol = new JFXTreeTableColumn<>("Sales Order #");
        SONumberCol.setCellValueFactory(param -> param.getValue().getValue().SONumber);

        initializePOTableData();

        final TreeItem<purchaseOrder> root = new RecursiveTreeItem<>(POTableData, RecursiveTreeObject::getChildren);
        POTable.setRoot(root);
        POTable.setShowRoot(false);
        POTable.getColumns().setAll(PONumberCol,SONumberCol);

        POTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue != null)
            {
                selectedPOLabel.setText("Selected Purchase Order:\t" + newValue.getValue().PONumber.get());
                setPartsTable(newValue.getValue().PONumber.get());
            }

        });

        POTable.getSelectionModel().select(0);

    }

    public void previousPage()
    {
        tabLayout.getSelectionModel().select(0);
    }

    public void nextPage()
    {
        tabLayout.getSelectionModel().select(1);
    }

    public void generateShipID()
    {
        ShipIDField.setText(Commons.utilities.generateUniqueShipID());
    }



    public void goBack()
    {
        myController.setScreen(Main.screen1ID);
    }








    //TableView with colored rows

    /*
    private void initTable()
    {
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        table.setRowFactory(new Callback<TreeTableView<purchaseOrder>, TreeTableRow<purchaseOrder>>() {
            @Override
            public TreeTableRow<purchaseOrder> call(TreeTableView<purchaseOrder> tableView) {
                final TreeTableRow<purchaseOrder> row = new TreeTableRow<purchaseOrder>() {
                    @Override
                    protected void updateItem(purchaseOrder purchaseOrder, boolean empty){

                        super.updateItem(purchaseOrder, empty);

                        if(purchaseOrder != null)
                        {
                            if(!purchaseOrder.complete)
                            {
                                setStyle("-fx-background-color: #ffabb7;");
                            }
                            else
                            {
                                setStyle("-fx-background-color: #FFFFFF");
                            }
                        }

                    }
                };

                return row;
            }
        });


        Commons.UIComponents.tableViews.initPurchaseOrderTable(table,purchaseOrderList);
    }

     */

}
