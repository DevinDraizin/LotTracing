package Commons;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class utils
{
    public static void copyToClip(String msg)
    {
        Clipboard clipboard = Clipboard.getSystemClipboard();

        final ClipboardContent content = new ClipboardContent();

        content.putString(msg);

        clipboard.setContent(content);
    }
}
