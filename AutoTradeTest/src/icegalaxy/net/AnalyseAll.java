package icegalaxy.net;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;

public class AnalyseAll{
	
	ArrayList<String> tables = new ArrayList<String>();
	
	public AnalyseAll(){
		SQLite asql = new SQLite(Setting.dataBase);
		try {
			DatabaseMetaData dbmd = asql.conn.getMetaData();
			String [] types = {"TABLE"};
			ResultSet rs = dbmd.getTables(null, null, "%", types);
			while (rs.next()){
				tables.add(rs.getString(3));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			asql.stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<String> getTables(){
		return tables;
	}
	
} 
