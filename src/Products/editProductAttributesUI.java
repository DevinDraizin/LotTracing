package Products;



public class editProductAttributesUI
{
    static void commitEdit()
    {

    }

    public static void createUI(product product)
    {
        addProductAttributesUI.createUI(product.productCategory.getValue(),2,product,null);
    }
}
