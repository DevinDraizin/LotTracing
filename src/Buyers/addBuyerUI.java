package Buyers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.octicons.OctIcon;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;



//Creates the UI for the add buyer Button
//in the 'Buyers' window
public class addBuyerUI
{
    private static ObservableList<buyer> list;
    private static Stage mainWindow;


    //Adds new buyer to database as well as updates the table in the UI.
    //This method also includes input sanitation
    private static void addBuyer(JFXTextField buyerNameField,JFXTextField buyerEmailField,JFXComboBox<String> companyDrop,JFXTextField buyerPhoneField,JFXTextField extensionField) {

        if(sanFields(buyerNameField,buyerEmailField,companyDrop,buyerPhoneField))
        {

            buyer newBuyer = new buyer(null,buyerNameField.getText(),companyDrop.getSelectionModel().getSelectedItem(), buyerEmailField.getText(), buyerPhoneField.getText());

            newBuyer.phoneNumber.setValue(newBuyer.phoneNumber.getValue().replace("-","."));

            if(!extensionField.getText().isEmpty())
            {
               newBuyer.phoneNumber.setValue(newBuyer.phoneNumber.getValue() + " EXT " + extensionField.getText());
            }

            int id = DAL.buyerDAO.insertBuyer(newBuyer);

            newBuyer.buyerID = new SimpleIntegerProperty(id).asObject();


            list.add(newBuyer);

            mainWindow.close();

            Alert success = new Alert(Alert.AlertType.CONFIRMATION);
            success.setTitle("Success");
            success.setHeaderText("Successfully added buyer");
            success.show();

        }

    }


    //This method will check fields for no input as well as a valid phone number and email address
    public static boolean sanFields(JFXTextField buyerNameField,JFXTextField buyerEmailField,JFXComboBox<String> companyDrop,JFXTextField buyerPhoneField)
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

        if(DAL.buyerDAO.findBuyer(buyerNameField.getText()) != -1)
        {
            Alert err = new Alert(Alert.AlertType.WARNING);
            err.setTitle("Invalid Input");
            err.setHeaderText("There already exists a buyer with that name");
            err.setContentText("");
            err.show();

            return false;
        }

        if(!Commons.staticLookupCommons.sanEmail(buyerEmailField.getText()))
        {
            return false;
        }

        return Commons.staticLookupCommons.sanPhone(buyerPhoneField.getText());
    }



    //This method will initialize the combo box in the UI with all the existing
    //buyer companies as well as make it so the user can add a new one
    public static void initCompanyDrop(JFXComboBox<String> companyDrop)
    {
        companyDrop.setEditable(true);

        companyDrop.setPromptText("Select A Company");

        DAL.buyerDAO.getCompanies(companyDrop);


    }


    //initialize and call formatting method on phone text field
    public static void initPhone(JFXTextField buyerPhoneField)
    {
        buyerPhoneField.setPrefColumnCount(12);

        TextFormatter<String> formatter = new TextFormatter<>(addBuyerUI::addPhoneNumberMask);

        buyerPhoneField.setTextFormatter(formatter);

    }


    //Learn how this works......
    private static TextFormatter.Change addPhoneNumberMask(TextFormatter.Change change)
    {

        // Ignore cursor movements, unless the text is empty (in which case
        // we're initializing the field).
        if (!change.isContentChange() && !change.getControlNewText().isEmpty()) {

            return change;
        }

        String text = change.getControlNewText();
        int start = change.getRangeStart();
        int end = change.getRangeEnd();

        int anchor = change.getAnchor();
        int caret = change.getCaretPosition();

        StringBuilder newText = new StringBuilder(text);

        int dash;
        while ((dash = newText.lastIndexOf("-")) >= start) {
            newText.deleteCharAt(dash);
            if (caret > dash) {
                caret--;
            }
            if (anchor > dash) {
                anchor--;
            }
        }

        while (newText.length() < 3) {
            newText.append('#');
        }
        if (newText.length() == 3 || newText.charAt(3) != '-') {
            newText.insert(3, '-');
            if (caret > 3 || (caret == 3 && end <= 3 && change.isDeleted())) {
                caret++;
            }
            if (anchor > 3 || (anchor == 3 && end <= 3 && change.isDeleted())) {
                anchor++;
            }
        }

        while (newText.length() < 7) {
            newText.append('#');
        }
        if (newText.length() == 7 || newText.charAt(7) != '-') {
            newText.insert(7, '-');
            if (caret > 7 || (caret == 7 && end <= 7 && change.isDeleted())) {
                caret++;
            }
            if (anchor > 7 || (anchor == 7 && end <= 7 && change.isDeleted())) {
                anchor++;
            }
        }

        while (newText.length() < 12) {
            newText.append('#');
        }

        if (newText.length() > 12) {
            newText.delete(12, newText.length());
        }

        text = newText.toString();
        anchor = Math.min(anchor, 12);
        caret = Math.min(caret, 12);

        change.setText(text);
        change.setRange(0, change.getControlText().length());
        change.setAnchor(anchor);
        change.setCaretPosition(caret);

        return change;
    }


    //Creates the main UI as well as
    //the event listeners
    public static void createUI(ObservableList<buyer> buyerList)
    {

        list = buyerList;

        Stage window = new Stage();
        window.setTitle("Add Buyer");
        window.setWidth(500);
        window.setHeight(660);
        window.initModality(Modality.APPLICATION_MODAL);
        window.initStyle(StageStyle.UNDECORATED);

        mainWindow = window;

        BorderPane mainLayout = new BorderPane();
        VBox centerBox = new VBox(40);
        HBox buttonBox = new HBox(20);
        HBox headerBox = new HBox(20);
        headerBox.setPadding(new Insets(40,0,20,0));

        Label header = new Label("Add New Buyer");
        header.getStyleClass().add("button-labels");

        JFXComboBox<String> companyDrop = new JFXComboBox<>();

        initCompanyDrop(companyDrop);



        JFXButton addButton = Commons.staticLookupCommons.createButton("Add",OctIcon.PLUS);

        JFXButton cancelButton = Commons.staticLookupCommons.createButton("Cancel",OctIcon.CIRCLE_SLASH);


        JFXTextField buyerNameField = new JFXTextField();
        buyerNameField.setPromptText("Buyer Name");

        JFXTextField buyerEmailField = new JFXTextField();
        buyerEmailField.setPromptText("Buyer Email");

        JFXTextField buyerPhoneField = new JFXTextField();
        buyerPhoneField.setPromptText("Buyer Phone Number");

        JFXTextField extensionField = new JFXTextField();
        extensionField.setPromptText("Extension (Optional)");

        initPhone(buyerPhoneField);

        headerBox.getChildren().add(header);
        headerBox.setAlignment(Pos.CENTER);

        buttonBox.getChildren().addAll(addButton,cancelButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(40,0,0,0));
        centerBox.getChildren().addAll(companyDrop,buyerNameField,buyerEmailField,buyerPhoneField,extensionField,buttonBox);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setFillWidth(false);

        mainLayout.setTop(headerBox);
        mainLayout.setCenter(centerBox);


        cancelButton.setOnAction(e -> window.close());
        addButton.setOnAction(e -> addBuyer(buyerNameField,buyerEmailField,companyDrop,buyerPhoneField,extensionField));


        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add("CSS/buyers.css");
        window.setScene(scene);
        window.show();

    }
}
