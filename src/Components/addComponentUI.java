package Components;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.octicons.OctIcon;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class addComponentUI
{
    public static void createUI()
    {
        Stage window = new Stage();
        window.setHeight(400);
        window.setWidth(400);
        window.setTitle("Add Component");
        window.initModality(Modality.APPLICATION_MODAL);
        window.initStyle(StageStyle.UNDECORATED);


        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20,20,20,20));

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        JFXButton closeButton = Commons.staticLookupCommons.createButton("Close", OctIcon.CIRCLE_SLASH);

        buttonBox.getChildren().add(closeButton);

        mainLayout.setBottom(buttonBox);

        closeButton.setOnAction(e -> window.close());

        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add("CSS/buyers.css");
        window.setScene(scene);

        mainLayout.requestFocus();

        window.showAndWait();
    }
}
