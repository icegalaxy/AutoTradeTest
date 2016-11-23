package icegalaxy.net;

import java.util.ArrayList;

import com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt.GetOptsException;

//Use the OPEN Line

public class RuleOpen extends Rules
{


	private double cutLoss;
	private Chasing chasing;
	private double OHLC;
	private double refHigh;
	private double refLow;

	public RuleOpen(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule)
	{
		super(wan1, wan2, globalRunRule);
		setOrderTime(93000, 115500, 130500, 160000, 231500, 231500);
		chasing = new Chasing();
		// wait for EMA6, that's why 0945
	}

	public void openContract()
	{

//		if (shutdown)
//		{
//			lossTimes++;
//			shutdown = false;
//		}
		
	
		
//		if (chasing.chaseUp() || chasing.chaseDown()){
//			
//			Global.setChasing(chasing);
//			chasing = new Chasing();
//		}
		
		if (!isOrderTime() || Global.getNoOfContracts() != 0 || Global.getOpen() == 0 || shutdown)
			return;
		
//		while (Math.abs(Global.getCurrentPoint() - Global.getOpen()) < 20)
//			{
//				wanPrevious.middleWaiter(wanNext);
//				if (!isOrderTime())
//					return;
//			}

		if (GetData.getEma5().getPreviousEMA(1) < Global.getOpen()
				&& GetData.getEma5().getEMA() > Global.getOpen())
		{
			refHigh = 0;
			refLow = 99999;
			
			Global.addLog("Waiting for first pull back");
			while (GetData.getEma5().getEMA() > GetData.getEma5().getPreviousEMA(1))
			{
//				if (TimePeriodDecider.getTime() > 100000)
//					return;
				
//				if (GetData.getEma5().getEMA() > GetData.getShortTB().getEMA(6))
//					break;
				
				if (GetData.getEma5().getEMA() < Global.getOpen())
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
				
//				if (TimePeriodDecider.getTime() > 100000)
//					return;
				
				if (GetData.getEma5().getEMA() < Global.getOpen())
					return;

			 if (GetData.getEma5().getEMA() < refLow)
					refLow = GetData.getEma5().getEMA();
				
			}
			
			longContract();
			cutLoss = buyingPoint - refLow;
			
		}else if (GetData.getEma5().getPreviousEMA(1) > Global.getOpen()
				&& GetData.getEma5().getEMA() < Global.getOpen())
		{	
			refHigh = 0;
			refLow = 99999;
			
			Global.addLog("Waiting for first pull back");
			while (GetData.getEma5().getEMA() < GetData.getEma5().getPreviousEMA(1))
			{
//				if (TimePeriodDecider.getTime() > 100000)
//					return;
				
//				if (GetData.getEma5().getEMA() > GetData.getShortTB().getEMA(6))
//					break;
				
				if (GetData.getEma5().getEMA()  > Global.getOpen())
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
				
//				if (TimePeriodDecider.getTime() > 100000)
//					return;
				
				if (GetData.getEma5().getEMA() > Global.getOpen())
					return;

				if (GetData.getEma5().getEMA() > refHigh)
					refHigh = GetData.getEma5().getEMA();
			
				
			}
			shortContract();		
			cutLoss = refHigh - buyingPoint;
		}
	}
	
	public double getCurrentClose(){
		return GetData.getShortTB().getLatestCandle().getClose();
	}
	
	

	// use 1min instead of 5min
	void updateStopEarn()
	{
		double ema5;
		double ema6;
//
//		if (getProfit() < 100)
//		{
			ema5 = GetData.getShortTB().getLatestCandle().getClose();
			ema6 = GetData.getLongTB().getEMA(5);
//		} else
//		{
//			ema5 = StockDataController.getLongTB().getEMA(5);
//			ema6 = StockDataController.getLongTB().getEMA(6);
//		}

		if (Global.getNoOfContracts() > 0)
		{

			// if (ema5 < ema6)
//			 tempCutLoss = buyingPoint + 5;

			if (ema5 < ema6){
				tempCutLoss = 99999;
//				if (getProfit() > 0)
//					chasing.setChaseUp(true);
			}

		} else if (Global.getNoOfContracts() < 0)
		{

			// if (ema5 > ema6)
//			 tempCutLoss = buyingPoint - 5;

			if (ema5 > ema6){
				tempCutLoss = 0;
//				if (getProfit() > 0)
//					chasing.setChaseDown(true);
			}
		}

	}

	// use 1min instead of 5min
	double getCutLossPt()
	{
		return Math.max(100, cutLoss);
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
	
	@Override
	boolean trendReversed(){
		
		if (Global.getNoOfContracts() > 0)
			return GetData.getEma5().getEMA() < refLow;
		else
			return GetData.getEma5().getEMA() > refHigh;
		
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
		
		
		
		return 50;
	}

	@Override
	public TimeBase getTimeBase()
	{
		return GetData.getLongTB();
	}
}