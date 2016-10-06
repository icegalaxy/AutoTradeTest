package icegalaxy.net;

public class RuleBouncing extends Rules {

	private double fluctuation = 100;
	private double smallFluctuation = 50;
	private double minFluctuation = 20;
	private double buffer = 10;
	protected double referencePoint;
	double highestPt = 0;
	double lowestPt = 99999;
	boolean hadProfit;
	int reverse = 0;


	boolean bouncingTrade;
	boolean trendingTrade;
	int bouncing = 0;
	int trending = 0;

	double refHLPoint;
	
	double totalWeightOfRSI = 0;

	public RuleBouncing(WaitAndNotify wan1, WaitAndNotify wan2,
			boolean globalRunRule) {
		super(wan1, wan2, globalRunRule);

	}

	@Override
	public void openContract() {

		highestPt = 0;
		lowestPt = 99999;
		hadProfit = false;

		// if (shutdown) {
		// reverse++;
		// shutdown = false;
		// }

		bouncingTrade = false;
		trendingTrade = false;

		if (
		 shutdown ||
		// !isSideWay() ||
		!Global.isOrderTime()
		// ||Global.noOfTrades >= 10
		)
			return;

		if(getTimeBase().getMA(10) > getTimeBase().getMA(20) 
				&& getTimeBase().isMARising(20,1)
				&& getTimeBase().getStandD() > 10)
			longContract();
		else if (getTimeBase().getMA(10) < getTimeBase().getMA(20) 
				&& getTimeBase().isMADropping(20, 1)
				&& getTimeBase().getStandD() > 10)
			shortContract();

	}
	
	
	

	private boolean isRising() {
		return getTimeBase().getMA(19) > getTimeBase().getMA(20);
	}

	private boolean isDropping() {
		return getTimeBase().getMA(20) > getTimeBase().getMA(19);
	}

	private float getLowestRSI() {

		if (getTimeBase().getLowestRSI() < 30)
			return getTimeBase().getLowestRSI();
		else
			return 30;
	}

	private float getHighestRSI() {

		if (getTimeBase().getHighestRSI() > 70)
			return getTimeBase().getHighestRSI();
		else
			return 70;
	}

	boolean isLargeAverageHL() {
		try {
			return getTimeBase().getAverageHL(15) > 10;
		} catch (NotEnoughPeriodException e) {
			return false;
		}
	}

//	@Override
//	boolean trendReversed() {
//
//		// if (Global.balance < 0)
//		// shutdown = true;
//
//		if (Global.getNoOfContracts() > 0) {
//			if (getTimeBase().getRSI() > 90)
//
//				return true;
//
//		} else if (Global.getNoOfContracts() < 0) {
//			if (getTimeBase().getRSI() < 10)
//
//				return true;
//		}
//		return false;
//	}

	//
	// return false;
	// }

	protected boolean isSideWay() {
		return getTimeBase().getRSI() < 60 && getTimeBase().getRSI() > 40;
	}

	boolean isTrending() {
		return getTimeBase().isMARising(30, 1.5f)
				|| getTimeBase().isMADropping(30, 1.5f)
		// ||getTimeBase().isMARising(10, 2)
		// || getTimeBase().isMADropping(10, 2)
		;

	}

	// private boolean isSmallFluctuation(){
	//
	// if (getTimeBase().getHL60().getFluctuation() > fluctuation)
	// return false;
	// else if(getTimeBase().getHL15().getFluctuation() > smallFluctuation)
	// return false;
	// else if(getTimeBase().getHL15().getFluctuation() < minFluctuation)
	// return false;
	// else
	// return true;
	//
	// }

	// protected void updateCutLoss() {
	// if (Global.getNoOfContracts() > 0)
	// tempCutLoss = buyingPoint - getCutLossPt();
	//
	// else if (Global.getNoOfContracts() < 0)
	// tempCutLoss = buyingPoint + getCutLossPt();
	//
	// }

