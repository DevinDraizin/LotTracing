package Components;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import main.Main;
import screensframework.ControlledScreen;
import screensframework.ScreensController;

public class componentsController implements ControlledScreen
{

    @FXML
    JFXTreeTableView<component> table;

    @FXML
    JFXTextField searchField;

    private static ObservableList<component> componentList = FXCollections.observableArrayList();

    ScreensController myController;

    @Override
    public void setScreenParent(ScreensController screenPage) {
        myController = screenPage;
    }

    @Override
    public void update() {

    }

    public void initialize()
    {
        initComponentTable();
        initSearchField();
    }

    private void initComponentTable()
    {
        String nullCharacter = "--";
        Commons.UIComponents.initComponentTable(table,componentList, nullCharacter);
    }

    private void initSearchField()
    {
        Commons.UIComponents.initComponentSearchField(searchField,table);
    }

    public void goBack()
    {
        myController.setScreen(Main.screen1ID);
    }

    public void addComponent()
    {
        addComponentUI.createUI();
    }

    public void editComponent()
    {
        editComponentUI.createUI();
    }

    public void removeComponent()
    {
        removeComponentUI.createUI();
    }


}
