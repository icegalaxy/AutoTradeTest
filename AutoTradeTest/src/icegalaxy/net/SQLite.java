package icegalaxy.net;

import java.sql.*;
 
import org.sqlite.SQLiteConfig;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class SQLite {

	public SQLite(String DBName) {
		this.DBName = DBName;
		try {
			connectToAccessDB();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void connectToAccessDB() throws SQLException{

		 SQLiteConfig config = new SQLiteConfig();
	        // config.setReadOnly(true);   
	        config.setSharedCache(true);
	        config.enableRecursiveTriggers(true);
	    
	            
//	        SQLiteDataSource ds = new SQLiteDataSource(config); 
////	        ds.setUrl("jdbc:sqlite:/" + DBName + ".sqlite");
//	        ds.setUrl("jdbc:sqlite:Analyse.sqlite");
	        conn = DriverManager.getConnection("jdbc:sqlite:" + DBName + ".sqlite");
	        //ds.setServerName("sample.db");
	        stmt = conn.createStatement();
	        System.out.println("Connected to DB");
//	        stmt.setQueryTimeout(30);
	 
	}

	public void createTable(String tableName,
			String fieldsAndTypesSeparatedByComma) {

		this.tableName = tableName;
		this.fieldString = fieldsAndTypesSeparatedByComma;

		String query = "create table " + tableName + "(" + fieldString + ")";

		try {
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println("Fail to create table");
			System.out.println(query);
			e.printStackTrace();
			return;
		}

		System.out.println("Created Table: " + tableName);
	}

	public void insertData(String valuesSeparatedByComma) {

		ArrayList<String> values = new ArrayList<String>();
		Scanner scr = new Scanner(valuesSeparatedByComma);
		scr.useDelimiter(",");
		while (scr.hasNext()){
			values.add(scr.next().trim());
		}
		
		StringBuffer query = new StringBuffer("INSERT INTO " + this.tableName + " VALUES(");
			
		for (int i=0; i<values.size(); i++){
			
			if (i != values.size()-1)
				query.append(quote(values.get(i))+ ",");
			else
				query.append(quote(values.get(i)) + ");");			
		}
		
		try {
			stmt.executeUpdate(query.toString());
		} catch (SQLException e) {
			System.out.println("Fail to insert data");
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			stmt.close();
			conn.close();
			System.out.println("Disconnected From DataBase:" + DBName);
		} catch (SQLException e) {
			System.out.println("Fail to Dissconnect");
			e.printStackTrace();
		}
	}
	
	
	private String quote(String point) {
		return ("'" + point + "'");
	}

	private String DBName;
	private String tableName;
	private String fieldString;
	public Statement stmt;
	public Connection conn;

}