	@Override
	// void updateStopEarn() {
	// if (Global.getNoOfContracts() > 0) {
	// if (Global.getCurrentPoint() > tempCutLoss)
	// tempCutLoss = Global.getCurrentPoint();
	// } else if (Global.getNoOfContracts() < 0) {
	// if (Global.getCurrentPoint() < tempCutLoss)
	// tempCutLoss = Global.getCurrentPoint();
	// }
	// }
	// boolean isUpperEnd(){
	// double difference = getTimeBase().getHL(60).getTempHigh() -
	// getTimeBase().getHL(60).getTempLow();
	//
	// return Global.getCurrentPoint() > Global.getDayLow() + difference * 0.8;
	// }
	//
	// boolean isLowerEnd(){
	// double difference = getTimeBase().getHL(60).getTempHigh() -
	// getTimeBase().getHL(60).getTempLow();
	//
	// return Global.getCurrentPoint() < Global.getDayLow() + difference * 0.2;
	// }
	// double getCutLossPt() {
	// return 50;
	// }
	//
	// double getStopEarnPt() {
	// return 15;
	// }
	// protected void updateCutLoss() {
	//
	// if (getCutLossPt() < cutLossPt)
	// cutLossPt = getCutLossPt();
	//
	// // if (getStopEarnPt() < stopEarnPt)
	// // stopEarnPt = getStopEarnPt();
	//
	// if (Global.getNoOfContracts() > 0) {
	// // if (Global.getCurrentPoint() - tempCutLoss > cutLossPt) {
	// // tempCutLoss = Global.getCurrentPoint() - cutLossPt;
	// // System.out.println("CurrentPt: " + Global.getCurrentPoint());
	// // System.out.println("cutLossPt: " + cutLossPt);
	// // System.out.println("TempCutLoss: " + tempCutLoss);
	// // }
	//
	// if (buyingPoint - cutLossPt > tempCutLoss)
	// tempCutLoss = buyingPoint - cutLossPt;
	//
	// if (Global.getCurrentPoint() + stopEarnPt < tempStopEarn) {
	// tempStopEarn = Global.getCurrentPoint() + stopEarnPt;
	// System.out.println("TempStopEarn: " + tempStopEarn);
	// }
	//
	// } else if (Global.getNoOfContracts() < 0) {
	// // if (tempCutLoss - Global.getCurrentPoint() > cutLossPt) {
	// // tempCutLoss = Global.getCurrentPoint() + cutLossPt;
	// // System.out.println("CurrentPt: " + Global.getCurrentPoint());
	// // System.out.println("cutLossPt: " + cutLossPt);
	// // System.out.println("TempCutLoss: " + tempCutLoss);
	// // }
	//
	// if (buyingPoint + cutLossPt < tempCutLoss)
	// tempCutLoss = buyingPoint + cutLossPt;
	//
	// if (Global.getCurrentPoint() - stopEarnPt > tempStopEarn) {
	// tempStopEarn = Global.getCurrentPoint() - stopEarnPt;
	// System.out.println("TempStopEarn: " + tempStopEarn);
	// }
	// }
	// }
	// public void closeContract() {
	//
	// if (Global.getNoOfContracts() > 0) {
	// tempCutLoss = buyingPoint - getCutLossPt() ;
	// tempStopEarn = buyingPoint + getStopEarnPt();
	// } else if (Global.getNoOfContracts() < 0) {
	// tempCutLoss = buyingPoint + getCutLossPt() ;
	// tempStopEarn = buyingPoint - getStopEarnPt();
	// }
	//
	// stopEarnPt = getStopEarnPt();
	// cutLossPt = getCutLossPt();
	//
	// while (!reachGreatProfitPt()) {
	//
	// updateCutLoss();
	// cutLoss();
	//
	// if (Global.isForceSellTime()) {
	// closeContract("Force Sell");
	// return;
	// }
	//
	// if (Global.getNoOfContracts() == 0) { // �i��ڨ�Lrule close���A��Trend
	// // truned�A�̧Y�Y�४�աA��
	// hasContract = false;
	// break;
	// }
	//
	// if (!hasContract)
	// break;
	//
	// wanPrevious.middleWaiter(wanNext);
	// }
	//
	// if (Global.getNoOfContracts() == 0) {
	// hasContract = false;
	// return;
	// }
	//
	// if (!hasContract)
	// return;
	//
	// refPt = Global.getCurrentPoint();
	//
	// while (hasContract) {
	//
	// if (Global.getNoOfContracts() == 0) {
	// hasContract = false;
	// break;
	// }
	//
	// if (Global.isForceSellTime()) {
	// closeContract("Force Sell");
	// return;
	// }
	//
	// updateStopEarn();
	// stopEarn();
	//
	// wanPrevious.middleWaiter(wanNext);
	// }
	// }
	// void updateStopEarn() {
	//
	// if (Global.getNoOfContracts() > 0) {
	//
	// if (StockDataController.getSec10TB().getLatestCandle().getLow() >
	// tempCutLoss) {
	// tempCutLoss =
	// StockDataController.getSec10TB().getLatestCandle().getLow();
	//
	// }
	//
	// } else if (Global.getNoOfContracts() < 0) {
	//
	// if (StockDataController.getSec10TB().getLatestCandle().getHigh() <
	// tempCutLoss) {
	// tempCutLoss =
	// StockDataController.getSec10TB().getLatestCandle().getHigh();
	//
	// }
	// }
	//
	// }
	// @Override
	public TimeBase getTimeBase() {

		return StockDataController.getM15TB();
	}

