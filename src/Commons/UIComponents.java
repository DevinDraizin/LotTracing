package Commons;


import Components.component;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

//We reuse a lot of UI components in each view that we
//should probably source from methods here.
//
//We should eventually move all the JFXTreeTableViews into a more general
//initialization method
public class UIComponents
{
    public static void initComponentTable(JFXTreeTableView<component> table, ObservableList<component> componentList, String nullCharacter)
    {
        JFXTreeTableColumn<component,String> partNumberCol = new JFXTreeTableColumn<>("Part Number");
        partNumberCol.setCellValueFactory(param -> param.getValue().getValue().partNumber);

        JFXTreeTableColumn<component,String> descriptionCol = new JFXTreeTableColumn<>("Description");
        descriptionCol.setCellValueFactory(param -> param.getValue().getValue().description);

        JFXTreeTableColumn<component,String> sectionCol = new JFXTreeTableColumn<>("Section");
        sectionCol.setCellValueFactory(param -> param.getValue().getValue().description);


        DAL.componentDAO.getComponentsList(componentList);

        sectionCol.setCellValueFactory(param -> (param.getValue().getValue().section.getValue() == null) ? new SimpleStringProperty(nullCharacter) : param.getValue().getValue().section);



        final TreeItem<component> root = new RecursiveTreeItem<>(componentList, RecursiveTreeObject::getChildren);

        table.getColumns().addAll(partNumberCol,descriptionCol,sectionCol);
        table.setRoot(root);
        table.setShowRoot(false);
    }

    public static void initComponentSearchField(JFXTextField searchField, JFXTreeTableView<component> table)
    {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> table.setPredicate(buyerTreeItem ->
                buyerTreeItem.getValue().partNumber.getValue().toUpperCase().contains(newValue.toUpperCase()) ||
                        Commons.staticLookupCommons.checkNull(buyerTreeItem.getValue().section.getValue()).toUpperCase().contains(newValue.toUpperCase()) ||
                        Commons.staticLookupCommons.checkNull(buyerTreeItem.getValue().description.getValue()).toUpperCase().contains(newValue.toUpperCase())));
    }
}
