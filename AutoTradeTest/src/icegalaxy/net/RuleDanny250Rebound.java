package icegalaxy.net;

import com.sun.javafx.css.parser.StopConverter;

//m1 EMA5 x EMA250, wait EMA5 x EMA50

public class RuleDanny250Rebound extends Rules
{

	private int lossTimes;
	// private double refEMA;
	

	public RuleDanny250Rebound(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule)
	{
		super(wan1, wan2, globalRunRule);
		setOrderTime(93000, 103000, 150000, 160000, 230000, 230000);
		
	}
	
	
	
	double getMADiff(){
		return GetData.getEma5().getEMA() - GetData.getEma50().getEMA();
	}
	
	double getClosePrice(){
		return GetData.getShortTB().getLatestCandle().getClose();
	}

	public void openContract()
	{
		
	

		if (shutdown)
		{
			lossTimes++;
			shutdown = false;
			Global.addLog("LossTimes: " + lossTimes);
		}

		if (!isOrderTime() || Global.getNoOfContracts() != 0 || lossTimes >= 1)
			return;

		
		if (GetData.getShortTB().getLatestCandle().getLow() < GetData.getEma250().getEMA()
				&& GetData.getShortTB().getPreviousCandle(1).getClose() > GetData.getEma250().getEMA())
		{
			
			
			refPt = GetData.getEma5().getEMA();
			
			while (GetData.getEma5().getEMA() < GetData.getEma5().getPreviousEMA(1)
					|| GetData.getEma5().getEMA() < GetData.getEma250().getEMA())
			{
				wanPrevious.middleWaiter(wanNext);	
				
//				if (GetData.getEma5().getEMA() < GetData.getEma250().getEMA())
//				{
//					Global.addLog("Trend Change");
//					return;
//				}

				if (!isOrderTime())
				{
					Global.addLog("Not order time");
					return;
				}
				
				if (GetData.getEma5().getEMA() < refPt)
					refPt = GetData.getEma5().getEMA();
					
			}
					
			longContract();
			cutLossPt = buyingPoint - GetData.getEma250().getEMA();
			
			
		}else if (GetData.getShortTB().getLatestCandle().getHigh() > GetData.getEma250().getEMA()
				&& GetData.getShortTB().getPreviousCandle(1).getClose()  < GetData.getEma250().getEMA())
		{
			
			
			
			refPt = GetData.getEma5().getEMA();
			
			while (GetData.getEma5().getEMA() > GetData.getEma5().getPreviousEMA(1)
					|| GetData.getEma5().getEMA() > GetData.getEma250().getEMA())
			{
				wanPrevious.middleWaiter(wanNext);	
				
//				if (GetData.getEma5().getEMA() >  GetData.getEma250().getEMA())
//				{
//					Global.addLog("Trend Change");
//					return;
//				}

				if (!isOrderTime())
				{
					Global.addLog("Not order time");
					return;
				}
				
				if (GetData.getEma5().getEMA() > refPt)
					refPt = GetData.getEma5().getEMA();
					
			}
			
			shortContract();
			cutLossPt = GetData.getEma250().getEMA() - buyingPoint;
		}
		
		wanPrevious.middleWaiter(wanNext);

	}

//	@Override
//	boolean trendReversed(){
//		
//		if (Global.getNoOfContracts() > 0)
//			return GetData.getEma5().getEMA() < refPt;
//		else
//			return GetData.getEma5().getEMA() > refPt;
//		
//	}

	// use 1min instead of 5min
	void updateStopEarn()
	{

		double ema5;
		double ema6;
		

		// if (Math.abs(getTimeBase().getEMA(5) - getTimeBase().getEMA(6)) <
		// 10){
		ema5 = GetData.getShortTB().getLatestCandle().getClose();
		ema6 = GetData.getEma25().getEMA();
				
	


		if (Global.getNoOfContracts() > 0)
		{

			if (isUpTrend2() && getProfit() > 50)
			{
				ema5 = GetData.getEma5().getEMA();
				ema6 = GetData.getEma50().getEMA();
			}
			
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
			
			if (isDownTrend2() && getProfit() > 50)
			{
				ema5 = GetData.getEma5().getEMA();
				ema6 = GetData.getEma50().getEMA();
			}

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

		
//		return Math.min(cutLossPt + 10, 40);
		return 20;

	}

	@Override
	protected void cutLoss()
	{

		if (Global.getNoOfContracts() > 0 && GetData.getShortTB().getLatestCandle().getClose() < tempCutLoss)
		{
			closeContract(className + ": CutLoss, short @ " + Global.getCurrentBid());
			shutdown = true;
		} else if (Global.getNoOfContracts() < 0 && GetData.getShortTB().getLatestCandle().getClose() > tempCutLoss)
		{
			closeContract(className + ": CutLoss, long @ " + Global.getCurrentAsk());
			shutdown = true;
		}
		
	
	}
	
	private int getLossTimesAllowed()
	{

		double balance = Global.balance + Global.getCurrentPoint() * Global.getNoOfContracts();

		if (balance > 30)
			return 3;
		else if (balance > 0)
			return 2;
		else
			return 1;
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
		return GetData.getLongTB();
	}

}