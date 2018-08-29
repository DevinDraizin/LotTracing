package ShipItems;

import screensframework.ControlledScreen;
import screensframework.ScreensController;

public class shipItemsController implements ControlledScreen
{
    ScreensController myController;

    @Override
    public void setScreenParent(ScreensController screenPage) {
        myController = screenPage;
    }
}
