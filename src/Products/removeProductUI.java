package Products;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;


//This class handles removing products from the database as well as
//program memory to update the UI.
public class removeProductUI
{

    //We are guaranteed that product will not be null so we can go strait to removing it
    //To do this we need to remove the product from the product table, it's category table,
    //and from the productList on the Main UI.
    private static void removeProduct(product product,ObservableList<product> productList)
    {
        //extract product category and partNumber
        String category = product.productCategory.get();
        String partNumber = product.partNumber.get();

        Alert result = new Alert(Alert.AlertType.CONFIRMATION);
        result.setTitle("Success");

        //the remove methods in the data access objects return
        //true for successful remove else false
        //here we remove the product from the product table and
        //its category table.
        boolean a = DAL.productDAO.removeProduct(partNumber);
        boolean b = DAL.productBuilderDAO.removeProduct(category,partNumber);


        //Search the product list for the partNumber and when
        //we find it, remove it and then break.
        for(int i=0; i<productList.size(); i++)
        {
            if(productList.get(i).partNumber.getValue().equals(partNumber))
            {
                productList.remove(i);
                break;
            }
        }

        //If either of the database removes fails we prompt the user.
        if(!a || !b)
        {
            result.setAlertType(Alert.AlertType.ERROR);
            result.setHeaderText("There was an error removing the product");
            result.setTitle("Error");
        }
        else
        {
            result.setHeaderText("Successfully removed " + partNumber + " from the database");
        }

        result.show();
    }


    //This method is called when we select remove product from a context menu or from
    //the remove button itself. Since we require the user to select the product they want
    //to remove before we do anything, we can guarantee the product parameter will not be null.
    //Now we prompt the user to confirm the remove function.
    public static void createUI(product product, ObservableList<product> productList)
    {

        //We don't need a custom UI since we are only confirming the remove
        //lets use an alert box
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Remove Product?");
        confirm.setHeaderText("Are you sure you want to remove this product?");
        confirm.setContentText("This action cannot be undone.\n\n");

        ButtonType delete = new ButtonType("Yes");
        ButtonType  quit = new ButtonType("No");


        confirm.getButtonTypes().setAll(delete,quit);
        Optional<ButtonType> result = confirm.showAndWait();


        //If the user confirms call the removeProduct() method
        if(result.isPresent() && result.get() == delete)
        {
            removeProduct(product,productList);
        }

    }
}
