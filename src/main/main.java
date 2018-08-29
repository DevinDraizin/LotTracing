package main;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import screensframework.ScreensController;

import java.sql.SQLException;


/*
    The goal of this application is to perform different types
    of lot tracing and management functions. It manages existing
    products as well as supports the creation of new products. Each
    product can also be broken down into components that are also
    tracked by lot. Simple vendor and buyer management is also
    supported. Managing products/components/vendors/buyers etc
    allows for detailed tracking of purchase orders along with
    shipping statuses. Lastly, the application implements a global
    search to quickly get information about current and past orders.

*/

public class main extends Application
{

    //ID and File names corresponding to all fxml screens
    public static String screen1ID = "main";
    public static String screen1File = "/main/LotTracer.fxml";
    public static String screen2ID = "NewOrder";
    public static String screen2File = "/NewOrder/newOrder.fxml";
    public static String screen3ID = "ShipItems";
    public static String screen3File = "/ShipItems/shipItems.fxml";
    public static String screen4ID = "ReturnItems";
    public static String screen4File = "/ReturnItems/returnItems.fxml";
    public static String screen5ID = "ShipItems";
    public static String screen5File = "/ShipItems/shipItems.fxml";
    public static String screen6ID = "Buyers";
    public static String screen6File = "/Buyers/buyers.fxml";
    public static String screen7ID = "Vendors";
    public static String screen7File = "/Vendors/vendors.fxml";
    public static String screen8ID = "Components";
    public static String screen8File = "/Components/components.fxml";
    public static String screen9ID = "Products";
    public static String screen9File = "/Products/products.fxml";
    public static String screen10ID = "LotNumbers";
    public static String screen10File = "/LotNumbers/lotNumbers.fxml";
    public static String screen11ID = "ProductBuilder";
    public static String screen11File = "/ProductBuilder/productBuilder.fxml";


    @Override
    public void start(Stage primaryStage)
    {
        Boolean checkConn;

        checkConn = DAL.DBConnectionManager.establishConnection();

        if(!checkConn)
        {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Database Error");
            error.setHeaderText("Database connection failed\nPlease make sure that the database is online");
            error.showAndWait();

            return;
        }

        //Load all screens into the screen controller
        ScreensController mainContainer = new ScreensController();
        mainContainer.loadScreen(main.screen1ID, main.screen1File);
        mainContainer.loadScreen(main.screen2ID, main.screen2File);
        mainContainer.loadScreen(main.screen3ID, main.screen3File);
        mainContainer.loadScreen(main.screen4ID, main.screen4File);
        mainContainer.loadScreen(main.screen5ID, main.screen5File);
        mainContainer.loadScreen(main.screen6ID, main.screen6File);
        mainContainer.loadScreen(main.screen7ID, main.screen7File);
        mainContainer.loadScreen(main.screen8ID, main.screen8File);
        mainContainer.loadScreen(main.screen9ID, main.screen9File);
        mainContainer.loadScreen(main.screen10ID, main.screen10File);
        mainContainer.loadScreen(main.screen11ID, main.screen11File);

        mainContainer.setScreen(main.screen1ID);

        Scene scene = new Scene(mainContainer);
        primaryStage.getIcons().add(new Image("images/headsUpIcon.png"));
        primaryStage.setTitle("Lot Tracer");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();


        //Before we shut down the application we need to
        //terminate the database connection
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            try {
                DAL.DBConnectionManager.closeConnection();
            } catch (SQLException e) {
                System.out.println("Failed to close database connection");
                e.printStackTrace();
            }
            primaryStage.close();
        });


    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
