package DAL;


import NewOrder.orderedPart;
import NewOrder.purchaseOrder;
import Products.product;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

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
            String pstmt = "INSERT INTO Purchase_Orders (PO_Number, SO_Number, PO_Date, Due_Date, Buyer_ID, Memos, complete) VALUES (?,?,?,?,?,?,?);";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);

            stmt.setString(1,purchaseOrder.PONumber.get());
            stmt.setString(2,purchaseOrder.SONumber.get());
            stmt.setDate(3, Date.valueOf(purchaseOrder.PODate));
            stmt.setDate(4,Date.valueOf(purchaseOrder.dueDate));
            stmt.setInt(5,purchaseOrder.buyerID.get());
            stmt.setString(6,purchaseOrder.memos.get());
            stmt.setInt(7,purchaseOrder.complete ? 1 : 0);


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

    //Auxiliary method to getPurchaseOrderList()
    //this method will take a specific row from the Purchase_Order
    //table and convert the data into a purchaseOrder object.
    //once converted, we return a purchaseOrder object
    private static purchaseOrder extractPurchaseOrderFromResultSet(ResultSet myRs) throws SQLException
    {
        purchaseOrder purchaseOrder = new purchaseOrder();

        purchaseOrder.PONumber = new SimpleStringProperty(myRs.getString(1));
        purchaseOrder.SONumber = new SimpleStringProperty(myRs.getString(2));
        purchaseOrder.PODate = (myRs.getDate(3).toLocalDate());
        purchaseOrder.dueDate = (myRs.getDate(4).toLocalDate());
        purchaseOrder.memos = new SimpleStringProperty(myRs.getString(5));
        purchaseOrder.buyerID = new SimpleIntegerProperty(myRs.getInt(6));
        purchaseOrder.complete = myRs.getInt(7) == 1;

        return purchaseOrder;
    }


    //Here we pull all Purchase Order data from the table and
    //convert them into an observable list of purchaseOrder
    //objects.
    public static void getPurchaseOrderList(ObservableList<purchaseOrder> purchaseOrderList)
    {

        purchaseOrderList.clear();

        PreparedStatement stmt = null;
        ResultSet myRst = null;

        try
        {
            String pstmt = "SELECT * FROM Purchase_Orders;";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);
            myRst = stmt.executeQuery();

            while(myRst.next())
            {
                purchaseOrderList.add(extractPurchaseOrderFromResultSet(myRst));
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

    //Returns true if num is unique and false if it is found
    //in the Purchase_Orders table
    public static boolean checkPurchaseOrderNum(String num)
    {
        PreparedStatement stmt = null;
        ResultSet myRst = null;

        try
        {
            String pstmt = " SELECT COUNT(*) FROM Purchase_Orders WHERE PO_Number = ?;";

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
        //we default to not allowing creation of a PO Number to
        //preserve data integrity
        return false;
    }

    //Returns true if num is unique and false if it is found
    //in the Purchase_Orders table
    public static boolean checkSalesOrderNum(String num)
    {
        PreparedStatement stmt = null;
        ResultSet myRst = null;

        try
        {
            String pstmt = " SELECT COUNT(*) FROM Purchase_Orders WHERE SO_Number = ?;";

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
        //we default to not allowing creation of a SO Number to
        //preserve data integrity
        return false;
    }

}
