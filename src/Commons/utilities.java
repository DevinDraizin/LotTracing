package Commons;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.util.Random;

public class utilities
{
    private static Random rnd = new Random();

    //Copy selected string to user clipboard
    public static void copyToClip(String msg)
    {
        Clipboard clipboard = Clipboard.getSystemClipboard();

        final ClipboardContent content = new ClipboardContent();

        content.putString(msg);

        clipboard.setContent(content);
    }

    //Generate a unique Component Lot Number
    //8 digit integer that does not exist in
    //the Component_Lots table
    public static String generateUniqueCompLotNumber()
    {
        int n;

        do { n = 10000000 + rnd.nextInt(90000000); }while(!DAL.componentDAO.checkComponentLotNum(String.valueOf(n)));

        return String.valueOf(n);
    }

}
