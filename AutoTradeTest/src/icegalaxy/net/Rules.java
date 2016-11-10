package icegalaxy.net;

import java.util.ArrayList;

public abstract class Rules implements Runnable
{

	protected boolean hasContract;
	protected WaitAndNotify wanPrevious;
	protected WaitAndNotify wanNext;
	protected double tempCutLoss;
	protected double tempStopEarn;
	protected double refPt;
	protected float buyingPoint;
	private boolean globalRunRule;
	protected String className;
	double stopEarnPt;
	double cutLossPt;

	public int lossTimes;

	private final float CUTLOSS_FACTOR = 5.0F;
	private final float STOPEARN_FACTOR = 5.0F;

	boolean usingMA20;
	boolean usingMA10;
	boolean usingMA5;
	boolean shutdown;

	private static float balance; // holding contracts �� balance

	// can use default trade time, just do not use the setTime method
	int morningOpen = 92000;
	int morningClose = 113000;
	int noonOpen = 130500;
	int noonClose = 160000;
	int nightOpen = 173000;
	int nightClose = 231500;

	public Rules(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule)
	{
		this.wanPrevious = wan1;
		this.wanNext = wan2;
		this.globalRunRule = globalRunRule;
		this.className = this.getClass().getSimpleName();

	}

	// �@�wrun
	public Rules(WaitAndNotify wan1, WaitAndNotify wan2)
	{
		this.wanPrevious = wan1;
		this.wanNext = wan2;
		this.globalRunRule = true;
		this.className = this.getClass().getSimpleName();

	}

	@Override
	public void run()
	{

		if (!globalRunRule)
		{
			while (Global.isRunning())
			{
				wanPrevious.middleWaiter(wanNext);
				;
			}
		} else
		{

			Global.addLog(className + " Acivated");

			while (Global.isRunning())
			{

				usingMA20 = true;
				usingMA10 = true;
				usingMA5 = true;

				if (hasContract)
				{
					closeContract();
				} else
				{
					openContract();
				}

				wanPrevious.middleWaiter(wanNext);
				;
			}
		}
	}

	protected boolean reachGreatProfitPt()
	{

		if (getStopEarnPt() < stopEarnPt)
			stopEarnPt = getStopEarnPt();

		if (Global.getNoOfContracts() > 0)
			return Global.getCurrentBid() - stopEarnPt > buyingPoint; // it's
		// moving
		else if (Global.getNoOfContracts() < 0)
			return Global.getCurrentAsk() + stopEarnPt < buyingPoint;
		else
			return false;
	}

	// can choose not to set the night time
	public void setOrderTime(int morningOpen, int morningClose, int noonOpen, int noonClose)
	{
		this.morningOpen = morningOpen;
		this.morningClose = morningClose;
		this.noonOpen = noonOpen;
		this.noonClose = noonClose;
	}

	// can choose to set the night time
	public void setOrderTime(int morningOpen, int morningClose, int noonOpen, int noonClose, int nightOpen,
			int nightClose)
	{
		this.morningOpen = morningOpen;
		this.morningClose = morningClose;
		this.noonOpen = noonOpen;
		this.noonClose = noonClose;
		this.nightOpen = nightOpen;
		this.nightClose = nightClose;
	}

	public boolean isOrderTime()
	{

		int time = TimePeriodDecider.getTime();

		// System.out.println("Check: " + time + morningOpen + morningClose +

		if (time > morningOpen && time < morningClose)
		{
			// Global.addLog("Time 1 = " + time + true);
			return true;
		} else if (time > noonOpen && time < noonClose)
		{
			// Global.addLog("Time 2 = " + time + true);
			return true;
		} else if (time > nightOpen && time < nightClose)
		{
			// Global.addLog("Time 3 = " + time + true);
			return true;
		} else
		{
			// Global.addLog("Time 4 = " + time + false);
			return false;
		}

	}

