package icegalaxy.net;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AutoTradeDB extends DB {

	public static void createTable(String tableName) {

		String query = "create table "
				+ tableName
				+ "(MyIndex int, TradeTime time, Deal float, Bid float, Ask float, Middle float)";

		System.out.println(query);

		AutoTradeDB.tableName = tableName;

		try {
			stmt.executeUpdate(query);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void insert(String num, String time, String deal, String bid, String ask, String middle) {

		String query = "INSERT INTO " + tableName + " VALUES(" + quote(num)
				+ "," + quote(time) + "," + quote(deal) + "," + quote(bid)
				+ "," + quote(ask) + "," + quote(middle) + ");";

		try {
			stmt.executeUpdate(query.toString());
		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	public static String getToday() {

		Calendar now = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
		String s = formatter.format(now.getTime());

		return s;
	}

	public static String getTime() {

		return currentTime.toString();
	}
	
	public static int getTimeInt(){
		
		String time = getTime();
		
		Integer a = new Integer(time.replaceAll(":", "").substring(11, 17));
		// System.out.println(a);
		int i = a;
		return i;
	}

	public static void sleep(int miniSecond) {

		try {
			Thread.sleep(miniSecond);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public static void setTime(String time){
		
		currentTime = new StringBuffer(time);
	}
	
	static StringBuffer currentTime = new StringBuffer("00000000000000000");
	static String tableName = "";
}
