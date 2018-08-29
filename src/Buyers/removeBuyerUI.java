package Buyers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import de.jensd.fx.glyphs.octicons.OctIcon;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Optional;

public class removeBuyerUI
{
    private static Integer selectedID;
    private static Stage mainWindow;


    //Here we will take the populate the combo box with buyer_ID and buyer_name from the database.
    //we also set the action listener to update label.
    public static void initCompanyDrop(JFXComboBox<String> buyerDrop, Label name, Label company, Label Email, Label phone)
    {
        buyerDrop.setPromptText("Select Buyer ID");


        DAL.buyerDAO.getOrderedBuyerSelector(buyerDrop);

        buyerDrop.setOnAction(event -> updateLabel(buyerDrop.getSelectionModel().getSelectedItem(),name,company,Email,phone));

    }

    //This is called when the user selects an item from the combo box
    //we extract the ID from the selected item and query the database
    //in order to initialize the labels on the UI with the buyer info
    private static void updateLabel(String ID, Label name, Label company, Label Email, Label phone)
    {
        selectedID = Integer.parseInt(ID.substring(0,ID.indexOf(" ")));

        buyer selectedBuyer = DAL.buyerDAO.getBuyer(selectedID);

        if(selectedBuyer != null)
        {
            name.setText(selectedBuyer.buyerName.getValue());
            company.setText(selectedBuyer.company.getValue());
            Email.setText(selectedBuyer.email.getValue());
            phone.setText(selectedBuyer.phoneNumber.getValue());
        }
        else
        {
            System.out.println("Failed to retrieve buyer from database at updateLabel in removeBuyerUI");
        }


    }


    //Here we remove the selected buyer from the table view as well
    //as from the database. This method includes confirmation dialogues
    private static void removeBuyer(ObservableList<buyer> buyerList)
    {
        if(selectedID == null)
        {
            return;
        }

        Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
        conf.setTitle("Delete Buyer?");
        conf.setHeaderText("Are you sure you would like\nto delete this buyer?");

        ButtonType cancel = new ButtonType("Cancel");
        ButtonType yes = new ButtonType("Yes");
        ButtonType  quit = new ButtonType("No");


        conf.getButtonTypes().setAll(yes,quit,cancel);
        Optional<ButtonType> result = conf.showAndWait();

        if(result.isPresent() && result.get() == yes)
        {

            DAL.buyerDAO.removeBuyer(selectedID);

            //buyerList is the observable list that is used to
            //display to the table view, we can delete the vendor
            //from here to automatically update the table
            for(int i=0; i<buyerList.size(); i++)
            {
                if(buyerList.get(i).buyerID.getValue().equals(selectedID))
                {
                    buyerList.remove(i);
                }
            }

            mainWindow.close();

            Alert success = new Alert(Alert.AlertType.CONFIRMATION);
            success.setTitle("Success");
            success.setHeaderText("Successfully removed buyer");
            success.showAndWait();
        }
    }



    //Create and initialize popup window
    public static void createUI(ObservableList<buyer> buyerList)
    {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.initStyle(StageStyle.UNDECORATED);
        window.setTitle("Delete Buyer");
        window.setHeight(500);
        window.setWidth(500);

        mainWindow = window;
        selectedID = null;

        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(40,0,40,0));
        VBox centerLayout = new VBox(80);
        HBox headerLayout = new HBox();
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);


        Label buyerNameLabel = new Label("Select Buyer");
        Label buyerCompanyLabel = new Label("Select Buyer");
        Label buyerEmailLabel = new Label("Select Buyer");
        Label buyerPhoneLabel = new Label("Select Buyer");

        Label nameDesc = new Label("Buyer Name: ");
        nameDesc.getStyleClass().add("desc-labels");
        Label companyDesc = new Label("Buyer Company: ");
        companyDesc.getStyleClass().add("desc-labels");
        Label emailDesc = new Label("Buyer Email: ");
        emailDesc.getStyleClass().add("desc-labels");
        Label phoneDesc = new Label("Buyer Phone: ");
        phoneDesc.getStyleClass().add("desc-labels");


        GridPane infoGrid = new GridPane();
        infoGrid.setAlignment(Pos.CENTER);
        infoGrid.setHgap(20);
        infoGrid.setVgap(8);

        infoGrid.add(buyerNameLabel,1,0);
        infoGrid.add(nameDesc,0,0);

        infoGrid.add(buyerCompanyLabel,1,1);
        infoGrid.add(companyDesc,0,1);

        infoGrid.add(buyerEmailLabel,1,2);
        infoGrid.add(emailDesc,0,2);

        infoGrid.add(buyerPhoneLabel,1,3);
        infoGrid.add(phoneDesc,0,3);



        JFXButton removeButton = Commons.staticLookupCommons.createButton("Delete", OctIcon.DASH);
        JFXButton cancelButton = Commons.staticLookupCommons.createButton("Cancel",OctIcon.CIRCLE_SLASH);

        buttonBox.getChildren().addAll(removeButton,cancelButton);


        Label header = new Label("Delete Buyer");
        header.getStyleClass().add("button-labels");
        JFXComboBox<String> buyerDrop = new JFXComboBox<>();

        centerLayout.setAlignment(Pos.CENTER);
        centerLayout.getChildren().addAll(buyerDrop,infoGrid);

        headerLayout.setAlignment(Pos.CENTER);
        headerLayout.getChildren().add(header);

        mainLayout.setTop(headerLayout);
        mainLayout.setCenter(centerLayout);
        mainLayout.setBottom(buttonBox);


        initCompanyDrop(buyerDrop,buyerNameLabel,buyerCompanyLabel,buyerEmailLabel,buyerPhoneLabel);

        /*
        try {
            initCompanyDrop(buyerDrop,buyerNameLabel,buyerCompanyLabel,buyerEmailLabel,buyerPhoneLabel);
        } catch (SQLException e) {
            System.out.println("Failed to initialize Buyer Combo Box in Remove Buyer");
            e.printStackTrace();
        }
        */

        cancelButton.setOnAction(e -> window.close());
        removeButton.setOnAction(e -> removeBuyer(buyerList));


        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add("CSS/buyers.css");
        window.setScene(scene);
        window.showAndWait();
    }
}
