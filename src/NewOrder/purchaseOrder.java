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
    public Integer buyerID;
    public SimpleStringProperty memos;

    public purchaseOrder(String PONumber, String SONumber, LocalDate PODate, LocalDate dueDate, Integer buyerID, String memos)
    {
        this.PONumber = new SimpleStringProperty(PONumber);
        this.SONumber = new SimpleStringProperty(SONumber);
        this.PODate = PODate;
        this.dueDate = dueDate;
        this.buyerID = buyerID;
        this.memos = new SimpleStringProperty(memos);
    }

    public purchaseOrder()
    {
        this.PONumber = null;
        this.SONumber = null;
        this.PODate = null;
        this.dueDate = null;
        this.buyerID = null;
        this.memos = null;
    }
}