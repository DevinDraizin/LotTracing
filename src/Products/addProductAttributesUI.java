package Products;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import de.jensd.fx.glyphs.octicons.OctIcon;
import de.jensd.fx.glyphs.octicons.OctIconView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;

//This class is responsible for managing the productCategory portion of
//adding a new product. Since every product will have a 'productCategory'
//we add an additional window in the 'add product' UI to initialize the
//specific attributes associated with that category.
//
//This class is a helper class to 'addProductUI' to handle the
//productCategory part of adding a new product.
public class addProductAttributesUI
{

    //This method is will retrieve all the names of columns from the
    //specified category table in the database except the primary key
    //This will be used inside the initList method since we never want
    //to overwrite the primary key (Part Number)
    static ArrayList<String> getAttributeNames(String category)
    {
        ArrayList<String> attributes = new ArrayList<>();

        DAL.productBuilderDAO.getCategoryDetailsPartial(attributes, category);

        return attributes;
    }




    //Since the UI has to be dynamic according to the selected category
    //we generate the UI node that contains the data entry components
    //dynamically. This method will add a variable number of JFXComboBox
    //to the node (VBox nested in an AnchorPane) and set the prompt texts
    //according to the column names of the category
    private static AnchorPane initList(ArrayList<String> attributes,String category,product product)
    {
        VBox container = new VBox(40);
        container.setFillWidth(false);
        container.setAlignment(Pos.CENTER);
        container.setStyle("-fx-background-color: white");

        AnchorPane primary = new AnchorPane();
        primary.setStyle("-fx-background-color: white");
        primary.setStyle("-fx-effect: dropshadow(gaussian, rgb(0.0, 0.0, 0.0, 0.15), 6.0, 0.7, 0.0,1.5)");

        AnchorPane.setTopAnchor(container, 0.0);
        AnchorPane.setBottomAnchor(container, 0.0);
        AnchorPane.setLeftAnchor(container, 0.0);
        AnchorPane.setRightAnchor(container, 0.0);

        primary.getChildren().add(container);


        for(int i=0; i<attributes.size(); i++)
        {

            //We need to initialize all of the
            //combo boxes with the attributes
            //specified in the passed in product.
            //Since we cant set text on a combo
            //box we have to select the correct
            //item from its array.
            //
            //Current solution: get attributes list from
            //from category table and parse each combo menu
            //for the associated item in the attributes list
            //Since We insert attribute combo boxes in the same
            //order as the attribute array, we can assume the
            //indices will match to the order of the combo boxes
            JFXComboBox<String> combo = new JFXComboBox<>();
            combo.setLabelFloat(true);
            combo.setEditable(true);
            combo.setMaxWidth(300);
            combo.setPrefWidth(300);
            combo.setPromptText(attributes.get(i));
            DAL.productBuilderDAO.getComboData(combo.getItems(),category,attributes.get(i));

            //if product is not null then this method has been
            //called from the edit product UI, not the add product.
            //in that case we need to initialize the attribute combo
            //boxes with the data of the selected product
            //***********************************************

            //data.get(i) works because attributes is the same size as data.
            //attributes holds the names of each column in the category and
            //data holds the data in each column of the category
            if(product != null)
            {
                ArrayList<String> data = new ArrayList<>();
                DAL.productBuilderDAO.getPartNumberDetails(data,product.productCategory.getValue(),product.partNumber.getValue());

                for(int j=0; j<combo.getItems().size(); j++)
                {
                    if(combo.getItems().get(j).compareTo(data.get(i)) == 0)
                    {
                        combo.getSelectionModel().select(j);
                    }
                }
            }

            //***********************************************

            container.getChildren().add(combo);
        }


        return primary;
    }

    //This is the method that will be called when the user clicks the 'Add Attributes'
    //button on the UI. When this happens we check to make sure all the fields have
    //been filled out. If they have we initialize a productCategory object located
    //in the 'addProductUI' class for use when we add the whole product.
    //If any of the fields have not been filled out, we show an error dialogue
    private static void setNewAttributes(AnchorPane primary,int size, Stage window,OctIconView icon)
    {
        Alert err = new Alert(Alert.AlertType.ERROR);

        for(int i=0; i<=size-1; i++)
        {
            VBox box = (VBox)primary.getChildren().get(0);
            JFXComboBox curr = (JFXComboBox)box.getChildren().get(i);
            String content = (String)curr.getValue();

            if(content == null)
            {
                err.setTitle("Invalid Input");
                err.setHeaderText("Please make sure all fields are complete");
                err.show();

                addProductUI.attributes.attributes.clear();

                return;
            }

            if(curr.getPromptText().toUpperCase().compareTo("DESCRIPTION") == 0 && content.length() > 255)
            {
                err.setTitle("Invalid Input");
                err.setHeaderText("Description can be at most 255 characters long");
                err.show();

                addProductUI.attributes.attributes.clear();

                return;
            }

            addProductUI.attributes.attributes.add(content);
        }

        window.close();

        err.setAlertType(Alert.AlertType.CONFIRMATION);
        err.setTitle("Success");
        err.setHeaderText("Successfully added attributes");
        err.show();

        icon.setIcon(OctIcon.CHECK);
        icon.setFill(Paint.valueOf("#008f00"));
    }


    //Creates and instantiates the UI
    public static void createUI(String category,int mode,product product,OctIconView icon)
    {
        Stage window = new Stage();
        window.setTitle("Add Product Attributes");
        window.initModality(Modality.APPLICATION_MODAL);
        window.initStyle(StageStyle.UNDECORATED);
        window.setHeight(650);
        window.setWidth(500);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20,40,40,40));

        HBox headerBox = new HBox(20);
        headerBox.setPadding(new Insets(0,0,40,0));
        headerBox.setAlignment(Pos.CENTER);


        Label header = new Label();

        if(mode == 1)
        {
            header.setText("Add Product Attributes");
        }
        else if(mode == 2)
        {
            header.setText("Edit Product Attributes");
        }

        header.getStyleClass().add("button-labels");

        headerBox.getChildren().add(header);

        HBox buttonBox = new HBox(30);
        buttonBox.setPadding(new Insets(60,0,0,0));
        buttonBox.setAlignment(Pos.CENTER);

        JFXButton closeButton = Commons.staticLookupCommons.createButton("Close", OctIcon.CIRCLE_SLASH);


        ArrayList<String> attributes = getAttributeNames(category);

        AnchorPane list = initList(attributes,category,product);

        if(mode == 1)
        {
            JFXButton addButton = Commons.staticLookupCommons.createButton("Add Attributes", OctIcon.PLUS);
            buttonBox.getChildren().addAll(addButton,closeButton);
            addButton.setOnAction(e -> setNewAttributes(list,attributes.size(),window,icon));
        }
        else if(mode == 2)
        {
            JFXButton editButton = Commons.staticLookupCommons.createButton("Edit Attributes", OctIcon.TOOLS);
            buttonBox.getChildren().addAll(editButton,closeButton);
            editButton.setOnAction(e -> editProductAttributesUI.commitEdit());
        }




        mainLayout.setTop(headerBox);
        mainLayout.setCenter(list);
        mainLayout.setBottom(buttonBox);

        closeButton.setOnAction(e -> window.close());



        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add("CSS/buyers.css");
        window.setScene(scene);

        mainLayout.requestFocus();
        window.showAndWait();
    }
}
