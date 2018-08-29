package Products;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class product extends RecursiveTreeObject<product>
{
    public StringProperty partNumber;
    public StringProperty productName;
    public StringProperty activeStatus;
    public StringProperty UOM;
    public ObservableValue<Double> cost;
    public ObservableValue<Double> price;
    public StringProperty UPC;
    public StringProperty productCategory;

    public product()
    {
        this.partNumber = null;
        this.productName = null;
        this.activeStatus = null;
        this.UOM = null;
        this.cost = null;
        this.price = null;
        this.UPC = null;
        this.productCategory = null;
    }

    product(String partNumber, String productName, String activeStatus, String UOM, Double cost, Double price, String UPC, String productCategory)
    {
        this.partNumber = new SimpleStringProperty(partNumber);
        this.productName = new SimpleStringProperty(productName);
        this.activeStatus = new SimpleStringProperty(activeStatus);
        this.UOM = new SimpleStringProperty(UOM);
        this.cost = new SimpleDoubleProperty(cost).asObject();
        this.price = new SimpleDoubleProperty(price).asObject();
        this.UPC = new SimpleStringProperty(UPC);
        this.productCategory = new SimpleStringProperty(productCategory);
    }

}
