package icegalaxy.net;

//Use the OPEN Line

public class RuleIBT extends Rules
{
	private double cutLoss;
	private boolean traded;
	private Chasing chasing;
	private double refHigh;
	private double refLow;

	public RuleIBT(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule)
	{
		super(wan1, wan2, globalRunRule);
		setOrderTime(91800, 92000, 160000, 160000, 230000, 230000);
		chasing = new Chasing();
		// wait for EMA6, that's why 0945
	}
	
	double getMADiff(){
		return GetData.getEma5().getEMA() - GetData.getEma50().getEMA();
	}

	public void openContract()
	{
		
		if (chasing.chaseUp() || chasing.chaseDown()){
			
			Global.setChasing(chasing);
			chasing = new Chasing();
		}

		if (!isOrderTime() || Global.getNoOfContracts() != 0 || shutdown
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
				&& GetData.getEma5().getEMA() > GetData.getEma5().getPreviousEMA(1) 
				&& TimePeriodDecider.getTime() > 91800)
		{

			
			refHigh = 0;
			refLow = 99999;
			
			Global.addLog("Waiting for first pull back");
			while (GetData.getEma5().getEMA() > GetData.getEma5().getPreviousEMA(1))
			{
				if (TimePeriodDecider.getTime() > 100000)
					return;
				
//				if (GetData.getEma5().getEMA() > GetData.getShortTB().getEMA(6))
//					break;
				
				if (Global.getCurrentPoint() < Global.getOpen() - 10)
					return;
				
				if (GetData.getEma5().getEMA() > refHigh)
					refHigh = GetData.getEma5().getEMA();
				else if (GetData.getEma5().getEMA() < refLow)
					refLow = GetData.getEma5().getEMA();
				
				wanPrevious.middleWaiter(wanNext);
			}
			
			while (GetData.getEma5().getEMA() < refHigh)
			{
				wanPrevious.middleWaiter(wanNext);
				
				if (TimePeriodDecider.getTime() > 100000)
					return;
				
				if (Global.getCurrentPoint() < Global.getOpen() - 10)
					return;

				if (GetData.getEma5().getEMA() > refHigh)
					refHigh = GetData.getEma5().getEMA();
				else if (GetData.getEma5().getEMA() < refLow)
					refLow = GetData.getEma5().getEMA();
				
			}
			
//			int spreadingTimes = 0;
//			double refDiff = 0;
//
//			while (spreadingTimes < 3)
//			{
//				wanPrevious.middleWaiter(wanNext);
//
//				if (!isUpTrend2() || GetData.getEma5().getEMA() < GetData.getEma50().getEMA())
//				{
//					Global.addLog("Trend Change");
//					return;
//				}
//				
//				if (!isOrderTime())
//				{
//					Global.addLog("Not order time");
//					return;
//				}
//				
//				if (getMADiff() > refDiff){
//					refDiff = getMADiff();
//					spreadingTimes++;
//					Global.addLog("Spreading time: "  + spreadingTimes);
//				}else if (getMADiff() < refDiff){
//					refDiff = getMADiff();
//					spreadingTimes--;
//					if (spreadingTimes < 0)
//						return;
//				}
//			}
//
//			if (Global.getCurrentPoint() - Global.getOpen() > 50)
//			{
//				
//				Global.addLog("Wait pull back");
//				while (Global.getCurrentPoint() - Global.getOpen() > 10)
//				{
//
//					if (TimePeriodDecider.getTime() > 100000)
//						return;
//
//					wanPrevious.middleWaiter(wanNext);
//				}
//			}

			longContract();
			traded = true;
			cutLoss = Math.abs(buyingPoint - Global.getOpen());
			Global.addLog("cutLoss: " + cutLoss);

		}

		else if (Global.getCurrentPoint() < Global.getOpen() - 15 && Global.getOpen() - 10 < Global.getpClose()
				&& GetData.getEma5().getEMA() < GetData.getEma5().getPreviousEMA(1) 
				&& TimePeriodDecider.getTime() > 91800)
		{
			
			refHigh = 0;
			refLow = 99999;
			
			Global.addLog("Waiting for first pull back");
			while (GetData.getEma5().getEMA() < GetData.getEma5().getPreviousEMA(1))
			{
				if (TimePeriodDecider.getTime() > 100000)
					return;
				
//				if (GetData.getEma5().getEMA() > GetData.getShortTB().getEMA(6))
//					break;
				
				if (Global.getCurrentPoint() > Global.getOpen() + 10)
					return;
				
				if (GetData.getEma5().getEMA() > refHigh)
					refHigh = GetData.getEma5().getEMA();
				else if (GetData.getEma5().getEMA() < refLow)
					refLow = GetData.getEma5().getEMA();
				
				wanPrevious.middleWaiter(wanNext);
			}
			
			while (GetData.getEma5().getEMA() > refLow)
			{
				wanPrevious.middleWaiter(wanNext);
				
				if (TimePeriodDecider.getTime() > 100000)
					return;
				
				if (Global.getCurrentPoint() > Global.getOpen() + 10)
					return;

				if (GetData.getEma5().getEMA() > refHigh)
					refHigh = GetData.getEma5().getEMA();
				else if (GetData.getEma5().getEMA() < refLow)
					refLow = GetData.getEma5().getEMA();
				
			}

//			while (GetData.getEma5().getEMA() > GetData.getEma250().getEMA())
//			{
//
////				if (GetData.getShortTB().getEMA(5) < GetData.getShortTB().getEMA(6))
////					break;
//				
//				if (TimePeriodDecider.getTime() > 100000)
//					return;
//				
//				if (Global.getCurrentPoint() > Global.getOpen() + 10)
//					return;
//				
//				wanPrevious.middleWaiter(wanNext);
//			}
//			int spreadingTimes = 0;
//			double refDiff = 0;
//
//			while (spreadingTimes < 3)
//			{
//				wanPrevious.middleWaiter(wanNext);
//
//				if (!isDownTrend2() || GetData.getEma5().getEMA() > GetData.getEma50().getEMA())
//				{
//					Global.addLog("Trend Change");
//					return;
//				}
//				
//				if ( !isOrderTime())
//				{
//					Global.addLog("Not order time");
//					return;
//				}
//				
//				if (getMADiff() < refDiff){
//					refDiff = getMADiff();
//					spreadingTimes++;
//					Global.addLog("Spreading time: "  + spreadingTimes);
//				}else if (getMADiff() > refDiff){
//					refDiff = getMADiff();
//					spreadingTimes--;
//					if (spreadingTimes < 0)
//						return;
//				}
//			}
//
//			if (Global.getOpen() - Global.getCurrentPoint() > 50)
//			{
//				
//				Global.addLog("Wait pull back");
//				while (Global.getOpen() - Global.getCurrentPoint() > 10)
//				{
//
//					if (TimePeriodDecider.getTime() > 100000)
//						return;
//
//					wanPrevious.middleWaiter(wanNext);
//				}
//			}

			shortContract();
			traded = true;
			cutLoss = Math.abs(buyingPoint - Global.getOpen());
			Global.addLog("cutLoss: " + cutLoss);
		}

		wanPrevious.middleWaiter(wanNext);

	}
	
	
	@Override
	boolean trendReversed(){
		
		if (Global.getNoOfContracts() > 0)
			return GetData.getEma5().getEMA() < refLow;
		else
			return GetData.getEma5().getEMA() > refHigh;
		
	}

