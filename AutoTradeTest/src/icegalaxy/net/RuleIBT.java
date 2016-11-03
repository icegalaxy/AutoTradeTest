package icegalaxy.net;

//Use the OPEN Line

public class RuleIBT extends Rules
{
	private double cutLoss;
	private boolean traded;
	private Chasing chasing;


	public RuleIBT(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule)
	{
		super(wan1, wan2, globalRunRule);
		setOrderTime(91600, 92000, 160000, 160000, 230000, 230000);
		chasing = new Chasing(0);
		// wait for EMA6, that's why 0945
	}

	public void openContract()
	{
		
		if (chasing.getRefHL() != 0){
			
//			Global.addLog("IBT start chasing");
			
			Global.setChasing(chasing);
			chasing = new Chasing(0);
		}
		

		if (!isOrderTime() || Global.getNoOfContracts() != 0 || shutdown || TimePeriodDecider.getTime() > 92000
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
				&& Global.getCurrentPoint() > getTimeBase().getMA(240) && TimePeriodDecider.getTime() > 91800)
		{

			longContract();
			traded = true;
			cutLoss = Math.abs(buyingPoint - Global.getOpen());

			// Global.addLog("BuyingPt: " + buyingPoint);
			// Global.addLog("Open: " + Global.getOpen());
			Global.addLog("cutLoss: " + cutLoss);

		}
		// else if (Global.getCurrentPoint() < Global.getOpen() - 30 &&
		// Global.getOpen() -10 < Global.getpClose() && Global.getCurrentPoint()
		// < getTimeBase().getMA(240)){
		// shortContract();
		// traded = true;
		// cutLoss = Math.abs(buyingPoint - Global.getOpen());
		// Global.addLog("cutLoss: " + cutLoss);
		// }

		else if (Global.getCurrentPoint() < Global.getOpen() - 15 && Global.getOpen() - 10 < Global.getpClose()
				&& Global.getCurrentPoint() < getTimeBase().getMA(240) && TimePeriodDecider.getTime() > 91800)
		{

			shortContract();
			traded = true;
			cutLoss = Math.abs(buyingPoint - Global.getOpen());
			Global.addLog("cutLoss: " + cutLoss);
		}

		wanPrevious.middleWaiter(wanNext);

	}

	// openOHLC(Global.getpHigh());

	// use 1min instead of 5min
	void updateStopEarn()
	{
		float shortMA;
		float LongMA;

		// if (getProfit() > 50 && getProfit() < 100){
		shortMA = StockDataController.getShortTB().getEMA(5);
		LongMA = StockDataController.getShortTB().getEMA(6);
		// }else
		// {
//		 shortMA = getTimeBase().getEMA(5);
//		 LongMA = getTimeBase().getEMA(6);
		// }

		if (Global.getNoOfContracts() > 0)
		{

			if (Global.getCurrentPoint() > refPt)
				refPt = Global.getCurrentPoint();

			if (shortMA < LongMA && getProfit() > 30)
			{
				tempCutLoss = 99999;
				
			}
			
			chasing.setRefHL(refPt);
			chasing.setChaseUp(true);

		} else if (Global.getNoOfContracts() < 0)
		{

			if (Global.getCurrentPoint() < refPt)
				refPt = Global.getCurrentPoint();

			if (shortMA > LongMA && getProfit() > 30)
			{
				tempCutLoss = 0;
				
			}
			
			chasing.setRefHL(refPt);
			chasing.setChaseDown(true);

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
		
		float shortMA;
		float LongMA;
		
		shortMA = StockDataController.getShortTB().getEMA(5);
		LongMA = StockDataController.getShortTB().getEMA(6);
		
//		shortMA = getTimeBase().getEMA(5);
//		LongMA = getTimeBase().getEMA(6);
		
		if (Global.getNoOfContracts() > 0)
		{
			
//			if (getProfit() > 30){
//				tempCutLoss = buyingPoint + 5;
//				Global.addLog("Free trade");
//				chasing.setRefHL(refPt);
//				chasing.setChaseUp(true);
//			}
			

			if (Global.getCurrentPoint() > refPt)
				refPt = Global.getCurrentPoint();

			if (shortMA > LongMA && getProfit() > 30)
				return -100;

		} else if (Global.getNoOfContracts() < 0)
		{
			

			

			if (Global.getCurrentPoint() < refPt)
				refPt = Global.getCurrentPoint();
			
//			if (getProfit() > 30){
//				tempCutLoss = buyingPoint - 5;
//				Global.addLog("Free trade");
//				chasing.setRefHL(refPt);
//				chasing.setChaseDown(true);
//			}

			if (shortMA < LongMA && getProfit() > 30)
				return -100;
		}

		return 50;
	}

	@Override
	public TimeBase getTimeBase()
	{
		return StockDataController.getLongTB();
	}

}