package Components;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import main.main;
import screensframework.ControlledScreen;
import screensframework.ScreensController;

public class componentsController implements ControlledScreen
{

    @FXML
    JFXTreeTableView<component> table;

    @FXML
    JFXTextField searchField;

    private static ObservableList<component> componentList;
    private static String nullCharacter = "--";

    ScreensController myController;

    @Override
    public void setScreenParent(ScreensController screenPage) {
        myController = screenPage;
    }

    public void initialize()
    {
        initTable();
        initSearchField();
    }

    private void initTable()
    {
        JFXTreeTableColumn<component,String> partNumberCol = new JFXTreeTableColumn<>("Part Number");
        partNumberCol.setCellValueFactory(param -> param.getValue().getValue().partNumber);

        JFXTreeTableColumn<component,String> descriptionCol = new JFXTreeTableColumn<>("Description");
        descriptionCol.setCellValueFactory(param -> param.getValue().getValue().description);

        JFXTreeTableColumn<component,String> sectionCol = new JFXTreeTableColumn<>("Section");
        sectionCol.setCellValueFactory(param -> param.getValue().getValue().description);



        componentList = DAL.componentDAO.getComponentsList();

        sectionCol.setCellValueFactory(param -> (param.getValue().getValue().section.getValue() == null) ? new SimpleStringProperty(nullCharacter) : param.getValue().getValue().section);



        final TreeItem<component> root = new RecursiveTreeItem<>(componentList, RecursiveTreeObject::getChildren);

        table.getColumns().addAll(partNumberCol,descriptionCol,sectionCol);
        table.setRoot(root);
        table.setShowRoot(false);
    }

    private void initSearchField()
    {

        searchField.textProperty().addListener((observable, oldValue, newValue) -> table.setPredicate(buyerTreeItem ->
                buyerTreeItem.getValue().partNumber.getValue().toUpperCase().contains(newValue.toUpperCase()) ||
                        Commons.staticLookupCommons.checkNull(buyerTreeItem.getValue().section.getValue()).toUpperCase().contains(newValue.toUpperCase()) ||
                        Commons.staticLookupCommons.checkNull(buyerTreeItem.getValue().description.getValue()).toUpperCase().contains(newValue.toUpperCase())));
    }

    public void goBack()
    {
        myController.setScreen(main.screen1ID);
    }

    public void addComponent()
    {
        addComponentUI.createUI();
    }

    public void editComponent()
    {
        editComponentUI.createUI();
    }


}
