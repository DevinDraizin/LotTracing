package main;


import screensframework.ControlledScreen;
import screensframework.ScreensController;


public class LotTracerController implements ControlledScreen
{
    private ScreensController myController;


    @Override
    public void setScreenParent(ScreensController screenPage)
    {
        myController = screenPage;
    }


    public void getNewOrderScene()
    {
        myController.setScreen(main.screen2ID);
    }

    public void getShipItemsScene()
    {
        myController.setScreen(main.screen3ID);
    }

    public void getReturnItemsScene()
    {
        myController.setScreen(main.screen4ID);
    }

    public void getSearchScene()
    {
        myController.setScreen(main.screen5ID);
    }

    public void getBuyersScene()
    {
        myController.setScreen(main.screen6ID);
    }

    public void getVendorsScene()
    {
        myController.setScreen(main.screen7ID);
    }

    public void getComponentsScene()
    {
        myController.setScreen(main.screen8ID);
    }

    public void getProductsScene()
    {
        myController.setScreen(main.screen9ID);
    }

    public void getLotNumbersScene()
    {
        myController.setScreen(main.screen10ID);
    }



}
