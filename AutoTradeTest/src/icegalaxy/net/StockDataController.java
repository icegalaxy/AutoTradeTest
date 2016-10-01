package icegalaxy.net;

import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JTextArea;

public class StockDataController implements Runnable {

	// private String hsiDB = new
	// String("C:/Documents and Settings/Hi2/My Documents/Dropbox/Programming/Stock/Analyse.mdb");
	private static TimeBase shortTB;
	private static TimeBase mediumTB;
	private static TimeBase longTB;
//	private static TimeBase sec10TB;
//	private static TimeBase secTB;
	// private static TimeBase secTB;

	private int quantityCounter = 0;

	private boolean added[] = new boolean[7];

	public boolean done = false;
	private WaitAndNotify wan;
	private int shortMinutes;
	private int longMinutes;
	private int mediumMinutes;

	public float gap;
	public float previousClose = 0;
	public ArrayList<Float> deal = new ArrayList<Float>();
	public ArrayList<Float> ask = new ArrayList<Float>();
	public ArrayList<Float> bid = new ArrayList<Float>();
	public ArrayList<Float> quantity = new ArrayList<Float>();
	public ArrayList<String> time = new ArrayList<String>();
	private String tableName;
	private int counter = 1;
//	private int counter10Sec = 0;
	StringBuffer timeNow = new StringBuffer();
	int sec;
	float dealPt;
	float askPt;
	float bidPt;
	float totalQuantity = 0.0F;
	float calDeal = 0.0F;
	private boolean noQuantity;

	static double totalWeightOfRSI = 0;

	ArrayList<Candle> candles = new ArrayList<Candle>();

	StockDataController.CandleData shortData;
	StockDataController.CandleData mediumData;
	StockDataController.CandleData longData;
//	StockDataController.CandleData sec10Data;
//	StockDataController.CandleData secData;

	// StockDataController.CandleData secData;

	public StockDataController(String tableName, WaitAndNotify wan) {
		this.wan = wan;
		this.tableName = tableName;
		
		for (int i=0; i<added.length; i++){
			added[i] = false;
		}


		shortTB = new TimeBase();
		shortTB.setBaseMin(Setting.getShortTB());
		mediumTB = new TimeBase();
		mediumTB.setBaseMin(Setting.getMediumTB());
		longTB = new TimeBase();
		longTB.setBaseMin(Setting.getLongTB());
		
		//must after creation of timebase
		getData();
		
//		sec10TB = new TimeBase();
//		secTB = new TimeBase();
//		secTB = new TimeBase();

		shortData = new CandleData();
		mediumData = new CandleData();
		longData = new CandleData();
//		sec10Data = new CandleData();
//		secData = new CandleData();
	}

	public void getData() {
		

		
		SQLite asql;
		if (Global.analysingAll)
			asql = Setting.asql;
		else
			
			asql = new SQLite(Setting.dataBase);
		
//		setOHLC();
		
//		CSVparser csv = new CSVparser("5minOHLC.csv");
//		csv.parseOHLC();
				
//		for (int i=0; i<csv.getLow().size(); i++){
//			
//			System.out.println(getCSVTime(csv.getTime().get(i)));
//			System.out.println(csv.getHigh().get(i));
//			System.out.println(csv.getLow().get(i));
//			System.out.println(csv.getOpen().get(i));
//			System.out.println(csv.getClose().get(i));
//			
//
//			
//			getLongTB().addPoint(csv.getClose().get(i).floatValue());
//			getLongTB().addCandle(getCSVTime(csv.getTime().get(i)), csv.getHigh().get(i), csv.getLow().get(i), csv.getOpen().get(i), csv.getClose().get(i), (double) i+1);
//			
//		}
		
		
		try {
			ResultSet result = asql.stmt.executeQuery("SELECT * FROM \""
					+ tableName + Setting.index + "\" ORDER BY MyIndex ASC");

			System.out.println("Connected to table");
			
			while (result.next()) {

				if (gap == 0) {
					gap = Float.valueOf(result.getFloat("Change"));
					Global.setGap(gap);
				}
				deal.add(Float.valueOf(result.getFloat("Deal")));
				ask.add(Float.valueOf(result.getFloat("Ask")));
				bid.add(Float.valueOf(result.getFloat("Bid")));
				if (new Integer(tableName).intValue() >= 110614)
					quantity.add(Float.valueOf(result.getFloat("TotalQuantity")));
				else
					noQuantity = true;

				if (previousClose == 0) {
					previousClose = deal.get(0) - gap;
					Global.setPreviousClose(previousClose);
				}
				time.add(result.getString("TradeTime"));
			}
			result.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!Global.analysingAll)
			asql.close();
	}

