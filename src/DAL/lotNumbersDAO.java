package DAL;

import LotNumbers.componentLot;

import java.sql.Date;
import java.sql.PreparedStatement;
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
}
