package DAL;


import NewOrder.orderedPart;
import NewOrder.purchaseOrder;
import Products.product;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class newOrderDAO
{
    public static String getProductDescription(product product)
    {
        PreparedStatement stmt = null;
        ResultSet myRst = null;


        try
        {
            String pstmt = "SELECT Description FROM " + product.productCategory.getValue() + " WHERE " + product.productCategory.getValue() + "_Part_Number" + " = ?";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);
            stmt.setString(1,product.partNumber.get());
            myRst = stmt.executeQuery();

            myRst.next();

            return myRst.getString(1);

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

    //Inserts orderedPart object into database and return
    //the incremented id it generates
    public static int insertOrderedPart(orderedPart orderedPart)
    {
        PreparedStatement stmt = null;

        try
        {
            String pstmt = "INSERT INTO Ordered_Parts (Transaction_ID, Part_Number, Total_Qty_Ordered, PO_Number) VALUES (NULL,?,?,?);";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);

            stmt.setString(1,orderedPart.partNumber.getValue());
            stmt.setInt(2,orderedPart.QtyOrdered.get());
            stmt.setString(3,orderedPart.PONumber.get());

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

    //Insert a new purchaseOrder into the database
    public static boolean insertPurchaseOrder(purchaseOrder purchaseOrder)
    {
        PreparedStatement stmt = null;

        try
        {
            String pstmt = "INSERT INTO Purchase_Orders (PO_Number, SO_Number, PO_Date, Due_Date, Buyer_ID, Memos) VALUES (?,?,?,?,?,?);";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);

            stmt.setString(1,purchaseOrder.PONumber.get());
            stmt.setString(2,purchaseOrder.SONumber.get());
            stmt.setDate(3, Date.valueOf(purchaseOrder.PODate));
            stmt.setDate(4,Date.valueOf(purchaseOrder.dueDate));
            stmt.setInt(5,purchaseOrder.buyerID);
            stmt.setString(6,purchaseOrder.memos.get());


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
