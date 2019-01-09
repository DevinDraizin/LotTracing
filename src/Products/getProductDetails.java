package Products;


import Commons.utilities;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;



//This class is responsible for handling when a specific row is double
//clicked in the products page. When this happens we want to display
//a window that shows the attributes associated with a specific part
//number in the respective product category table.

//1. Initialize data from database
//2. create and display UI

public class getProductDetails
{
    static ArrayList<String> data = new ArrayList<>();
    static ArrayList<String> dataNames = new ArrayList<>();


    //Here we query the database to match the part number with all of the attributes
    //by looking up the part number in its respective product category.
    public static void getProperUI(String productCategory,String partNumber)
    {
        //We have to clear the array every time the method is called so
        //the data does not show up multiple times on the UI
        data.clear();
        dataNames.clear();

        //----------------------------------------------------------------------------

        //Initialize dataNames with data from productCategory
        DAL.productBuilderDAO.getCategoryDetails(dataNames,productCategory);

        //----------------------------------------------------------------------------

        //Initializes data with attributes from part number
        DAL.productBuilderDAO.getPartNumberDetails(data,productCategory,partNumber);


        //Now that everything is initialized we can
        //create the Main UI
        createUI();
    }



    private static void initLabel(Label source, Stage window)
    {
        ContextMenu menu = new ContextMenu();
        MenuItem copyItem = new MenuItem("    Copy Text    ");
        MenuItem closeWindow = new MenuItem("    Close    ");

        menu.getItems().addAll(copyItem,closeWindow);
        source.setContextMenu(menu);

        copyItem.setOnAction(e -> utilities.copyToClip(source.getText()));
        closeWindow.setOnAction(e -> window.close());
    }



    private static void createUI()
    {
        Stage window = new Stage();
        window.setTitle("Product Details");
        window.initModality(Modality.APPLICATION_MODAL);
        window.setWidth(500);
        window.setHeight(350);


        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10,40,40,40));

        HBox headerBox = new HBox(10);
        headerBox.setPadding(new Insets(20,0,20,0));
        headerBox.setAlignment(Pos.CENTER);

        Label header = new Label("Product Details");
        header.getStyleClass().add("button-labels");
        headerBox.getChildren().add(header);

        GridPane grid = new GridPane();
        grid.setVgap(20);
        grid.setHgap(20);
        grid.setAlignment(Pos.CENTER);

        //Since 'dataNames' contains all of the column names
        //in the entry we queried for, we are guaranteed that
        //'data' and 'dataNames' are the same length. 'data'
        //holds the data in the columns and 'dataNames' hold
        //the names of the columns
        for(int i=0; i<data.size(); i++)
        {
            Label names = new Label(dataNames.get(i) + ":");
            names.getStyleClass().add("desc-labels");
            names.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
            Label dataLabels = new Label(data.get(i));
            initLabel(dataLabels,window);
            dataLabels.setWrapText(true);

            grid.add(names,0,i);
            grid.add(dataLabels,1,i);
        }

        mainLayout.setTop(headerBox);
        mainLayout.setCenter(grid);

        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add("CSS/buyers.css");
        window.setScene(scene);
        window.showAndWait();
    }
}



