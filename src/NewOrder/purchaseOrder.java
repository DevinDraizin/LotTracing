package NewOrder;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

public class purchaseOrder extends RecursiveTreeObject<purchaseOrder>
{
    public SimpleStringProperty PONumber;
    public SimpleStringProperty SONumber;
    public LocalDate PODate;
    public LocalDate dueDate;
    public SimpleStringProperty buyerID;

    public purchaseOrder(String PONumber, String SONumber, LocalDate PODate, LocalDate dueDate, String buyerID)
    {
        this.PONumber.setValue(PONumber);
        this.SONumber.setValue(SONumber);
        this.PODate = PODate;
        this.dueDate = dueDate;
        this.buyerID.setValue(buyerID);
    }

    public purchaseOrder()
    {
        this.PONumber = null;
        this.SONumber = null;
        this.PODate = null;
        this.dueDate = null;
        this.buyerID = null;
    }
}