package ProductBuilder;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.jensd.fx.glyphs.octicons.OctIcon;
import de.jensd.fx.glyphs.octicons.OctIconView;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import main.main;
import screensframework.ControlledScreen;
import screensframework.ScreensController;


public class productBuilderController implements ControlledScreen
{
    ScreensController myController;

    @FXML
    JFXTreeTableView<productCategory> table;

    @FXML
    JFXListView<Label> listView;

    @FXML
    JFXTextField nameField, categoryField;

    private ObservableList<productCategory> tableData;

    @Override
    public void setScreenParent(ScreensController screenPage)
    {
        myController = screenPage;
    }

    public void initialize()
    {
        initTable();
        tableListener();
        initListView();
    }

    private void initializeTableData()
    {
       tableData = DAL.productBuilderDAO.getProductCategoryList();
    }


    private void initListView()
    {
        listView.setExpanded(true);

        if(table.currentItemsCountProperty().getValue() > 0)
        {
            table.getSelectionModel().select(0);
        }

        //Change the background color of the last element in the list view (Add)
        listView.setCellFactory(new Callback<ListView<Label>, ListCell<Label>>() {
            @Override
            public ListCell<Label> call(ListView<Label> param) {
                return new JFXListCell<Label>(){
                    @Override
                    public void updateItem(Label item, boolean empty) {
                        super.updateItem(item, empty);
                        if(item!=null && !empty){
                            if(item.getText().isEmpty())
                            {
                                setStyle("-fx-background-color: #3f50b5");
                            }
                        }
                    }
                };
            }
        });

    }


    private Label getEndLabel()
    {
        Label last = new Label();
        OctIconView icon = new OctIconView(OctIcon.PLUS);
        icon.setFill(Color.WHITE);
        icon.setGlyphSize(28);
        last.setGraphic(icon);

        return last;
    }

    //When we click on an existing category from the table view
    //initialize the editor with the contents.
    private void tableListener()
    {
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            listView.getItems().clear();

            categoryField.setText(newValue.getValue().categoryName.getValue());
            nameField.setText(newValue.getValue().productName.getValue());

            for(int i=0; i<newValue.getValue().attributes.size(); i++)
            {
                listView.getItems().add(new Label(newValue.getValue().attributes.get(i)));
            }

            listView.getItems().add(getEndLabel());
        });
    }



    private void initTable()
    {
        JFXTreeTableColumn<productCategory,String> categoryCol = new JFXTreeTableColumn<>("Category");
        categoryCol.setCellValueFactory(param -> param.getValue().getValue().categoryName);

        JFXTreeTableColumn<productCategory,String> nameCol = new JFXTreeTableColumn<>("Name");
        nameCol.setCellValueFactory(param -> param.getValue().getValue().productName);

        initializeTableData();

        final TreeItem<productCategory> root = new RecursiveTreeItem<>(tableData, RecursiveTreeObject::getChildren);
        table.setRoot(root);
        table.setShowRoot(false);
        table.getColumns().setAll(categoryCol,nameCol);
    }



    public void goBack()
    {
        myController.setScreen(main.screen9ID);
    }
}


