package NewOrder;

import DAL.productBuilderDAO;
import Products.product;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.jensd.fx.glyphs.octicons.OctIcon;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;

public class getProductsUI
{

    private static JFXTreeTableView<product> table;
    private static ObservableList<product> productList;

    private static JFXListView<JFXComboBox<String>> sideList;

    private static ArrayList<String> attributes = new ArrayList<>();
    private static ArrayList<String> filters = new ArrayList<>();

    private static String nullCharacter = "--";

    public static boolean exec = true;


    private static void getTopUI(BorderPane mainLayout)
    {
        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER);


        Label header = new Label("Add Products");
        header.getStyleClass().add("button-labels");

        JFXTextField searchField = new JFXTextField();
        searchField.setLabelFloat(true);
        searchField.setPromptText("Search Products");

        Region spacer1 = new Region();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2,Priority.ALWAYS);

        headerBox.getChildren().addAll(spacer1,header,spacer2,searchField);

        mainLayout.setTop(headerBox);
    }


    //also broken
    //Think of a different way to initialize combo box
    //on change
    private static void filterList(String category)
    {
        String oldVal;

        //exec toggles the action listener
        //so we don't fire when we are editing
        //the actual combo boxes
        if(!exec)
        {
            return;
        }

        if(sideList.getItems().isEmpty())
        {
            return;
        }

        attributes.clear();
        filters.clear();

        DAL.productBuilderDAO.getCategoryDetails(attributes,category);


        for(int i=0; i<sideList.getItems().size(); i++)
        {
            filters.add(sideList.getItems().get(i).getValue());
        }

        for(int j=0; j<sideList.getItems().size(); j++)
        {
            oldVal = sideList.getItems().get(j).getValue();
            sideList.getItems().get(j).getItems().clear();
            sideList.getItems().get(j).getSelectionModel().clearSelection();
            DAL.newOrderDAO.getFilteredComboData(sideList.getItems().get(j),category,attributes.get(j).replace(" ","_"),filters,attributes,oldVal);
        }

    }


    private static void initSideList(String category)
    {
        sideList.getItems().clear();

        ArrayList<String> columnNames = new ArrayList<>();
        DAL.productBuilderDAO.getCategoryDetails(columnNames,category);

        exec = false;

        for (String columnName : columnNames)
        {
            JFXComboBox<String> box = getComboBox(columnName);
            box.getItems().add("Any");
            productBuilderDAO.getComboData(box.getItems(), category, columnName.replace(" ", "_"));
            box.getSelectionModel().select(0);


            box.setOnAction(event -> filterList(category));

            sideList.getItems().add(box);
        }

        exec = true;

    }


    private static void initProductTable()
    {
        table = new JFXTreeTableView<>();


        table.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

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
    }


    //The event listener for key should still trigger as long
    //as we use an Action listener and not a specific listener since
    //key press is an action
    private static JFXComboBox<String> getComboBox(String prompt)
    {
        JFXComboBox<String> combo = new JFXComboBox<>();
        //Disable key selection of combo items in case it
        //fails to fire event for action listeners. May remove
        //later on after testing
        combo.addEventFilter(KeyEvent.ANY, Event::consume);
        combo.setLabelFloat(true);
        combo.setPromptText(prompt);
        combo.setMinWidth(260);
        combo.setMaxWidth(260);

        return combo;
    }

    //Initializes combo box as well as the listeners for the side list
    private static void initCategoryCombo(JFXComboBox<String> categoryDrop)
    {
        categoryDrop.getItems().add("Any");

        DAL.productDAO.getProductCategories(categoryDrop);

        categoryDrop.getSelectionModel().select(0);

        categoryDrop.setOnAction(e -> initSideList(categoryDrop.getValue()));
    }

    private static void getCenterUI(BorderPane mainLayout)
    {

        initProductTable();

        sideList = new JFXListView<>();
        sideList.setMinWidth(360);
        sideList.addEventFilter(MouseEvent.MOUSE_PRESSED, Event::consume);

        VBox centerLayout = new VBox(40);
        centerLayout.setAlignment(Pos.CENTER);

        HBox topBox = new HBox();
        topBox.setAlignment(Pos.CENTER_LEFT);

        HBox centerBox = new HBox(20);
        centerBox.setAlignment(Pos.CENTER_LEFT);

        HBox.setHgrow(table,Priority.ALWAYS);


        JFXComboBox<String> categoryDrop = getComboBox("Filter By Category");

        initCategoryCombo(categoryDrop);


        topBox.getChildren().add(categoryDrop);
        centerBox.getChildren().addAll(sideList,table);


        centerLayout.getChildren().addAll(topBox,centerBox);

        mainLayout.setCenter(centerLayout);
    }

    private static void getBottomUI(BorderPane mainLayout,Stage window)
    {
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        JFXButton closeButton = Commons.staticLookupCommons.createButton("Close", OctIcon.CIRCLE_SLASH);

        buttonBox.getChildren().add(closeButton);

        mainLayout.setBottom(buttonBox);

        closeButton.setOnAction(e -> window.close());
    }

    public static void createUI()
    {
        Stage window = new Stage();
        window.setTitle("Add Products");
        window.setHeight(900);
        window.setWidth(1500);
        window.initStyle(StageStyle.UNDECORATED);
        window.initModality(Modality.APPLICATION_MODAL);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(30,20,20,20));

        getTopUI(mainLayout);
        getCenterUI(mainLayout);
        getBottomUI(mainLayout,window);


        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add("CSS/buyers.css");
        window.setScene(scene);

        mainLayout.requestFocus();

        window.show();
    }
}
