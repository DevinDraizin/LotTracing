package Vendors;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class vendor extends RecursiveTreeObject<vendor>
{
    public ObservableValue<Integer> vendorID;
    public StringProperty vendorName;



    public vendor(Integer vendorID, String vendorName)
    {
        if(vendorID != null)
        this.vendorID = new SimpleIntegerProperty(vendorID).asObject();
        this.vendorName = new SimpleStringProperty(vendorName);
    }

    public vendor()
    {
        this.vendorID = null;
        this.vendorName = null;
    }
}
