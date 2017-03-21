package icegalaxy.net;

import java.util.ArrayList;

import com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt.GetOptsException;

//Use the OPEN Line

public class TestBreakThrough extends Rules
{



	public TestBreakThrough(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule)
	{
		super(wan1, wan2, globalRunRule);
		setOrderTime(93000, 120000, 130000, 160000, 231500, 231500);
		
		// wait for EMA6, that's why 0945
	}

	public void openContract()
	{


		
		if (!isOrderTime())
			return;
		
		OHLC[] ohlcs = new OHLC[]{GetData.open, GetData.pHigh, GetData.pLow, GetData.pClose, GetData.AOL, GetData.AOH};

		for (OHLC ohlc: ohlcs)
		{
			
			if (ohlc.testedBreakThrough)
				continue;
		
		if (GetData.getEma5().getPreviousEMA(1) < ohlc.position
				&& GetData.getEma5().getEMA() > ohlc.position)
		{
			
			while (Global.getCurrentPoint() < ohlc.position + 50 && GetData.getEma5().getEMA() > ohlc.position)
			{
				wanPrevious.middleWaiter(wanNext);
			}
			
			ohlc.testedBreakThrough = true;
			
			if (Global.getCurrentPoint() >= ohlc.position + 50)
				ohlc.breakThroughValid = true;
			
		}else if (GetData.getEma5().getPreviousEMA(1) > ohlc.position
				&& GetData.getEma5().getEMA() < ohlc.position)
		{
			
			while (Global.getCurrentPoint() > ohlc.position - 50 && GetData.getEma5().getEMA() < ohlc.position)
			{
				wanPrevious.middleWaiter(wanNext);
			}
			
			ohlc.testedBreakThrough = true;
			
			if (Global.getCurrentPoint() <= ohlc.position - 50)
				ohlc.breakThroughValid = true;
			
		}
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
			ema6 = GetData.getEma25().getEMA();
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
		return 50;
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
		
		
		
		return 20;
	}

	@Override
	public TimeBase getTimeBase()
	{
		return GetData.getLongTB();
	}
}