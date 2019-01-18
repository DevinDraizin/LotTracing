package DAL;


import Products.product;
import com.jfoenix.controls.JFXComboBox;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class productDAO
{
    //Here we pull all buyer data from the table and
    //convert them into an observable list of buyer
    //objects.
    public static void getProductList(ObservableList<product> productList)
    {

        productList.clear();

        PreparedStatement stmt = null;
        ResultSet myRst = null;

        try
        {
            String pstmt = "SELECT * FROM Products;";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);
            myRst = stmt.executeQuery();

            while(myRst.next())
            {
                productList.add(extractProductFromResultSet(myRst));
            }

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try { if (myRst != null) myRst.close(); } catch (Exception ignored) {}
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
        }

    }

    //Auxiliary method to getProductList()
    //this method will take a specific row from the product
    //table and convert the data into a product object.
    //once converted, we return a product object
    private static product extractProductFromResultSet(ResultSet myRs) throws SQLException {
        product product = new product();

        product.partNumber = new SimpleStringProperty(myRs.getString(1));
        product.productName = new SimpleStringProperty(myRs.getString(2));
        product.activeStatus = new SimpleStringProperty(myRs.getString(3));
        product.UOM = new SimpleStringProperty(myRs.getString(4));
        product.cost = new SimpleDoubleProperty(myRs.getDouble(5)).asObject();
        product.price = new SimpleDoubleProperty(myRs.getDouble(6)).asObject();
        product.UPC = new SimpleStringProperty(myRs.getString(7));
        product.productCategory = new SimpleStringProperty(myRs.getString(8));
        product.lotSuffix = new SimpleStringProperty(myRs.getString(9));

        return product;
    }

    //returns true if partNumber is NOT in the table and
    //false otherwise.
    public static boolean checkPartNumber(String partNumber)
    {
        PreparedStatement stmt = null;
        ResultSet myRst = null;

        try
        {
            String pstmt = "SELECT Product_Category FROM Products WHERE Part_Number = ?;";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);
            stmt.setString(1,partNumber);
            myRst = stmt.executeQuery();

            if(!myRst.next())
            {
                return true;
            }


        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try { if (myRst != null) myRst.close(); } catch (Exception ignored) {}
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
        }

        return false;
    }

    public static boolean getProductCategories(JFXComboBox<String> productCategoryDrop)
    {
        PreparedStatement stmt = null;
        ResultSet myRst = null;

        try
        {
            String pstmt = "SELECT Product_Category FROM Product_Categories;";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);
            myRst = stmt.executeQuery();

            while(myRst.next())
            {
                productCategoryDrop.getItems().add(myRst.getString(1));
            }

            return true;

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try { if (myRst != null) myRst.close(); } catch (Exception ignored) {}
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
        }

        return false;
    }

    public static boolean insertProduct(product product)
    {
        PreparedStatement stmt = null;

        try
        {
            String pstmt = "INSERT INTO Products (Part_Number, Product_Name, Active_Status, UOM, Cost, Price, UPC, Product_Category, Lot_Suffix) VALUES (?,?,?,?,?,?,?,?,?);";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);

            stmt.setString(1,product.partNumber.getValue());
            stmt.setString(2,product.productName.getValue());
            stmt.setString(3,product.activeStatus.getValue());
            stmt.setString(4,product.UOM.getValue());
            stmt.setDouble(5,product.cost.getValue());
            stmt.setDouble(6,product.price.getValue());
            stmt.setString(7,product.UPC == null ? null : product.UPC.getValue());
            stmt.setString(8,product.productCategory.getValue());
            stmt.setString(9,product.lotSuffix == null ? null : product.lotSuffix.getValue());

            stmt.executeUpdate();

            return true;

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
        }

        return false;
    }


    public static boolean getUOM(JFXComboBox<String> UOMDrop)
    {
        PreparedStatement stmt = null;
        ResultSet myRst = null;

        try
        {
            String pstmt = "SELECT DISTINCT UOM FROM Products;";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);
            myRst = stmt.executeQuery();

            while(myRst.next())
            {
                UOMDrop.getItems().add(myRst.getString(1));
            }

            return true;

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try { if (myRst != null) myRst.close(); } catch (Exception ignored) {}
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
        }

        return false;
    }


    //This will removed a specified product from the product table
    //DO NOT CALL THIS METHOD DIRECTLY. Since a part number exists in
    //the products table AND it's respective category table we must remove
    //it from both. This will be called in conjunction with the remove product
    //method in the productBuilderDAO which will handle removing from its category table.
    public static boolean removeProduct(String partNumber)
    {
        PreparedStatement stmt = null;

        try
        {
            String pstmt = "DELETE FROM Products WHERE Part_Number = ?";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);
            stmt.setString(1,partNumber);
            int i = stmt.executeUpdate();

            if(i==1)
            {
                return true;
            }

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
        }

        return false;
    }
}
