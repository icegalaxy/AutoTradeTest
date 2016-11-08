package icegalaxy.net;

//Use the OPEN Line

public class RuleIBT extends Rules
{
	private double cutLoss;
	private boolean traded;

	public RuleIBT(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule)
	{
		super(wan1, wan2, globalRunRule);
		setOrderTime(91600, 92000, 160000, 160000, 230000, 230000);
		// wait for EMA6, that's why 0945
	}

	public void openContract()
	{

		if (!isOrderTime() || Global.getNoOfContracts() != 0 || shutdown || TimePeriodDecider.getTime() > 92000
				|| Global.getOpen() == 0 || traded)
			return;

		// Global.addLog("Open: " + Global.getOpen());
		// Global.addLog("EMA50: " + getTimeBase().getEMA(50));
		// Global.addLog("EMA240: " + getTimeBase().getEMA(240));
		// Global.addLog("0");

		// if (Global.getCurrentPoint() > Global.getOpen() + 30 &&
		// Global.getOpen() > Global.getpClose() + 10 &&
		// Global.getCurrentPoint() > getTimeBase().getMA(240)){

		// longContract();
		// traded = true;
		// cutLoss = Math.abs(buyingPoint - Global.getOpen());
		// Global.addLog("cutLoss: " + cutLoss);

		// }else
		if (Global.getCurrentPoint() > Global.getOpen() + 15 && Global.getOpen() > Global.getpClose() + 10
				&& Global.getCurrentPoint() > getTimeBase().getMA(240) && TimePeriodDecider.getTime() > 91800)
		{

			while (getTimeBase().getMA(240) < getTimeBase().getPreviousMA(240))
			{
				if (TimePeriodDecider.getTime() > 100000)
					return;
				
				if (StockDataController.getShortTB().getEMA(5) > StockDataController.getShortTB().getEMA(6))
					break;
				
				if (Global.getCurrentPoint() < Global.getOpen() - 10)
					return;
				
				wanPrevious.middleWaiter(wanNext);
			}

			if (Global.getCurrentPoint() - Global.getOpen() > 50)
			{
				
				Global.addLog("Wait pull back");
				while (Global.getCurrentPoint() - Global.getOpen() > 10)
				{

					if (TimePeriodDecider.getTime() > 100000)
						return;

					wanPrevious.middleWaiter(wanNext);
				}
			}

			longContract();
			traded = true;
			cutLoss = Math.abs(buyingPoint - Global.getOpen());
			Global.addLog("cutLoss: " + cutLoss);

		}

		else if (Global.getCurrentPoint() < Global.getOpen() - 15 && Global.getOpen() - 10 < Global.getpClose()
				&& Global.getCurrentPoint() < getTimeBase().getMA(240) && TimePeriodDecider.getTime() > 91800)
		{

			while (getTimeBase().getMA(240) > getTimeBase().getPreviousMA(240))
			{

				if (StockDataController.getShortTB().getEMA(5) < StockDataController.getShortTB().getEMA(6))
					break;
				
				if (TimePeriodDecider.getTime() > 100000)
					return;
				
				if (Global.getCurrentPoint() > Global.getOpen() + 10)
					return;
				
				wanPrevious.middleWaiter(wanNext);
			}

			if (Global.getOpen() - Global.getCurrentPoint() > 50)
			{
				
				Global.addLog("Wait pull back");
				while (Global.getOpen() - Global.getCurrentPoint() > 10)
				{

					if (TimePeriodDecider.getTime() > 100000)
						return;

					wanPrevious.middleWaiter(wanNext);
				}
			}

			shortContract();
			traded = true;
			cutLoss = Math.abs(buyingPoint - Global.getOpen());
			Global.addLog("cutLoss: " + cutLoss);
		}

		wanPrevious.middleWaiter(wanNext);

	}

	// openOHLC(Global.getpHigh());

	// use 1min instead of 5min
	void updateStopEarn()
	{
		double ema5;
		double ema6;

		if (getProfit() < 100)
		{
			ema5 = StockDataController.getShortTB().getLatestCandle().getClose();
			ema6 = StockDataController.getLongTB().getEMA(5);
		} else
		{
			ema5 = StockDataController.getLongTB().getEMA(5);
			ema6 = StockDataController.getLongTB().getEMA(6);
		}

		if (Global.getNoOfContracts() > 0)
		{

			// if (ema5 < ema6)
			// tempCutLoss = buyingPoint + 5;

			if (ema5 < ema6)
				tempCutLoss = 99999;

		} else if (Global.getNoOfContracts() < 0)
		{

			// if (ema5 > ema6)
			// tempCutLoss = buyingPoint - 5;

			if (ema5 > ema6)
				tempCutLoss = 0;

		}

	}

	// use 1min instead of 5min
	double getCutLossPt()
	{
		return cutLoss + 10;
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
		if (Global.getNoOfContracts() > 0)
		{
			if (StockDataController.getShortTB().getLatestCandle().getClose() > getTimeBase().getEMA(5))
				return -100;
			
			
			
			
		} else if (Global.getNoOfContracts() < 0)
		{
			if (StockDataController.getShortTB().getLatestCandle().getClose() < getTimeBase().getEMA(6))
				return -100;
		}
		return 30;
	}

	@Override
	public TimeBase getTimeBase()
	{
		return StockDataController.getLongTB();
	}

}