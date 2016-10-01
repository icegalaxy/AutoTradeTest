package icegalaxy.net;



public class RuleSeconds extends Rules {

	float upperLine = 70;
	float lowerLine = 30;
	boolean isOutOfRSI;

	float upperPoint = 0;
	float lowerPoint = 99999;
	float referencePoint = 0;
	int durationOfGap = 0;

	public RuleSeconds(WaitAndNotify wan1, WaitAndNotify wan2,
			boolean globalRunRule) {
		super(wan1, wan2, globalRunRule);
		// TODO Auto-generated constructor stub
	}

	// @Override
	// boolean trendReversed() {
	// // TODO Auto-generated method stub
	// return false;
	// }

	@Override
	public void openContract() {

		checkRSI();
		checkGap();

		if (!Global.isOrderTime()
//		 || shutdown
//		 || !isClose()
		// || isOutOfRSI
		// || outOfRSI()
		// || !waitedFor50Points()
		)
			return;

		if(durationOfGap >= 7){
			referencePoint = Global.getCurrentPoint();
			
			while(Math.abs(Global.getCurrentPoint() - referencePoint) <= 10){
				sleep();
			}
			
			if (Global.getCurrentPoint() - referencePoint > 10)
				longContract();
			else
				shortContract();
			
		}
		
		sleep();
		

	}
	
	private void checkGap(){
		if(isClose())
			durationOfGap++;
		else
			durationOfGap = 0;
	}

	private boolean isRising() {
		return getTimeBase().getMA(10) > getTimeBase().getMA(20);
	}

	private boolean isDropping() {
		return getTimeBase().getMA(10) < getTimeBase().getMA(20);
	}

	private boolean waitedFor50Points() {
		return Global.getCurrentPoint() > upperPoint
				|| Global.getCurrentPoint() < lowerPoint;
	}

	private void checkRSI() {
		if (StockDataController.getShortTB().getRSI() < 30
				|| StockDataController.getShortTB().getRSI() > 70)
			isOutOfRSI = true;
	}

	private boolean outOfRSI() {
		return getTimeBase().getRSI() < 30 || getTimeBase().getRSI() > 70;
	}

	private boolean isClose() {
		return Global.getCurrentAsk() - Global.getCurrentBid() <= 1;
	}

	double getCutLossPt() {
		return 300 - lossAdjustment;
	}

	double getStopEarnPt() {
		return 50 - earnAdjustment;
	}

	// protected void cutLoss() {
	// super.cutLoss();
	//
	// // if(Global.getNoOfContracts() > 0 && getTimeBase().getRSI() < 85)
	// // closeContract("CutLoss RSI < 70 " + className);
	// // else if(Global.getNoOfContracts() < 0 && getTimeBase().getRSI() > 15)
	// // closeContract("CutLoss RSI > 30 " + className);
	//
	// while (getTimeBase().getRSI() > 60 || getTimeBase().getRSI() < 40)
	// sleep();
	// }

	// @Override
	// protected void cutLoss() {
	//
	// if (Global.getNoOfContracts() > 0
	// && Global.getCurrentPoint() < tempCutLoss){
	// closeContract("CutLoss " + className);
	// while (getTimeBase().getRSI() > upperLine || getTimeBase().getRSI() <
	// lowerLine){
	// sleep();
	// checkRSI();
	// }
	// }
	// else if (Global.getNoOfContracts() < 0
	// && Global.getCurrentPoint() > tempCutLoss){
	// closeContract("CutLoss " + className);
	// while (getTimeBase().getRSI() > upperLine || getTimeBase().getRSI() <
	// lowerLine){
	// sleep();
	// checkRSI();
	// }
	// }
	//
	// }

	//
	// void updateStopEarn() {
	//
	// if (Global.getNoOfContracts() > 0) {
	//
	// tempCutLoss = buyingPoint + getStopEarnPt();
	//
	//
	//
	// } else if (Global.getNoOfContracts() < 0) {
	//
	// tempCutLoss = buyingPoint - getStopEarnPt();
	//
	// }
	//
	//
	// }

	// void stopEarn() {
	// if (Global.getNoOfContracts() > 0
	// && Global.getCurrentBid() < tempCutLoss){
	// closeContract("StopEarn " + className);
	// }
	// else if (Global.getNoOfContracts() < 0
	// && Global.getCurrentAsk() > tempCutLoss){
	// closeContract("StopEarn " + className);
	// }
	// }

//	@Override
//	protected void cutLoss() {
//		super.cutLoss();
//		if (shutdown) {
//
//			if (getTimeBase().getMA(10) > getTimeBase().getMA(20)) {
//				while (getTimeBase().getMA(10) > getTimeBase().getMA(20))
//					wanPrevious.middleWaiter(wanNext);
//			}
//			if (getTimeBase().getMA(10) < getTimeBase().getMA(20)) {
//				while (getTimeBase().getMA(10) < getTimeBase().getMA(20))
//					wanPrevious.middleWaiter(wanNext);
//			}
//
//			shutdown = false;
//		}
//
//	}

//	@Override
//	protected void cutLoss() {
//		
//		boolean tempCutLoss = false;
//
//		if (Global.getNoOfContracts() > 0) {
//
//			if (buyingPoint - Global.getCurrentPoint() > lossAdjustment) {
//				closeContract("CutLoss " + className);
//				tempCutLoss = true;
//			} else if (Global.balance + Global.getCurrentPoint() <= -25) {
//				closeContract("CutLoss " + className);
//				shutdown = true;
//			}
//		} else if (Global.getNoOfContracts() < 0) {
//			
//			if (Global.getCurrentPoint() - buyingPoint > getCutLossPt()) {
//				closeContract("CutLoss " + className);
//				tempCutLoss = true;
//			} else if (Global.balance - Global.getCurrentPoint() <= -20) {
//				closeContract("CutLoss " + className);
//				shutdown = true;
//			}
//		}
//	}
//
//		if (tempCutLoss) {
//
//			if (getTimeBase().getMA(10) > getTimeBase().getMA(20)) {
//				while (getTimeBase().getMA(10) > getTimeBase().getMA(20))
//					wanPrevious.middleWaiter(wanNext);
//			}
//			if (getTimeBase().getMA(10) < getTimeBase().getMA(20)) {
//				while (getTimeBase().getMA(10) < getTimeBase().getMA(20))
//					wanPrevious.middleWaiter(wanNext);
//			}
//			
//			tempCutLoss = false;
//		}
//	}

	@Override
	public TimeBase getTimeBase() {
		return StockDataController.getShortTB();
	}

	void sleep() {
		wanPrevious.middleWaiter(wanNext);
	}

}
