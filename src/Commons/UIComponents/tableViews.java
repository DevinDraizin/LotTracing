package Commons.UIComponents;


import Components.component;
import LotNumbers.componentLot;
import NewOrder.purchaseOrder;
import Products.product;
import com.jfoenix.controls.*;
import com.jfoenix.controls.cells.editors.base.JFXTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.time.LocalDate;

//We reuse a lot of UI components in each view that we
//should probably source from methods here.
//
//Eventually I will move all the JFXTreeTableViews into a more general
//initialization factory to be configured and called from here
public class tableViews
{

    public static void initPurchaseOrderTable(JFXTreeTableView<purchaseOrder> table, ObservableList<purchaseOrder> purchaseOrderList)
    {
        JFXTreeTableColumn<purchaseOrder,String> PONumberCol = new JFXTreeTableColumn<>("PO Number");
        PONumberCol.setCellValueFactory(param -> param.getValue().getValue().PONumber);

        JFXTreeTableColumn<purchaseOrder,String> SONumberCol = new JFXTreeTableColumn<>("SO Number");
        SONumberCol.setCellValueFactory(param -> param.getValue().getValue().SONumber);

        JFXTreeTableColumn<purchaseOrder, String> PODateCol = new JFXTreeTableColumn<>("PO Date");
        PODateCol.setCellValueFactory(param -> Commons.utilities.formatLocalDate(param.getValue().getValue().PODate));

        JFXTreeTableColumn<purchaseOrder,String> dueDateCol = new JFXTreeTableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(param -> Commons.utilities.formatLocalDate(param.getValue().getValue().dueDate));

        JFXTreeTableColumn<purchaseOrder,String> completedCol = new JFXTreeTableColumn<>("Completed");
        completedCol.setCellValueFactory(param -> param.getValue().getValue().complete ? new SimpleStringProperty("Yes") : new SimpleStringProperty("No"));

        DAL.newOrderDAO.getPurchaseOrderList(purchaseOrderList);

        /*
        completedCol.setCellFactory(new Callback<TreeTableColumn<purchaseOrder, String>, TreeTableCell<purchaseOrder, String>>() {
            @Override
            public TreeTableCell<purchaseOrder, String> call(TreeTableColumn<purchaseOrder, String> param) {
                return new JFXTreeTableCell<purchaseOrder,String>() {

                    @Override
                    public void updateItem(String item, boolean empty)
                    {
                        super.updateItem(item, empty);

                        if (!isEmpty()) {

                            TreeTableRow<purchaseOrder> row = this.getTreeTableRow();

                            if(item.compareTo("No") == 0)
                            {
                                //this.setTextFill(Color.RED);
                                //row.setTextFill(Color.RED);
                                //row.setStyle("-fx-text-fill: red");
                            }
                            else
                            {
                                //this.setTextFill(Color.BLACK);
                                //row.setTextFill(Color.BLACK);
                                //row.setStyle("-fx-text-fill: black");
                            }

                            setText(item);
                        }

                    }
                };
            }
        });
        */

        final TreeItem<purchaseOrder> root = new RecursiveTreeItem<>(purchaseOrderList, RecursiveTreeObject::getChildren);

        table.getColumns().addAll(PONumberCol,SONumberCol,PODateCol,dueDateCol,completedCol);
        table.setRoot(root);
        table.setShowRoot(false);


    }

    public static void initComponentTable(JFXTreeTableView<component> table, ObservableList<component> componentList, String nullCharacter)
    {
        JFXTreeTableColumn<component,String> partNumberCol = new JFXTreeTableColumn<>("Part Number");
        partNumberCol.setCellValueFactory(param -> param.getValue().getValue().partNumber);

        JFXTreeTableColumn<component,String> descriptionCol = new JFXTreeTableColumn<>("Description");
        descriptionCol.setCellValueFactory(param -> param.getValue().getValue().description);

        JFXTreeTableColumn<component,String> sectionCol = new JFXTreeTableColumn<>("Section");
        sectionCol.setCellValueFactory(param -> param.getValue().getValue().section);


        //The data access layer does not implement notifications so failure to pull from the database here
        //is handled with a generic no connection error. We can use the return val from the DAO to
        //display the specific error directly onto the table here
        DAL.componentDAO.getComponentsList(componentList);

        sectionCol.setCellValueFactory(param -> (param.getValue().getValue().section.getValue() == null) ? new SimpleStringProperty(nullCharacter) : param.getValue().getValue().section);


        final TreeItem<component> root = new RecursiveTreeItem<>(componentList, RecursiveTreeObject::getChildren);

        table.getColumns().addAll(partNumberCol,descriptionCol,sectionCol);
        table.setRoot(root);
        table.setShowRoot(false);
    }

