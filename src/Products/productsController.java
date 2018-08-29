package Products;


import Commons.staticLookupCommons;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.main;
import screensframework.ControlledScreen;
import screensframework.ScreensController;


public class productsController implements ControlledScreen
{
    private ScreensController myController;

    @FXML
    JFXTreeTableView<product> table;

    @FXML
    JFXTextField searchField;

    private static ObservableList<product> productList;

    //Change this string to override what the table
    //will display a null value as
    private static String nullCharacter = "--";

    @Override
    public void setScreenParent(ScreensController screenPage)
    {
        myController = screenPage;
    }

    public void initialize()
    {
        initTable();
        initSearchField();
    }


    //Here we initialize the table injected by the FXML
    //This is similar to the rest of the tables we create
    //in other pages in the sense that it is initialized
    //with data from the database. However this method also
    //create an action listener to detect double clicks so
    //we can create a popup with relevant data about the source
    private void initTable()
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
        productList = DAL.productDAO.getProductList();


        //By default null values are displayed as empty strings so lets overwrite that to display the 'nullCharacter' string instead
        //Since UPCs are the only fields that are allowed to be null we only need to set the cellValueFactory for that column
        UPCCol.setCellValueFactory(param -> (param.getValue().getValue().UPC.getValue() == null) ? new SimpleStringProperty(nullCharacter) : param.getValue().getValue().UPC);


        final TreeItem<product> root = new RecursiveTreeItem<>(productList, RecursiveTreeObject::getChildren);

        table.getColumns().addAll(partNumberCol,productNameCol,activeStatusCol,UOMCol,costCol,priceCol,UPCCol,productCategoryCol);
        table.setRoot(root);
        table.setShowRoot(false);

        //This is the action listener responsible for displaying
        //an attribute menu on double click
        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                ProductDetails(table.getSelectionModel().getSelectedItem().getValue().productCategory.getValue(),
                        table.getSelectionModel().getSelectedItem().getValue().partNumber.getValue());
            }
        });




        //Add context menus to each row so we can pull the specific product instance
        //into the context menu. This add the edit product option to the table.
        //NOTE: We should pass a reference to the tale to the editProductUI and
        //move this there
        table.setRowFactory(tableView -> {
            final TreeTableRow<product> row = new TreeTableRow<>();
            final ContextMenu contextMenu = new ContextMenu();
            final MenuItem editProduct = new MenuItem("     Edit Product    ");
            editProduct.setOnAction(event -> editProductUI.createUI(row.getItem()));
            contextMenu.getItems().add(editProduct);
            // Set context menu on row, but use a binding to make it only show for non-empty rows:
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu)null)
                            .otherwise(contextMenu)
            );
            return row ;
        });


        /*
        ContextMenu menu = new ContextMenu();
        MenuItem copyItem = new MenuItem("Edit Product       ");

        menu.getItems().addAll(copyItem);

        copyItem.setOnAction(e -> editProductUI.createUI());

        table.setContextMenu(menu);
        */



        //table.setColumnResizePolicy(TreeTableView.UNCONSTRAINED_RESIZE_POLICY);

    }



    //This method is called within the action listener for displaying the attribute menu
    private void ProductDetails(String product_category,String partNumber)
    {
        getProductDetails.getProperUI(product_category,partNumber);
    }

    private void initSearchField()
    {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> table.setPredicate(ProductTreeItem ->
                staticLookupCommons.checkNull(ProductTreeItem.getValue().partNumber.getValue()).toUpperCase().contains(newValue.toUpperCase()) ||
                        staticLookupCommons.checkNull(ProductTreeItem.getValue().productName.getValue()).toUpperCase().contains(newValue.toUpperCase()) ||
                        staticLookupCommons.checkNull(ProductTreeItem.getValue().UOM.getValue()).toUpperCase().contains(newValue.toUpperCase()) ||
                        staticLookupCommons.checkNull(ProductTreeItem.getValue().UPC.getValue()).toUpperCase().contains(newValue.toUpperCase()) ||
                        staticLookupCommons.checkNull(ProductTreeItem.getValue().activeStatus.getValue()).toUpperCase().contains(newValue.toUpperCase()) ||
                        staticLookupCommons.checkNull(ProductTreeItem.getValue().productCategory.getValue()).toUpperCase().contains(newValue.toUpperCase())));

    }


    public void addProduct()
    {
        addProductUI.createUI(productList);
    }

    public void editProduct()
    {
        if(table.getSelectionModel().getSelectedItem() != null)
        {
            editProductUI.createUI(table.getSelectionModel().getSelectedItem().getValue());
        }
        else
        {
            Alert test = new Alert(Alert.AlertType.INFORMATION);
            test.setTitle("");
            test.setHeaderText("Please select a product to edit");
            test.show();
        }
    }

    public void removeProduct()
    {
        Alert test = new Alert(Alert.AlertType.WARNING);
        test.setTitle("");
        test.setHeaderText("This function has not been implemented yet");
        test.show();
    }


    public void buildProduct()
    {
        myController.setScreen(main.screen11ID);
    }


    public void goBack()
    {
        myController.setScreen(main.screen1ID);
    }
}
