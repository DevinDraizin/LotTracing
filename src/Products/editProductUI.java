package Products;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import de.jensd.fx.glyphs.octicons.OctIcon;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class editProductUI
{
    private static JFXTextField partNumberField;
    private static JFXTextField productNameField;
    private static JFXTextField costField;
    private static JFXTextField priceField;
    private static JFXTextField UPCField;

    private static JFXComboBox<String> UOMFields;

    private static JFXToggleButton activeToggle;

    private static void initFields(product product)
    {

        if(!product.partNumber.isNull().get())
        {
            partNumberField.setText(product.partNumber.getValue());
        }
        if(!product.UPC.isNull().get())
        {
            UPCField.setText(product.UPC.getValue());
        }
        if(product.price != null)
        {
            priceField.setText(String.valueOf(product.price.getValue()));
        }
        if(product.cost != null)
        {
            costField.setText(String.valueOf(product.cost.getValue()));
        }
        if(!product.UOM.isNull().get())
        {
            for(int i=0; i<UOMFields.getItems().size(); i++)
            {
                if(UOMFields.getItems().get(i).compareTo(product.UOM.getValue()) == 0)
                {
                    UOMFields.getSelectionModel().select(i);
                }
            }
        }
        if(!product.productName.isNull().get())
        {
            productNameField.setText(product.productName.getValue());
        }
        if(!product.activeStatus.isNull().get())
        {
            if(product.activeStatus.getValue().compareTo("Active") == 0)
            {
                activeToggle.setSelected(true);
            }
            else
            {
                activeToggle.setSelected(false);
                activeToggle.setText("Not-Active");
            }
        }
    }

    static JFXComboBox<String> initUOM()
    {
       JFXComboBox<String> UOMBox = new JFXComboBox<>();

        UOMBox.setLabelFloat(true);
        UOMBox.setEditable(true);
        UOMBox.setMaxWidth(300);
        UOMBox.setPrefWidth(300);
        UOMBox.setPromptText("Unit Of Measure");
        DAL.productDAO.getUOM(UOMBox);

        return UOMBox;
    }


    public static void createUI(product product)
    {
        Stage window = new Stage();
        window.setTitle("Edit Product");
        window.initModality(Modality.APPLICATION_MODAL);
        window.initStyle(StageStyle.UNDECORATED);
        window.setHeight(650);
        window.setWidth(750);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20,20,20,20));

        HBox headerBox = new HBox(20);
        headerBox.setPadding(new Insets(40,0,20,0));
        headerBox.setAlignment(Pos.CENTER);

        GridPane infoGrid = new GridPane();
        infoGrid.setAlignment(Pos.CENTER);
        infoGrid.setHgap(80);
        infoGrid.setVgap(40);


        Label header = new Label("Edit Product");
        header.getStyleClass().add("button-labels");

        headerBox.getChildren().add(header);

        HBox buttonBox = new HBox(30);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20,0,0,0));

        HBox switchBox = new HBox(80);
        switchBox.setAlignment(Pos.CENTER);

        VBox productCatBox = new VBox(20);
        productCatBox.setAlignment(Pos.CENTER);

        VBox contentBox = new VBox(50);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setFillWidth(false);

        JFXButton closeButton = Commons.staticLookupCommons.createButton("Close", OctIcon.CIRCLE_SLASH);
        JFXButton editButton = Commons.staticLookupCommons.createButton("Edit Product", OctIcon.PLUS);

        partNumberField = addProductUI.initTextField("Part Number");
        productNameField = addProductUI.initTextField("Product Name");
        costField = addProductUI.initTextField("Cost");
        priceField = addProductUI.initTextField("Price");
        UPCField = addProductUI.initTextField("UPC");

        UOMFields = initUOM();

        activeToggle = addProductUI.getActiveToggle();


        JFXButton getDetailsButton = new JFXButton("Product Category");
        getDetailsButton.getStyleClass().add("primary-button-1");



        productCatBox.getChildren().add(getDetailsButton);
        switchBox.getChildren().addAll(productCatBox,activeToggle);


        contentBox.getChildren().addAll(infoGrid,switchBox);
        buttonBox.getChildren().addAll(editButton,closeButton);

        infoGrid.add(partNumberField,1,0);
        infoGrid.add(productNameField,0,0);

        infoGrid.add(UOMFields,1,1);
        infoGrid.add(UPCField,0,1);

        infoGrid.add(priceField,1,2);
        infoGrid.add(costField,0,2);



        mainLayout.setTop(headerBox);
        mainLayout.setCenter(contentBox);
        mainLayout.setBottom(buttonBox);

        initFields(product);

        closeButton.setOnAction(e -> window.close());
        getDetailsButton.setOnAction(e -> editProductAttributesUI.createUI(product));

        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add("CSS/buyers.css");
        window.setScene(scene);

        mainLayout.requestFocus();

        window.show();
    }
}
