package Buyers;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;


public class buyer extends RecursiveTreeObject<buyer>
{
    public ObservableValue<Integer> buyerID;
    public StringProperty buyerName;
    public StringProperty company;
    public StringProperty email;
    public StringProperty phoneNumber;

    public buyer(Integer buyerID, String buyerName, String company, String email, String phoneNumber)
    {
        if(buyerID != null)
        this.buyerID = new SimpleIntegerProperty(buyerID).asObject();
        this.buyerName = new SimpleStringProperty(buyerName);
        this.company = new SimpleStringProperty(company);
        this.email = new SimpleStringProperty(email);
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
    }

    public buyer()
    {
        this.buyerID = null;
        this.buyerName = null;
        this.company = null;
        this.email = null;
        this.phoneNumber = null;
    }

}
