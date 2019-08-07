package Commons.UIComponents.popUps;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class Notifier
{
    public static void getSuccessNotification(String title, String content)
    {
        org.controlsfx.control.Notifications.create().title(title)
                .text("\n\n" + content)
                .position(Pos.TOP_RIGHT)
                .hideAfter(new Duration(5000))
                .graphic(new ImageView(new Image("/images/checkmark.png",true)))
                .show();
    }


    public static void getWarningNotification(String title, String content)
    {
        org.controlsfx.control.Notifications.create().title(title)
                .text("\n\n" + content)
                .position(Pos.TOP_RIGHT)
                .hideAfter(new Duration(5000))
                .graphic(new ImageView(new Image("/images/warning.png",true)))
                .show();
    }


    public static void getErrorNotification(String title, String content)
    {
        org.controlsfx.control.Notifications.create().title(title)
                .text("\n\n" + content)
                .position(Pos.TOP_RIGHT)
                .hideAfter(new Duration(5000))
                .graphic(new ImageView(new Image("/images/failed.png",true)))
                .show();
    }
}
