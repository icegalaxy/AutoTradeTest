package icegalaxy.net;

//Use the OPEN Line

public class RuleChasing extends Rules
{
	private double cutLoss;

	public RuleChasing(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule)
	{
		super(wan1, wan2, globalRunRule);
		setOrderTime(91600, 115500, 130500, 160000, 230000, 230000);
		// wait for EMA6, that's why 0945
	}

	public void openContract()
	{

		if (!isOrderTime() || Global.getNoOfContracts() != 0 || Global.getChasing().getRefHL() == 0)
			return;

		Chasing chasing = Global.getChasing();
		Global.addLog(className + ": refPt " + chasing.getRefHL());

		if (chasing.chaseUp())
		{
			Global.addLog(className + ": Chasing Up");

			refPt = Global.getCurrentPoint();

			while (Global.getCurrentPoint() < chasing.getRefHL())
			{
				wanPrevious.middleWaiter(wanNext);

				if (Global.getCurrentPoint() < refPt)
					refPt = Global.getCurrentPoint();

//				if (Global.getCurrentPoint() < chasing.getRefHL())
//				{
//					chasing = new Chasing(0);
//					Global.setChasing(chasing);
//					Global.addLog(className + ": Lower than previous low");
//					return;
//				}

				if (Global.getCurrentPoint() - refPt > 100)
				{
					chasing = new Chasing(0);
					Global.setChasing(chasing);
					Global.addLog(className + ": Risk too high");
					return;
				}

			}

			longContract();
			cutLoss = buyingPoint - refPt;
			chasing = new Chasing(0);
			Global.setChasing(chasing);

		} else if (chasing.chaseDown())
		{
			Global.addLog(className + ": Chasing Down");
			refPt = Global.getCurrentPoint();

			while (Global.getCurrentPoint() > chasing.getRefHL())
			{
				wanPrevious.middleWaiter(wanNext);

				if (Global.getCurrentPoint() > refPt)
					refPt = Global.getCurrentPoint();

//				if (Global.getCurrentPoint() > chasing.getRefHL())
//				{
//					chasing = new Chasing(0);
//					Global.setChasing(chasing);
//					Global.addLog(className + ": Higher than previous High");
//					return;
//				}

				if (refPt - Global.getCurrentPoint() > 100)
				{
					chasing = new Chasing(0);
					Global.setChasing(chasing);
					Global.addLog(className + ": Risk too high");
					return;
				}

			}

			shortContract();
			cutLoss = refPt - buyingPoint;
			chasing = new Chasing(0);
			Global.setChasing(chasing);

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
		// shortMA = getTimeBase().getEMA(5);
		// LongMA = getTimeBase().getEMA(6);
		// }

		if (Global.getNoOfContracts() > 0)
		{
			if (shortMA < LongMA)
			{
				tempCutLoss = 99999;
				Chasing chasing = new Chasing(StockDataController.getShortTB().getLatestCandle().getLow());
				chasing.setChaseUp(true);
				Global.setChasing(chasing);
			}

		} else if (Global.getNoOfContracts() < 0)
		{

			if (shortMA > LongMA)
			{
				tempCutLoss = 0;
				Chasing chasing = new Chasing(StockDataController.getShortTB().getLatestCandle().getLow());
				chasing.setChaseDown(true);
				Global.setChasing(chasing);
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
//		if (Global.getNoOfContracts() > 0 && StockDataController.getShortTB().getEMA(5) > StockDataController.getShortTB().getMA(20))
//			return -100;
//		else if (Global.getNoOfContracts() < 0 && StockDataController.getShortTB().getEMA(5) < StockDataController.getShortTB().getMA(20))
//			return -100;

		return 30;
	}

	@Override
	public TimeBase getTimeBase()
	{
		return StockDataController.getShortTB();
	}

}