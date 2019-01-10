package LotNumbers;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

public class componentLot extends RecursiveTreeObject<componentLot>
{
    public SimpleStringProperty ComponentLotNumber;
    public LocalDate receiveDate;
    public SimpleIntegerProperty receiveQty;
    public SimpleStringProperty vendorPO;
    public SimpleIntegerProperty vendorID;
    public SimpleStringProperty componentPartNumber;


    public componentLot(String componentLotNumber, LocalDate receiveDate, Integer receiveQty, String vendorPO, Integer vendorID, String componentPartNumber)
    {
        this.ComponentLotNumber = new SimpleStringProperty(componentLotNumber);
        this.receiveDate = receiveDate;
        this.receiveQty = new SimpleIntegerProperty(receiveQty);
        this.vendorPO = new SimpleStringProperty(vendorPO);
        this.vendorID = new SimpleIntegerProperty(vendorID);
        this.componentPartNumber = new SimpleStringProperty(componentPartNumber);
    }

    public componentLot()
    {
        this.ComponentLotNumber = null;
        this.receiveDate = null;
        this.receiveQty = null;
        this.vendorPO = null;
        this.vendorID = null;
        this.componentPartNumber = null;
    }

}
