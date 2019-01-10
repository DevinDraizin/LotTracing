package Vendors;


import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import main.Main;
import screensframework.ControlledScreen;
import screensframework.ScreensController;


public class vendorsController implements ControlledScreen {

    @FXML
    JFXTreeTableView<vendor> table;

    @FXML
    JFXTextField searchField;

    @FXML
    JFXSlider slider;

    private static ObservableList<vendor> vendorList;

    ScreensController myController;

    public void initialize()
    {
        initTable();
        initSearchField();
        slider.setValue(18);
    }


    private void initTable()
    {
        JFXTreeTableColumn<vendor,Integer> vendorIDCol = new JFXTreeTableColumn<>("Vendor ID");
        vendorIDCol.setCellValueFactory(param -> param.getValue().getValue().vendorID);

        JFXTreeTableColumn<vendor,String> vendorNameCol = new JFXTreeTableColumn<>("Vendor Name");
        vendorNameCol.setCellValueFactory(param -> param.getValue().getValue().vendorName);

        vendorList = DAL.vendorDAO.getVendorList();

        /*
        vendorList = FXCollections.observableArrayList();


        try {
            getVendorData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        */


        final TreeItem<vendor> root = new RecursiveTreeItem<>(vendorList, RecursiveTreeObject::getChildren);

        table.getColumns().addAll(vendorNameCol,vendorIDCol);
        table.setRoot(root);
        table.setShowRoot(false);
    }

    public void setFont()
    {
        int size = (int)slider.getValue();
        table.setStyle("-fx-font-size: " + size);
    }

    private void initSearchField()
    {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> table.setPredicate(buyerTreeItem ->
                buyerTreeItem.getValue().vendorName.getValue().toUpperCase().contains(newValue.toUpperCase())));
    }

    /*
    private void getVendorData() throws SQLException
    {

        ResultSet myRs = DBConnection.mainConnection.executeStatement("SELECT * FROM Vendors");

        while(myRs.next())
        {
            vendor vendor = new vendor();
            vendor.vendorID = new SimpleIntegerProperty(myRs.getInt("VendorID")).asObject();
            vendor.vendorName = new SimpleStringProperty(myRs.getString("Vendor_Name"));

            vendorList.add(vendor);
        }


        DBConnection.mainConnection.closeStatement();



    }
    */

    public void goBack()
    {
        myController.setScreen(Main.screen1ID);
    }

    public void addVendor()
    {
        addVendorUI.createUI(vendorList);
    }

    public void editVendor()
    {
        editVendorUI.createUI(vendorList);
    }

    public void removeVendor()
    {
        removeVendorUI.createUI(vendorList);
    }

    @Override
    public void setScreenParent(ScreensController screenPage) {
        myController = screenPage;
    }

    @Override
    public void update() {

    }
}