	protected void updateCutLoss()
	{

		if (getCutLossPt() < cutLossPt)
			cutLossPt = getCutLossPt();

		// if (getStopEarnPt() < stopEarnPt)
		// stopEarnPt = getStopEarnPt();

		if (Global.getNoOfContracts() > 0)
		{
			// if (Global.getCurrentPoint() - tempCutLoss > cutLossPt) {
			// tempCutLoss = Global.getCurrentPoint() - cutLossPt;
			// System.out.println("CurrentPt: " + Global.getCurrentPoint());
			// System.out.println("cutLossPt: " + cutLossPt);
			// System.out.println("TempCutLoss: " + tempCutLoss);
			// }

			if (buyingPoint - cutLossPt > tempCutLoss)
				tempCutLoss = buyingPoint - cutLossPt;

			// if (Global.getCurrentPoint() + stopEarnPt < tempStopEarn) {
			// tempStopEarn = Global.getCurrentPoint() + stopEarnPt;
			// System.out.println("TempStopEarn: " + tempStopEarn);
			// }

		} else if (Global.getNoOfContracts() < 0)
		{
			// if (tempCutLoss - Global.getCurrentPoint() > cutLossPt) {
			// tempCutLoss = Global.getCurrentPoint() + cutLossPt;
			// System.out.println("CurrentPt: " + Global.getCurrentPoint());
			// System.out.println("cutLossPt: " + cutLossPt);
			// System.out.println("TempCutLoss: " + tempCutLoss);
			// }

			if (buyingPoint + cutLossPt < tempCutLoss)
				tempCutLoss = buyingPoint + cutLossPt;

			// if (Global.getCurrentPoint() - stopEarnPt > tempStopEarn) {
			// tempStopEarn = Global.getCurrentPoint() - stopEarnPt;
			// System.out.println("TempStopEarn: " + tempStopEarn);
			// }
		}
	}

	// protected void cutLoss() {
	// if (Global.getNoOfContracts() > 0
	// && Global.getCurrentPoint() < tempCutLoss) {
	// closeContract(className + ": CutLoss, short @ "
	// + Global.getCurrentBid());
	// shutdown = true;
	// } else if (Global.getNoOfContracts() < 0
	// && Global.getCurrentPoint() > tempCutLoss) {
	// closeContract(className + ": CutLoss, long @ "
	// + Global.getCurrentAsk());
	// shutdown = true;
	// }
	// }

	// Use the latest 1min close instead of current point
	protected void cutLoss()
	{

		double refPt = 0;

		if (isInsideDay())
			// refPt =
			// StockDataController.getLongTB().getLatestCandle().getClose();
			refPt = Global.getCurrentPoint();
		else
			// refPt =
			// StockDataController.getShortTB().getLatestCandle().getClose();
			refPt = Global.getCurrentPoint();

		if (Global.getNoOfContracts() > 0 && refPt < tempCutLoss)
		{
			closeContract(className + ": CutLoss, short @ " + Global.getCurrentBid());
			shutdown = true;
		} else if (Global.getNoOfContracts() < 0 && refPt > tempCutLoss)
		{
			closeContract(className + ": CutLoss, long @ " + Global.getCurrentAsk());
			shutdown = true;
		}
	}

	void stopEarn()
	{
		if (Global.getNoOfContracts() > 0 && Global.getCurrentPoint() < tempCutLoss)
		{

			if (Global.getCurrentPoint() < buyingPoint)
			{
				cutLoss();
				return;
			}

			closeContract(className + ": StopEarn, short @ " + Global.getCurrentBid());
			if (lossTimes > 0)
				lossTimes--;

		} else if (Global.getNoOfContracts() < 0 && Global.getCurrentPoint() > tempCutLoss)
		{

			if (Global.getCurrentPoint() > buyingPoint)
			{
				cutLoss();
				return;
			}

			closeContract(className + ": StopEarn, long @ " + Global.getCurrentAsk());
			if (lossTimes > 0)
				lossTimes--;
		}
	}

	public void closeContract(String msg)
	{

		boolean b = Sikuli.closeContract();
		Global.addLog(msg);
		Global.addLog("");
		Global.addLog("Current Balance: " + Global.balance + " points");
		Global.addLog("____________________");
		Global.addLog("");
		if (b)
			hasContract = false;
	}

