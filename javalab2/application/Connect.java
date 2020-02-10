package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//import java.sql.ResultSet;
import java.sql.Statement;
public class Connect {
	public static String URL = "jdbc:mysql://localhost:3306/lab2?serverTimezone=UTC";
	public static String Admin = "admin";
	public static String AdminPwd = "admin";
	public Statement GetConnect(String url,String user,String password) throws Exception{		
		Connection conn = null;
		Statement stmt=null;
		try {
		    // create a connection to the database
		    conn = DriverManager.getConnection(url, user, password);
		    stmt  = conn.createStatement();  
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return stmt;
	}
}