	public void run() {

		for (int i = 0; i < time.size(); ++i) {

			if (time.get(i).toString().contains("12:59:"))
				continue;

			timeNow = new StringBuffer((String) time.get(i));

			sec = new Integer(timeNow.substring(15, 17)).intValue();
			AutoTradeDB.setTime(timeNow.toString());

			if (Global.isTradeTime()) {
				dealPt = ((Float) deal.get(i)).floatValue();
				askPt = ((Float) ask.get(i)).floatValue();
				bidPt = ((Float) bid.get(i)).floatValue();

				if (!(noQuantity))
					totalQuantity = ((Float) quantity.get(i)).floatValue();

				// if (dealPt > askPt) {
				// calDeal = askPt;
				// } else if (dealPt < bidPt) {
				// calDeal = bidPt;
				// } else {
				calDeal = dealPt;
				// }

				Global.setCurrentDeal(calDeal);
				Global.setCurrentAsk(askPt);
				Global.setCurrentBid(bidPt);
				Global.setCurrentPoint(calDeal);

				addRenko();

				if (calDeal > Global.getDayHigh())
					Global.setDayHigh(calDeal);
				if (calDeal < Global.getDayLow()) {
					Global.setDayLow(calDeal);
				}

				// secData.getHighLow();
				// secData.getOpen();

//				sec10Data.getHighLow();
//				sec10Data.getOpen();

				shortData.getHighLow();
				shortData.getOpen();

				mediumData.getHighLow();
				mediumData.getOpen();
				
				longData.getHighLow();
				longData.getOpen();

				// getSecTB().addData(Float.valueOf(calDeal),
				// Float.valueOf(totalQuantity));
				//
				// getSecTB().addCandle(getTime(), sec10Data.periodHigh,
				// sec10Data.periodLow, sec10Data.openPt, calDeal,
				// totalQuantity);
				//
				// secData.reset();

				// for secTB
				// �O���ignore���\�]���ӺC getSecTB().addData(Float.valueOf(calDeal),
				// Float.valueOf(totalQuantity));
				//
				// getSecTB().addCandle(getTime(), sec10Data.periodHigh,
				// sec10Data.periodLow, sec10Data.openPt, calDeal,
				// totalQuantity);
				//
				// secData.reset();

				// for secTB

				if (sec == 58 || sec == 59 || sec == 00 || sec == 01 || sec == 02) {
					if (counter >= 0) {
						counter = -20;
						shortMinutes++;
						longMinutes++;
						mediumMinutes++;
					}
				}

//				int remain = sec % 10;

//				if (remain == 9 || remain == 0 || remain == 1
//						&& counter10Sec > 0) {
//					counter10Sec = -5;
//					getSec10TB().addData(Float.valueOf(calDeal),
//							Float.valueOf(totalQuantity));

//					getSec10TB().addCandle(getTime(), sec10Data.periodHigh,
//							sec10Data.periodLow, sec10Data.openPt, calDeal,
//							totalQuantity);

//					sec10Data.reset();

					// System.out.println("Sec10 Up Slope: " +
					// getSec10TB().getMainUpRail().getSlope());
					// System.out.println("Sec10 Down Slope: " +
					// getSec10TB().getMainDownRail().getSlope());
//				}

				if (shortMinutes == Setting.getShortTB()) {
					if (!(noQuantity))
						getShortTB().addData(Float.valueOf(calDeal),
								Float.valueOf(totalQuantity));
					else
						getShortTB().addPoint(Float.valueOf(calDeal));

					if (getShortTB().tops.size() > 1)
						getShortTB().zone.setResist();
					if (getShortTB().bottoms.size() > 1)
						getShortTB().zone.setSupport();

					getShortTB().addCandle(getTime(), shortData.periodHigh,
							shortData.periodLow, shortData.openPt, calDeal,
							totalQuantity);

//					System.out.println(AutoTradeDB.getTime() + " " + calDeal);
//					System.out.println("MA10: " + getShortTB().getMA(10));
//					System.out.println("MA20: " + getShortTB().getMA(20));
//					System.out.println("RSI14 :" + getShortTB().getRSI());
//					System.out.println("Resist: "
//							+ getShortTB().zone.getTopOfResist());
//					System.out.println("Support: "
//							+ getShortTB().zone.getBottomOfSupport());
//					System.out.println("MovingRSI: "
//							+ getShortTB().getMovingRSI());

//					try {
//						System.out.println("Average HL15: "
//								+ getShortTB().getAverageHL(15));
//					} catch (NotEnoughPeriodException e) {
//						System.out.println("Average HL15: Not enough periods");
//					}

					getShortTB().getMACD();

					shortMinutes = 0;
					shortData.reset();
				}

				if (mediumMinutes == Setting.getMediumTB()) {
					if (!(noQuantity))
						getMediumTB().addData(Float.valueOf(calDeal),
								Float.valueOf(totalQuantity));
					else
						getMediumTB().addPoint(Float.valueOf(calDeal));

					getMediumTB().addCandle(getTime(), mediumData.periodHigh,
							mediumData.periodLow, mediumData.openPt, calDeal,
							totalQuantity);

//					System.out.println(AutoTradeDB.getTime() + " " + calDeal);
					getMediumTB().getMACD();
					mediumMinutes = 0;
					mediumData.reset();

				}

				if (longMinutes == Setting.getLongTB()) {
					if (!(noQuantity))
						getLongTB().addData(Float.valueOf(calDeal),
								Float.valueOf(totalQuantity));
					else
						getLongTB().addPoint(Float.valueOf(calDeal));
					
					getLongTB().addCandle(getTime(), longData.periodHigh,
							longData.periodLow, longData.openPt, calDeal,
							totalQuantity);

					getLongTB().getMACD();
					// System.out.println("MACD Histo: "
					// + getLongTB().getMACDHistogram());
					longMinutes = 0;
					longData.reset();
				}

				setGlobal();
				counter += 1;
//				counter10Sec += 1;
			}
			wan.endWaiter();
			if (!(Global.isTradeTime()))
				counter = 1;

		}

		Global.setTradeTime(false);
		Global.setQuotePowerTime(false);
		Global.setOrderTime(false);
		Global.setRunning(false);
		Global.setForceSellTime(false);

	
		addQuantities();

		Global.totalBalance += Global.balance;

		if (!(Global.analysingAll)) {

			Global.addLog("5 min Quantity: "
					+ getShortTB().getQuatityByPeriods(5));
			Global.addLog("total Quantity: " + getShortTB().getTotalQuantity());
			Global.addLog("Balance: " + Global.balance + " Trades: "
					+ Global.noOfTrades);
			Setting.result.setText(Global.getLog());
			Global.balance = 0.0F;
		}

		if (!(Global.analysingAll))
			getShortTB().createChart();

		Global.clearLog();
		shortMinutes = 0;
		longMinutes = 0;
		mediumMinutes = 0;
	}

