package DAL;

import Components.component;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class componentDAO
{
    //Here we pull all buyer data from the table and
    //convert them into an observable list of buyer
    //objects.
    public static void getComponentsList(ObservableList<component> componentList)
    {
        componentList.clear();

        PreparedStatement stmt = null;
        ResultSet myRst = null;

        try
        {
            String pstmt = "SELECT * FROM Components_Part_Numbers;";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);
            myRst = stmt.executeQuery();

            while(myRst.next())
            {
                componentList.add(extractComponentFromResultSet(myRst));
            }

            return;

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
    //in the component_lots table
    public static boolean checkComponentLotNum(String num)
    {
        PreparedStatement stmt = null;
        ResultSet myRst = null;

        try
        {
            String pstmt = " SELECT COUNT(*) FROM Component_Lots WHERE Component_Lot_Number = ?;";

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
        //we default to not allowing creation of new component lot numbers to
        //preserve data integrity
        return false;
    }


    //Auxiliary method to getComponentList()
    //this method will take a specific row from the component
    //table and convert the data into a component object.
    //once converted, we return that component object
    private static component extractComponentFromResultSet(ResultSet myRs) throws SQLException
    {
        component component = new component();

        component.partNumber = new SimpleStringProperty(myRs.getString(1));
        component.description = new SimpleStringProperty(myRs.getString(2));
        component.section = new SimpleStringProperty(myRs.getString(3));

        return component;
    }
}