	// @Override
	// protected boolean reachGreatProfitPt() {
	// if (Global.getNoOfContracts() > 0)
	// return Global.getCurrentPoint() - buyingPoint > getStopEarnPt()
	// || getTimeBase().getRSI() > 70;
	// else if (Global.getNoOfContracts() < 0)
	// return buyingPoint - Global.getCurrentPoint() > getStopEarnPt()
	// || getTimeBase().getRSI() < 30;
	// return false;
	// }

	// @Override
	// void updateStopEarn() {
	//
	// float ma = getTimeBase().getMA(10);
	//
	// if (Global.getNoOfContracts() > 0) {
	//
	// if (getTimeBase().getRSI() > 65){
	// super.updateStopEarn();
	// return;
	// }
	//
	// // if (ma > tempCutLoss && Global.getCurrentPoint() > ma)
	// // tempCutLoss = ma;
	// if (getTimeBase().getMainUpRail().getRail() > tempCutLoss)
	// tempCutLoss = getTimeBase().getMainUpRail().getRail();
	//
	// } else if (Global.getNoOfContracts() < 0) {
	//
	// if (getTimeBase().getRSI() < 35){
	// super.updateStopEarn();
	// return;
	// }
	//
	// // if (ma < tempCutLoss && Global.getCurrentPoint() < ma)
	// // tempCutLoss = ma;
	// if (getTimeBase().getMainDownRail().getRail() != 0
	// && getTimeBase().getMainDownRail().getRail() < tempCutLoss)
	// tempCutLoss = getTimeBase().getMainDownRail().getRail();
	// }
	// }

	// protected boolean reachGreatProfitPt() {
	//
	// return true;
	// }

	// @Override
	// void updateStopEarn() {
	//
	// float ma10 = getTimeBase().getMA(10);
	//
	// if (Global.getNoOfContracts() > 0) {
	//
	// if (ma10 > tempCutLoss && Global.getCurrentPoint() > ma10)
	// tempCutLoss = ma10;
	// if (getTimeBase().getMainUpRail().getRail() > tempCutLoss)
	// tempCutLoss = getTimeBase().getMainUpRail().getRail();
	//
	// } else if (Global.getNoOfContracts() < 0) {
	//
	// if (ma10 < tempCutLoss && Global.getCurrentPoint() < ma10)
	// tempCutLoss = ma10;
	// if (getTimeBase().getMainDownRail().getRail() != 0
	// && getTimeBase().getMainDownRail().getRail() < tempCutLoss)
	// tempCutLoss = getTimeBase().getMainDownRail().getRail();
	// }
	// }

	double getCutLossPt() {
		// if (Global.getNoOfContracts() > 0)
		// return maxDrop()/4;
		// else if (Global.getNoOfContracts() < 0)
		// return maxRise()/4;

		// if (getTimeBase().getRSI() > 70 || getTimeBase().getRSI() < 30)
		// return 100;

		if (getTimeBase().getStandD() * 2 < 10)
			return 10;
		else
			return getTimeBase().getStandD() * 2;
	}

	double getStopEarnPt() {
		return getTimeBase().getStandD() * 4;
	}

