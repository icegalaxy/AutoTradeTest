package icegalaxy.net;

import javax.sql.rowset.CachedRowSet;

//Use the OPEN Line

public class RuleChasing extends Rules
{
	private double cutLoss;
	private Chasing chasing;

	public RuleChasing(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule)
	{
		super(wan1, wan2, globalRunRule);
		setOrderTime(91600, 115500, 130500, 160000, 230000, 230000);

		
		// wait for EMA6, that's why 0945
	}
	
	private double getShortMA(){
		return StockDataController.getShortTB().getLatestCandle().getClose();
	}

	private double getLongMA(){
		return StockDataController.getLongTB().getEMA(5);
	}

	public void openContract()
	{

		if (!isOrderTime() || Global.getNoOfContracts() != 0)
			return;

		chasing = Global.getChasing();
		
		if (chasing.chaseUp())
		{
			Global.addLog(className + ": Chasing Up");
			Global.setChasing(new Chasing()); //reset it
			Global.addLog("Ref High: " + chasing.getRefHigh());
			Global.addLog("Ref Low: " + chasing.getRefLow());
			
			refPt = Global.getCurrentPoint();

			while (Global.getCurrentPoint() < chasing.getRefHigh())
			{
				wanPrevious.middleWaiter(wanNext);

				if (Global.getCurrentPoint() < refPt)
					refPt = Global.getCurrentPoint();
				
				if(Global.getCurrentPoint() < chasing.getRefLow()){
					Global.addLog("Lower than previous low");
					return;
				}

			}

			longContract();
			cutLoss = buyingPoint - refPt;

		} else if (chasing.chaseDown())
		{
			Global.addLog(className + ": Chasing Down");
			Global.setChasing(new Chasing()); //reset it
			Global.addLog("Ref High: " + chasing.getRefHigh());
			Global.addLog("Ref Low: " + chasing.getRefLow());
			refPt = Global.getCurrentPoint();

			while (Global.getCurrentPoint() > chasing.getRefLow())
			{
				wanPrevious.middleWaiter(wanNext);

				if (Global.getCurrentPoint() > refPt)
					refPt = Global.getCurrentPoint();

				if(Global.getCurrentPoint() > chasing.getRefHigh()){
					Global.addLog("Higher than previous high");
					return;
				}
				

			}

			shortContract();
			cutLoss = refPt - buyingPoint;

		}

		wanPrevious.middleWaiter(wanNext);

	}

	// openOHLC(Global.getpHigh());

	// use 1min instead of 5min
	void updateStopEarn()
	{

		// }else
		// {
		// shortMA = getTimeBase().getEMA(5);
		// LongMA = getTimeBase().getEMA(6);
		// }

		if (Global.getNoOfContracts() > 0)
		{

			if (Global.getCurrentPoint() > refPt)
				refPt = Global.getCurrentPoint();

			if (getShortMA() < getLongMA())
			{
				tempCutLoss = 99999;
//				chasing.setChaseUp(true);
//				chasing.setRefHigh(refPt);

			}

		} else if (Global.getNoOfContracts() < 0)
		{

			if (Global.getCurrentPoint() < refPt)
				refPt = Global.getCurrentPoint();

			if (getShortMA() > getLongMA())
			{
				tempCutLoss = 0;
//				chasing.setChaseDown(true);
//				chasing.setRefHigh(refPt);
			}

		}

	}

	// use 1min instead of 5min
	double getCutLossPt()
	{
		if (cutLoss < 15)
			return 15;
		else
			return cutLoss;
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
//		if (Global.getNoOfContracts() > 0)
//		{
//
//			if (Global.getCurrentPoint() > refPt)
//				refPt = Global.getCurrentPoint();
//
//			if (getShortMA() > getLongMA() && getProfit() > 30)
//				return -100;
//
//		} else if (Global.getNoOfContracts() < 0)
//		{
//
//			if (Global.getCurrentPoint() < refPt)
//				refPt = Global.getCurrentPoint();
//
//			if (getShortMA() < getLongMA() && getProfit() > 30)
//				return -100;
//		}
		return 10;
	}

	@Override
	public TimeBase getTimeBase()
	{
		return StockDataController.getShortTB();
	}

}