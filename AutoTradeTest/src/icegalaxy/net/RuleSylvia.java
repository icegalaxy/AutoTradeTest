package icegalaxy.net;

//Use the OPEN Line

public class RuleSylvia extends Rules
{

	private int lossTimes;
	// private double refEMA;
	private boolean firstCorner = false;
	private double cutLoss;

	public RuleSylvia(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule)
	{
		super(wan1, wan2, globalRunRule);
		setOrderTime(93000, 113000, 130500, 160000, 230000, 230000);
		// wait for EMA6, that's why 0945
	}

	public void openContract()
	{

		if (shutdown)
		{
			lossTimes++;
			firstCorner = true;
			shutdown = false;
		}

		if (!isOrderTime() || Global.getNoOfContracts() != 0 || lossTimes >= 2)
			return;

		while (isSylviaUpTrend() || isSylviaDownTrend())
		{
			Global.addLog("Waiting to settle");
			wanPrevious.middleWaiter(wanNext);
		}
		
		while (!isSylviaUpTrend() && !isSylviaDownTrend())
		{
			Global.addLog("Waiting to break through");
			wanPrevious.middleWaiter(wanNext);
		}

		if (isSylviaUpTrend())
		{

			longContract();
		} else if (isSylviaDownTrend())
		{

			shortContract();

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
		return GetData.getLongTB();
	}

}