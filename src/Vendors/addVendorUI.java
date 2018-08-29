package Vendors;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.octicons.OctIcon;
import javafx.beans.property.SimpleIntegerProperty;
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



public class addVendorUI
{

    private static void addBuyer(ObservableList<vendor> vendorList, JFXTextField input, Stage window)
    {
        if(input.getText().isEmpty())
        {
            return;
        }


        vendor newVendor = new vendor(null,input.getText());

        int id = DAL.vendorDAO.insertVendor(newVendor);

        newVendor.vendorID = new SimpleIntegerProperty(id).asObject();

        vendorList.add(newVendor);


        window.close();

        Alert success = new Alert(Alert.AlertType.CONFIRMATION);
        success.setTitle("Success");
        success.setHeaderText("Successfully added vendor");
        success.showAndWait();


    }

    public static void createUI(ObservableList<vendor> vendorList)
    {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.initStyle(StageStyle.UNDECORATED);
        window.setTitle("Add Vendor");
        window.setHeight(300);
        window.setWidth(400);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(40,0,40,0));

        Label header = new Label("Add Vendor");
        header.getStyleClass().add("button-labels");

        JFXTextField input = new JFXTextField();
        input.setPromptText("Enter Vendor Name");

        JFXButton addButton = Commons.staticLookupCommons.createButton("Add", OctIcon.PLUS);

        JFXButton cancelButton = Commons.staticLookupCommons.createButton("Cancel",OctIcon.CIRCLE_SLASH);

        HBox headerLayout = new HBox();
        headerLayout.setAlignment(Pos.CENTER);
        headerLayout.getChildren().add(header);

        HBox centerLayout = new HBox();
        centerLayout.setAlignment(Pos.CENTER);
        centerLayout.getChildren().add(input);

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(addButton,cancelButton);


        cancelButton.setOnAction(e -> window.close());

        addButton.setOnAction(event -> addBuyer(vendorList,input,window));


        mainLayout.setTop(headerLayout);
        mainLayout.setCenter(centerLayout);
        mainLayout.setBottom(buttonBox);

        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add("CSS/buyers.css");
        window.setScene(scene);
        window.showAndWait();
    }
}
