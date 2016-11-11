package icegalaxy.net;

//Use the OPEN Line

public class RulePHigh extends Rules
{

	private int lossTimes;
	// private double refEMA;
	private boolean firstCorner = true;
	private double cutLoss;

	private double tempHigh;
	private double tempLow;

	public double OHLC;

	public RulePHigh(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule)
	{
		super(wan1, wan2, globalRunRule);
		setOrderTime(94500, 113000, 130500, 160000, 213000, 231500);
		// wait for EMA6, that's why 0945
	}

	public void openContract()
	{

		if (shutdown)
		{
			lossTimes++;
			shutdown = false;
		}

		OHLC = Global.getpHigh();

		if (!isOrderTime() || Global.getNoOfContracts() != 0 || lossTimes >= 2)
			return;

		Global.addLog("Waiting to reach OHLC");
		while (Math.abs(Global.getCurrentPoint() - OHLC) > 5)
			wanPrevious.middleWaiter(wanNext);

		tempHigh = Global.getCurrentPoint();
		tempLow = Global.getCurrentPoint();

		waitForANewCandle();

		if (GetData.getShortTB().getLatestCandle().getHigh() > tempHigh)
			tempHigh = GetData.getShortTB().getLatestCandle().getHigh();
		if (GetData.getShortTB().getLatestCandle().getLow() < tempLow)
			tempLow = GetData.getShortTB().getLatestCandle().getLow() ;
		
		Global.addLog("TempHigh: " + tempHigh);
		Global.addLog("TempLow: " + tempLow);

		Global.addLog("Waiting for a first corner");
		while (GetData.getShortTB().getLatestCandle().getHigh() < OHLC + 15
				&& GetData.getShortTB().getLatestCandle().getLow() > OHLC - 15)
		{

			wanPrevious.middleWaiter(wanNext);

			if (Global.getCurrentPoint() > tempHigh){
				tempHigh = Global.getCurrentPoint();
				Global.addLog("TempHigh: " + tempHigh);
			}
			if (Global.getCurrentPoint() < tempLow){
				tempLow = Global.getCurrentPoint();
				Global.addLog("TempLow: " + tempLow);
			}
		}

		Global.addLog("Waiting for a break through");
		while (Global.getCurrentPoint() < tempHigh
				&& Global.getCurrentPoint() > tempLow)
		{

			wanPrevious.middleWaiter(wanNext);

			if (Global.getCurrentPoint() > tempHigh){
				tempHigh = Global.getCurrentPoint();
				Global.addLog("TempHigh: " + tempHigh);
			}
			if (Global.getCurrentPoint() < tempLow){
				tempLow = Global.getCurrentPoint();
				Global.addLog("TempLow: " + tempLow);
			}
		}

		if (Global.getCurrentPoint() >= tempHigh)
		{
			longContract();
			cutLoss = Math.abs(buyingPoint - tempLow);
		} else if (Global.getCurrentPoint() <= tempLow)
		{
			shortContract();
			cutLoss = Math.abs(buyingPoint - tempHigh);
		}

	}

	// use 1min instead of 5min
	void updateStopEarn()
	{

		double ema5;
		double ema6;
		int difference;

		if (getProfit() > 100)
			difference = 0;
		else
			difference = 2;

		// if (Math.abs(getTimeBase().getEMA(5) - getTimeBase().getEMA(6)) <
		// 10){
		ema5 = getTimeBase().getEMA(5);
		ema6 = getTimeBase().getEMA(6);
		// }else{
		// ema5 = StockDataController.getShortTB().getEMA(5);
		// ema6 = StockDataController.getShortTB().getEMA(6);
		// }
		// use 1min TB will have more profit sometime, but will lose so many
		// times when ranging.

		if (Global.getNoOfContracts() > 0)
		{

			if (buyingPoint > tempCutLoss && getProfit() > 30)
			{
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint;
			}

			if (ema5 < ema6)
			{
				tempCutLoss = 99999;
				Global.addLog(className + " StopEarn: EMA5 < EMA6");
			}
		} else if (Global.getNoOfContracts() < 0)
		{

			if (buyingPoint < tempCutLoss && getProfit() > 30)
			{
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint;
			}

			if (ema5 > ema6)
			{
				tempCutLoss = 0;
				Global.addLog(className + " StopEarn: EMA5 > EMA6");

			}
		}

	}
	
	public void waitForANewCandle() {
		
		int currentSize = GetData.getShortTB().getCandles().size();
		
		while (currentSize == GetData.getShortTB().getCandles().size())
			wanPrevious.middleWaiter(wanNext);;
		
	}

	// use 1min instead of 5min
	double getCutLossPt()
	{

		// One time lost 100 at first trade >_< 20160929
		// if (Global.getNoOfContracts() > 0){
		// if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6))
		// return 1;
		// else
		// return 30;
		// }else{
		// if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6))
		// return 1;
		// else
		// return 30;
		// }

		return cutLoss;

	}

	@Override
	protected void cutLoss()
	{

		if (Global.getNoOfContracts() > 0 && Global.getCurrentPoint() < tempCutLoss)
		{
			//
			// while (Global.getCurrentPoint() <
			// StockDataController.getShortTB().getEMA(5)){
			// wanPrevious.middleWaiter(wanNext);
			// if (getProfit() < -30)
			// break;
			// }
			//

			closeContract(className + ": CutLoss, short @ " + Global.getCurrentBid());
			shutdown = true;

			// wait for it to clam down

			// if (Global.getCurrentPoint() < getTimeBase().getEMA(6)){
			// Global.addLog(className + ": waiting for it to calm down");
			// }

			// while (Global.getCurrentPoint() < getTimeBase().getEMA(6))
			// wanPrevious.middleWaiter(wanNext);

		} else if (Global.getNoOfContracts() < 0 && Global.getCurrentPoint() > tempCutLoss)
		{
			//
			//
			// while (Global.getCurrentPoint() >
			// StockDataController.getShortTB().getEMA(5)){
			// wanPrevious.middleWaiter(wanNext);
			// if (getProfit() < -30)
			// break;
			// }

			closeContract(className + ": CutLoss, long @ " + Global.getCurrentAsk());
			shutdown = true;

			// if (Global.getCurrentPoint() > getTimeBase().getEMA(6)){
			// Global.addLog(className + ": waiting for it to calm down");
			// }
			//
			// while (Global.getCurrentPoint() > getTimeBase().getEMA(6))
			// wanPrevious.middleWaiter(wanNext);
		}
	}

	double getStopEarnPt()
	{
		if (Global.getNoOfContracts() > 0)
		{
			if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6))
				return -100;
		} else if (Global.getNoOfContracts() < 0)
		{
			if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6))
				return -100;
		}

		return 30;
	}

	@Override
	public TimeBase getTimeBase()
	{
		return GetData.getLongTB();
	}

}