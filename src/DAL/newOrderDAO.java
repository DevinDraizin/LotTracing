package DAL;


import Products.product;
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

}