	// openOHLC(Global.getpHigh());

	// use 1min instead of 5min
	void updateStopEarn()
	{
		double ema5;
		double ema6;
//
//		if (getProfit() < 100)
		
		
		
//		if (Math.abs(GetData.getEma5().getEMA() - GetData.getEma25().getEMA()) > 30)
//			{
//			ema6 = GetData.getEma5().getPreviousEMA(1);
//			ema5 = GetData.getEma5().getEMA();
//			}
//		else
//		{
			ema5 = GetData.getShortTB().getLatestCandle().getClose();
			ema6 = GetData.getEma25().getEMA();
//		}
//		} else
//		{
//			ema5 = StockDataController.getLongTB().getEMA(5);
//			ema6 = StockDataController.getLongTB().getEMA(6);
//		}

		if (Global.getNoOfContracts() > 0)
		{

			// if (ema5 < ema6)
			// tempCutLoss = buyingPoint + 5;

			if (buyingPoint > tempCutLoss && getProfit() > 50)
			{
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint + 5;
			}
			
			if (ema5 < ema6){
				tempCutLoss = 99999;
//				if (getProfit() > 0)
					chasing.setChaseUp(true);
			}

		} else if (Global.getNoOfContracts() < 0)
		{

			// if (ema5 > ema6)
			// tempCutLoss = buyingPoint - 5;

			if (buyingPoint < tempCutLoss && getProfit() > 50)
			{
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint - 5;
			}
			
			if (ema5 > ema6){
				tempCutLoss = 0;
//				if (getProfit() > 0)
					chasing.setChaseDown(true);
			}
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
		
		if (Global.getCurrentPoint() > chasing.getRefHigh())
			chasing.setRefHigh(Global.getCurrentPoint());
		if (Global.getCurrentPoint() < chasing.getRefLow())
			chasing.setRefLow(Global.getCurrentPoint());
		
	}

	double getStopEarnPt()
	{
//		if (Global.getNoOfContracts() > 0)
//		{
//			if (StockDataController.getShortTB().getLatestCandle().getClose() > getTimeBase().getEMA(5))
//				return -100;
//			
//			
//			
//			
//		} else if (Global.getNoOfContracts() < 0)
//		{
//			if (StockDataController.getShortTB().getLatestCandle().getClose() < getTimeBase().getEMA(6))
//				return -100;
//		}
		
		
		
		return 30;
	}

	@Override
	public TimeBase getTimeBase()
	{
		return GetData.getLongTB();
	}

}