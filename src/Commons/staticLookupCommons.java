package Commons;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.octicons.OctIcon;
import de.jensd.fx.glyphs.octicons.OctIconView;
import javafx.scene.control.Alert;
import javafx.scene.control.ContentDisplay;
import javafx.scene.paint.Color;

import java.util.regex.Pattern;

public class staticLookupCommons
{
    public static String checkNull(String in)
    {
        return (in != null) ? in : "";
    }

    //Returns a JFXButton with a specified title and icon
    //formats as well
    public static JFXButton createButton(String title, OctIcon icon)
    {
        JFXButton button = new JFXButton(title);
        OctIconView view = new OctIconView(icon);
        view.setGlyphSize(18);
        view.setFill(Color.valueOf("white"));
        button.setGraphic(view);
        button.setGraphicTextGap(12);
        button.contentDisplayProperty().setValue(ContentDisplay.RIGHT);
        button.getStyleClass().add("primary-button-1");
        button.setStyle("-fx-font-size: 16");


        return button;
    }

    //regex for phone number sanitation
    public static Boolean sanPhone(String phone)
    {

        if((phone.matches("^\\d{3}-\\d{3}-\\d{4}$") && (phone.length() > 0)))
        {
            return true;
        }
        else
        {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Validation Error");
            err.setHeaderText("");
            err.setContentText("Please only enter the digits of the phone number");
            err.showAndWait();
            return false;
        }
    }

    //regex for email sanitation
    public static Boolean sanEmail(String email)
    {
        String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);


        if((email.matches(pattern.toString()) && (email.length() > 0)))
        {
            return true;
        }
        else
        {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Validation Error");
            err.setHeaderText("");
            err.setContentText("Email must be valid");
            err.showAndWait();
            return false;
        }
    }



}
