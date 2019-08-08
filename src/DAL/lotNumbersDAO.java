package DAL;

import LotNumbers.assemblyLot;
import LotNumbers.componentLot;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class lotNumbersDAO
{
    //Insert a new componentLot into the database
    public static boolean insertComponentLot(componentLot lot)
    {
        PreparedStatement stmt = null;

        try
        {
            String pstmt = "INSERT INTO Component_Lots (Component_Lot_Number, Receive_Date, Receive_Qty, Vendor_PO, Vendor_ID, Component_Part_Number) VALUES (?,?,?,?,?,?);";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);

            stmt.setString(1,lot.ComponentLotNumber.get());
            stmt.setDate(2,Date.valueOf(lot.receiveDate));
            stmt.setInt(3, lot.receiveQty.get());
            stmt.setString(4,lot.vendorPO.get());
            stmt.setInt(5,lot.vendorID.get());
            stmt.setString(6,lot.componentPartNumber.get());



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

    //Returns true if num is unique and false if it is found
    //in the assembly_lots table
    public static boolean checkAssemblyLotNum(String num)
    {
        PreparedStatement stmt = null;
        ResultSet myRst = null;

        try
        {
            String pstmt = " SELECT COUNT(*) FROM Assembly_Lot_Numbers WHERE Assembly_Lot_Number = ?;";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);
            stmt.setString(1,num);
            myRst = stmt.executeQuery();

            myRst.next();

            return myRst.getInt(1) == 0;

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try { if (myRst != null) myRst.close(); } catch (Exception ignored) {}
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
        }

        //we should never get here unless the database fails in which case
        //we default to not allowing creation of new assembly lot numbers to
        //preserve data integrity
        return false;
    }

    //Auxiliary method to getComponentLotList()
    //this method will take a specific row from the Component_Lot
    //table and convert the data into a componentLot object.
    //once converted, we return a componentLot object
    private static componentLot extractComponentLotFromResultSet(ResultSet myRs) throws SQLException
    {
        componentLot componentLot = new componentLot();

        componentLot.ComponentLotNumber = new SimpleStringProperty(myRs.getString(1));
        componentLot.receiveDate = (myRs.getDate(2).toLocalDate());
        componentLot.receiveQty = new SimpleIntegerProperty(myRs.getInt(3));
        componentLot.vendorPO = new SimpleStringProperty(myRs.getString(4));
        componentLot.vendorID = new SimpleIntegerProperty(myRs.getInt(5));
        componentLot.componentPartNumber = new SimpleStringProperty(myRs.getString(6));


        return componentLot;
    }


    //Here we pull all Component_Lot data from the table and
    //convert them into an observable list of componentLot
    //objects.
    public static void getComponentLotList(ObservableList<componentLot> componentLotList)
    {

        componentLotList.clear();

        PreparedStatement stmt = null;
        ResultSet myRst = null;

        try
        {
            String pstmt = "SELECT * FROM Component_Lots;";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);
            myRst = stmt.executeQuery();

            while(myRst.next())
            {
                componentLotList.add(extractComponentLotFromResultSet(myRst));
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


    //This method will take an assembly lot number without the ending unique character and
    //it will locate all the assembly lot numbers that correspond to lots of the same type
    //on the same day and store them all in an array.
    public static ArrayList<String> getSameDayAssemblyLots(String lot)
    {
        ArrayList<String> results = new ArrayList<>();

        PreparedStatement stmt = null;
        ResultSet myRst = null;

        try
        {
            String pstmt = "SELECT Assembly_Lot_Number FROM Assembly_Lot_Numbers WHERE Assembly_Lot_Number LIKE ?;";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);
            stmt.setString(1,lot + "%");
            myRst = stmt.executeQuery();

            while(myRst.next())
            {
                results.add(myRst.getString(1));
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

        return results;

    }

    //Insert a new assemblyLot into the database
    public static boolean insertAssemblyLot(assemblyLot lot)
    {
        PreparedStatement stmt = null;

        try
        {
            String pstmt = "INSERT INTO Assembly_Lot_Numbers (Assembly_Lot_Number, Assembly_Date, Assembly_Qty, Memo, Part_Number) VALUES (?,?,?,?,?);";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);

            stmt.setString(1,lot.AssemblyLotNumber.get());
            stmt.setDate(2,Date.valueOf(lot.assembleDate));
            stmt.setInt(3, lot.assembleQty.get());
            stmt.setString(4,lot.memos.get());
            stmt.setString(5,lot.partNumber.get());


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


}
