package ReturnItems;

import screensframework.ControlledScreen;
import screensframework.ScreensController;

public class returnItemsController implements ControlledScreen
{
    ScreensController myController;

    @Override
    public void setScreenParent(ScreensController screenPage) {
        myController = screenPage;
    }
}