	private void addRenko() {

		if (candles.size() == 0) {

		}

	}

	private Date addTime() {
		if (candles.size() == 0)
			return getTime();
		else {
			DateFormat formatter = new SimpleDateFormat("hh:mm");
			Date time = candles.get(candles.size() - 1).getTime();
			String hr = time.toString().substring(0, 2);
			String min = time.toString().substring(3, 5);

			if (min.equals("59")) {
				min = "00";
				Integer i = new Integer(hr);
				i++;
				hr = i.toString();
			} else {
				Integer i = new Integer(min);
				i++;
				min = i.toString();
			}
			try {
				return formatter.parse(hr + ":" + min);
			} catch (ParseException e) {
				e.printStackTrace();
				return getTime();
			}
		}
	}

	private void setGlobal() {
		if (Global.getDayHigh() - Global.getDayLow() < 100.0F)
			Global.setLowFluctuation(true);
		else
			Global.setLowFluctuation(false);
	}

	private Date getCSVTime(String time){
		Date s = new Date();
		DateFormat formatter = new SimpleDateFormat("hh:mm");
		try {
			s = formatter.parse(time.substring(10,12) + ":" + time.substring(12, 14));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	private Date getTime() {
		Date s = null;
		DateFormat formatter = new SimpleDateFormat("hh:mm");
		try {
			s = formatter.parse(timeNow.substring(9, 14));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (s.toString().contains("00:59:00")) {
			System.out.println("Error: getTime " + s);
			System.out.println("Error: TimeNow " + timeNow);
		}
		return s;
	}

//	public static synchronized double getTotalWeightOfRSI() {
//		return totalWeightOfRSI;
//	}

	public static synchronized TimeBase getShortTB() {
		return shortTB;
	}

	public static synchronized TimeBase getMediumTB() {
		return mediumTB;
	}

	public static synchronized TimeBase getLongTB() {
		return longTB;
	}

//	public static synchronized TimeBase getSec10TB() {
//		return sec10TB;
//	}

//	public static synchronized TimeBase getSecTB() {
//		return secTB;
//	}

	// public static synchronized TimeBase getSecTB() {
	// return secTB;
	// }

	class CandleData {
		private double periodHigh = 0.0D;
		private double periodLow = 99999.0D;
		private double openPt = 0.0D;
		private boolean openAdded = false;

		void reset() {

			periodHigh = 0.0D;
			periodLow = 99999.0D;
			openAdded = false;
		}

		void getHighLow() {
			if (calDeal > periodHigh)
				periodHigh = calDeal;
			if (calDeal < periodLow)
				periodLow = calDeal;
		}

		void getOpen() {
			if (!openAdded) {
				openPt = calDeal;
				openAdded = true;
			}
		}

	}

	private void addQuantities() {

		// Setting.quantities[0] ++;
		//
		// for (int i=1; i<Setting.quantityPeriods.length; i++){
		//
		// Setting.quantities[i] +=
		// getShortTB().getQuatityByPeriods(Setting.quantityPeriods[i])
		// - getShortTB().getQuatityByPeriods(Setting.quantityPeriods[i]-1);
		// }

		Setting.quantity[0][quantityCounter] = quantityCounter;

		for (int i = 1; i < Setting.quantity.length; i++) {

			Setting.quantity[i][quantityCounter] = getShortTB()
					.getQuatityByPeriods(Setting.quantityPeriods[i])
			// - getShortTB().getQuatityByPeriods(Setting.quantityPeriods[i]-1)
			;
		}

		quantityCounter++;
		if (quantityCounter == 30)
			quantityCounter = 0;

		// Setting.quantities[1] += getShortTB().getQuatityByPeriods(5);
		//
		// Setting.quantities[2] += getShortTB().getQuatityByPeriods(30) -
		// getShortTB().getQuatityByPeriods(5);
		//
		// Setting.quantities[3] += getShortTB().getQuatityByPeriods(60) -
		// getShortTB().getQuatityByPeriods(30);
		//
		// Setting.quantities[4] += getShortTB().getQuatityByPeriods(120) -
		// getShortTB().getQuatityByPeriods(60);
		//
		// Setting.quantities[5] += getShortTB().getQuatityByPeriods(180) -
		// getShortTB().getQuatityByPeriods(120);
		//
		// Setting.quantities[6] += getShortTB().getQuatityByPeriods(240) -
		// getShortTB().getQuatityByPeriods(180);

		// Setting.quantities[7] += getShortTB().getQuatityByPeriods(300);
		//
		// System.out.println(Setting.quantities[1]);
		// System.out.println(Setting.quantities[2]);
		// System.out.println(Setting.quantities[3]);
		// System.out.println(Setting.quantities[4]);
		// System.out.println(Setting.quantities[5]);
		// System.out.println(Setting.quantities[6]);
		// System.out.println(Setting.quantities[7]);

	}

	private void setOHLC(){
		XMLReader ohlc = new XMLReader(tableName);
		Global.setpHigh(ohlc.getpHigh());
		Global.setpLow(ohlc.getpLow());
		Global.setpOpen(ohlc.getpOpen());
		Global.setpClose(ohlc.getpClose());
		Global.setpFluc(ohlc.getpFluc());
	}

}
