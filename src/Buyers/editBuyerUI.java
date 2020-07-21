package Buyers;

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


public class editBuyerUI
{
    private static ObservableList<buyer> list;
    private static buyer selected = null;


    //This method will sanitize the input fields then update the selected buyer object
    //Since we are just updating the object we don't need to add it to the tree view
    //again. Then we update the database
    private static void editBuyer(JFXTextField buyerNameField,JFXTextField buyerEmailField,JFXComboBox<String> companyDrop,JFXTextField buyerPhoneField,JFXTextField extensionField,Stage window)
    {
        if(selected == null)
        {
            return;
        }

        if(sanFields(buyerNameField,buyerEmailField,companyDrop,buyerPhoneField))
        {

            //Update buyer information in the buyer observable list
            //this should update the table view
            selected.buyerName.setValue(buyerNameField.getText());
            selected.email.setValue(buyerEmailField.getText());
            selected.company.setValue(companyDrop.getValue());

            selected.phoneNumber.setValue(buyerPhoneField.getText().replace("-","."));

            if(!extensionField.getText().isEmpty())
            {
                selected.phoneNumber.setValue(selected.phoneNumber.getValue() + " EXT " + extensionField.getText());
            }


            DAL.buyerDAO.editBuyer(selected);

            window.close();

            Alert success = new Alert(Alert.AlertType.CONFIRMATION);
            success.setTitle("Success");
            success.setHeaderText("Successfully edited buyer");
            success.show();

        }
    }

    //This method will check fields for no input as well as a valid phone number and email address
    private static boolean sanFields(JFXTextField buyerNameField,JFXTextField buyerEmailField,JFXComboBox<String> companyDrop,JFXTextField buyerPhoneField)
    {
        if(buyerNameField.getText().isEmpty() || buyerEmailField.getText().isEmpty() || companyDrop.getSelectionModel().getSelectedItem().isEmpty() || buyerPhoneField.getText().isEmpty())
        {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Validation Error");
            err.setHeaderText("");
            err.setContentText("One or more fields are empty");
            err.show();

            return false;
        }

        if(!Commons.staticLookupCommons.sanEmail(buyerEmailField.getText()))
        {
            return false;
        }

        return Commons.staticLookupCommons.sanPhone(buyerPhoneField.getText());
    }

    //This displays all the existing buyers in a combo box.
    //We also attach an action listener to initialize
    //The subsequent fields based off the selected buyer
    private static void initBuyerSelector(JFXTextField buyerNameField, JFXTextField buyerEmailField, JFXComboBox<String> companyDrop, JFXTextField buyerPhoneField, JFXTextField extensionField, JFXComboBox<String> buyerSelector)
    {

        buyerSelector.setPromptText("Select Buyer To Edit");

        DAL.buyerDAO.getBuyerSelector(buyerSelector);

        buyerSelector.setOnAction(e -> updateFields(buyerNameField,buyerEmailField,companyDrop,buyerPhoneField,extensionField,buyerSelector));

    }

    //This method will initialize the fields in the UI based off
    //of the selection from the buyer selector
    private static void updateFields(JFXTextField buyerNameField, JFXTextField buyerEmailField, JFXComboBox<String> companyDrop, JFXTextField buyerPhoneField, JFXTextField extensionField, JFXComboBox<String> buyerSelector)
    {
        if(buyerSelector.getSelectionModel().isEmpty())
        {
            buyerNameField.setDisable(true);
            buyerEmailField.setDisable(true);
            companyDrop.setDisable(true);
            buyerPhoneField.setDisable(true);
            extensionField.setDisable(true);
        }
        else
        {
            buyerNameField.setDisable(false);
            buyerEmailField.setDisable(false);
            companyDrop.setDisable(false);
            buyerPhoneField.setDisable(false);
            extensionField.setDisable(false);



            int buyerID = DAL.buyerDAO.findBuyer(buyerSelector.getSelectionModel().getSelectedItem());

            for (buyer aList : list)
            {
                if (aList.buyerID.getValue() == buyerID)
                {
                    selected = aList;
                }
            }

            if(selected == null)
            {
                System.out.println("Could not find buyer to edit");
                return;
            }

            buyerNameField.setText(selected.buyerName.getValue());
            buyerEmailField.setText(selected.email.getValue());
            companyDrop.getSelectionModel().select(selected.company.getValue());


            if(selected.phoneNumber.getValue().length() > 12)
            {
                buyerPhoneField.setText(selected.phoneNumber.getValue().substring(0,12).replace(".","-"));
                extensionField.setText(selected.phoneNumber.getValue().substring(17));
            }
            else
            {
                buyerPhoneField.setText(selected.phoneNumber.getValue().replace(".","-"));

            }

        }
    }


    //Creates the Main UI as well as
    //the event listeners
    public static void createUI(ObservableList<buyer> buyerList)
    {
        list = buyerList;
        Stage window = new Stage();

        window.setTitle("Edit Buyer");
        window.setWidth(500);
        window.setHeight(660);
        window.initModality(Modality.APPLICATION_MODAL);
        window.initStyle(StageStyle.UNDECORATED);

        BorderPane mainLayout = new BorderPane();
        VBox centerBox = new VBox(40);
        HBox buttonBox = new HBox(20);
        HBox headerBox = new HBox(20);
        headerBox.setPadding(new Insets(40,0,20,0));

        Label header = new Label("Edit Buyer");
        header.getStyleClass().add("button-labels");

        JFXComboBox<String> companyDrop = new JFXComboBox<>();

        JFXComboBox<String> buyerSelector = new JFXComboBox<>();

        addBuyerUI.initCompanyDrop(companyDrop);

        JFXButton editButton = Commons.staticLookupCommons.createButton("Edit", OctIcon.PLUS);

        JFXButton cancelButton = Commons.staticLookupCommons.createButton("Cancel",OctIcon.CIRCLE_SLASH);


        JFXTextField buyerNameField = new JFXTextField();
        buyerNameField.setPromptText("Buyer Name");

        JFXTextField buyerEmailField = new JFXTextField();
        buyerEmailField.setPromptText("Buyer Email");

        JFXTextField buyerPhoneField = new JFXTextField();
        buyerPhoneField.setPromptText("Buyer Phone Number");

        JFXTextField extensionField = new JFXTextField();
        extensionField.setPromptText("Extension (Optional)");

        addBuyerUI.initPhone(buyerPhoneField);

        headerBox.getChildren().add(header);
        headerBox.setAlignment(Pos.CENTER);

        buttonBox.getChildren().addAll(editButton,cancelButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(40,0,0,0));
        centerBox.getChildren().addAll(buyerSelector,companyDrop,buyerNameField,buyerEmailField,buyerPhoneField,extensionField,buttonBox);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setFillWidth(false);

        mainLayout.setTop(headerBox);
        mainLayout.setCenter(centerBox);

        initBuyerSelector(buyerNameField,buyerEmailField,companyDrop,buyerPhoneField,extensionField,buyerSelector);



        cancelButton.setOnAction(e -> window.close());
        editButton.setOnAction(e -> editBuyer(buyerNameField,buyerEmailField,companyDrop,buyerPhoneField,extensionField,window));


        buyerNameField.setDisable(true);
        buyerEmailField.setDisable(true);
        companyDrop.setDisable(true);
        buyerPhoneField.setDisable(true);
        extensionField.setDisable(true);


        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add("CSS/buyers.css");
        window.setScene(scene);
        window.show();

    }
}
