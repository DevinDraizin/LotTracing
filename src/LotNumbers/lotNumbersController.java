package LotNumbers;

import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import main.Main;
import screensframework.ControlledScreen;
import screensframework.ScreensController;

//Part of the importance of commenting code is so that
//When you come back to a method you wrote you not only
//understand what it is doing, but also where all the
//arguments and referenced objects are coming from.

public class lotNumbersController implements ControlledScreen
{

    public AnchorPane componentSource;
    public AnchorPane assemblySource;

    ScreensController myController;

    @FXML
    componentLotController componentSourceController;

    @FXML
    assemblyLotController assemblySourceController;

    @FXML
    JFXTabPane tabLayout;

    @FXML
    JFXTextField searchField;

    @FXML
    Label headerLabel;



    @Override
    public void setScreenParent(ScreensController screenPage)
    {
        myController = screenPage;
    }

    @Override
    public void update()
    {
        //Whenever a new component or vendor is added we
        //call the update function inside the component controller
        //to query the database again
        componentSourceController.updateComponentTable();
        assemblySourceController.updateAssemblyTable();
    }

    public void initialize()
    {
        componentSourceController.setParentController(this);
        assemblySourceController.setParentController(this);

        componentSourceController.initSearchField(searchField);
        searchField.setVisible(false);
        tabLayout.getSelectionModel().select(1);
    }

    public void goBack()
    {
        assemblySourceController.clearAll();

        if(!tabLayout.getSelectionModel().isSelected(1))
        {
            tabLayout.getSelectionModel().select(1);
        }


        myController.setScreen(Main.screen1ID);
    }


    public void goToComponent()
    {
        componentSourceController.updateComponentTable();
        tabLayout.getSelectionModel().select(0);
        headerLabel.setText("Create Component Lot");
        componentSource.requestFocus();
        searchField.setVisible(true);
    }

    public void goToAssembly()
    {
        assemblySourceController.updateAssemblyTable();
        tabLayout.getSelectionModel().select(2);
        headerLabel.setText("Create Assembly Lot");

    }



}
