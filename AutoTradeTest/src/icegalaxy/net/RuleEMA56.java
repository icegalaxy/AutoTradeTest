package icegalaxy.net;

import org.jfree.data.time.TimePeriod;

//Use the OPEN Line

public class RuleEMA56 extends Rules
{

	private int lossTimes;
	// private double refEMA;
	private boolean firstCorner = true;
	private double cutLoss;
	private boolean breakEven;

	public RuleEMA56(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule)
	{
		super(wan1, wan2, globalRunRule);
		setOrderTime(94500, 113000, 130500, 160000, 213000, 230000);
		// wait for EMA6, that's why 0945
	}

	public void openContract()
	{

		if (shutdown)
		{
			lossTimes++;
			// firstCorner = true;
			shutdown = false;
		}

		breakEven = false;

		// while (lossTimes > 0 && TimePeriodDecider.getTime() < 100000)
		// wanPrevious.middleWaiter(wanNext);

		if (!isOrderTime() || Global.getNoOfContracts() != 0 || Global.getpHigh() == 0 || lossTimes >= 1)
			return;

//		if (firstCorner)
//			firstCorner();

		if (hasContract)
			return;

		if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6) && isUpTrend()
//				&& StockDataController.getShortTB().getEMA(5) > StockDataController.getShortTB().getEMA(6)
				)
		{

			// wait for a better position
			Global.addLog(className + ": waiting for a pull back");
			refPt = Global.getCurrentPoint();

			// Global.addLog("Latest Close; " +
			// getTimeBase().getLatestCandle().getClose());
			// Global.addLog("Previous Low; " +
			// getTimeBase().getPreviousCandle(1).getLow());

			while (Global.getCurrentPoint() > getTimeBase().getCurrentEMA(5))
			{
				wanPrevious.middleWaiter(wanNext);

				if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6) || !isUpTrend()
//						|| StockDataController.getShortTB().getEMA(5) < StockDataController.getShortTB().getEMA(6)
						)
				{
					Global.addLog(className + ": trend change");
					return;
				}
			}

			Global.addLog(className + ": waiting for a second corner");
			refPt = Global.getCurrentPoint();

			while (StockDataController.getShortTB().getLatestCandle().getClose() < StockDataController.getShortTB().getPreviousCandle(1).getHigh())
			{
				wanPrevious.middleWaiter(wanNext);

				if (Global.getCurrentPoint() < refPt)
					refPt = Global.getCurrentPoint();

			}

			longContract();
			cutLoss = Math.abs(Global.getCurrentPoint() - refPt);
			Global.addLog("CutLossPt: " + cutLoss);
		} else if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6) && isDownTrend()
//				 && StockDataController.getShortTB().getEMA(5) < StockDataController.getShortTB().getEMA(6)
				 )
		{

			// wait for a better position
			Global.addLog(className + ": waiting for a pull back");
			refPt = Global.getCurrentPoint();

//			Global.addLog("Latest Close; " + getTimeBase().getLatestCandle().getClose());
//			Global.addLog("Previous High; " + getTimeBase().getPreviousCandle(1).getHigh());

			while (Global.getCurrentPoint() < getTimeBase().getCurrentEMA(5))
			{
				wanPrevious.middleWaiter(wanNext);

				if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6) || !isDownTrend()
//						|| StockDataController.getShortTB().getEMA(5) > StockDataController.getShortTB().getEMA(6)
						)
				{
					Global.addLog(className + ": trend change");
					return;
				}

			}

			Global.addLog(className + ": waiting for a second corner");
			refPt = Global.getCurrentPoint();

			while (StockDataController.getShortTB().getLatestCandle().getClose() > StockDataController.getShortTB().getPreviousCandle(1).getLow())
			{
				wanPrevious.middleWaiter(wanNext);

				if (Global.getCurrentPoint() > refPt)
					refPt = Global.getCurrentPoint();
			}

			shortContract();
			cutLoss = Math.abs(Global.getCurrentPoint() - refPt);
			Global.addLog("CutLossPt: " + cutLoss);
		}

	}

	// use 1min instead of 5min
	void updateStopEarn()
	{

		double ema5;
		double ema6;
		
		
		if (getProfit() > 50 && getProfit() < 100){
			ema5 = StockDataController.getShortTB().getEMA(5);
			ema6 = StockDataController.getShortTB().getEMA(6);
		}else
		{
			ema5 = getTimeBase().getEMA(5);
			ema6 = getTimeBase().getEMA(6);
		}
		// }else{
		// ema5 = StockDataController.getShortTB().getEMA(5);
		// ema6 = StockDataController.getShortTB().getEMA(6);
		// }
		// use 1min TB will have more profit sometime, but will lose so many
		// times when ranging.

		if (Global.getNoOfContracts() > 0)
		{

			if (getProfit() > 30)
				tempCutLoss = buyingPoint + 5;

			if (getProfit() > 10 && breakEven)
				tempCutLoss = buyingPoint + 5;

			if (ema5 < ema6 && getProfit() > 5)
			{
				tempCutLoss = 99999;
				Global.addLog(className + " StopEarn: EMA5 < EMA6");
			}
		} else if (Global.getNoOfContracts() < 0)
		{

			if (getProfit() > 30)
				tempCutLoss = buyingPoint - 5;

			if (getProfit() > 10 && breakEven)
				tempCutLoss = buyingPoint - 5;

			if (ema5 > ema6 && getProfit() > 5)
			{
				tempCutLoss = 0;
				Global.addLog(className + " StopEarn: EMA5 > EMA6");

			}
		}

	}

	// use 1min instead of 5min
	double getCutLossPt()
	{
		if (cutLoss < 20){
			return 20;
		}
		return cutLoss + 5;
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

		if (getProfit() < -10)
		{
			breakEven = true;
			return -100;
		}

		return 10;
	}

	@Override
	public TimeBase getTimeBase()
	{
		return StockDataController.getLongTB();
	}

}