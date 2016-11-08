package icegalaxy.net;

//Use the OPEN Line

public class RuleDanny50 extends Rules
{

	private int lossTimes;
	// private double refEMA;
	private boolean firstCorner = true;

	public RuleDanny50(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule)
	{
		super(wan1, wan2, globalRunRule);
		setOrderTime(91600, 113000, 130500, 160000, 230000, 230000);
		// wait for EMA6, that's why 0945
	}

	public void openContract()
	{

		if (shutdown)
		{
			lossTimes++;
			shutdown = false;
		}

		if (!isOrderTime() || Global.getNoOfContracts() != 0 || lossTimes >= getLossTimesAllowed())
			return;
//		
		//in case it is a small range
		while (Math.abs(Global.getCurrentPoint() - getTimeBase().getEMA(50)) < 50)
			wanPrevious.middleWaiter(wanNext);

		while (Math.abs(Global.getCurrentPoint() - getTimeBase().getEMA(50)) > 10)
			wanPrevious.middleWaiter(wanNext);

		if (isUpTrend())
		{

			Global.addLog("Up Trend");

//			while (Global.getCurrentPoint() > getTimeBase().getEMA(50) + 5)
//			{
//				wanPrevious.middleWaiter(wanNext);
//
//				if (!isUpTrend())
//				{
//					Global.addLog("Trend Change");
//					return;
//				}
//			}
			refPt = Global.getCurrentPoint();

			Global.addLog("EMA5: " + StockDataController.getShortTB().getEMA(5));
			Global.addLog("MA20: " + StockDataController.getShortTB().getMA(20));

			while (StockDataController.getShortTB().getEMA(5) < StockDataController.getShortTB().getMA(20))
			{
				wanPrevious.middleWaiter(wanNext);


				if (Global.getCurrentPoint() < refPt)
					refPt = Global.getCurrentPoint();
				
				if (!isUpTrend())
				{
					Global.addLog("Trend Change");
					return;
				}
				
				if (Global.getCurrentPoint() < getTimeBase().getEMA(50) - 30)
				{
					Global.addLog("Penatraded");
					shutdown = true;
					return;
				}
				
				if (Global.getCurrentPoint() > refPt + 50)
				{
					Global.addLog("Risk too high");
					return;
				}


			}
			
			
			longContract();
			cutLossPt = Math.abs(buyingPoint - refPt);
			Global.addLog("Before Low: " + refPt);
			Global.addLog("CutLossPt: " + cutLossPt);

		} else if (isDownTrend())
		{

			Global.addLog("Down Trend");

//			while (Global.getCurrentPoint() < getTimeBase().getEMA(50) - 5)
//			{
//				wanPrevious.middleWaiter(wanNext);
//
//				if (!isDownTrend())
//				{
//					Global.addLog("Trend Change");
//					return;
//				}
//			}
			refPt = Global.getCurrentPoint();

			Global.addLog("EMA5: " + StockDataController.getShortTB().getEMA(5));
			Global.addLog("EMA20: " + StockDataController.getShortTB().getMA(20));

			while (StockDataController.getShortTB().getEMA(5) > StockDataController.getShortTB().getMA(20))
			{

				wanPrevious.middleWaiter(wanNext);

			
				
				if (Global.getCurrentPoint() > refPt)
					refPt = Global.getCurrentPoint();

				if (!isDownTrend())
				{
					Global.addLog("Trend Change");
					return;
				}
				
				if (Global.getCurrentPoint() < refPt - 50)
				{
					Global.addLog("Risk too high");
					return;
				}

				
				if (Global.getCurrentPoint() > getTimeBase().getEMA(50) + 30)
				{
					Global.addLog("Penatraded");
					shutdown = true;
					return;
				}
				
			}
			
		
			shortContract();
			cutLossPt = Math.abs(buyingPoint - refPt);
			Global.addLog("Before High: " + refPt);
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
		// }else{
		// ema5 = StockDataController.getShortTB().getEMA(5);
		// ema6 = StockDataController.getShortTB().getEMA(6);
		// }
		// use 1min TB will have more profit sometime, but will lose so many
		// times when ranging.

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

			}
		}

	}

	// use 1min instead of 5min
	double getCutLossPt()
	{

		if (cutLossPt < 15)
			return 15;
		else
			return cutLossPt;

	}

	@Override
	protected void cutLoss()
	{

		if (Global.getNoOfContracts() > 0 && StockDataController.getShortTB().getLatestCandle().getClose() < tempCutLoss)
		{
			closeContract(className + ": CutLoss, short @ " + Global.getCurrentBid());
			shutdown = true;
		} else if (Global.getNoOfContracts() < 0 && StockDataController.getShortTB().getLatestCandle().getClose() > tempCutLoss)
		{
			closeContract(className + ": CutLoss, long @ " + Global.getCurrentAsk());
			shutdown = true;
		}
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