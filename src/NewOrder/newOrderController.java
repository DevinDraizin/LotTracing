package NewOrder;


import Buyers.buyer;
import Products.product;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import main.main;
import screensframework.ControlledScreen;
import screensframework.ScreensController;



public class newOrderController implements ControlledScreen
{
    ScreensController myController;


    @FXML
    JFXComboBox<String> companyName, buyerName;

    @FXML
    JFXDatePicker orderDatePicker, dueDatePicker;

    @FXML
    JFXTreeTableView<orderedPart> table;


    private static ObservableList<orderedPart> productList;
    private static String nullCharacter = "--";


    @Override
    public void setScreenParent(ScreensController screenPage) {
        myController = screenPage;
    }



    public void initialize()
    {
        initTable();
        initializeBuyerDrop();
        initializeCompanyDrop();
    }



    private void initTable()
    {

        JFXTreeTableColumn<orderedPart,String> partNumberCol = new JFXTreeTableColumn<>("Part Number");
        partNumberCol.setCellValueFactory(param -> param.getValue().getValue().partNumber);

        JFXTreeTableColumn<orderedPart,Integer> qtyCol = new JFXTreeTableColumn<>("Qty Ordered");
        qtyCol.setCellValueFactory(param -> param.getValue().getValue().QtyOrdered.asObject());

        JFXTreeTableColumn<orderedPart,String> descriptionCol = new JFXTreeTableColumn<>("Description");
        descriptionCol.setCellValueFactory(param -> param.getValue().getValue().description);

        JFXTreeTableColumn<orderedPart,Double> priceCol = new JFXTreeTableColumn<>("Price ($)");
        priceCol.setCellValueFactory(param -> param.getValue().getValue().price);


        productList = FXCollections.observableArrayList();



        final TreeItem<orderedPart> root = new RecursiveTreeItem<>(productList, RecursiveTreeObject::getChildren);

        table.getColumns().addAll(partNumberCol,descriptionCol,qtyCol,priceCol);
        table.setRoot(root);
        table.setShowRoot(false);
    }

    void addProductToTable(orderedPart orderedPart)
    {
        productList.add(orderedPart);
    }

    private void initializeBuyerDrop()
    {
        DAL.buyerDAO.getBuyerSelector(buyerName);
    }

    private void initializeCompanyDrop()
    {
        DAL.buyerDAO.getBuyerCompanySelector(companyName);
    }

    public void createOrder()
    {

    }

    public void addProduct()
    {
        getProductsUI.createUI();
    }

    public void goBack()
    {
        myController.setScreen(main.screen1ID);
    }




}
