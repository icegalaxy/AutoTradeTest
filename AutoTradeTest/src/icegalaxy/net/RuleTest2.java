package icegalaxy.net;

//Use the OPEN Line

public class RuleTest2 extends Rules {

	private int lossTimes;
	// private double refEMA;
	private boolean firstCorner = false;
	private double cutLoss;
	private boolean trendReversed;

	public RuleTest2(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule) {
		super(wan1, wan2, globalRunRule);
		setOrderTime(103000, 113000, 130500, 140000, 230000, 230000);
		// wait for EMA6, that's why 0945
	}

	public void openContract() {
		
		trendReversed = false;

		if (shutdown) {
			lossTimes++;
			firstCorner = true;
			shutdown = false;
		}
		
		if (!isOrderTime() || Global.getNoOfContracts() != 0
				|| lossTimes >=2
		)
			return;
		
		if (GetData.getShortTB().getRSI() < 35 && GetData.getShortTB().getRSI() > 30 && GetData.getShortTB().getHL(15).getFluctuation() < 50
				&& Global.getCurrentPoint() > Global.getAOL())
		{
			
			
			
			
			longContract();
			
			cutLoss = buyingPoint - refPt;
		}
		else if  (GetData.getShortTB().getRSI() > 65 && GetData.getShortTB().getRSI() < 70 && GetData.getShortTB().getHL(15).getFluctuation() < 50
				&& Global.getCurrentPoint() < Global.getAOH())
		{
			
			
			
			shortContract();
			cutLoss = refPt - buyingPoint;
		}
	
	}
	
		
//		openOHLC(Global.getpHigh());


	// use 1min instead of 5min
	void updateStopEarn() {
		
		

		if (Global.getNoOfContracts() > 0) {
			
			
			if (GetData.getShortTB().getRSI() > 60)
				tempCutLoss = 99999;
			
		} else if (Global.getNoOfContracts() < 0) {
			
		
			
			if (GetData.getShortTB().getRSI() < 40)
				tempCutLoss = 0;

		}

	}

	// use 1min instead of 5min
	double getCutLossPt() {

		return 50;

	}

	@Override
	protected void cutLoss() {

		if (Global.getNoOfContracts() > 0 && Global.getCurrentPoint() < tempCutLoss) {
			closeContract(className + ": CutLoss, short @ " + Global.getCurrentBid());
			shutdown = true;
		} else if (Global.getNoOfContracts() < 0 && Global.getCurrentPoint() > tempCutLoss) {
			closeContract(className + ": CutLoss, long @ " + Global.getCurrentAsk());
			shutdown = true;

		}
	}


	double getStopEarnPt() {
		
		if (trendReversed)
			return 5;
		else
			return 30;
	}

	@Override
	public TimeBase getTimeBase() {
		return GetData.getLongTB();
	}
	
	@Override
	boolean trendReversed()
	{

		if (Global.getNoOfContracts() > 0)
			return GetData.getShortTB().getRSI() < 30;
		else
			return GetData.getShortTB().getRSI() > 70;
	}
	
	@Override
	public void trendReversedAction()
	{

		trendReversed = true;
	}

}