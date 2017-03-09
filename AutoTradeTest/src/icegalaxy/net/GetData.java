package icegalaxy.net;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GetData implements Runnable
{

	private static TimeBase shortTB;
	private static TimeBase m15TB;
	private static TimeBase longTB;

	XMLReader ohlc;

	// private static EMA ema5;
	// private static EMA ema25;
	// private static EMA ema50;
	// private static EMA ema100;
	// private static EMA ema250;
	// private static EMA ema1200;

	// private boolean isOpenAdded = false;

	private int quantityCounter = 0;

	private boolean added[] = new boolean[7];

	public boolean done = false;
	private WaitAndNotify wan;

	// to add the first data as a candle
	private int shortMinutes = 1;
	private int longMinutes = 5;
	private int m15Minutes = 15;

	public float gap;
	public float previousClose = 0;
	public ArrayList<Float> deal = new ArrayList<Float>();
	public ArrayList<Float> ask = new ArrayList<Float>();
	public ArrayList<Float> bid = new ArrayList<Float>();
	public ArrayList<Float> quantity = new ArrayList<Float>();
	public ArrayList<String> time = new ArrayList<String>();
	private String tableName;
	private int counter = 1;
	// private int counter10Sec = 0;
	String timeNow;
	int min;
	int refMin = 15;
	float dealPt;
	float askPt;
	float bidPt;
	float totalQuantity = 0.0F;
	float calDeal = 0.0F;
	private boolean noQuantity;

	static double totalWeightOfRSI = 0;

	ArrayList<Candle> candles = new ArrayList<Candle>();

	GetData.CandleData shortData;
	GetData.CandleData m15Data;
	GetData.CandleData longData;
	// StockDataController.CandleData sec10Data;
	// StockDataController.CandleData secData;

	// StockDataController.CandleData secData;
	ArrayList<Float> m1Deal;

	int[] EMAs = new int[]
	{ 5, 25, 50, 100, 250, 1200 };

	public GetData(String tableName, WaitAndNotify wan)
	{
		this.wan = wan;
		this.tableName = tableName;
		m1Deal = new ArrayList<Float>();

		for (int i = 0; i < added.length; i++)
		{
			added[i] = false;
		}

		ohlc = new XMLReader(tableName);
		ohlc.findOHLC();

		// ema5 = new EMA(ohlc.getpEMA5(), 5);
		// ema25 = new EMA(ohlc.getpEMA25(), 25);
		// ema50 = new EMA(ohlc.getpEMA50(), 50);
		// ema100 = new EMA(ohlc.getpEMA100(), 100);
		// ema250 = new EMA(ohlc.getpEMA250(), 250);
		// ema1200 = new EMA(ohlc.getpEMA1200(), 1200);

		// Global.addLog("pEMA250: " + ohlc.getpEMA250());

		shortTB = new TimeBase();
		shortTB.setBaseMin(Setting.getShortTB());
		m15TB = new TimeBase();
		m15TB.setBaseMin(15);
		longTB = new TimeBase();
		longTB.setBaseMin(Setting.getLongTB());

		// must after creation of timebase
		getData();

		// sec10TB = new TimeBase();
		// secTB = new TimeBase();
		// secTB = new TimeBase();

		shortData = new CandleData();
		m15Data = new CandleData();
		longData = new CandleData();
		// sec10Data = new CandleData();
		// secData = new CandleData();

	}

	public void getData()
	{

		SQLite asql;
		if (Global.analysingAll)
			asql = Setting.asql;
		else
			asql = new SQLite(Setting.dataBase);

		setOHLC();

		// if (ohlc.getpEMA250() != 0) // for days without spData
		// {
		//
		// for (int i = 0; i < shortTB.EMAs.length; i++)
		// shortTB.EMAs[i] = new EMA(ohlc.getpEMA5(), EMAs[i]);
		// } else
		// {
		getPreviousData();
		// }
		// getLongTB().setPreviousEMA(5, (float) 23628.89);
		// getLongTB().setPreviousEMA(6, (float) 23635.57);

		try
		{
			ResultSet result = asql.stmt
					.executeQuery("SELECT * FROM \"" + Setting.index + tableName + "\" ORDER BY MyIndex ASC");

			System.out.println("Connected to table");

			while (result.next())
			{

				if (gap == 0)
				{
					gap = Float.valueOf(result.getFloat("Change"));
					Global.setGap(gap);
				}
				deal.add(Float.valueOf(result.getFloat("Deal")));
				ask.add(Float.valueOf(result.getFloat("Ask")));
				bid.add(Float.valueOf(result.getFloat("Bid")));
				time.add(result.getString("TradeTime"));
				// if (new Integer(tableName).intValue() >= 110614)
				quantity.add(Float.valueOf(result.getFloat("TotalQuantity")));
				// else
				// noQuantity = true;

				// if (previousClose == 0) {
				// previousClose = deal.get(0) - gap;
				// Global.setPreviousClose(previousClose);
				// }

			}
			result.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		if (!Global.analysingAll)
			asql.close();
	}

	public void run()
	{

		// while (!Global.isTradeTime())
		// wan.endWaiter();

		for (int i = 0; i < time.size(); i++)
		{

			if (TimePeriodDecider.getTime() >= 163000)
				break; // skip night market
			// if (time.get(i).toString().contains("12:59:"))
			// continue;

			timeNow = time.get(i);

			min = new Integer(timeNow.substring(4, 6)).intValue();
			AutoTradeDB.setTime(timeNow);

			// if (Global.isTradeTime())
			// {
			dealPt = ((Float) deal.get(i)).floatValue();
			askPt = ((Float) ask.get(i)).floatValue();
			bidPt = ((Float) bid.get(i)).floatValue();
			totalQuantity = ((Float) quantity.get(i)).floatValue();

			// if (dealPt > askPt) {
			// calDeal = askPt;
			// } else if (dealPt < bidPt) {
			// calDeal = bidPt;
			// } else {
			calDeal = dealPt;
			// }

			if (m1Deal.size() >= 60)
				m1Deal.remove(0);
			m1Deal.add(dealPt);

			Global.setCurrentDeal(calDeal);
			Global.setCurrentAsk(askPt);
			Global.setCurrentBid(bidPt);
			Global.setCurrentPoint(calDeal);

			// addRenko(); ???

			if (calDeal > Global.getDayHigh())
				Global.setDayHigh(calDeal);
			if (calDeal < Global.getDayLow())
			{
				Global.setDayLow(calDeal);
			}

			// if (!isOpenAdded)
			// {
			// Global.setOpen(calDeal);
			// isOpenAdded = true;
			// }

			shortData.getHighLow();
			shortData.getOpen();

			m15Data.getHighLow();
			m15Data.getOpen();

			longData.getHighLow();
			longData.getOpen();

			try
			{
			if (TimePeriodDecider.getTime() > 92000)
			{
				if (Global.getCurrentPoint() - getShortTB().getLatestCandle().getLow() > 15)
					Global.setRapidRise(true);
				else
					Global.setRapidRise(false);

				if (getShortTB().getLatestCandle().getHigh() - Global.getCurrentPoint() > 15)
					Global.setRapidDrop(true);
				else
					Global.setRapidDrop(false);
			}
			}catch (Exception e)
			{
				e.printStackTrace();
			}
			
			// that Math.abs is for when min = 59 and ref = -1
			if (min > refMin && Math.abs(min - refMin) < 10)
			{

				// System.out.println("min " + min);
				// System.out.println("refMin " + refMin);
				// System.out.println(" ");

				shortMinutes++;
				longMinutes++;
				m15Minutes++;

				if (refMin == 58)
					refMin = -1;
				else
					refMin = min;
			}

			if (shortMinutes == Setting.getShortTB())
			{

				getShortTB().addData(getPreviousPt(), Float.valueOf(totalQuantity));

				if (getShortTB().tops.size() > 1)
					getShortTB().zone.setResist();
				if (getShortTB().bottoms.size() > 1)
					getShortTB().zone.setSupport();

				getShortTB().addCandle(getTime(), shortData.periodHigh, shortData.periodLow, shortData.openPt,
						getPreviousPt(), totalQuantity);

				for (int x = 0; x < shortTB.EMAs.length; x++)
					shortTB.EMAs[x].setlatestEMA(calDeal);

				// getShortTB().getMACD();

				// Global.addLog("EMA250: " + getEma250().getEMA());

				// if (calDeal == 23868)
				// System.out.println("xxx time: " + getTime());
				//
				// System.out.println("time: " + getTime());

				shortMinutes = 0;
				shortData.reset();
			}

			if (m15Minutes == 15)
			{
				if (!(noQuantity))
					getM15TB().addData(getPreviousPt(), Float.valueOf(totalQuantity));
				else
					getM15TB().addPoint(getPreviousPt());

				getM15TB().addCandle(getTime(), m15Data.periodHigh, m15Data.periodLow, m15Data.openPt, getPreviousPt(),
						totalQuantity);

				// System.out.println(AutoTradeDB.getTime() + " " +
				// calDeal);
				getM15TB().getMACD();
				m15Minutes = 0;
				m15Data.reset();

				if (Global.getAOH() == 0)
				{
					if (TimePeriodDecider.getTime() >= 91510)
					{
						Global.setAOH(getM15TB().getLatestCandle().getHigh());
						Global.setAOL(getM15TB().getLatestCandle().getLow());

						Global.addLog("--------------------");
						Global.addLog("AOH: " + Global.getAOH());
						Global.addLog("AOL: " + Global.getAOL());
						Global.addLog("--------------------");

					}

				}

			}

			if (longMinutes == Setting.getLongTB())
			{

				getLongTB().addData(getPreviousPt(), Float.valueOf(totalQuantity));

				getLongTB().addCandle(getTime(), longData.periodHigh, longData.periodLow, longData.openPt,
						getPreviousPt(), totalQuantity);

				for (int x = 0; x < longTB.EMAs.length; x++)
					longTB.EMAs[x].setlatestEMA(calDeal);

				// getLongTB().getMACD();
				// System.out.println("MACD Histo: "
				// + getLongTB().getMACDHistogram());
				longMinutes = 0;
				longData.reset();

				// getLongTB().ti.calculateEMA(5);
				// getLongTB().ti.calculateEMA(6);
				// Global.addLog("EMA5: " + getLongTB().getEMA(5));

				// Global.addLog("EMA5/6: " + getLongTB().getEMA(5) + " / "
				// + getLongTB().getEMA(6));
				// Global.addLog("Diff: " + (getLongTB().getEMA(5) -
				// getLongTB().getEMA(6)));
				// Global.addLog("------------------------");
			}

			setGlobal();
			// counter += 1;
			// counter10Sec += 1;
			// }
			wan.endWaiter();
			if (!Global.isTradeTime())
			{
				// counter = 1;
			}
		}

		Global.setTradeTime(false);
		Global.setQuotePowerTime(false);
		Global.setOrderTime(false);

		Global.setForceSellTime(false);

		// addQuantities();

		if (!(Global.analysingAll))
		{

			// Global.addLog("5 min Quantity: "
			// + getShortTB().getQuatityByPeriods(5));
			// Global.addLog("total Quantity: " +
			// getShortTB().getTotalQuantity());
			Global.addLog("Balance: " + Global.balance + " Trades: " + Global.noOfTrades);
			Setting.result.setText(Global.getLog());
			Global.balance = 0.0F;
		}

		if (!(Global.analysingAll))
			getShortTB().createChart();

		Global.clearLog();
		shortMinutes = 0;
		longMinutes = 0;
		m15Minutes = 0;

		// should be put to the end
		Global.setRunning(false);
	}

	private void addRenko()
	{

		if (candles.size() == 0)
		{

		}

	}

	// private Date addTime() {
	// if (candles.size() == 0)
	// return getTime();
	// else {
	// DateFormat formatter = new SimpleDateFormat("hh:mm");
	// Date time = candles.get(candles.size() - 1).getTime();
	// String hr = time.toString().substring(0, 2);
	// String min = time.toString().substring(3, 5);
	//
	// if (min.equals("59")) {
	// min = "00";
	// Integer i = new Integer(hr);
	// i++;
	// hr = i.toString();
	// } else {
	// Integer i = new Integer(min);
	// i++;
	// min = i.toString();
	// }
	// try {
	// return formatter.parse(hr + ":" + min);
	// } catch (ParseException e) {
	// e.printStackTrace();
	// return getTime();
	// }
	// }
	// }

	private void setGlobal()
	{
		if (Global.getDayHigh() - Global.getDayLow() < 100.0F)
			Global.setLowFluctuation(true);
		else
			Global.setLowFluctuation(false);
	}

	public static EMA getEma5()
	{
		return shortTB.EMAs[0];
	}

	public static EMA getEma25()
	{
		return shortTB.EMAs[1];
	}

	public static EMA getEma50()
	{
		return shortTB.EMAs[2];
	}

	public static EMA getEma100()
	{
		return shortTB.EMAs[3];
	}

	public static EMA getEma250()
	{
		return shortTB.EMAs[4];
	}

	public static EMA getEma1200()
	{
		return shortTB.EMAs[5];
	}

	private Date getCSVTime(String time)
	{
		Date s = new Date();
		DateFormat formatter = new SimpleDateFormat("hh:mm");
		try
		{
			s = formatter.parse(time.substring(11, 13) + ":" + time.substring(14, 16));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return s;
	}

	private Date getTime()
	{
		Date s = null;

		// HH is 24hr, hh is 12
		DateFormat formatter = new SimpleDateFormat("HH:mm");
		try
		{
			s = formatter.parse(timeNow.substring(1, 6));
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		if (s.toString().contains("00:59:00"))
		{
			System.out.println("Error: getTime " + s);
			System.out.println("Error: TimeNow " + timeNow);
		}
		return s;
	}

	// public static synchronized double getTotalWeightOfRSI() {
	// return totalWeightOfRSI;
	// }

	public static synchronized TimeBase getShortTB()
	{
		return shortTB;
	}

	public static synchronized TimeBase getM15TB()
	{
		return m15TB;
	}

	public static synchronized TimeBase getLongTB()
	{
		return longTB;
	}

	// public static synchronized TimeBase getSec10TB() {
	// return sec10TB;
	// }

	// public static synchronized TimeBase getSecTB() {
	// return secTB;
	// }

	// public static synchronized TimeBase getSecTB() {
	// return secTB;
	// }

	class CandleData
	{
		private double periodHigh = 0.0D;
		private double periodLow = 99999.0D;
		private double openPt = 0.0D;
		private boolean openAdded = false;

		void reset()
		{

			periodHigh = 0.0D;
			periodLow = 99999.0D;
			openAdded = false;
		}

		void getHighLow()
		{
			if (getPreviousPt() > periodHigh)
				periodHigh = getPreviousPt();
			if (getPreviousPt() < periodLow)
				periodLow = getPreviousPt();
		}

		void getOpen()
		{
			if (!openAdded)
			{
				openPt = getPreviousPt();
				openAdded = true;
			}
		}

	}

	private float getPreviousPt()
	{
		if (m1Deal.size() < 2)
			return m1Deal.get(0);
		else
			return m1Deal.get(m1Deal.size() - 2);
	}

	private void addQuantities()
	{

		// Setting.quantities[0] ++;
		//
		// for (int i=1; i<Setting.quantityPeriods.length; i++){
		//
		// Setting.quantities[i] +=
		// getShortTB().getQuatityByPeriods(Setting.quantityPeriods[i])
		// - getShortTB().getQuatityByPeriods(Setting.quantityPeriods[i]-1);
		// }

		Setting.quantity[0][quantityCounter] = quantityCounter;

		for (int i = 1; i < Setting.quantity.length; i++)
		{

			Setting.quantity[i][quantityCounter] = getShortTB().getQuatityByPeriods(Setting.quantityPeriods[i])
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

	private void getPreviousData()
	{

		parseSPRecord csv = new parseSPRecord("SPRecords/" + tableName + "/m1.txt");
		csv.parseOHLC();

		int m5Period = 0;
		double m5Open = 0;
		double m5High = 0;
		double m5Low = 99999;
		// int k = 0;

		for (int i = 0; i < csv.getLow().size(); i++)
		{

			Double close = csv.getClose().get(i);
			// addPoint is for technical indicators
			// getShortTB().addData(close.floatValue(),
			// csv.getVolume().get(i).floatValue());
			// addCandle History is made for previous data, volume is not
			// accumulated
			// getShortTB().addCandleHistory(getCSVTime(csv.getTime().get(i)),
			// csv.getHigh().get(i), csv.getLow().get(i),
			// csv.getOpen().get(i), close, csv.getVolume().get(i));

			if (i == 0)
			{

				for (int x = 0; x < EMAs.length; x++)
				{
					shortTB.EMAs[x] = new EMA(close, EMAs[x]); // creating new
																// instance of
																// EMA and set
																// it to the EMA
																// is array,
																// changing it's
																// reference. It
																// is not the
																// field in
																// shortTB
																// anymore
					longTB.EMAs[x] = new EMA(close, EMAs[x]);

				}
			} else
			{

				for (int x = 0; x < EMAs.length; x++)
				{
					shortTB.EMAs[x].setlatestEMA(close);
					// System.out.println("settting latest EMA" + EMAs[x]);
				}

				m5Period++;

				if (m5Period == 5)
				{
					for (int x = 0; x < EMAs.length; x++)
						longTB.EMAs[x].setlatestEMA(close);

					m5Period = 0;
				}

			}

			// j++;
			// k++;

			// if (j == 5)
			// {
			// getLongTB().addData(close.floatValue(),
			// csv.getVolume().get(i).floatValue());
			// getLongTB().addCandleHistory(getCSVTime(csv.getTime().get(i)),
			// csv.getHigh().get(i),
			// csv.getLow().get(i), csv.getOpen().get(i), close,
			// csv.getVolume().get(i));
			// j = 0;
			// }
			//
			// if (k == 15)
			// {
			// getM15TB().addData(close.floatValue(),
			// csv.getVolume().get(i).floatValue());
			// getM15TB().addCandleHistory(getCSVTime(csv.getTime().get(i)),
			// csv.getHigh().get(i), csv.getLow().get(i),
			// csv.getOpen().get(i), close, csv.getVolume().get(i));
			// k = 0;
			// }

		}

		Global.addLog("Previous m1_EMA250: " + getEma250().getEMA());

	}

	private void setOHLC()
	{

		Global.setpHigh(ohlc.getpHigh());
		Global.setpLow(ohlc.getpLow());
		Global.setpOpen(ohlc.getpOpen());
		Global.setpClose(ohlc.getpClose());
		Global.setOpen(ohlc.getOpen());
		// Global.setpFluc(ohlc.getpFluc());

		if (Global.getpHigh() != 0)
		{
			Global.addLog("-------------------------------------");
			Global.addLog("P.High: " + Global.getpHigh());
			Global.addLog("P.Low: " + Global.getpLow());
			Global.addLog("-------------------------------------");
		}
	}

}
