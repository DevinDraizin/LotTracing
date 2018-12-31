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

    @Override
    public void update(){}


    public void getNewOrderScene()
    {
        myController.setScreen(Main.screen2ID);
    }

    public void getShipItemsScene()
    {
        myController.setScreen(Main.screen3ID);
    }

    public void getReturnItemsScene()
    {
        myController.setScreen(Main.screen4ID);
    }

    public void getSearchScene()
    {
        myController.setScreen(Main.screen5ID);
    }

    public void getBuyersScene()
    {
        myController.setScreen(Main.screen6ID);
    }

    public void getVendorsScene()
    {
        myController.setScreen(Main.screen7ID);
    }

    public void getComponentsScene()
    {
        myController.setScreen(Main.screen8ID);
    }

    public void getProductsScene()
    {
        myController.setScreen(Main.screen9ID);
    }

    public void getLotNumbersScene()
    {
        myController.setScreen(Main.screen10ID);
    }



}
