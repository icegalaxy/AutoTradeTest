package icegalaxy.net;

//Use the OPEN Line

public class RuleIBT extends Rules
{

	private double cutLoss;

	public RuleIBT(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule)
	{
		super(wan1, wan2, globalRunRule);
		setOrderTime(91600, 100000, 16000, 160000, 230000, 230000);
		// wait for EMA6, that's why 0945
	}

	public void openContract()
	{

		if (!isOrderTime() || Global.getNoOfContracts() != 0 || shutdown
				|| TimePeriodDecider.getTime() > 91800 || Global.getOpen() == 0)
			return;

//		Global.addLog("Open: " + Global.getOpen());
//		Global.addLog("EMA50: " + getTimeBase().getEMA(50));
//		Global.addLog("EMA240: " + getTimeBase().getEMA(240));
//		Global.addLog("0");
		
		if (Global.getCurrentPoint() > Global.getOpen() && Global.getCurrentPoint() > getTimeBase().getMA(240))
		{
			
//			Global.addLog("1");

			while (TimePeriodDecider.getTime() < 91600)
			{

				if (Global.getCurrentPoint() < Global.getOpen())
				{
					shutdown = true;
					Global.addLog(className + ": not standing up");
					return;
				}

				wanPrevious.middleWaiter(wanNext);
			}

			Global.addLog(className + ": waiting for a pull back");

			while (Global.getCurrentPoint() > Global.getpOpen() + 15)
			{

				if (Global.getCurrentPoint() < Global.getOpen())
				{
					shutdown = true;
					Global.addLog(className + ": not standing up");
					return;
				}

				if (TimePeriodDecider.getTime() > 91800)
				{
					shutdown = true;
					Global.addLog(className + ": waited for too long");
					return;
				}

				wanPrevious.middleWaiter(wanNext);
			}

			Global.addLog(className + ": waiting for a second corner");

			while (StockDataController.getShortTB().getLatestCandle().getClose() < StockDataController.getShortTB()
					.getPreviousCandle(1).getClose())
			{

				if (Global.getCurrentPoint() < Global.getOpen())
				{
					shutdown = true;
					Global.addLog(className + ": not standing up");
					return;
				}
				
				if (TimePeriodDecider.getTime() > 91800)
				{
					shutdown = true;
					Global.addLog(className + ": waited for too long");
					return;
				}

				wanPrevious.middleWaiter(wanNext);
			}

			longContract();

		} else if (Global.getCurrentPoint() < Global.getOpen() && Global.getCurrentPoint() < getTimeBase().getMA(240))
		{
//			Global.addLog("2");
			
			while (TimePeriodDecider.getTime() < 91600)
			{

				if (Global.getCurrentPoint() > Global.getOpen())
				{
					shutdown = true;
					Global.addLog(className + ": not standing down");
					return;
				}

				wanPrevious.middleWaiter(wanNext);
			}

			Global.addLog(className + ": waiting for a pull back");

			while (Global.getCurrentPoint() < Global.getpOpen() - 15)
			{

				if (Global.getCurrentPoint() > Global.getOpen())
				{
					shutdown = true;
					Global.addLog(className + ": not standing down");
					return;
				}

				if (TimePeriodDecider.getTime() > 91800)
				{
					shutdown = true;
					Global.addLog(className + ": waited for too long");
					return;
				}

				wanPrevious.middleWaiter(wanNext);
			}

			Global.addLog(className + ": waiting for a second corner");

			while (StockDataController.getShortTB().getLatestCandle().getClose() > StockDataController.getShortTB()
					.getPreviousCandle(1).getClose())
			{

				if (Global.getCurrentPoint() > Global.getOpen())
				{
					shutdown = true;
					Global.addLog(className + ": not standing down");
					return;
				}
				
				if (TimePeriodDecider.getTime() > 91800)
				{
					shutdown = true;
					Global.addLog(className + ": waited for too long");
					return;
				}

				wanPrevious.middleWaiter(wanNext);
			}

			shortContract();
			cutLoss = refPt - buyingPoint;
		}

	}

	// openOHLC(Global.getpHigh());

	// use 1min instead of 5min
	void updateStopEarn()
	{

		if (Global.getNoOfContracts() > 0)
		{

			if (buyingPoint > tempCutLoss && getProfit() > 30)
			{
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint;
			}

			if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6))
				tempCutLoss = 99999;

		} else if (Global.getNoOfContracts() < 0)
		{

			if (buyingPoint < tempCutLoss && getProfit() > 30)
			{
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint;
			}

			if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6))
				tempCutLoss = 0;

		}

	}

	// use 1min instead of 5min
	double getCutLossPt()
	{

		return 15;

	}

	@Override
	protected void cutLoss()
	{

		if (Global.getNoOfContracts() > 0 && Global.getCurrentPoint() < tempCutLoss)
		{
			closeContract(className + ": CutLoss, short @ " + Global.getCurrentBid());
			shutdown = true;
		} else if (Global.getNoOfContracts() < 0 && Global.getCurrentPoint() > tempCutLoss)
		{
			closeContract(className + ": CutLoss, long @ " + Global.getCurrentAsk());
			shutdown = true;

		}
	}

	

	double getStopEarnPt()
	{

		if (Global.getNoOfContracts() > 0 && getTimeBase().getEMA(5) > getTimeBase().getEMA(6))
			return -100;
		else if (Global.getNoOfContracts() < 0 && getTimeBase().getEMA(5) < getTimeBase().getEMA(6))
			return -100;

		// 有可能行夠50點都未 5 > 6，咁會即刻食左
		return 30;
	}

	@Override
	public TimeBase getTimeBase()
	{
		return StockDataController.getLongTB();
	}

}