	public void closeContract()
	{

		if (Global.getNoOfContracts() > 0)
		{
			tempCutLoss = buyingPoint - getCutLossPt();
			tempStopEarn = buyingPoint + getStopEarnPt();
		} else if (Global.getNoOfContracts() < 0)
		{
			tempCutLoss = buyingPoint + getCutLossPt();
			tempStopEarn = buyingPoint - getStopEarnPt();
		}

		stopEarnPt = getStopEarnPt();
		cutLossPt = 100; // �O�׫Y�O�I�A��ĤG��set���H�Pcut loss�Ӥj,
		// �O�ӫYMaximum

		while (!reachGreatProfitPt())
		{

			updateCutLoss();
			cutLoss();

			// checkRSI();
			// checkDayHighLow();
			if (trendReversed())
			{
				closeContract(className + ": Trend Reversed");
				return;
			}

			if (trendUnstable())
			{
				closeContract(className + ": Trend Unstable");
				return;
			}
			// if (maReversed())
			// return;

			if (Global.isForceSellTime())
			{
				closeContract("Force Sell");
				return;
			}

			if (Global.getNoOfContracts() == 0)
			{ // �i��ڨ�Lrule
				// close���A��Trend
				// truned�A�̧Y�Y�४�աA��
				hasContract = false;
				break;
			}

			if (!hasContract)
				break;

			wanPrevious.middleWaiter(wanNext);
			;
		}

		if (Global.getNoOfContracts() == 0)
		{
			hasContract = false;
			return;
		}

		if (!hasContract)
			return;

		// updateCutLoss();
		refPt = Global.getCurrentPoint();

		Global.addLog(className + ": Secure profit @ " + Global.getCurrentPoint());

		while (hasContract)
		{

			if (Global.getNoOfContracts() == 0)
			{
				hasContract = false;
				break;
			}

			if (trendReversed2())
				closeContract(className + ": TrendReversed2");

			if (Global.isForceSellTime())
			{
				closeContract("Force Sell");
				return;
			}

			updateStopEarn();
			stopEarn();

			updateCutLoss();
			cutLoss();

			// System.out.println("Temp Stop Earn" + tempCutLoss);

			wanPrevious.middleWaiter(wanNext);
			;
		}
	}

	boolean trendReversed2()
	{
		return false;
	}

	boolean trendUnstable()
	{
		return false;
	}

	protected float getAGAL()
	{

		StockDataController.getShortTB().getRSI(); // ���[�O�y����AGAL�Y���|����
		return (StockDataController.getShortTB().getAG() + StockDataController.getShortTB().getAL()); // �Y���O�׫Y���Y�n�εfShort
		// Period
		// ALAG��Ĺ��
	}

	public void shortContract()
	{

		if (!Global.isOrderTime())
		{
			Global.addLog(className + ": not order time");
			return;
		}

		if (Global.getNoOfContracts() != 0)
		{
			Global.addLog(className + ": Has order already!");
			return;
		}

		boolean b = Sikuli.shortContract(1);
		if (!b)
			return;
		hasContract = true;
		Global.addLog(className + ": Short @ " + Global.getCurrentBid());
		buyingPoint = Global.getCurrentBid();
		balance += Global.getCurrentBid();
	}

	public void longContract()
	{

		if (!Global.isOrderTime())
		{
			Global.addLog(className + ": not order time");
			return;
		}

		if (Global.getNoOfContracts() != 0)
		{
			Global.addLog(className + ": Has order already!");
			return;
		}

		boolean b = Sikuli.longContract(1);
		if (!b)
			return;
		hasContract = true;
		Global.addLog(className + ": Long @" + Global.getCurrentAsk());
		buyingPoint = Global.getCurrentAsk();
		balance -= Global.getCurrentAsk();
	}

	public abstract void openContract();

