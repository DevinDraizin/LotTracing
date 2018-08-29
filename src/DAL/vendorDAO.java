package DAL;

import Vendors.vendor;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class vendorDAO
{
    public static boolean removeVendor(int id)
    {
        PreparedStatement stmt = null;

        try
        {
            String pstmt = "DELETE FROM Vendors WHERE VendorID = ?";

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


    public static ObservableList<vendor> getVendorList()
    {
        ObservableList<vendor> vendorList =  FXCollections.observableArrayList();

        PreparedStatement stmt = null;
        ResultSet myRst = null;

        try
        {
            String pstmt = "SELECT * FROM Vendors;";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);
            myRst = stmt.executeQuery();

            while(myRst.next())
            {
                vendorList.add(extractVendorFromResultSet(myRst));
            }

            return vendorList;

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

    private static vendor extractVendorFromResultSet(ResultSet myRs) throws SQLException
    {
        vendor vendor = new vendor();

        vendor.vendorID = new SimpleIntegerProperty(myRs.getInt(1)).asObject();
        vendor.vendorName = new SimpleStringProperty(myRs.getString(2));


        return vendor;
    }

    public static int editVendor(vendor vendor)
    {
        PreparedStatement stmt = null;

        try
        {
            String pstmt = "UPDATE Vendors SET VendorID = ?, Vendor_Name = ? WHERE VendorID = ?;";

            stmt = DBConnectionManager.con.prepareStatement(pstmt, Statement.RETURN_GENERATED_KEYS);

            stmt.setInt(1,vendor.vendorID.getValue());
            stmt.setString(2,vendor.vendorName.getValue());
            stmt.setInt(3,vendor.vendorID.getValue());


            return stmt.executeUpdate();

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

    public static int getLastID()
    {
        PreparedStatement stmt = null;
        ResultSet myRst = null;

        try {

            String pstmt = "SELECT LAST_INSERT_ID()";
            stmt = DBConnectionManager.con.prepareStatement(pstmt);

            myRst = stmt.executeQuery();

            myRst.next();

            return myRst.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }



    public static int insertVendor(vendor vendor)
    {
        PreparedStatement stmt = null;

        try
        {
            String pstmt = "INSERT INTO Vendors (VendorID, Vendor_Name) VALUES (NULL,?);";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);

            stmt.setString(1,vendor.vendorName.getValue());

            stmt.executeUpdate();

            return getLastID();

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


}
