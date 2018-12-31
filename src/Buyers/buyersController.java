package Buyers;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import main.Main;
import screensframework.ControlledScreen;
import screensframework.ScreensController;


public class buyersController implements ControlledScreen
{

    @FXML
    JFXTreeTableView<buyer> table;

    @FXML
    JFXTextField searchField;

    private ScreensController myController;

    private static ObservableList<buyer> buyerList;


    public void initialize()
    {
        initTable();
        initSearchField();
    }

    @Override
    public void setScreenParent(ScreensController screenPage)
    {
        myController = screenPage;
    }

    @Override
    public void update() {

    }


    private void initTable()
    {
        JFXTreeTableColumn<buyer,Integer> buyerIDCol = new JFXTreeTableColumn<>("Buyer ID");
        buyerIDCol.setCellValueFactory(param -> param.getValue().getValue().buyerID);

        JFXTreeTableColumn<buyer,String> buyerNameCol = new JFXTreeTableColumn<>("Buyer Name");
        buyerNameCol.setCellValueFactory(param -> param.getValue().getValue().buyerName);

        JFXTreeTableColumn<buyer,String> companyCol = new JFXTreeTableColumn<>("company");
        companyCol.setCellValueFactory(param -> param.getValue().getValue().company);

        JFXTreeTableColumn<buyer,String> emailCol = new JFXTreeTableColumn<>("Email");
        emailCol.setCellValueFactory(param -> param.getValue().getValue().email);

        JFXTreeTableColumn<buyer,String> phoneNumberCol = new JFXTreeTableColumn<>("Phone Number");
        phoneNumberCol.setCellValueFactory(param -> param.getValue().getValue().phoneNumber);

        buyerList = DAL.buyerDAO.getBuyerList();



        final TreeItem<buyer> root = new RecursiveTreeItem<>(buyerList, RecursiveTreeObject::getChildren);

        table.getColumns().addAll(buyerIDCol,buyerNameCol,companyCol,emailCol,phoneNumberCol);
        table.setRoot(root);
        table.setShowRoot(false);
    }

    private void initSearchField()
    {

        searchField.textProperty().addListener((observable, oldValue, newValue) -> table.setPredicate(buyerTreeItem ->
                buyerTreeItem.getValue().buyerName.getValue().toUpperCase().contains(newValue.toUpperCase()) ||
                buyerTreeItem.getValue().company.getValue().toUpperCase().contains(newValue.toUpperCase()) ||
                buyerTreeItem.getValue().email.getValue().toUpperCase().contains(newValue.toUpperCase()) ||
                buyerTreeItem.getValue().phoneNumber.getValue().toUpperCase().contains(newValue.toUpperCase())));
    }




    public void addBuyer()
    {
        addBuyerUI.createUI(buyerList);
    }

    public void editBuyer()
    {
        editBuyerUI.createUI(buyerList);
    }

    public void removeBuyer()
    {
        removeBuyerUI.createUI(buyerList);
    }

    public void goBack()
    {
        myController.setScreen(Main.screen1ID);
    }
}
