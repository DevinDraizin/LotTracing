package LotNumbers;

import screensframework.ControlledScreen;
import screensframework.ScreensController;

public class lotNumbersController implements ControlledScreen
{
    ScreensController myController;

    @Override
    public void setScreenParent(ScreensController screenPage)
    {
        myController = screenPage;
    }
}