	void updateStopEarn()
	{

		if (Global.getNoOfContracts() > 0)
		{

			if (StockDataController.getShortTB().getLatestCandle().getLow() > tempCutLoss)
			{
				tempCutLoss = StockDataController.getShortTB().getLatestCandle().getLow();
				// usingMA20 = false;
				// usingMA10 = false;
				// usingMA5 = false;
			}

		} else if (Global.getNoOfContracts() < 0)
		{

			if (StockDataController.getShortTB().getLatestCandle().getHigh() < tempCutLoss)
			{
				tempCutLoss = StockDataController.getShortTB().getLatestCandle().getHigh();
				// usingMA20 = false;
				// usingMA10 = false;
				// usingMA5 = false;
			}
		}

	}

	public boolean isInsideDay()
	{

		return Global.getCurrentPoint() > Global.getpLow() + 5 && Global.getCurrentPoint() < Global.getpHigh() - 5;

	}

	void secondStopEarn()
	{

		if (Global.getNoOfContracts() > 0)
		{
			if (Global.getCurrentPoint() < StockDataController.getLongTB().getEMA(5))
			{
				tempCutLoss = 99999;
				Global.addLog(className + " StopEarn: Current Pt < EMA5");
			}
		} else if (Global.getNoOfContracts() < 0)
		{
			if (Global.getCurrentPoint() > StockDataController.getLongTB().getEMA(5))
			{
				tempCutLoss = 0;
				Global.addLog(className + " StopEarn: Current Pt > EMA5");

			}
		}

	}

	void thirdStopEarn()
	{

		if (Global.getNoOfContracts() > 0)
		{
			if (StockDataController.getLongTB().getEMA(5) < StockDataController.getLongTB().getEMA(6))
			{
				tempCutLoss = 99999;
				Global.addLog(className + " StopEarn: EMA5 < EMA6");
			}
		} else if (Global.getNoOfContracts() < 0)
		{
			if (StockDataController.getLongTB().getEMA(5) > StockDataController.getLongTB().getEMA(6))
			{
				tempCutLoss = 0;
				Global.addLog(className + " StopEarn: EMA5 > EMA6");

			}
		}

	}

	double getCutLossPt()
	{
		return getAGAL() * CUTLOSS_FACTOR;
		// return StockDataController.getShortTB().getHL15().getFluctuation() /
		// CUTLOSS_FACTOR;
	}

	double getStopEarnPt()
	{
		return getAGAL() * STOPEARN_FACTOR;
		// return StockDataController.getShortTB().getHL15().getFluctuation() /
		// STOPEARN_FACTOR;
	}

	public void setName(String s)
	{
		className = s;
	}

	public static synchronized float getBalance()
	{
		if (Global.getNoOfContracts() > 0)
			return balance + Global.getCurrentPoint() * Global.getNoOfContracts();
		else if (Global.getNoOfContracts() < 0)
			return balance + Global.getCurrentPoint() * Global.getNoOfContracts();
		else
		{
			balance = 0;
			return balance;
		}
	}

	public static synchronized void setBalance(float balance)
	{
		Rules.balance = balance;
	}

	public abstract TimeBase getTimeBase();

	boolean maRising(int period)
	{
		return getTimeBase().isMARising(period, 1);
	}

	boolean maDropping(int period)
	{
		return getTimeBase().isMADropping(period, 1);
	}

	boolean emaRising(int period)
	{
		return getTimeBase().isEMARising(period, 1);
	}

	boolean emaDropping(int period)
	{
		return getTimeBase().isEMADropping(period, 1);
	}

	boolean trendReversed()
	{

		// double slope = 0;
		// double longSlope = 0;
		//
		// if (Global.getNoOfContracts() > 0){
		// if (StockDataController.getSec10TB().getMainDownRail()
		// .getSlope() != 100)
		// slope = StockDataController.getSec10TB().getMainDownRail()
		// .getSlope();
		//
		// if (getTimeBase().getMainUpRail().getSlope() != 100)
		// longSlope = getTimeBase().getMainUpRail().getSlope();
		//
		// }
		// if (Global.getNoOfContracts() < 0){
		//
		// if (StockDataController.getSec10TB().getMainUpRail().getSlope() !=
		// 100)
		// slope = StockDataController.getSec10TB().getMainUpRail().getSlope();
		//
		// if (getTimeBase().getMainDownRail().getSlope() != 100)
		// longSlope = getTimeBase().getMainDownRail().getSlope();
		// }
		// return slope > 5 && slope > longSlope*2;

		return false;

	}

