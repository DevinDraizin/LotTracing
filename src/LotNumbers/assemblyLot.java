package LotNumbers;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

public class assemblyLot extends RecursiveTreeObject<assemblyLot>
{
    public SimpleStringProperty AssemblyLotNumber;
    public LocalDate assembleDate;
    public SimpleIntegerProperty assembleQty;
    public SimpleStringProperty partNumber;
    public SimpleStringProperty memos;


    public assemblyLot(String assemblyLotNumber, LocalDate assembleDate, Integer assembleQty, String partNumber,
                       String memos)
    {
        this.AssemblyLotNumber = new SimpleStringProperty(assemblyLotNumber);
        this.assembleDate = assembleDate;
        this.assembleQty = new SimpleIntegerProperty(assembleQty);
        this.partNumber = new SimpleStringProperty(partNumber);
        this.memos = new SimpleStringProperty(memos);
    }

    public assemblyLot()
    {
        this.AssemblyLotNumber = null;
        this.assembleDate = null;
        this.assembleQty = null;
        this.partNumber = null;
        this.memos = null;
    }
}
