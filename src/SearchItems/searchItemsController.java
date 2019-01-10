package SearchItems;

import screensframework.ControlledScreen;
import screensframework.ScreensController;

public class searchItemsController implements ControlledScreen
{
    ScreensController myController;

    @Override
    public void setScreenParent(ScreensController screenPage) {
        myController = screenPage;
    }

    @Override
    public void update() {

    }
}
