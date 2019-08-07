package Commons.UIComponents.popUps;

import Commons.utilities;
import Components.component;
import com.jfoenix.controls.JFXButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class componentDetailsPopUp
{

    public static void getComponentDetails(String partNumber)
    {
        Stage window = new Stage();
        window.setTitle("Component Details");
        window.initModality(Modality.APPLICATION_MODAL);
        window.setWidth(450);
        window.setHeight(325);

        component comp = DAL.componentDAO.getComponent(partNumber);

        if(comp == null)
        {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Database Error");
            err.setContentText("Failed to retrieve component information");
            err.setHeaderText("");
            err.show();

            return;
        }


        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10,40,40,40));

        HBox headerBox = new HBox(10);
        headerBox.setPadding(new Insets(20,0,20,0));
        headerBox.setAlignment(Pos.CENTER);

        Label header = new Label("Component Details");
        header.getStyleClass().add("button-labels");
        headerBox.getChildren().add(header);

        GridPane grid = new GridPane();
        grid.setVgap(20);
        grid.setHgap(20);
        grid.setAlignment(Pos.CENTER);


        Label partNumberDescLabel = new Label("Part Number:");
        partNumberDescLabel.getStyleClass().add("desc-labels");
        partNumberDescLabel.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        grid.add(partNumberDescLabel,0,0);

        Label descriptionDescLabel = new Label("Description:");
        descriptionDescLabel.getStyleClass().add("desc-labels");
        descriptionDescLabel.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        grid.add(descriptionDescLabel,0,1);

        Label sectionDescLabel = new Label("Section:");
        sectionDescLabel.getStyleClass().add("desc-labels");
        sectionDescLabel.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        grid.add(sectionDescLabel,0,2);

        Label partNumberLabel = new Label(comp.partNumber.get());
        partNumberLabel.setWrapText(true);
        initLabel(partNumberLabel);
        grid.add(partNumberLabel,1,0);

        Label descriptionLabel = new Label(comp.description.get());
        partNumberLabel.setWrapText(true);
        initLabel(descriptionLabel);
        grid.add(descriptionLabel,1,1);

        Label sectionLabel = new Label(comp.section.get());
        partNumberLabel.setWrapText(true);
        initLabel(sectionLabel);
        grid.add(sectionLabel,1,2);


        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);

        JFXButton closeButton = new JFXButton("Close");
        closeButton.getStyleClass().add("primary-button-1");
        closeButton.setOnAction(e -> window.close());

        buttonBox.getChildren().add(closeButton);


        mainLayout.setTop(headerBox);
        mainLayout.setCenter(grid);
        mainLayout.setBottom(buttonBox);

        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add("CSS/buyers.css");
        window.setScene(scene);

        mainLayout.requestFocus();

        window.showAndWait();

    }

    private static void initLabel(Label source)
    {
        ContextMenu menu = new ContextMenu();
        MenuItem copyItem = new MenuItem("    Copy Text    ");

        menu.getItems().addAll(copyItem);
        source.setContextMenu(menu);

        copyItem.setOnAction(e -> utilities.copyToClip(source.getText()));
    }
}