	protected void sleep(int i)
	{
		try
		{
			Thread.sleep(i);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	public double getProfit()
	{
		if (Global.getNoOfContracts() > 0)
			return Global.getCurrentPoint() - buyingPoint;
		else
			return buyingPoint - Global.getCurrentPoint();
	}

	public boolean isSylviaUpTrend()
	{
		return Global.getCurrentPoint() > StockDataController.getShortTB().getEMA(5)
				&& Global.getCurrentPoint() > StockDataController.getShortTB().getMA(20)
				&& Global.getCurrentPoint() > StockDataController.getShortTB().getEMA(50)
				&& Global.getCurrentPoint() > StockDataController.getShortTB().getEMA(240);

	}

	public boolean isSylviaDownTrend()
	{
		return Global.getCurrentPoint() < StockDataController.getShortTB().getEMA(5)
				&& Global.getCurrentPoint() < StockDataController.getShortTB().getMA(20)
				&& Global.getCurrentPoint() < StockDataController.getShortTB().getEMA(50)
				&& Global.getCurrentPoint() < StockDataController.getShortTB().getEMA(240);

	}

	public boolean isMyUpTrend()
	{
		return StockDataController.getLongTB().getEMA(5) > StockDataController.getLongTB().getEMA(6);
	}

	public boolean isMyDownTrend()
	{
		return StockDataController.getLongTB().getEMA(5) < StockDataController.getLongTB().getEMA(6);
	}

	public double getHighestMA()
	{

		double highestMA = 0;

		for (int i = 0; i < get4MAs().size(); i++)
		{
			highestMA = Math.max(highestMA, get4MAs().get(i));
		}
		return highestMA;
	}

	public double getLowestMA()
	{

		double lowestMA = 99999;

		for (int i = 0; i < get4MAs().size(); i++)
		{
			lowestMA = Math.min(lowestMA, get4MAs().get(i));
		}
		return lowestMA;
	}

	private ArrayList<Float> get4MAs()
	{
		ArrayList<Float> mas = new ArrayList<Float>();

		mas.add(StockDataController.getM15TB().getMA(20));
		mas.add(StockDataController.getM15TB().getEMA(50));
		mas.add(StockDataController.getLongTB().getEMA(50));
		mas.add(StockDataController.getLongTB().getEMA(240));

		return mas;

	}

	// Danny �l�ȥ�e�w��V
	public boolean isUpTrend()
	{
		return StockDataController.getM15TB().getMA(20) > StockDataController.getM15TB().getEMA(50)
				&& StockDataController.getLongTB().getEMA(50) > StockDataController.getLongTB().getEMA(240);
		// && StockDataController.getLongTB().getLatestCandle().getClose() >
		// StockDataController.getM15TB()
		// .getMA(20)
		// && StockDataController.getLongTB().getLatestCandle().getClose() >
		// StockDataController.getM15TB()
		// .getEMA(50)
		// && StockDataController.getLongTB().getLatestCandle().getClose() >
		// StockDataController.getLongTB()
		// .getEMA(50)
		// && StockDataController.getLongTB().getLatestCandle().getClose() >
		// StockDataController.getLongTB()
		// .getEMA(240);
	}

	public boolean isDownTrend()
	{
		return StockDataController.getM15TB().getMA(20) < StockDataController.getM15TB().getEMA(50)
				&& StockDataController.getLongTB().getEMA(50) < StockDataController.getLongTB().getEMA(240);
		// && StockDataController.getLongTB().getLatestCandle().getClose() >
		// StockDataController.getM15TB()
		// .getMA(20)
		// && StockDataController.getLongTB().getLatestCandle().getClose() >
		// StockDataController.getM15TB()
		// .getEMA(50)
		// && StockDataController.getLongTB().getLatestCandle().getClose() >
		// StockDataController.getLongTB()
		// .getEMA(50)
		// && StockDataController.getLongTB().getLatestCandle().getClose() >
		// StockDataController.getLongTB()
		// .getEMA(240);

	}

	public boolean isSideWay()
	{

		return !StockDataController.getLongTB().isEMARising(50, 1)
				&& !StockDataController.getLongTB().isEMADropping(50, 1);
	}

	void reverseOHLC(double ohlc)
	{
		if (Global.getCurrentPoint() <= ohlc + 5 && Global.getCurrentPoint() >= ohlc - 5)
		{

			Global.addLog(className + ": Entered waiting zone");
			Global.addLog("MA20(M15): " + StockDataController.getM15TB().getMA(20));
			Global.addLog("EMA50(M15): " + StockDataController.getM15TB().getEMA(50));
			Global.addLog("EMA50(M5): " + StockDataController.getLongTB().getEMA(50));
			Global.addLog("EMA240(M5): " + StockDataController.getLongTB().getEMA(240));
			Global.addLog("");

			while (Global.getCurrentPoint() <= ohlc + 20 && Global.getCurrentPoint() >= ohlc - 20)
				wanPrevious.middleWaiter(wanNext);
			;

			if (Global.getCurrentPoint() > ohlc + 20 && isSideWay())
			{
				shortContract();
			} else if (Global.getCurrentPoint() < ohlc - 20 && isSideWay())
			{
				longContract();
			}
		}
	}

	void openOHLC(double ohlc)
	{
		if (Math.abs(Global.getCurrentPoint() - ohlc) < 5)
		{

			Global.addLog(className + ": Entered waiting zone");
			waitForANewCandle();
			// wait until it standing firmly
			while (Math.abs(Global.getCurrentPoint() - ohlc) < 30)
				wanPrevious.middleWaiter(wanNext);
			;

			// Global.addLog(className + ": Waiting for a pull back");
			// //in case it get too fast, wait until it come back, just like
			// second corner but a little bit earlier
			// while (Math.abs(Global.getCurrentPoint() - ohlc) > 10) {
			// if (Math.abs(Global.getCurrentPoint() - ohlc) > 50) {
			// Global.addLog(className + ": Risk is too big");
			// return;
			// }

			// wanPrevious.middleWaiter(wanNext);;
			// }

			// for outside
			// if (Global.getCurrentPoint() > Global.getDayHigh()) {
			// if (StockDataController.getLongTB().getLatestCandle().getClose()
			// > ohlc + 5)
			// longContract();
			// } else if (Global.getCurrentPoint() < Global.getDayLow()) {
			// if (StockDataController.getLongTB().getLatestCandle().getClose()
			// < ohlc - 5)
			// shortContract();
			//
			// // for inside
			// } else {
			if (StockDataController.getShortTB().getLatestCandle().getClose() > ohlc && isUpTrend())
			{
				// if (StockDataController.getShortTB().getEMA(5) <
				// StockDataController.getShortTB().getEMA(6)){
				// Global.addLog("Not Trending Up: EMA5 < EMA6");
				// return;
				// }
				longContract();
			} else if (StockDataController.getShortTB().getLatestCandle().getClose() < ohlc && isDownTrend())
			{
				// if (StockDataController.getShortTB().getEMA(5) >
				// StockDataController.getShortTB().getEMA(6)){
				// Global.addLog("Not Trending Down: EMA5 > EMA6");
				// return;
				// }
				shortContract();
			}
			// }
		}
	}

	public void waitForANewCandle()
	{

		int currentSize = StockDataController.getLongTB().getCandles().size();

		while (currentSize == StockDataController.getLongTB().getCandles().size())
			wanPrevious.middleWaiter(wanNext);
		;

	}

	public boolean isAfternoonTime()
	{

		int time = TimePeriodDecider.getTime();

		if (time > noonOpen && time < noonClose)
			return true;
		else
			return false;
	}
}
