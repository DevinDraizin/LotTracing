package Vendors;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import de.jensd.fx.glyphs.octicons.OctIcon;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;



public class removeVendorUI
{

    //This will initialize the combo box on the delete dialogue with
    //All the current vendors
    public static void initDropdown(JFXComboBox<vendor> vendorDrop)
    {
        vendorDrop.setPromptText("Select Vendor");

        //Get List of vendor objects from database
        ObservableList<vendor> vendorList = DAL.vendorDAO.getVendorList();

        //add all the vendors to the combo box
        if(vendorList != null)
        {
            for (vendor aVendorList : vendorList)
            {
                vendorDrop.getItems().add(aVendorList);
            }
        }

        //We can set a string converter to specify the format of
        //the combo box as well as how we retrieve the vendor
        //object from a selection
        vendorDrop.setConverter(new StringConverter<vendor>() {

            @Override
            public String toString(vendor object) {
                return object.vendorID.getValue() + "   " + object.vendorName.getValue();
            }

            @Override
            public vendor fromString(String string) {
                return vendorDrop.getItems().stream().filter(ap ->
                        ap.vendorName.getValue().equals(string)).findFirst().orElse(null);
            }
        });

    }

    //Deletes selected vendor from database as well as from the table view
    private static void removeVendor(JFXComboBox<vendor> vendorDrop, Stage window, ObservableList<vendor> vendorList)
    {
        if(vendorDrop.getSelectionModel().isEmpty())
        {
            return;
        }

        //Remove selected vendor from database
        DAL.vendorDAO.removeVendor(vendorDrop.getValue().vendorID.getValue());


        //VendorList is the observable list that is used to
        //display to the table view, we can delete the vendor
        //from here to automatically update the table
        for(int i=0; i<vendorList.size(); i++)
        {
            if(vendorList.get(i).vendorID.getValue().equals(vendorDrop.getValue().vendorID.getValue()))
            {
                vendorList.remove(i);
            }
        }


        //Close the window and display success dialogue
        window.close();

        Alert success = new Alert(Alert.AlertType.CONFIRMATION);
        success.setTitle("Success");
        success.setHeaderText("Successfully removed vendor");
        success.showAndWait();


    }

    //Called when we select remove vendor.
    //Initializes the delete vendor window
    public static void createUI(ObservableList<vendor> vendorList)
    {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.initStyle(StageStyle.UNDECORATED);
        window.setTitle("Remove Vendor");
        window.setHeight(300);
        window.setWidth(400);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(40,0,40,0));

        JFXButton removeButton = Commons.staticLookupCommons.createButton("Remove", OctIcon.PLUS);
        JFXButton cancelButton = Commons.staticLookupCommons.createButton("Cancel",OctIcon.CIRCLE_SLASH);

        JFXComboBox<vendor> vendorDrop = new JFXComboBox<>();
        initDropdown(vendorDrop);

        Label header = new Label("Remove Vendor");
        header.getStyleClass().add("button-labels");

        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER);
        HBox centerBox = new HBox();
        centerBox.setAlignment(Pos.CENTER);
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        headerBox.getChildren().add(header);
        centerBox.getChildren().add(vendorDrop);
        buttonBox.getChildren().addAll(removeButton,cancelButton);

        mainLayout.setTop(headerBox);
        mainLayout.setCenter(centerBox);
        mainLayout.setBottom(buttonBox);

        cancelButton.setOnAction(e -> window.close());
        removeButton.setOnAction(e -> removeVendor(vendorDrop,window,vendorList));

        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add("CSS/buyers.css");
        window.setScene(scene);
        window.showAndWait();
    }
}
