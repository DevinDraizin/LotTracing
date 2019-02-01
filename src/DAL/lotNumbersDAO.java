package DAL;

import LotNumbers.componentLot;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class lotNumbersDAO
{
    //Insert a new purchaseOrder into the database
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
}
