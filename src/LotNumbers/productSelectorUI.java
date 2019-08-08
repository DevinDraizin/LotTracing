package LotNumbers;

import Commons.UIComponents.popUps.Notifier;
import Commons.UIComponents.tableViews;
import Commons.staticLookupCommons;
import Products.product;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import de.jensd.fx.glyphs.octicons.OctIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.Notifications;


public class productSelectorUI
{

    private JFXTreeTableView<product> table;
    private JFXTextField searchField;
    private ObservableList<product> productList;
    private String nullCharacter = "-";


    private void initSearchField()
    {
        searchField = new JFXTextField();

        searchField.getStyleClass().add("header-text-field");
        searchField.setPromptText("Search");
        searchField.setLabelFloat(true);
        searchField.setFocusColor(Paint.valueOf("#4052af"));

        searchField.textProperty().addListener((observable, oldValue, newValue) -> table.setPredicate(ProductTreeItem ->
                staticLookupCommons.checkNull(ProductTreeItem.getValue().partNumber.getValue()).toUpperCase().contains(newValue.toUpperCase()) ||
                        staticLookupCommons.checkNull(ProductTreeItem.getValue().productName.getValue()).toUpperCase().contains(newValue.toUpperCase()) ||
                        staticLookupCommons.checkNull(ProductTreeItem.getValue().UOM.getValue()).toUpperCase().contains(newValue.toUpperCase()) ||
                        staticLookupCommons.checkNull(ProductTreeItem.getValue().UPC.getValue()).toUpperCase().contains(newValue.toUpperCase()) ||
                        staticLookupCommons.checkNull(ProductTreeItem.getValue().activeStatus.getValue()).toUpperCase().contains(newValue.toUpperCase()) ||
                        staticLookupCommons.checkNull(ProductTreeItem.getValue().productCategory.getValue()).toUpperCase().contains(newValue.toUpperCase())));
    }


    private void initTableView()
    {
        table = new JFXTreeTableView<>();

        productList = FXCollections.observableArrayList();

        tableViews.initProductTable(table,productList,nullCharacter);

        table.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

    }

    private void selectProduct(Stage window, assemblyLotController controller)
    {

        if(table.getSelectionModel().isEmpty())
        {
            Notifier.getWarningNotification("No Input Error","Please select a product from the table");

        }
        else
        {
            product product = table.getSelectionModel().getSelectedItem().getValue();
            controller.selectProduct(product);


            window.close();

            Notifier.getSuccessNotification("Successfully added product","Added " + product.productName.get());

        }

    }

    public void createUI(assemblyLotController controller)
    {
        Stage window = new Stage();
        window.setWidth(1400);
        window.setHeight(750);
        window.setTitle("Product Selector");
        window.initModality(Modality.APPLICATION_MODAL);
        window.initStyle(StageStyle.UNDECORATED);

        initTableView();
        initSearchField();


        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10,10,40,10));

        HBox headerBox = new HBox(20);
        headerBox.setStyle("-fx-background-color: linear-gradient(to bottom right, #73C9CB, #747ee1);" +
                "-fx-effect: dropshadow(gaussian, rgb(0.0, 0.0, 0.0), 4.0, 0.1, 0.0,0.0);");

        headerBox.setPadding(new Insets(20,40,20,0));
        headerBox.setAlignment(Pos.CENTER);

        Label header = new Label();

        header.setText("Select Assembled Product");
        header.getStyleClass().add("button-labels");


        Region frontSpacer = new Region();
        Region backSpacer = new Region();
        HBox.setHgrow(frontSpacer, Priority.ALWAYS);
        HBox.setHgrow(backSpacer,Priority.ALWAYS);

        headerBox.getChildren().addAll(backSpacer,header,frontSpacer,searchField);


        HBox buttonBox = new HBox(60);
        buttonBox.setAlignment(Pos.CENTER);


        JFXButton closeButton = Commons.staticLookupCommons.createButton("close", OctIcon.CIRCLE_SLASH);
        JFXButton selectButton = Commons.staticLookupCommons.createButton("Select Product",OctIcon.CHECK);

        buttonBox.getChildren().addAll(closeButton,selectButton);

        VBox centerLayout = new VBox();
        centerLayout.setPadding(new Insets(0,40,0,40));
        centerLayout.setAlignment(Pos.CENTER);
        centerLayout.getChildren().add(table);


        closeButton.setOnAction(e -> window.close());
        selectButton.setOnAction(e -> selectProduct(window,controller));

        mainLayout.setTop(headerBox);
        mainLayout.setCenter(centerLayout);
        mainLayout.setBottom(buttonBox);

        Scene scene = new Scene(mainLayout);
        window.setScene(scene);

        scene.getStylesheets().add("CSS/buyers.css");
        mainLayout.requestFocus();
        window.show();
    }

}
