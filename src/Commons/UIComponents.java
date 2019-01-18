package Commons;


import Components.component;
import Products.product;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

//We reuse a lot of UI components in each view that we
//should probably source from methods here.
//
//We should eventually move all the JFXTreeTableViews into a more general
//initialization method
public class UIComponents
{
    public static void initComponentTable(JFXTreeTableView<component> table, ObservableList<component> componentList, String nullCharacter)
    {
        JFXTreeTableColumn<component,String> partNumberCol = new JFXTreeTableColumn<>("Part Number");
        partNumberCol.setCellValueFactory(param -> param.getValue().getValue().partNumber);

        JFXTreeTableColumn<component,String> descriptionCol = new JFXTreeTableColumn<>("Description");
        descriptionCol.setCellValueFactory(param -> param.getValue().getValue().description);

        JFXTreeTableColumn<component,String> sectionCol = new JFXTreeTableColumn<>("Section");
        sectionCol.setCellValueFactory(param -> param.getValue().getValue().description);


        DAL.componentDAO.getComponentsList(componentList);

        sectionCol.setCellValueFactory(param -> (param.getValue().getValue().section.getValue() == null) ? new SimpleStringProperty(nullCharacter) : param.getValue().getValue().section);



        final TreeItem<component> root = new RecursiveTreeItem<>(componentList, RecursiveTreeObject::getChildren);

        table.getColumns().addAll(partNumberCol,descriptionCol,sectionCol);
        table.setRoot(root);
        table.setShowRoot(false);
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
}
