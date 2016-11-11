package icegalaxy.net;

public class RuleRail extends Rules {

	double refPt;
	protected double referencePoint;
	
	public RuleRail(WaitAndNotify wan1, WaitAndNotify wan2,
			boolean globalRunRule) {
		super(wan1, wan2, globalRunRule);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void openContract() {

//		double upSlope = 0;
//		double downSlope = 0;
		
//		if (getTimeBase().getHL(60).getFluctuation() < 50)
//			return;
		
		if (
				getTimeBase().getMainUpRail().getSlope() != 0 && 
				getTimeBase().getMainUpRail().getSlope() != 100 &&
				!getTimeBase().isMADropping(20, 1) &&
				Global.getCurrentPoint() < getTimeBase().getMA(20)
				){
						
			if (!Global.isOrderTime())
				return;
			
			if (getTimeBase().getMainDownRail().getSlope() != 100 &&
					getTimeBase().getMainDownRail().getSlope() != 0)
				return;
			
			if (getTimeBase().isMADropping(20, 1))
				return;
			
			longContract();
			referencePoint = buyingPoint;
			wanPrevious.middleWaiter(wanNext);
		}
		else if (
				getTimeBase().getMainDownRail().getSlope() != 100 && 
				getTimeBase().getMainDownRail().getSlope() != 0 && 
				!getTimeBase().isMARising(20, 1) &&
				Global.getCurrentPoint() > getTimeBase().getMA(20)
				){
						
			if (!Global.isOrderTime())
				return;
			
			if (getTimeBase().getMainUpRail().getSlope() != 100 &&
					getTimeBase().getMainUpRail().getSlope() != 0)
				return;
			
			if (getTimeBase().isMARising(20, 1))
				return;
			
			shortContract();
			referencePoint = buyingPoint;
			wanPrevious.middleWaiter(wanNext);
		}
		
		
	}

//	@Override
//	public void closeContract() {
//
//		if (Global.getNoOfContracts() > 0)
//			tempCutLoss = buyingPoint - 30;
//		else if (Global.getNoOfContracts() < 0)
//			tempCutLoss = buyingPoint + 30;
//
//		if (Global.getNoOfContracts() > 0) {
//			while (hasContract) {
//				if (getTimeBase().getMainUpRail().getRail() > tempCutLoss) {
//					tempCutLoss = (float) getTimeBase().getMainUpRail()
//							.getRail();
//					System.out.println("TempCutLoss: " + tempCutLoss);
//				}
//
//				// if (getTimeBase().getMA(10) > tempCutLoss)
//				// tempCutLoss = getTimeBase().getMA(10);
//
//				stopEarn();
//				if (Global.isForceSellTime())
//					closeContract("Force Sell");
//				wanPrevious.middleWaiter(wanNext);
//			}
//		} else if (Global.getNoOfContracts() < 0) {
//			while (hasContract) {
//				if (getTimeBase().getMainDownRail().getRail() != 0
//						&& getTimeBase().getMainDownRail().getRail() < tempCutLoss) {
//					tempCutLoss = (float) getTimeBase().getMainDownRail()
//							.getRail();
//					System.out.println("TempCutLoss: " + tempCutLoss);
//				}
//
//				// if (getTimeBase().getMA(10) < tempCutLoss)
//				// tempCutLoss = getTimeBase().getMA(10);
//
//				stopEarn();
//				if (Global.isForceSellTime())
//					closeContract("Force Sell");
//				wanPrevious.middleWaiter(wanNext);
//			}
//		}
//
//	}

//	@Override
//	protected boolean reachGreatProfitPt() {
//		return true;		
//	}
	
	@Override
	public TimeBase getTimeBase() {
		return GetData.getShortTB();
	}

//	@Override
//	boolean trendReversed() {
//		if (Global.getNoOfContracts() > 0)
//			return getTimeBase().isMADropping(20, 1);
//				
//		else if (Global.getNoOfContracts() < 0)
//			return getTimeBase().isMARising(20, 1);
//						
//		return false;
//	}
//	
	double getCutLossPt() {
		return 50;
		
		//return getTimeBase().getHL(60).getFluctuation()/3;
	}

	double getStopEarnPt() {
		return 15;
	}

	boolean trendReversed2() {
		double slope = 0;

		if (Global.getNoOfContracts() > 0)
			slope = GetData.getShortTB().getMainDownRail()
					.getSlope();

		if (Global.getNoOfContracts() < 0)
			slope = GetData.getShortTB().getMainUpRail().getSlope();

		return slope > 5 && slope != 100;
	}
	
	protected boolean reachGreatProfitPt() {

//		if (getStopEarnPt() < stopEarnPt)
//			stopEarnPt = getStopEarnPt();

		if (Global.getNoOfContracts() > 0){
			
			if (Global.getCurrentPoint() < referencePoint)
				referencePoint = Global.getCurrentPoint();
			
			return Global.getCurrentPoint() - getStopEarnPt() > referencePoint; 
		}
		
		else if (Global.getNoOfContracts() < 0){
			
			if (Global.getCurrentPoint() > referencePoint)
				referencePoint = Global.getCurrentPoint();
			
			return Global.getCurrentPoint() + getStopEarnPt() < referencePoint;
			
		}
		else
			return false;
	}
	
}
