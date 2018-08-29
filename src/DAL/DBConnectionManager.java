package DAL;

import java.sql.*;

//This connection manager uses one connection
//and maintains that same connection object
//throughout the application lifetime.
//This works since there is only one user
//however, if we need more than 1 user
//we should switch the connection to use
//a connection pool to manage connections
public class DBConnectionManager
{
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Network360";
    private static final String CONN = "jdbc:mysql://localhost:3306/lottracer?useSSL=false";

    static Connection con = null;


    public static boolean establishConnection() {

        try {
            con = DriverManager.getConnection(CONN, USERNAME, PASSWORD);

            System.out.println("Successfully Created Connection");

        } catch (SQLException e) {
            System.out.println("Connection Failed\n\n");
            e.printStackTrace();
            return false;
        }

        return true;
    }


    public static boolean closeConnection() throws SQLException {
        if (con != null) {
            con.close();
            System.out.println("Successfully Terminated Connection");
            return true;
        } else {
            return false;
        }
    }
}
