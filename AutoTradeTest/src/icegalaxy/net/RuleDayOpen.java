package icegalaxy.net;

public class RuleDayOpen extends Rules {

	public RuleDayOpen(WaitAndNotify wan1, WaitAndNotify wan2,
			boolean globalRunRule) {
		super(wan1, wan2, globalRunRule);
		// TODO Auto-generated constructor stub
	}

	public RuleDayOpen(WaitAndNotify wan1, WaitAndNotify wan2) {
		super(wan1, wan2);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void openContract() {
		
		if (TimePeriodDecider.isOpeningTime() && isRising())
			longContract();
		
		else if (TimePeriodDecider.isOpeningTime() && isDropping())
			shortContract();
	}
	
	private boolean isRising(){
		return StockDataController.getSec10TB().getMainUpRail().getSlope() != 100
				&& StockDataController.getSec10TB().getMainUpRail().getSlope() > 3
				&& StockDataController.getSec10TB().getMainDownRail().getSlope() == 100
				;
	}
	
	private boolean isDropping(){
		return StockDataController.getSec10TB().getMainDownRail().getSlope() != 100
				&& StockDataController.getSec10TB().getMainDownRail().getSlope() > 3
				&& StockDataController.getSec10TB().getMainUpRail().getSlope() == 100
				;
	}

	@Override
	public TimeBase getTimeBase() {
		return StockDataController.getShortTB();
	}
	
	@Override
	double getStopEarnPt(){
		return 20;
	}
	
	@Override
	double getCutLossPt(){
		return 20;
	}
}