	// protected void updateCutLoss() {
	//
	// if (Global.getNoOfContracts() > 0) {
	//
	// tempCutLoss = buyingPoint - getCutLossPt();
	// } else if (Global.getNoOfContracts() < 0) {
	//
	// tempCutLoss = buyingPoint + getCutLossPt();
	//
	// }
	// }

	// protected boolean reachGreatProfitPt() {
	//
	// // if (getStopEarnPt() < stopEarnPt)
	// // stopEarnPt = getStopEarnPt();

	// if (Global.getNoOfContracts() > 0){
	//
	// // if (profit() > 10 || hadProfit){
	// //
	// // hadProfit = true;
	// //
	// // if (Global.getCurrentPoint() > highestPt)
	// // highestPt = Global.getCurrentPoint();
	// //
	// // return Global.getCurrentPoint() < ((highestPt - buyingPoint)/2) +
	// buyingPoint
	// // || Global.getCurrentPoint() > buyingPoint + 30
	// // || getTimeBase().getRSI() > 70;
	// // }

	// if (Global.getCurrentPoint() < referencePoint)
	// referencePoint = Global.getCurrentPoint();
	//
	// return Global.getCurrentPoint() - getStopEarnPt() > referencePoint;
	// }
	//
	// else if (Global.getNoOfContracts() < 0){
	//
	//
	// // if (profit() > 10 || hadProfit){
	// //
	// // hadProfit = true;
	// //
	// // if (Global.getCurrentPoint() < lowestPt)
	// // lowestPt = Global.getCurrentPoint();
	// //
	// // return Global.getCurrentPoint() > buyingPoint - ((buyingPoint -
	// lowestPt)/2)
	// // || Global.getCurrentPoint() < buyingPoint - 30
	// // || getTimeBase().getRSI() < 30;
	// // }
	//
	// if (Global.getCurrentPoint() > referencePoint)
	// referencePoint = Global.getCurrentPoint();
	//
	// return Global.getCurrentPoint() + getStopEarnPt() < referencePoint;
	//
	// }
	// else
	// return false;
	// }

	private double profit() {

		if (Global.getNoOfContracts() > 0)
			return Global.getCurrentPoint() - buyingPoint;
		else if (Global.getNoOfContracts() < 0)
			return buyingPoint - Global.getCurrentPoint();
		return 0;
	}

	// @Override
	// boolean trendReversed2() {
	// double slope = 0;
	//
	// if (Global.getNoOfContracts() > 0)
	// slope = StockDataController.getSec10TB().getMainDownRail()
	// .getSlope();
	//
	// if (Global.getNoOfContracts() < 0)
	// slope = StockDataController.getSec10TB().getMainUpRail().getSlope();
	//
	// return slope > 5 && slope != 100;
	// }

	// void updateStopEarn() {
	//
	// if (Global.getNoOfContracts() > 0) {
	//
	// if (getTimeBase().getHL(3).getTempLow()> tempCutLoss) {
	// tempCutLoss = getTimeBase().getHL(3).getTempLow();
	//
	// }
	//
	// } else if (Global.getNoOfContracts() < 0) {
	//
	// if (getTimeBase().getHL(3).getTempHigh() < tempCutLoss) {
	// tempCutLoss = getTimeBase().getHL(3).getTempHigh();
	//
	// }
	// }
	//
	// }

	double getFlucIndex() {
		return StockDataController.getShortTB().getHL(60).getFluctuation() / 5;
	}

	double maxRise() {
		return getTimeBase().getHL(30).getMaxRise();
	}

	double maxDrop() {
		return getTimeBase().getHL(30).getMaxDrop();
	}

	// @Override
	// protected void cutLoss() {
	//
	// if (Global.getNoOfContracts() > 0){
	// if (Global.balance + Global.getCurrentPoint() <= -20){
	//
	// // Global.addLog("Balance: " + (Global.balance +
	// Global.getCurrentPoint()));
	// closeContract("CutLoss " + className);
	// shutdown = true;
	// }
	// }else if (Global.getNoOfContracts() < 0){
	// if (Global.balance - Global.getCurrentPoint() <= -20){
	// // Global.addLog("Balance: " + (Global.balance -
	// Global.getCurrentPoint()));
	// closeContract("CutLoss " + className);
	// shutdown = true;
	// }
	// }
	//
	//
	// }
	

}
