package NewOrder;


import Commons.staticLookupCommons;
import DAL.newOrderDAO;
import Products.product;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.jensd.fx.glyphs.octicons.OctIcon;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.Main;
import screensframework.ControlledScreen;
import screensframework.ScreensController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;


public class newOrderController implements ControlledScreen
{
    ScreensController myController;



    @FXML
    JFXComboBox<String> companyName, buyerName;

    @FXML
    JFXDatePicker orderDatePicker, dueDatePicker;

    @FXML
    JFXTextField POField, SOField, searchField;

    @FXML
    JFXTreeTableView<orderedPart> addedTable;

    @FXML
    JFXTreeTableView<product> productTable;

    @FXML
    JFXTextArea memoField;

    @FXML
    JFXTabPane tabPane;

    private static ObservableList<product> productList = FXCollections.observableArrayList();
    private static ObservableList<orderedPart> addedProductList;
    private static String nullCharacter = "--";


    @Override
    public void setScreenParent(ScreensController screenPage) {
        myController = screenPage;
    }


    public void initialize()
    {
        initAddedTable();
        initProductTable();
        initializeBuyerDrop();
        initializeCompanyDrop();
        initSearchField();
        searchField.setVisible(false);
        tabPane.requestFocus();
    }

    @Override
    public void update()
    {
        DAL.productDAO.getProductList(productList);
        initializeBuyerDrop();
        initializeCompanyDrop();
    }


