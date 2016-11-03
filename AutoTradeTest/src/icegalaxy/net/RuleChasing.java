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

		chasing = new Chasing(0);
		// wait for EMA6, that's why 0945
	}
	
	private double getShortMA(){
		return StockDataController.getShortTB().getEMA(5);
	}

	private double getLongMA(){
		return StockDataController.getShortTB().getEMA(6);
	}

	public void openContract()
	{

		if (chasing.getRefHL() != 0)
		{
			Global.setChasing(chasing);
			chasing = new Chasing(0);
		}

		if (!isOrderTime() || Global.getNoOfContracts() != 0 || Global.getChasing().getRefHL() == 0)
			return;

		Chasing chasing = Global.getChasing();
		Global.addLog(className + ": refPt " + chasing.getRefHL());

		if (chasing.chaseUp())
		{
			Global.addLog(className + ": Chasing Up");

			refPt = Global.getCurrentPoint();

			while (Global.getCurrentPoint() < chasing.getRefHL() && Math.abs(Global.getCurrentPoint() - refPt) < 30)
			{
				wanPrevious.middleWaiter(wanNext);

				if (Global.getCurrentPoint() < refPt)
					refPt = Global.getCurrentPoint();

				// if (Global.getCurrentPoint() < chasing.getRefHL())
				// {
				// chasing = new Chasing(0);
				// Global.setChasing(chasing);
				// Global.addLog(className + ": Lower than previous low");
				// return;
				// }

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

			while (Global.getCurrentPoint() > chasing.getRefHL() &&  Math.abs(Global.getCurrentPoint() - refPt) < 30)
			{
				wanPrevious.middleWaiter(wanNext);

				if (Global.getCurrentPoint() > refPt)
					refPt = Global.getCurrentPoint();

				// if (Global.getCurrentPoint() > chasing.getRefHL())
				// {
				// chasing = new Chasing(0);
				// Global.setChasing(chasing);
				// Global.addLog(className + ": Higher than previous High");
				// return;
				// }

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
				chasing.setChaseUp(true);
				chasing.setRefHL(refPt);

			}

		} else if (Global.getNoOfContracts() < 0)
		{

			if (Global.getCurrentPoint() < refPt)
				refPt = Global.getCurrentPoint();

			if (getShortMA() > getLongMA())
			{
				tempCutLoss = 0;
				chasing.setChaseDown(true);
				chasing.setRefHL(refPt);
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
		if (Global.getNoOfContracts() > 0)
		{

			if (Global.getCurrentPoint() > refPt)
				refPt = Global.getCurrentPoint();

			if (getShortMA() > getLongMA() && getProfit() > 30)
				return -100;

		} else if (Global.getNoOfContracts() < 0)
		{

			if (Global.getCurrentPoint() < refPt)
				refPt = Global.getCurrentPoint();

			if (getShortMA() < getLongMA() && getProfit() > 30)
				return -100;
		}
		return 30;
	}

	@Override
	public TimeBase getTimeBase()
	{
		return StockDataController.getShortTB();
	}

}