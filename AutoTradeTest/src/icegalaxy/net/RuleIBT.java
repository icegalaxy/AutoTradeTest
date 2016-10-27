package icegalaxy.net;

//Use the OPEN Line

public class RuleIBT extends Rules
{

	private double cutLoss;

	public RuleIBT(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule)
	{
		super(wan1, wan2, globalRunRule);
		setOrderTime(91800, 92000, 160000, 160000, 230000, 230000);
		// wait for EMA6, that's why 0945
	}

	public void openContract()
	{

		if (!isOrderTime() || Global.getNoOfContracts() != 0 || shutdown
				|| TimePeriodDecider.getTime() > 92000 || Global.getOpen() == 0)
			return;

		Global.addLog("Open: " + Global.getOpen());
//		Global.addLog("EMA50: " + getTimeBase().getEMA(50));
//		Global.addLog("EMA240: " + getTimeBase().getEMA(240));
//		Global.addLog("0");
		
		if (Global.getCurrentPoint() > Global.getOpen() + 15 && Global.getOpen() > Global.getpClose() + 10 && Global.getCurrentPoint() > getTimeBase().getMA(240))
		{

			longContract();
			cutLoss = Math.abs(buyingPoint - Global.getOpen());
			
//			Global.addLog("BuyingPt: " + buyingPoint);
//			Global.addLog("Open: " + Global.getOpen());
			Global.addLog("cutLoss: " + cutLoss);
			

		} else if (Global.getCurrentPoint() < Global.getOpen() - 15 && Global.getOpen() -10 < Global.getpClose()  && Global.getCurrentPoint() < getTimeBase().getMA(240))
		{
			


			shortContract();
			cutLoss = Math.abs(buyingPoint - Global.getOpen());
			Global.addLog("cutLoss: " + cutLoss);
		}

	}

	// openOHLC(Global.getpHigh());

	// use 1min instead of 5min
	void updateStopEarn()
	{
		float ema5;
		float ema6;
		
		if (getProfit() > 50 && getProfit() < 100){
			ema5 = StockDataController.getShortTB().getEMA(5);
			ema6 = StockDataController.getShortTB().getEMA(6);
		}else
		{
			ema5 = getTimeBase().getEMA(5);
			ema6 = getTimeBase().getEMA(6);
		}

		if (Global.getNoOfContracts() > 0)
		{
			if (ema5 < ema6 && getProfit() > 30)
				tempCutLoss = 99999;

		} else if (Global.getNoOfContracts() < 0)
		{

			if (ema5 > ema6 && getProfit() > 30)
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
		if (Global.getNoOfContracts() > 0 && getTimeBase().getEMA(5) > getTimeBase().getEMA(6))
			return -100;
		else if (Global.getNoOfContracts() < 0 && getTimeBase().getEMA(5) < getTimeBase().getEMA(6))
			return -100;

		return 30;
	}

	@Override
	public TimeBase getTimeBase()
	{
		return StockDataController.getLongTB();
	}
	


}