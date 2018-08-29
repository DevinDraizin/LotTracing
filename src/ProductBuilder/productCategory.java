package ProductBuilder;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class productCategory extends RecursiveTreeObject<productCategory>
{
    public StringProperty categoryName;
    public StringProperty productName;
    public ObservableList<String> attributes;

    public productCategory()
    {
        this.categoryName = new SimpleStringProperty();
        this.productName = new SimpleStringProperty();
        attributes = FXCollections.observableArrayList();
    }

    public productCategory(String categoryName, String productName)
    {
        this.categoryName = new SimpleStringProperty(categoryName);
        this.productName = new SimpleStringProperty(productName);
        attributes = FXCollections.observableArrayList();
    }

}
