package LotNumbers;


import Components.component;
import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.octicons.OctIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.HashMap;


public class componentLotController
{

    private lotNumbersController parentController;

    @FXML
    JFXTextField lotNumberTextField, vendorPOField;

    @FXML
    JFXTreeTableView<component> componentTable;

    @FXML
    JFXDatePicker receiveDatePicker;

    @FXML
    JFXComboBox<String> vendorDrop;

    private static ObservableList<component> componentList = FXCollections.observableArrayList();

    private static HashMap<String,Integer> vendorMap;


    public void initialize()
    {
        initComponentTable();
        initVendorDrop();
    }

    void setParentController(lotNumbersController parentController) {
        this.parentController = parentController;
    }


    //This is called from the parent controller whenever we load 'Lot Numbers'
    void updateComponentTable()
    {
        //componentList is Observable so we can just update
        //the list and the UI will listen for the changes
        DAL.componentDAO.getComponentsList(componentList);
        initVendorDrop();
    }

    //Clear the combo box and insert all
    //vendors
    private void initVendorDrop()
    {
        vendorDrop.getItems().clear();
        vendorMap = DAL.vendorDAO.getVendorSelector(vendorDrop);

        if(vendorMap == null)
        {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Database Error");
            err.setHeaderText("Failed to load vendor information");
            err.setContentText("");
            err.show();
        }
    }

    private void initComponentTable()
    {
        String nullCharacter = "--";
        Commons.UIComponents.initComponentTable(componentTable,componentList, nullCharacter);

    }

    //Called from initialize method of parent controller
    void initSearchField(JFXTextField searchField)
    {
        Commons.UIComponents.initComponentSearchField(searchField,componentTable);
    }

    //This will set the textfield value to a generated
    //lot number that is safe
    public void generateLotNumber()
    {
        lotNumberTextField.setText(Commons.utilities.generateUniqueCompLotNumber());
    }

    //move to the home tab (center) then hide
    //the table search field. Then change the
    //header label back to the home tab header
    public void goBack()
    {
        parentController.tabLayout.getSelectionModel().select(1);
        parentController.searchField.setVisible(false);
        parentController.headerLabel.setText("Lot Numbers");
    }

    //Clears all information from the UI form
    //DO NOT CALL DIRECTLY
    //If this is called after validateLot() then
    //createLot() will fail miserably
    private void clearFields()
    {
        lotNumberTextField.clear();

        vendorPOField.clear();

        vendorDrop.getSelectionModel().clearSelection();

        componentTable.getSelectionModel().clearSelection();

        receiveDatePicker.getEditor().clear();

        if(receiveDatePicker.getValue() != null)
        {
            receiveDatePicker.setValue(null);
        }

    }


    private void createLot(JFXTextField input, Stage window)
    {
        String in = input.getText();
        int qty;


        //Here we need to extract the qty ordered.
        //after we have the qty we can initialize the
        //actual orderedPart object.
        Alert conf = new Alert(Alert.AlertType.ERROR);
        conf.setTitle("Error");
        conf.setHeaderText("Please enter a valid quantity");
        conf.setContentText("");

        if(in.isEmpty())
        {
            conf.showAndWait();
            return;
        }
        else
        {
            try
            {
                qty = Integer.parseInt(in);
            }
            catch (NumberFormatException e)
            {
                conf.showAndWait();
                return;
            }

            if(qty < 1)
            {
                conf.showAndWait();
            }

        }

        //If we reach here qty is now valid so we can extract the componentLot info

        //Assemble the new valid lot into a componentLot object
        componentLot lot = new componentLot(lotNumberTextField.getText(),receiveDatePicker.getValue(),
                qty,vendorPOField.getText(),vendorMap.get(vendorDrop.getValue()),
                componentTable.getSelectionModel().getSelectedItem().getValue().partNumber.getValue());

        //Now we can insert the new component lot into the database and then clear all fields for cleanup

        boolean outcome = DAL.lotNumbersDAO.insertComponentLot(lot);

        Alert success = new Alert(Alert.AlertType.CONFIRMATION);

        if(outcome)
        {
            success.setTitle("Success");
            success.setHeaderText("Successfully created component lot");
            success.setContentText("");
            success.showAndWait();
        }
        else
        {
            success.setAlertType(Alert.AlertType.ERROR);
            success.setTitle("Error");
            success.setHeaderText("Failed to create component lot");
            success.setContentText("");
            success.showAndWait();
        }

        //Close Qty window
        window.close();

        //Clear UI fields
        clearFields();

        //Go back to main lot number page
        goBack();

    }



    //If this passes we can safely construct the DAO
    //and update the database
    private boolean validateLot()
    {
        Alert err = new Alert(Alert.AlertType.WARNING);
        err.setContentText("");

        if(componentTable.getSelectionModel().isEmpty())
        {
            err.setTitle("No Input Error");
            err.setHeaderText("Please select a component");
            err.showAndWait();

            return false;
        }

        if(vendorDrop.getSelectionModel().isEmpty() || vendorPOField.getText().isEmpty() ||
                lotNumberTextField.getText().isEmpty() || receiveDatePicker.getValue() == null)
        {
            err.setTitle("No Input Error");
            err.setHeaderText("Please make sure all fields have been completed");
            err.showAndWait();

            return false;
        }


        if(!DAL.componentDAO.checkComponentLotNum(lotNumberTextField.getText()))
        {
            err.setTitle("Duplicate Lot Number");
            err.setHeaderText("That component lot number already exists");
            err.showAndWait();

            return false;
        }


        return true;
    }


    //Create a small UI for extracting qty as well as calling the create lot
    //method.
    public void getQty()
    {
        if(!validateLot())
        {
            return;
        }

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.initStyle(StageStyle.UNDECORATED);
        window.setHeight(200);
        window.setWidth(360);
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(18,20,18,20));
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER);

        Label header = new Label("Enter Quantity");
        header.getStyleClass().add("header-label");

        JFXTextField input = new JFXTextField();
        input.setPromptText("Quantity");
        input.setLabelFloat(true);


        JFXButton closeButton = Commons.staticLookupCommons.createButton("Cancel", OctIcon.CIRCLE_SLASH);
        JFXButton submitButton = Commons.staticLookupCommons.createButton("Submit",OctIcon.CHECK);

        headerBox.getChildren().add(header);
        buttonBox.getChildren().addAll(submitButton,closeButton);

        mainLayout.setTop(headerBox);
        mainLayout.setCenter(input);
        mainLayout.setBottom(buttonBox);


        closeButton.setOnAction(e -> window.close());
        submitButton.setOnAction(e -> createLot(input,window));

        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add("CSS/Buyers.css");

        window.setScene(scene);

        input.requestFocus();
        window.showAndWait();
    }


}
