package util;

import java.sql.*;

public class DBConnector {
    static{
        try {
            System.out.println("Initializing jdbc driver...");
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return
     * Get connected to the database.
     */
    //
    public static Connection getConnection(){
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/java_web_movie?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            String username = "jwtd_user";
            String password = "jwtd";
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     *
     * @param conn
     * @param pstmt
     * @param rs
     * Close the connection to the database.
     */
    public static void closeConnection(Connection conn, PreparedStatement pstmt, ResultSet rs){
        try{
            if(rs != null){
                rs.close();
            }
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
