package DAL;

import Buyers.buyer;
import com.jfoenix.controls.JFXComboBox;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//This is the data access object for the buyer manager.
//Collection of static methods to encapsulate all JDBC
//calls and database queries.
//
//For all of the DAOs we use parameterized queries.
public class buyerDAO
{

    //Remove buyer from database specified by id
    public static boolean removeBuyer(int id)
    {
        PreparedStatement stmt = null;

        try
        {
            String pstmt = "DELETE FROM Buyers WHERE Buyer_ID = ?";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);
            stmt.setInt(1,id);
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

    //This method returns a buyer object
    //with data from the database with
    //provided id. If id does not exist in
    //database we return null
    public static buyer getBuyer(int id)
    {
        PreparedStatement stmt = null;
        ResultSet myRst = null;
        buyer buyer = new buyer();


        try
        {
            String pstmt = "SELECT * FROM Buyers WHERE Buyer_ID = ?";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);
            stmt.setInt(1,id);
            myRst = stmt.executeQuery();

            myRst.next();

            buyer.buyerID = new SimpleIntegerProperty(id).asObject();
            buyer.buyerName = new SimpleStringProperty(myRst.getString(2));
            buyer.company = new SimpleStringProperty(myRst.getString(3));
            buyer.email = new SimpleStringProperty(myRst.getString(4));
            buyer.phoneNumber = new SimpleStringProperty(myRst.getString(5));

            return buyer;

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

    //Here we pull all buyer data from the table and
    //convert them into an observable list of buyer
    //objects.
    public static ObservableList<buyer> getBuyerList()
    {
        ObservableList<buyer> buyerList =  FXCollections.observableArrayList();

        PreparedStatement stmt = null;
        ResultSet myRst = null;

        try
        {
            String pstmt = "SELECT * FROM Buyers;";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);
            myRst = stmt.executeQuery();

            while(myRst.next())
            {
                buyerList.add(extractBuyerFromResultSet(myRst));
            }

            return buyerList;

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

    //Auxiliary method to getBuyerList()
    //this method will take a specific row from the buyer
    //table and convert the data into a buyer object.
    //once converted, we return a buyer object
    private static buyer extractBuyerFromResultSet(ResultSet myRs) throws SQLException
    {
        buyer buyer = new buyer();


        buyer.buyerID = new SimpleIntegerProperty(myRs.getInt("Buyer_ID")).asObject();
        buyer.buyerName = new SimpleStringProperty(myRs.getString("Buyer_Name"));
        buyer.company = new SimpleStringProperty(myRs.getString("Company"));
        buyer.email = new SimpleStringProperty(myRs.getString("Email"));
        buyer.phoneNumber = new SimpleStringProperty(myRs.getString("Telephone"));

        return buyer;
    }

    //Inserts buyer object into database
    public static int insertBuyer(buyer buyer)
    {
        PreparedStatement stmt = null;

        try
        {
            String pstmt = "INSERT INTO Buyers (Buyer_ID, Buyer_Name, Company, Email, Telephone) VALUES (NULL,?,?,?,?);";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);

            stmt.setString(1,buyer.buyerName.getValue());
            stmt.setString(2,buyer.company.getValue());
            stmt.setString(3,buyer.email.getValue());
            stmt.setString(4,buyer.phoneNumber.getValue());

            stmt.executeUpdate();

            return vendorDAO.getLastID();

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
        }

        return -1;
    }


    //This method takes an edited buyer and
    //updates the database using the buyer ID
    public static void editBuyer(buyer buyer)
    {
        PreparedStatement stmt = null;

        try
        {
            String pstmt = "UPDATE Buyers SET Buyer_ID = ?, Buyer_Name = ?, Company = ?, Email = ?, Telephone = ? WHERE Buyer_ID = ?;";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);

            stmt.setInt(1,buyer.buyerID.getValue());
            stmt.setString(2,buyer.buyerName.getValue());
            stmt.setString(3,buyer.company.getValue());
            stmt.setString(4,buyer.email.getValue());
            stmt.setString(5,buyer.phoneNumber.getValue());
            stmt.setInt(6,buyer.buyerID.getValue());

           stmt.executeUpdate();

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
        }

    }

    //This method initializes a combo box with companies from the
    //'Company' table
    public static boolean getCompanies(JFXComboBox<String> companyDrop)
    {
        PreparedStatement stmt = null;
        ResultSet myRst = null;

        try
        {
            String pstmt = "SELECT DISTINCT Company FROM Buyers";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);
            myRst = stmt.executeQuery();

            while(myRst.next())
            {
                companyDrop.getItems().add(myRst.getString(1));
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

    /*
    //Initializes a combo box with all existing buyer names and IDs
    public static boolean getOrderedBuyerSelector(JFXComboBox<String> buyerSelector)
    {
        PreparedStatement stmt = null;
        ResultSet myRst = null;

        try
        {
            String pstmt = "SELECT Buyer_ID, Buyer_Name FROM Buyers";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);
            myRst = stmt.executeQuery();

            while(myRst.next())
            {
                buyerSelector.getItems().add(myRst.getString(1) + "    " + myRst.getString(2));
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
    */



    //Initializes a combo box with all existing buyer names
    public static boolean getBuyerSelector(JFXComboBox<String> buyerSelector)
    {
        PreparedStatement stmt = null;
        ResultSet myRst = null;

        try
        {
            String pstmt = "SELECT Buyer_Name FROM Buyers";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);
            myRst = stmt.executeQuery();

            while(myRst.next())
            {
                buyerSelector.getItems().add(myRst.getString(1));
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

    //Returns the ID of a buyer given a name
    //returns -1 if buyer does not exist
    public static int findBuyer(String name)
    {
        PreparedStatement stmt = null;
        ResultSet myRst = null;

        try
        {
            String pstmt = "SELECT Buyer_ID FROM Buyers WHERE Buyer_Name = ?;";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);
            stmt.setString(1,name);
            myRst = stmt.executeQuery();

            boolean check = myRst.next();

            if(!check)
            {
                return -1;
            }

            return myRst.getInt(1);


        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try { if (myRst != null) myRst.close(); } catch (Exception ignored) {}
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
        }


        return -1;

    }




}
