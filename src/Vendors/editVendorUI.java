package Vendors;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.octicons.OctIcon;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class editVendorUI
{
    private static void updateField(JFXTextField nameField,JFXComboBox<vendor> vendorDrop)
    {
        if(vendorDrop.getSelectionModel().isEmpty())
        {
            nameField.setDisable(true);
        }
        else
        {
            nameField.setDisable(false);
            nameField.setText(vendorDrop.getValue().vendorName.getValue());
        }
    }

    private static void editVendor(ObservableList<vendor> vendorList,JFXTextField nameField,JFXComboBox<vendor> vendorDrop, Stage window)
    {
        if(nameField.getText().isEmpty())
        {
            return;
        }


        vendor updatedVendor = new vendor(vendorDrop.getValue().vendorID.getValue(),nameField.getText());

        DAL.vendorDAO.editVendor(updatedVendor);



        //Try to replace for loop search with indexOf(updatedVendor)
        for (vendor aVendorList : vendorList)
        {
            if (aVendorList.vendorID.getValue().equals(vendorDrop.getValue().vendorID.getValue())) {
                aVendorList.vendorName.setValue(nameField.getText());

                window.close();

                Alert success = new Alert(Alert.AlertType.CONFIRMATION);
                success.setTitle("Success");
                success.setHeaderText("Successfully edited vendor");
                success.showAndWait();

                return;
            }
        }
    }

    public static void createUI(ObservableList<vendor> vendorList)
    {
        Stage window = new Stage();
        window.setTitle("Edit Vendor");
        window.initStyle(StageStyle.UNDECORATED);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setHeight(350);
        window.setWidth(400);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(40,0,40,0));

        Label header = new Label("Edit Vendor");
        header.getStyleClass().add("button-labels");

        JFXButton editButton = Commons.staticLookupCommons.createButton("Edit", OctIcon.PLUS);
        JFXButton cancelButton = Commons.staticLookupCommons.createButton("Cancel",OctIcon.CIRCLE_SLASH);

        JFXComboBox<vendor> vendorDrop = new JFXComboBox<>();
        removeVendorUI.initDropdown(vendorDrop);

        JFXTextField nameField = new JFXTextField();
        nameField.setPromptText("Enter Vendor Name");
        nameField.setAlignment(Pos.CENTER);
        nameField.setDisable(true);

        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER);
        VBox centerBox = new VBox(40);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setFillWidth(false);
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);


        headerBox.getChildren().add(header);
        centerBox.getChildren().addAll(vendorDrop,nameField);
        buttonBox.getChildren().addAll(editButton,cancelButton);

        mainLayout.setTop(headerBox);
        mainLayout.setCenter(centerBox);
        mainLayout.setBottom(buttonBox);

        cancelButton.setOnAction(e -> window.close());
        vendorDrop.setOnAction(e -> updateField(nameField,vendorDrop));
        editButton.setOnAction(e -> editVendor(vendorList,nameField,vendorDrop,window));

        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add("CSS/buyers.css");

        window.setScene(scene);
        window.showAndWait();
    }
}
