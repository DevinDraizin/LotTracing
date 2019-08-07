package NewOrder;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;


//This is a DAO for the Ordered Part table
public class orderedPart extends RecursiveTreeObject<orderedPart>
{
    //TransactionID is the primary key for the table
    //and is auto incremented
    public SimpleIntegerProperty transactionID;
    public SimpleStringProperty partNumber;
    public SimpleIntegerProperty QtyOrdered;
    public SimpleStringProperty PONumber;
    public SimpleStringProperty description;
    public ObservableValue<Double> price;
    public ObservableValue<Double> totalPrice;

    public orderedPart(Integer transactionID, String partNumber, Integer QtyOrdered,
                       String PONumber, String description, Double price, Double totalPrice) {
        this.transactionID = new SimpleIntegerProperty(transactionID);
        this.partNumber = new SimpleStringProperty(partNumber);
        this.QtyOrdered = new SimpleIntegerProperty(QtyOrdered);
        this.PONumber = new SimpleStringProperty(PONumber);
        this.description = new SimpleStringProperty(description);
        this.price = new SimpleDoubleProperty(price).asObject();
        this.totalPrice = new SimpleDoubleProperty(totalPrice).asObject();
    }

    public orderedPart() {
        this.transactionID = null;
        this.partNumber = null;
        this.QtyOrdered = null;
        this.PONumber = null;
        this.description = null;
        this.price = null;
        this.totalPrice = null;
    }



}
