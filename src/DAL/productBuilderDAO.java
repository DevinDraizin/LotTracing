package DAL;

import ProductBuilder.productCategory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class productBuilderDAO
{
    //Here we pull all product category data from the table and
    //convert them into an observable list of buyer
    //objects.
    public static ObservableList<productCategory> getProductCategoryList()
    {
        ObservableList<productCategory> productCategoryList =  FXCollections.observableArrayList();

        PreparedStatement stmt = null;
        ResultSet myRst = null;

        try
        {
            String pstmt = "SELECT * FROM Product_Categories;";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);
            myRst = stmt.executeQuery();

            while(myRst.next())
            {
                productCategoryList.add(extractProductCategoryFromResultSet(myRst));
            }

            return productCategoryList;

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try { if (myRst != null) myRst.close(); } catch (Exception ignored) {}
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
        }

        return null;
    }

    //Auxiliary method to get productCategoryList()
    //this method will take a specific row from the productCategory
    //result set and convert the data into a buyer object.
    //once converted, we return a buyer object
    private static productCategory extractProductCategoryFromResultSet(ResultSet myRs) throws SQLException
    {
        productCategory productCategory = new productCategory();

        productCategory.categoryName.setValue(myRs.getString(2));
        productCategory.productName.setValue(myRs.getString(3));
        getAttributes(productCategory);


        return productCategory;
    }

    //This will initialize the attributes array for
    //the passed in productCategory object
    private static void getAttributes(productCategory product) {
        PreparedStatement stmt = null;
        ResultSet myRst = null;

        try {
            //we can use DESCRIBE query if we hard code the tale name.
            //swap the parameterized statement for a normal statement
            //object since we will not take dynamic input

            //String pstmt = "DESCRIBE ?;";

            String pstmt = "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ?";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);
            stmt.setString(1, product.categoryName.getValue());
            myRst = stmt.executeQuery();

            while (myRst.next()) {
                product.attributes.add(myRst.getString(4));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (myRst != null) myRst.close();
            } catch (Exception ignored) {
            }
            try {
                if (stmt != null) stmt.close();
            } catch (Exception ignored) {
            }
        }

    }

    //This statement is responsible for initializing the 'dataNames' array with the names of all the columns in the respective table
    //in the order they are already in.
    public static void getCategoryDetails(ArrayList<String> dataNames, String table)
    {
        PreparedStatement stmt = null;
        ResultSet myRst = null;

        try
        {
            String pstmt = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ?";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);
            stmt.setString(1,table);
            myRst = stmt.executeQuery();

            //Since this will always be a table with 1 column
            //we can just hard code 1 into the column index
            //and move row by row down the table
            while(myRst.next())
            {
                dataNames.add(myRst.getString(1).replace("_"," "));
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

    //This statement will initialize the 'data' array with the entry corresponding to the specific
    //part number contained in the specified product category table
    public static void getPartNumberDetails(ArrayList<String> data, String table, String partNumber)
    {
        PreparedStatement stmt = null;
        ResultSet myRst = null;

        try
        {
            String pstmt = "SELECT * FROM " + table + " WHERE " + table + "_Part_Number" + " = ?";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);
            stmt.setString(1,partNumber);


            myRst = stmt.executeQuery();
            ResultSetMetaData rsmd = myRst.getMetaData();

            //move the cursor to the first position
            myRst.next();

            //size is the number of columns present in the specified product category table
            //we are looking through. This loop just goes through the entry we just got and
            //inputs them into the 'data' array in order
            int size = rsmd.getColumnCount();

            for(int j=1; j<=size; j++)
            {
                data.add(myRst.getString(j));
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

    //Since we are trying to insert a variable number of attributes into
    //the specified category table, we need to construct the SQL statement
    //dynamically. This method generates the dynamic part of the insert statement
    private static String constructValues(productCategory product,ArrayList<String> attributes,String partNumber)
    {
        //Since we will already have INSERT INTO *Category* we can start with the '('

        /* STMT: INSERT INTO *Category* */
        StringBuilder val = new StringBuilder(" (");

        /* STMT: INSERT INTO *Category* ( */

        //the attributes array holds the names of
        //each column in the specified category table
        //here we add them all to the string in the
        //proper format
        for(int i=0; i<attributes.size(); i++)
        {
            attributes.set(i,attributes.get(i).replace(" ","_"));
            val.append(attributes.get(i));
            //If we are at the last attribute we
            //don't need to add the extra comma
            if(i != attributes.size()-1)
            {
                val.append(",");
            }

        }

        /* STMT: INSERT INTO *Category* (*All columns in table* */

        //Close the columns and start values
        val.append(") VALUES ('");

        /* STMT: INSERT INTO *Category* (*All columns in table*) VALUES (' */

        //Since attributes does not contain the part
        //number we have to add that separately
        val.append(partNumber);

        /* STMT: INSERT INTO *Category* (*All columns in table*) VALUES ('*PartNumber* */

        //It is possible that a product category can
        //have no attributes except the part number.
        //if this is the case make sure we don't add
        //the extra comma in the statement
        if(!attributes.isEmpty())
        {
            val.append("',");
        }
        else
        {
            val.append("'");
        }

        /* STMT: INSERT INTO *Category* (*All columns in table*) VALUES ('*PartNumber*', */


        //Now we can get the actual values for each column
        //by pulling the values from the productCategory
        //'product' object's 'attributes' array
        for(int i=0; i<product.attributes.size(); i++)
        {
            val.append("'");
            val.append(product.attributes.get(i));
            //If we are at the last value make sure
            //we don't add the extra comma
            if(i != product.attributes.size()-1)
            {
                val.append("',");
            }
            else
            {
                val.append("'");
            }
        }

        /* STMT: INSERT INTO *Category* (*All columns in table*) VALUES ('*PartNumber*,'*All Values*' */

        //finally we close the statement
        val.append(");");

        /* STMT: INSERT INTO *Category* (*All columns in table*) VALUES ('*PartNumber*,'*All Values*'); */

        return val.toString();
    }

    //This inserts a productCategory object into the specified category table
    public static boolean insertCategory(productCategory product,ArrayList<String> attributes,String partNumber)
    {
        PreparedStatement stmt = null;

        try
        {
            String pstmt = "INSERT INTO " + product.categoryName.getValue() + constructValues(product,attributes,partNumber);

            stmt = DBConnectionManager.con.prepareStatement(pstmt);
            System.out.println(pstmt);

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

    public static void getComboData(ObservableList<String> list,String table, String column)
    {
        PreparedStatement stmt = null;
        ResultSet myRst = null;

        try
        {
            String pstmt = "SELECT DISTINCT " + column + " FROM " + table + ";";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);

            myRst = stmt.executeQuery();

            while(myRst.next())
            {
                list.add(myRst.getString(1));
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try { if (myRst != null) myRst.close(); } catch (Exception ignored) {}
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
        }

    }

}
