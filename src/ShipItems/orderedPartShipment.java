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
}
