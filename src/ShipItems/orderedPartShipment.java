package ShipItems;

import NewOrder.orderedPart;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleIntegerProperty;

public class orderedPartShipment extends RecursiveTreeObject<orderedPartShipment>
{
    public orderedPart orderedPart;
    public SimpleIntegerProperty amountToShip;

    public orderedPartShipment(orderedPart part, Integer amt) {
        this.orderedPart = part;
        this.amountToShip = new SimpleIntegerProperty(amt);

    }

    public orderedPartShipment() {
        this.orderedPart = new orderedPart();
        this.amountToShip = new SimpleIntegerProperty(0);
    }


    //Since this object is mutable we will override hashcode and equals
    //to operate using the transactionID since we know that is always
    //unique and immutable.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return this.orderedPart.transactionID.get() == ((orderedPartShipment) o).orderedPart.transactionID.get();
    }

    @Override
    public int hashCode() {
        return this.orderedPart.transactionID.get();
    }
}
