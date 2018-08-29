package Components;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;

public class component extends RecursiveTreeObject<component>
{
    public SimpleStringProperty partNumber;
    public SimpleStringProperty description;
    public SimpleStringProperty section;


    public component(String partNumber, String description, String section)
    {
        this.partNumber = new SimpleStringProperty(partNumber);
        this.description = new SimpleStringProperty(description);
        this.section = new SimpleStringProperty(section);
    }

    public component()
    {
        partNumber = null;
        description = null;
        section = null;
    }
}
