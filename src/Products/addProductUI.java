package Products;

import ProductBuilder.productCategory;
import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.octicons.OctIcon;
import de.jensd.fx.glyphs.octicons.OctIconView;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class addProductUI
{
    private static JFXTextField partNumberField;
    private static JFXTextField productNameField;
    private static JFXTextField costField;
    private static JFXTextField priceField;
    private static JFXTextField UPCField;
    private static JFXTextField lotSuffix;

    private static JFXComboBox<String> UOMFields;

    private static JFXToggleButton activeToggle;



    private static product newProduct = null;
    static productCategory attributes = new productCategory();

    private static int lastInsertedIndex = -1;
    private static int lotSuffixMaxLength = 2;


    private static boolean validateDoubles()
    {
        try
        {
            Double a = Double.valueOf(costField.getText());
            Double b = Double.valueOf(priceField.getText());

            return a >= 0 && b >= 0;

        }
        catch (NumberFormatException e)
        {
            return false;
        }

    }

    private static boolean validateAttributes()
    {
        return attributes != null && attributes.attributes != null && !attributes.attributes.isEmpty();
    }



    private static void addProduct(ObservableList<product> productList, Stage window,JFXComboBox<String> productCategoryDrop)
    {
        Alert err = new Alert(Alert.AlertType.ERROR);

        if (!partNumberField.getText().isEmpty() && !productNameField.getText().isEmpty()
                && !UOMFields.getValue().isEmpty() && !costField.getText().isEmpty() &&
                !priceField.getText().isEmpty() && !lotSuffix.getText().isEmpty() && !UPCField.getText().isEmpty())
        {
            if(!DAL.productDAO.checkPartNumber(partNumberField.getText()))
            {
                err.setTitle("Invalid Input");
                err.setHeaderText("Part Number Already Exists");
                err.show();

                return;
            }

            if(!validateDoubles())
            {
                err.setTitle("Invalid Input");
                err.setHeaderText("Cost and price must be valid numbers greater than 0");
                err.show();

                return;
            }

            if(!validateAttributes())
            {
                err.setTitle("Invalid Input");
                err.setHeaderText("Please make sure all product attributes have been entered");
                err.show();

                return;
            }

            if(!lotSuffix.getText().matches("(([A-Z]|[a-z]){2})"))
            {
                err.setTitle("Invalid Input");
                err.setHeaderText("Please make you enter a valid (2 character) lot suffix");
                err.show();

                return;
            }

            attributes.productName.setValue(productNameField.getText());
            attributes.categoryName.setValue(productCategoryDrop.getValue());


            String partNumber = partNumberField.getText();
            String productName = productNameField.getText();
            String activeStatus = activeToggle.isSelected() ? "Active" : "Not-active";
            String UOM = UOMFields.getValue();
            Double cost = Double.valueOf(costField.getText());
            Double price = Double.valueOf(priceField.getText());
            String UPC = UPCField.getText();
            String productCategory = productCategoryDrop.getValue();
            String suffix = lotSuffix.getText().isEmpty() ? null : lotSuffix.getText();

            newProduct = new product(partNumber,productName,activeStatus,UOM,cost,price,UPC,productCategory,suffix);

            DAL.productDAO.insertProduct(newProduct);
            DAL.productBuilderDAO.insertCategory(attributes,addProductAttributesUI.getAttributeNames(attributes.categoryName.getValue()),newProduct.partNumber.getValue());

            productList.add(newProduct);

            //Worst naming convention in the whole program
            attributes.attributes.clear();

            window.close();

            err.setAlertType(Alert.AlertType.CONFIRMATION);
            err.setTitle("Success!");
            err.setHeaderText("Successfully Added Product");
            err.show();


        }
        else
        {
            err.setTitle("Invalid Input");
            err.setHeaderText("One or more fields are empty");
            err.show();
        }
    }


    static JFXTextField initTextField(String prompt)
    {
        JFXTextField field = new JFXTextField();

        field.setLabelFloat(true);
        field.setAlignment(Pos.CENTER);
        field.setPromptText(prompt);
        field.setStyle("-fx-font-size: 16");
        field.setMinWidth(260);

        return field;
    }

    static JFXToggleButton getActiveToggle()
    {
        JFXToggleButton toggle = new JFXToggleButton();
        toggle.setSelected(true);
        toggle.setContentDisplay(ContentDisplay.TOP);
        toggle.setText("Active");

        toggle.setOnAction(event -> {
            if(toggle.isSelected())
            {
                toggle.setText("Active");
            }
            else
            {
                toggle.setText("Not-Active");
            }
        });

        return toggle;
    }


    //While program is running we track the last
    //product category type that way if user adds
    //more than 1 product they will not always
    //have to switch categories
    static void initCombo(JFXComboBox<String> productCategoryDrop)
    {
        DAL.productDAO.getProductCategories(productCategoryDrop);

        if(lastInsertedIndex == -1)
        {
            if(!productCategoryDrop.getItems().isEmpty())
            {
                productCategoryDrop.getSelectionModel().select(0);
            }
        }
        else
        {
            if(!productCategoryDrop.getItems().isEmpty())
            {
                productCategoryDrop.getSelectionModel().select(lastInsertedIndex);
            }
        }

        productCategoryDrop.setOnAction(event -> lastInsertedIndex = productCategoryDrop.getSelectionModel().getSelectedIndex());

    }

    private static void closeUI(Stage window)
    {
        attributes.attributes.clear();
        newProduct = null;
        window.close();
    }

    private static void initLotSuffixField()
    {
        lotSuffix.textProperty().addListener((observable, oldValue, newValue) -> {
            if(lotSuffix.getText().length() > lotSuffixMaxLength)
            {
                String s = lotSuffix.getText().substring(0,lotSuffixMaxLength);
                lotSuffix.setText(s);
            }
        });
    }



    public static void createUI(ObservableList<product> productList)
    {
        Stage window = new Stage();
        window.setTitle("Add Product");
        window.initModality(Modality.APPLICATION_MODAL);
        window.initStyle(StageStyle.UNDECORATED);
        window.setHeight(750);
        window.setWidth(850);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20,20,20,20));

        HBox headerBox = new HBox(20);
        headerBox.setPadding(new Insets(40,0,20,0));
        headerBox.setAlignment(Pos.CENTER);

        GridPane infoGrid = new GridPane();
        infoGrid.setAlignment(Pos.CENTER);
        infoGrid.setHgap(80);
        infoGrid.setVgap(40);


        Label header = new Label("Add Product");
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
        JFXButton addButton = Commons.staticLookupCommons.createButton("Add Product", OctIcon.PLUS);

        partNumberField = initTextField("Part Number");
        productNameField = initTextField("Product Name");
        costField = initTextField("Cost");
        priceField = initTextField("Price");
        UPCField = initTextField("UPC");
        lotSuffix = initTextField("Lot Suffix");
        lotSuffix.setTooltip(new Tooltip("3 digit description for lot numbers"));

        initLotSuffixField();

        UOMFields = editProductUI.initUOM();

        activeToggle = getActiveToggle();

        JFXComboBox<String> productCategoryDrop = new JFXComboBox<>();

        //Initialize product category drop down
        initCombo(productCategoryDrop);

        JFXButton getDetailsButton = new JFXButton("Product Category");
        getDetailsButton.getStyleClass().add("primary-button-1");



        HBox detailsBox = new HBox(16);
        detailsBox.setAlignment(Pos.CENTER);
        OctIconView icon = new OctIconView();
        icon.setIcon(OctIcon.X);
        icon.setFill(Paint.valueOf("#ff0000"));
        icon.setGlyphSize(24);
        detailsBox.getChildren().addAll(icon,getDetailsButton);

        productCatBox.getChildren().addAll(productCategoryDrop,detailsBox);
        switchBox.getChildren().addAll(productCatBox,activeToggle);


        contentBox.getChildren().addAll(infoGrid,lotSuffix,switchBox);
        buttonBox.getChildren().addAll(addButton,closeButton);

        infoGrid.add(partNumberField,1,0);
        infoGrid.add(productNameField,0,0);

        infoGrid.add(UOMFields,1,1);
        infoGrid.add(UPCField,0,1);

        infoGrid.add(priceField,1,2);
        infoGrid.add(costField,0,2);





        mainLayout.setTop(headerBox);
        mainLayout.setCenter(contentBox);
        mainLayout.setBottom(buttonBox);

        closeButton.setOnAction(e -> closeUI(window));
        addButton.setOnAction(e -> addProduct(productList,window,productCategoryDrop));
        getDetailsButton.setOnAction(e -> addProductAttributesUI.createUI(productCategoryDrop.getValue(),1,null,icon));

        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add("CSS/buyers.css");
        window.setScene(scene);

        mainLayout.requestFocus();

        window.show();
    }
}