    public static void updateComponentTable(ObservableList<component> componentList, JFXTreeTableView<component> table)
    {
        componentList.clear();
        DAL.componentDAO.getComponentsList(componentList);
        table.refresh();

    }

    public static void initComponentSearchField(JFXTextField searchField, JFXTreeTableView<component> table)
    {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> table.setPredicate(buyerTreeItem ->
                buyerTreeItem.getValue().partNumber.getValue().toUpperCase().contains(newValue.toUpperCase()) ||
                        Commons.staticLookupCommons.checkNull(buyerTreeItem.getValue().section.getValue()).toUpperCase().contains(newValue.toUpperCase()) ||
                        Commons.staticLookupCommons.checkNull(buyerTreeItem.getValue().description.getValue()).toUpperCase().contains(newValue.toUpperCase())));
    }

    public static void initProductTable(JFXTreeTableView<product> table, ObservableList<product> productList, String nullCharacter)
    {
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


        final TreeItem<product> root = new RecursiveTreeItem<>(productList, RecursiveTreeObject::getChildren);

        table.getColumns().addAll(partNumberCol,productNameCol,activeStatusCol,UOMCol,costCol,priceCol,UPCCol,productCategoryCol);
        table.setRoot(root);
        table.setShowRoot(false);


    }

    public static void initAssemblyProductTable(JFXTreeTableView<product> table, ObservableList<product> productList, String nullCharacter)
    {
        //Create and populate table
        JFXTreeTableColumn<product,String> partNumberCol = new JFXTreeTableColumn<>("Part Number");
        partNumberCol.setCellValueFactory(param -> param.getValue().getValue().partNumber);

        JFXTreeTableColumn<product,String> productNameCol = new JFXTreeTableColumn<>("Product Name");
        productNameCol.setCellValueFactory(param -> param.getValue().getValue().productName);

        JFXTreeTableColumn<product,String> UOMCol = new JFXTreeTableColumn<>("UOM");
        UOMCol.setCellValueFactory(param -> param.getValue().getValue().UOM);


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


        final TreeItem<product> root = new RecursiveTreeItem<>(productList, RecursiveTreeObject::getChildren);

        table.getColumns().addAll(partNumberCol,productNameCol,UOMCol,UPCCol,productCategoryCol);
        table.setRoot(root);
        table.setShowRoot(false);


    }

    public static void initComponentLotTable(JFXTreeTableView<componentLot> table, ObservableList<componentLot> componentLotList, String nullCharacter)
    {
        //Create and populate table
        JFXTreeTableColumn<componentLot,String> lotNumberCol = new JFXTreeTableColumn<>("Component Lot Number");
        lotNumberCol.setCellValueFactory(param -> param.getValue().getValue().ComponentLotNumber);

        JFXTreeTableColumn<componentLot,String> partNumberCol = new JFXTreeTableColumn<>("Component Part Number");
        partNumberCol.setCellValueFactory(param -> param.getValue().getValue().componentPartNumber);

        JFXTreeTableColumn<componentLot, String> receiveDateCol = new JFXTreeTableColumn<>("Receive Date");
        receiveDateCol.setCellValueFactory(param -> Commons.utilities.formatLocalDate(param.getValue().getValue().receiveDate));

        JFXTreeTableColumn<componentLot,String> qtyCol = new JFXTreeTableColumn<>("Quantity");
        qtyCol.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue().getValue().receiveQty.getValue())));

        JFXTreeTableColumn<componentLot,String> vendorPOCol = new JFXTreeTableColumn<>("Vendor PO#");
        vendorPOCol.setCellValueFactory(param -> param.getValue().getValue().vendorPO);




        //This is the list that the table will pull all of its data from
        //here we pull data from the database to initialize the table
        DAL.lotNumbersDAO.getComponentLotList(componentLotList);


        final TreeItem<componentLot> root = new RecursiveTreeItem<>(componentLotList, RecursiveTreeObject::getChildren);

        table.getColumns().addAll(lotNumberCol,partNumberCol,receiveDateCol,qtyCol,vendorPOCol);
        table.setRoot(root);
        table.setShowRoot(false);


    }
}
