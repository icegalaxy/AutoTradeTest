package icegalaxy.net;

public class RuleRateOfChange extends Rules {

	private double referencePoint;
	public RuleRateOfChange(WaitAndNotify wan1, WaitAndNotify wan2,
			boolean globalRunRule) {
		super(wan1, wan2, globalRunRule);
		// TODO Auto-generated constructor stub
	}

	public RuleRateOfChange(WaitAndNotify wan1, WaitAndNotify wan2) {
		super(wan1, wan2);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void openContract() {


		if (!Global.isOrderTime())
			return;
		
		if (getTimeBase().GetRsiDifference() > 10 && getTimeBase().getRSI() > 40){ //因為30下面上去好易+10
			longContract();
			referencePoint = buyingPoint;
		}
		else if (getTimeBase().GetRsiDifference() < -10 && getTimeBase().getRSI() < 60){
			shortContract();
			referencePoint = buyingPoint;
		}
		wanPrevious.middleWaiter(wanNext);
	}

	
	boolean isAccUp(){
		return getTimeBase().getEMA(10) - getTimeBase().getEMA(20) >= 10;
	}
	
	boolean isConUp(){
		return getTimeBase().getEMA(10) - getTimeBase().getEMA(20) > 0 
		&& getTimeBase().getEMA(10) - getTimeBase().getEMA(20) < 10;
	}
	
	

	@Override
	public TimeBase getTimeBase() {
		
		return StockDataController.getSec10TB();
	}
	
	double getCutLossPt() {
		return 20;
	}

	double getStopEarnPt() {
		return 20;
	}

	@Override
	boolean trendReversed() {
		// TODO Auto-generated method stub
		return false;
	}
//	protected boolean reachGreatProfitPt() {
//
////		if (getStopEarnPt() < stopEarnPt)
////			stopEarnPt = getStopEarnPt();
//
//		if (Global.getNoOfContracts() > 0){
//			
////			if (getTimeBase().getRSI() > 50)
////				return true;
//			
//			if (Global.getCurrentPoint() < referencePoint)
//				referencePoint = Global.getCurrentPoint();
//			
//			return Global.getCurrentPoint() - getStopEarnPt() > referencePoint; 
//		}
//		
//		else if (Global.getNoOfContracts() < 0){
//			
////			if (getTimeBase().getRSI() < 50)
////				return true;
//			
//			if (Global.getCurrentPoint() > referencePoint)
//				referencePoint = Global.getCurrentPoint();
//			
//			return Global.getCurrentPoint() + getStopEarnPt() < referencePoint;
//			
//		}
//		else
//			return false;
//	}
}
