package DAL;


import NewOrder.getProductsUI;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class newOrderDAO
{
    private static String constructVals(ArrayList<String> filters, ArrayList<String> attributes)
    {
        StringBuilder val = new StringBuilder();


        for(int i=0; i<filters.size(); i++)
        {
            if(filters.get(i).compareTo("Any") != 0)
            {
                val.append(attributes.get(i).replace(" ","_")).append(" = '").append(filters.get(i)).append("' AND ");
            }

        }

        if(val.toString().isEmpty())
        {
            return ";";
        }
        else
        {
            return " WHERE " + (val.substring(0,val.length()-5)) + ";";
        }

    }


    //this is really broken
    public static void getFilteredComboData(JFXComboBox<String> list, String table, String column, ArrayList<String> filters, ArrayList<String> attributes,String oldVal)
    {
        PreparedStatement stmt = null;
        ResultSet myRst = null;
        ObservableList<String> newList = FXCollections.observableArrayList();

        getProductsUI.exec = false;

        try
        {
            String pstmt = "SELECT DISTINCT " + column + " FROM " + table + constructVals(filters,attributes);
            //System.out.println(pstmt);

            stmt = DBConnectionManager.con.prepareStatement(pstmt);

            myRst = stmt.executeQuery();

            //String oldVal = list.getValue();


            //For some reason list.getItems.clear()
            //is firing all the actionListeners
            //list.getSelectionModel().clearSelection();
            //list.getItems().clear();



            //list.getItems().add("Any");
            newList.add("Any");


            while(myRst.next())
            {
                //list.getItems().add(myRst.getString(1));
                newList.add(myRst.getString(1));
            }

            list.setItems(newList);

            if(oldVal.compareTo("Any") != 0)
            {
                for(int i=0; i<list.getItems().size(); i++)
                {
                    if(list.getItems().get(i).compareTo(oldVal) == 0)
                    {
                        list.getSelectionModel().select(i);
                    }
                }
            }
            else
            {
                list.getSelectionModel().select(0);
            }


            getProductsUI.exec = true;

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try { if (myRst != null) myRst.close(); } catch (Exception ignored) {}
            try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
        }

    }
}
