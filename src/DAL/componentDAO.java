package DAL;

import Components.component;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class componentDAO
{
    //Here we pull all buyer data from the table and
    //convert them into an observable list of buyer
    //objects.
    public static ObservableList<component> getComponentsList()
    {
        ObservableList<component> buyerList =  FXCollections.observableArrayList();

        PreparedStatement stmt = null;
        ResultSet myRst = null;

        try
        {
            String pstmt = "SELECT * FROM Components_Part_Numbers;";

            stmt = DBConnectionManager.con.prepareStatement(pstmt);
            myRst = stmt.executeQuery();

            while(myRst.next())
            {
                buyerList.add(extractComponentFromResultSet(myRst));
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

    //Auxiliary method to getComponentList()
    //this method will take a specific row from the buyer
    //table and convert the data into a buyer object.
    //once converted, we return a buyer object
    private static component extractComponentFromResultSet(ResultSet myRs) throws SQLException
    {
        component component = new component();

        component.partNumber = new SimpleStringProperty(myRs.getString(1));
        component.description = new SimpleStringProperty(myRs.getString(2));
        component.section = new SimpleStringProperty(myRs.getString(3));

        return component;
    }
}