    private void initSearchField()
    {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> productTable.setPredicate(ProductTreeItem ->
                staticLookupCommons.checkNull(ProductTreeItem.getValue().partNumber.getValue()).toUpperCase().contains(newValue.toUpperCase()) ||
                        staticLookupCommons.checkNull(ProductTreeItem.getValue().productName.getValue()).toUpperCase().contains(newValue.toUpperCase()) ||
                        staticLookupCommons.checkNull(ProductTreeItem.getValue().UOM.getValue()).toUpperCase().contains(newValue.toUpperCase()) ||
                        staticLookupCommons.checkNull(ProductTreeItem.getValue().UPC.getValue()).toUpperCase().contains(newValue.toUpperCase()) ||
                        staticLookupCommons.checkNull(ProductTreeItem.getValue().activeStatus.getValue()).toUpperCase().contains(newValue.toUpperCase()) ||
                        staticLookupCommons.checkNull(ProductTreeItem.getValue().productCategory.getValue()).toUpperCase().contains(newValue.toUpperCase())));

    }

    private void initProductTable()
    {
        productTable.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

        //Create and populate table
        JFXTreeTableColumn<product,String> partNumberCol = new JFXTreeTableColumn<>("Part Number");
        partNumberCol.setCellValueFactory(param -> param.getValue().getValue().partNumber);

        JFXTreeTableColumn<product,String> productNameCol = new JFXTreeTableColumn<>("Product Name");
        productNameCol.setCellValueFactory(param -> param.getValue().getValue().productName);

        JFXTreeTableColumn<product,String> activeStatusCol = new JFXTreeTableColumn<>("Active Status");
        activeStatusCol.setCellValueFactory(param -> param.getValue().getValue().activeStatus);

        JFXTreeTableColumn<product,String> UOMCol = new JFXTreeTableColumn<>("UOM");
        UOMCol.setCellValueFactory(param -> param.getValue().getValue().UOM);

        JFXTreeTableColumn<product,Double> costCol = new JFXTreeTableColumn<>("Cost ($)");
        costCol.setCellValueFactory(param -> param.getValue().getValue().cost);

        JFXTreeTableColumn<product,Double> priceCol = new JFXTreeTableColumn<>("Price ($)");
        priceCol.setCellValueFactory(param -> param.getValue().getValue().price);

        JFXTreeTableColumn<product,String> UPCCol = new JFXTreeTableColumn<>("UPC");
        UPCCol.setCellValueFactory(param -> param.getValue().getValue().UPC);

        JFXTreeTableColumn<product,String> productCategoryCol = new JFXTreeTableColumn<>("Product Category");
        productCategoryCol.setCellValueFactory(param -> param.getValue().getValue().productCategory);


        //This is the list that the table will pull all of its data from
        //here we pull data from the database to initialize the table
        DAL.productDAO.getProductList(productList);


        //By default null values are displayed as empty strings so lets overwrite that to display the 'nullCharacter' string instead
        //Since UPCs are the only fields that are allowed to be null we only need to set the cellValueFactory for that column
        UPCCol.setCellValueFactory(param -> (param.getValue().getValue().UPC.getValue() == null) ? new SimpleStringProperty(nullCharacter) : param.getValue().getValue().UPC);


        //Add context menus to each row so we can pull the specific product instance
        //into the context menu. This adds the add product option to the table.
        productTable.setRowFactory(tableView -> {
            final TreeTableRow<product> row = new TreeTableRow<>();
            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem addProduct = new MenuItem("     Add Product    ");
            addProduct.setOnAction(event -> getQty(row.getItem()));
            contextMenu.getItems().add(addProduct);
            // Set context menu on row, but use a binding to make it only show for non-empty rows:
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu)null)
                            .otherwise(contextMenu)
            );
            return row ;
        });

        final TreeItem<product> root = new RecursiveTreeItem<>(productList, RecursiveTreeObject::getChildren);

        productTable.getColumns().addAll(partNumberCol,productNameCol,activeStatusCol,UOMCol,costCol,priceCol,UPCCol,productCategoryCol);
        productTable.setRoot(root);
        productTable.setShowRoot(false);
    }



    //This will take a selected product and open a quantity UI
    //Then pass the information to the table in the new order page
    //product will always be non null
    //
    //EDIT: Not anymore
    private void addSelectedProduct(product product, JFXTextField input, Stage window)
    {
        String in = input.getText();
        int qty;


        //Here we need to extract the qty ordered.
        //after we have the qty we can initialize the
        //actual orderedPart object.


        Alert conf = new Alert(Alert.AlertType.ERROR);
        conf.setTitle("Error");
        conf.setHeaderText("Please enter a valid quantity");
        conf.setContentText("");

        if(in.isEmpty())
        {
            conf.showAndWait();
            return;
        }
        else
        {
            try
            {
                qty = Integer.parseInt(in);
            }
            catch (NumberFormatException e)
            {
                conf.showAndWait();
                return;
            }

            if(qty < 1)
            {
                conf.showAndWait();
                return;
            }

        }




        //now qty should be the actual quantity
        //Also, if we get to this page we can
        //safely assume we have a real PO#
        orderedPart newPart = new orderedPart();
        newPart.PONumber = new SimpleStringProperty(POField.getText());
        newPart.partNumber = new SimpleStringProperty(product.partNumber.get());
        newPart.QtyOrdered = new SimpleIntegerProperty(qty);
        newPart.price = product.price;
        newPart.description = new SimpleStringProperty(DAL.newOrderDAO.getProductDescription(product));
        //Since we are dealing with money, round the right way
        double totalPrice = new BigDecimal(product.price.getValue()*qty).setScale(2, RoundingMode.HALF_UP).doubleValue();
        newPart.totalPrice = new SimpleDoubleProperty(totalPrice).asObject();

        addedProductList.add(newPart);

        //How to init orderedPart

        //Initialize new orderedPart object with null
        //transaction id and then insert into db.
        //Copy the insert method from insertBuyer() in
        //the buyer DAO. This returns an ID from an internal
        //query to resolve the auto-incremented value. Now
        //before we return we can use the return value to
        //finish initializing the orderedPart Object

        //Now we should have the orderedPart object fully initialized
        //and inserted into the database. This will eventually need to
        //be attached the purchase order but until we are done adding
        //products we will just pass them back to the New Order UI.

        //EDIT
        //Instead of inserting each ordered part every time we add one
        //to get the transaction ID just keep them all in an array (in
        //tableView) with null IDs. then after the user confirms the
        //PO we can insert all the ordered parts and then generate the
        //actual purchase order object


        /*
        conf.setAlertType(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Success");
        conf.setHeaderText("Added product");
        conf.setContentText("");
        conf.show();
        */

        window.close();

    }

    private void getQty(product product)
    {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.initStyle(StageStyle.UNDECORATED);
        window.setHeight(200);
        window.setWidth(360);
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(18,20,18,20));
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER);

        Label header = new Label(product.productName.get());
        header.getStyleClass().add("header-label");

        JFXTextField input = new JFXTextField();
        input.setPromptText("Quantity");
        input.setLabelFloat(true);


        JFXButton closeButton = Commons.staticLookupCommons.createButton("Cancel", OctIcon.CIRCLE_SLASH);
        JFXButton submitButton = Commons.staticLookupCommons.createButton("Submit",OctIcon.CHECK);

        headerBox.getChildren().add(header);
        buttonBox.getChildren().addAll(submitButton,closeButton);

        mainLayout.setTop(headerBox);
        mainLayout.setCenter(input);
        mainLayout.setBottom(buttonBox);


        closeButton.setOnAction(e -> window.close());
        submitButton.setOnAction(e -> addSelectedProduct(product,input,window));

        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add("CSS/Buyers.css");

        window.setScene(scene);

        input.requestFocus();
        window.showAndWait();
    }



    private void initAddedTable()
    {

        JFXTreeTableColumn<orderedPart,String> partNumberCol = new JFXTreeTableColumn<>("Part Number");
        partNumberCol.setCellValueFactory(param -> param.getValue().getValue().partNumber);

        JFXTreeTableColumn<orderedPart,Integer> qtyCol = new JFXTreeTableColumn<>("Qty Ordered");
        qtyCol.setCellValueFactory(param -> param.getValue().getValue().QtyOrdered.asObject());

        JFXTreeTableColumn<orderedPart,String> descriptionCol = new JFXTreeTableColumn<>("Description");
        descriptionCol.setCellValueFactory(param -> param.getValue().getValue().description);

        JFXTreeTableColumn<orderedPart,Double> priceCol = new JFXTreeTableColumn<>("Price ($)");
        priceCol.setCellValueFactory(param -> param.getValue().getValue().price);

        JFXTreeTableColumn<orderedPart,Double> totalPriceCol = new JFXTreeTableColumn<>("Total Price ($)");
        totalPriceCol.setCellValueFactory(param -> param.getValue().getValue().totalPrice);


        addedTable.setRowFactory(tableView -> {
            final TreeTableRow<orderedPart> row = new TreeTableRow<>();
            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem removeProduct = new MenuItem("     Remove Product    ");
            removeProduct.setOnAction(event -> removeAddedProduct(row.getItem()));
            contextMenu.getItems().add(removeProduct);
            // Set context menu on row, but use a binding to make it only show for non-empty rows:
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu)null)
                            .otherwise(contextMenu)
            );
            return row ;
        });


        addedProductList = FXCollections.observableArrayList();



        final TreeItem<orderedPart> root = new RecursiveTreeItem<>(addedProductList, RecursiveTreeObject::getChildren);

        addedTable.getColumns().addAll(partNumberCol,descriptionCol,qtyCol,priceCol,totalPriceCol);
        addedTable.setRoot(root);
        addedTable.setShowRoot(false);
    }

    //called from context menu listener on the addedTable. just wrapping
    //in this method for clarity and scalability
    private void removeAddedProduct(orderedPart part)
    {
        addedProductList.remove(part);
    }

    public void nextPage()
    {
        //Before we do anything we need to sanitize
        //all fields.
        if(POField.getText().isEmpty() || SOField.getText().isEmpty() || companyName.getSelectionModel().isEmpty() ||
        buyerName.getSelectionModel().isEmpty() || orderDatePicker.getValue() == null || dueDatePicker.getValue() == null)
        {
            Alert err = new Alert(Alert.AlertType.WARNING);
            err.setTitle("Incomplete Error");
            err.setHeaderText("Please fill out all fields before continuing");
            err.setContentText("");
            err.showAndWait();
            return;
        }

        //If we make it past here all the fields should
        //have valid information

        //if they choose previous we need to check if
        //there are any ordered parts already created
        //if that is the case we need to make sure that
        //all the order parts still match the PO#.
        if(!addedTable.getRoot().getChildren().isEmpty())
        {
            if(POField.getText().compareTo(addedTable.getRoot().getChildren().get(0).getValue().PONumber.get()) != 0)
            {
                for(int i=0; i<addedTable.getRoot().getChildren().size(); i++)
                {
                    addedTable.getRoot().getChildren().get(i).getValue().PONumber.setValue(POField.getText());
                }
            }
        }
        tabPane.getSelectionModel().select(1);
        searchField.setVisible(true);
        tabPane.requestFocus();
    }



    //This method wraps the event listener for the add button on the Main
    //UI. Since we need to check that an item has been selected before we add it.
    //getSelectedItem() will return null when no item is selected
    //So check for null and if not we can directly call the addSelectedProduct
    //method and pass the selected item
    private void checkSelectedElement()
    {
        if (productTable.getSelectionModel().getSelectedItem() != null)
        {
            getQty(productTable.getSelectionModel().getSelectedItem().getValue());
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Input Error");
            alert.setHeaderText("Please select a product to add");
            alert.setContentText("");
            alert.show();
        }
    }

    public void previousPage()
    {
        searchField.setVisible(false);
        tabPane.requestFocus();
        tabPane.getSelectionModel().select(0);
    }

    void addProductToTable(orderedPart orderedPart)
    {
        addedProductList.add(orderedPart);
    }

    private void initializeBuyerDrop()
    {
        buyerName.getItems().clear();
        DAL.buyerDAO.getBuyerSelector(buyerName);
    }

    private void initializeCompanyDrop()
    {
        companyName.getItems().clear();
        DAL.buyerDAO.getCompanies(companyName);
    }


    public void createOrder()
    {
        Alert err = new Alert(Alert.AlertType.ERROR);

        if(addedProductList.isEmpty())
        {
            err.setTitle("Invalid Input Error");
            err.setHeaderText("You cannot create an empty purchase order");
            err.setContentText("");
            err.show();

            return;
        }

        //Check for unique PO
        if(!newOrderDAO.checkPurchaseOrderNum(POField.getText()))
        {
            err.setTitle("Duplicate Purchase Order");
            err.setHeaderText("The Purchase Order Number already exists");
            err.setContentText("");
            err.show();

            return;
        }

        //Check for unique SO
        if(!newOrderDAO.checkSalesOrderNum(SOField.getText()))
        {
            err.setTitle("Duplicate Sales Order");
            err.setHeaderText("The Sales Order Number already exists");
            err.setContentText("");
            err.show();

            return;
        }

        //If we make it here assume that all fields have valid information
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Confirmation");
        conf.setHeaderText("Create Purchase Order?");
        conf.setContentText("");

        ButtonType yes = new ButtonType("Yes");
        ButtonType  no = new ButtonType("No");


        conf.getButtonTypes().setAll(yes,no);
        Optional<ButtonType> result = conf.showAndWait();


        if (result.isPresent() && result.get() == yes)
        {
            insertPO();
        }
        else
        {
            return;
        }

        clearInfo();

        //Clear the ordered part table and then
        //switch screens
        addedTable.getRoot().getChildren().clear();
        searchField.setVisible(false);
        myController.setScreen(Main.screen1ID);
        tabPane.getSelectionModel().select(0);

        Alert success = new Alert(Alert.AlertType.CONFIRMATION);
        success.setTitle("Success");
        success.setHeaderText("Successfully created new purchase order");
        success.setContentText("");
        success.show();


    }

    //Once we confirm the creation of the
    //new PO we initialize a new purchaseOrder object to
    //insert into the database. After we insert
    //the PO we can finish initializing all the
    //orderedParts associated with the PO
    //
    //NOTE: DO NOT CALL DIRECTLY
    private void insertPO()
    {
        purchaseOrder newOrder = new purchaseOrder();
        newOrder.PONumber = new SimpleStringProperty(POField.getText());
        newOrder.SONumber = new SimpleStringProperty(SOField.getText());
        newOrder.PODate = orderDatePicker.getValue();
        newOrder.dueDate = dueDatePicker.getValue();
        newOrder.buyerID = new SimpleIntegerProperty(DAL.buyerDAO.findBuyer(buyerName.getSelectionModel().getSelectedItem()));
        newOrder.memos = new SimpleStringProperty("");
        newOrder.complete = false;
        if(!memoField.getText().isEmpty())
        {
            newOrder.memos.setValue(memoField.getText());
        }



        DAL.newOrderDAO.insertPurchaseOrder(newOrder);
        insertOrderedParts();
    }


    //This method should only be called from
    //inside insertPO(). Here we will take
    //all the existing orderedPart objects and
    //insert them into the database, this will
    //return the transaction id we can also use
    //to finish initializing the objects before
    //we finish.
    //
    //NOTE: DO NOT CALL DIRECTLY
    //Only valid when called from
    //inside insertPO()
    private void insertOrderedParts()
    {
        for (orderedPart part : addedProductList)
        {
            int ID;

            ID = newOrderDAO.insertOrderedPart(part);

            part.transactionID = new SimpleIntegerProperty(ID);
        }
    }

    //This is only for readability
    public void addProduct()
    {
        checkSelectedElement();
    }

    private void clearInfo()
    {
        //Clear the ordered part table and then
        //switch screens
        addedProductList.clear();
        myController.setScreen(Main.screen1ID);
        tabPane.getSelectionModel().select(0);
        buyerName.getSelectionModel().clearSelection();
        companyName.getSelectionModel().clearSelection();
        POField.clear();
        SOField.clear();
        memoField.clear();
        orderDatePicker.setValue(null);
        dueDatePicker.setValue(null);
    }

    public void goBack()
    {
        if(!companyName.getSelectionModel().isEmpty()
                || !buyerName.getSelectionModel().isEmpty()
                || !POField.getText().isEmpty()
                || !SOField.getText().isEmpty()
                || orderDatePicker.getValue() != null
                || dueDatePicker.getValue() != null)
        {
            Alert conf = new Alert(Alert.AlertType.WARNING);
            conf.setTitle("Confirmation");
            conf.setHeaderText("Are you sure you want to go back?");
            conf.setContentText("This will remove any incomplete purchase order data");

            ButtonType yes = new ButtonType("Yes");
            ButtonType  no = new ButtonType("No");


            conf.getButtonTypes().setAll(yes,no);
            Optional<ButtonType> result = conf.showAndWait();


            if (result.isPresent() && result.get() == yes)
            {
                clearInfo();
                searchField.setVisible(false);
            }

        }
        else
        {
            //Clear the ordered part table and then
            //switch screens
            addedTable.getRoot().getChildren().clear();
            myController.setScreen(Main.screen1ID);
            tabPane.getSelectionModel().select(0);
            searchField.setVisible(false);
        }


    }




}
