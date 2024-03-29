package icegalaxy.net;

//Use the OPEN Line

public class RuleDanny2 extends Rules
{

	private int lossTimes;
	// private double refEMA;
	private boolean firstCorner = true;
	private Chasing chasing;

	public RuleDanny2(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule)
	{
		super(wan1, wan2, globalRunRule);
		setOrderTime(91600, 113000, 130500, 160000, 230000, 230000);
		chasing = new Chasing();
		// wait for EMA6, that's why 0945
	}
	
	private double getRefPt(){
		return getTimeBase().getEMA(10);
	}

	public void openContract()
	{
		


		if (shutdown)
		{
			lossTimes++;
			shutdown = false;
		}

		if (!isOrderTime() || Global.getNoOfContracts() != 0 || lossTimes >= 10)
			return;

		if (isUpTrend2() 
				&& getTimeBase().getEMA(5) > getTimeBase().getEMA(10)
				&& StockDataController.getShortTB().getLatestCandle().getClose() < getTimeBase().getEMA(5)
				&& Global.getCurrentPoint() > getTimeBase().getEMA(10))
		{

			Global.addLog("Up Trend");

			while (StockDataController.getShortTB().getLatestCandle().getClose() < getTimeBase().getEMA(5))
			{
				wanPrevious.middleWaiter(wanNext);

				if (!isUpTrend2())
				{
					Global.addLog("Trend Change");
					return;
				}
			}
				
			longContract();
			cutLossPt = Math.abs(buyingPoint - getTimeBase().getEMA(10));
			Global.addLog("EMA5: " + getTimeBase().getEMA(5));
			Global.addLog("EMA10: " + getTimeBase().getEMA(10));
			Global.addLog("EMA50: " + getTimeBase().getEMA(50));
			Global.addLog("EMA240: " + getTimeBase().getEMA(240));
			Global.addLog("CutLossPt: " + cutLossPt);
			
		} else if (isDownTrend2()
				&& getTimeBase().getEMA(5) < getTimeBase().getEMA(10)
				&& StockDataController.getShortTB().getLatestCandle().getClose() > getTimeBase().getEMA(5)
				&& Global.getCurrentPoint() < getTimeBase().getEMA(10))
		{

			Global.addLog("Down Trend");

			while (StockDataController.getShortTB().getLatestCandle().getClose() > getTimeBase().getEMA(5))
			{
				wanPrevious.middleWaiter(wanNext);

				if (!isDownTrend2())
				{
					Global.addLog("Trend Change");
					return;
				}
			}

		
			shortContract();
			cutLossPt = Math.abs(buyingPoint - getTimeBase().getEMA(10));
			Global.addLog("EMA5: " + getTimeBase().getEMA(5));
			Global.addLog("EMA10: " + getTimeBase().getEMA(10));
			Global.addLog("EMA50: " + getTimeBase().getEMA(50));
			Global.addLog("EMA240: " + getTimeBase().getEMA(240));
//			Global.addLog("Before High: " + refPt);
			Global.addLog("CutLossPt: " + cutLossPt);

		}

	}

	private int getLossTimesAllowed()
	{

		double balance = Global.balance + Global.getCurrentPoint() * Global.getNoOfContracts();

		if (balance > 60)
			return 3;
		else if (balance > 30)
			return 2;
		else
			return 1;
	}

	// use 1min instead of 5min
	void updateStopEarn()
	{

		double ema5;
		double ema6;
		

		// if (Math.abs(getTimeBase().getEMA(5) - getTimeBase().getEMA(6)) <
		// 10){
		ema5 = StockDataController.getShortTB().getLatestCandle().getClose();
		ema6 = getTimeBase().getEMA(5);


		if (Global.getNoOfContracts() > 0)
		{

			if (buyingPoint > tempCutLoss && getProfit() > 50)
			{
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint + 5;
			}

			if (ema5 < ema6)
			{
				tempCutLoss = 99999;
				Global.addLog(className + " StopEarn: EMA5 x MA20");
				chasing.setChaseUp(true);
			}
		} else if (Global.getNoOfContracts() < 0)
		{

			if (buyingPoint < tempCutLoss && getProfit() > 50)
			{
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint - 5;
	
			}

			if (ema5 > ema6)
			{
				tempCutLoss = 0;
				Global.addLog(className + " StopEarn: EMA5 x MA20");
				chasing.setChaseDown(true);
			}
		}

	}

	// use 1min instead of 5min
	double getCutLossPt()
	{

		
		return Math.max(cutLossPt + 10, 30);

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
		
		if (Global.getCurrentPoint() > chasing.getRefHigh())
			chasing.setRefHigh(Global.getCurrentPoint());
		if (Global.getCurrentPoint() < chasing.getRefLow())
			chasing.setRefLow(Global.getCurrentPoint());
	}

	double getStopEarnPt()
	{

		// if (Global.getNoOfContracts() > 0){
		// if (!isUpTrend())
		// return -100;
		// }else if (Global.getNoOfContracts() < 0){
		// if (!isDownTrend())
		// return -100;
		// }

		return 30;
	}

	@Override
	public TimeBase getTimeBase()
	{
		return StockDataController.getLongTB();
	}

}