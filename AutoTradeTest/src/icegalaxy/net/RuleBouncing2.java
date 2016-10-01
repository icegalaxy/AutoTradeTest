package icegalaxy.net;


public class RuleBouncing2 extends Rules {

	private double fluctuation = 100;
	private double smallFluctuation = 50;
	private double minFluctuation = 20;
	private double buffer = 10;
	protected double referencePoint;
	double highestPt = 0;
	double lowestPt = 99999;
	boolean hadProfit;
	boolean shutdown2;
	
	double refHLPoint;

	public RuleBouncing2(WaitAndNotify wan1, WaitAndNotify wan2,
			boolean globalRunRule) {
		super(wan1, wan2, globalRunRule);
		// TODO Auto-generated constructor stub
          // setOrderTime(110000, 1145000, 140000, 154500);
//		testing = true;
	}

	@Override
	public void openContract() {
		
		if (TimePeriodDecider.getTime() < 94500){
			//if(Global.getChange() > 150
			//		|| Global.getChange() < -150)
			if(getTimeBase().getHL(3).getFluctuation() > 50)
				shutdown2 = true;
				//setOrderTime(120000,120000,140000,154500);
		}
		
		if (	!isSideWay() || 
				!Global.isOrderTime() ||
				Global.noOfTrades >= 10
				)
			return;
		
		if (shutdown2)
			return;

		if (shutdown)
			return;
		
		refHLPoint = Global.getCurrentPoint();
		
		

		if(getTimeBase().getRSI() < 30){	
			
			if(Global.getCurrentPoint() - 10 < Global.getDayLow())
				return;
			
			while (Global.getCurrentPoint() - 10 < refHLPoint
					|| getTimeBase().getRSI() < 30){
				if (Global.getCurrentPoint() < refHLPoint)
					refHLPoint = Global.getCurrentPoint();
				
				if (!Global.isOrderTime() 
					|| !isSideWay()
//					|| getTimeBase().getRSI() < 15
//					|| getTimeBase().getHL(30).getMaxRise() < 20
						)
					return;
				
				wanPrevious.middleWaiter(wanNext);
			}
				
			
			longContract();
			referencePoint = buyingPoint;
		}
			
		if (getTimeBase().getRSI() > 70){
			
			if(Global.getCurrentPoint() + 10 > Global.getDayHigh())
				return;
			
			while (Global.getCurrentPoint() + 10 > refHLPoint
					|| getTimeBase().getRSI() > 70){
				if (Global.getCurrentPoint() > refHLPoint)
					refHLPoint = Global.getCurrentPoint();
				
				if (!Global.isOrderTime() 
					|| !isSideWay()
//					|| getTimeBase().getRSI() > 85
//					|| getTimeBase().getHL(30).getMaxDrop() < 20
						)
					return;
				
				wanPrevious.middleWaiter(wanNext);
			}
			
			shortContract();
			referencePoint = buyingPoint;
		}

	}

	boolean isLargeAverageHL(){
		try {
			return getTimeBase().getAverageHL(15) > 10;
		} catch (NotEnoughPeriodException e) {
			return false;
		}
	}
	
	@Override
	boolean trendReversed() {
//		if (Global.getNoOfContracts() > 0){
//			return getTimeBase().getRSI() < 30;
//		}else if (Global.getNoOfContracts() < 0)
//			return getTimeBase().getRSI() > 70;
		return false;
	}
	
	protected boolean isSideWay() {
		return 
				!isTrending()
				&& 	StockDataController.getShortTB().getHL(60).getFluctuation() < 80
				&& StockDataController.getShortTB().getHL(60).getFluctuation() > 35;
	}
	
	boolean isTrending(){
		return 
				getTimeBase().isMARising(30, 1.5f) 
				|| getTimeBase().isMADropping(30, 1.5f)
//		||getTimeBase().isMARising(10, 2) 
//		|| getTimeBase().isMADropping(10, 2)
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

//	@Override
//	void updateStopEarn() {
//		if (Global.getNoOfContracts() > 0) {
//			if (Global.getCurrentPoint() > tempCutLoss)
//				tempCutLoss = Global.getCurrentPoint();
//		} else if (Global.getNoOfContracts() < 0) {
//			if (Global.getCurrentPoint() < tempCutLoss)
//				tempCutLoss = Global.getCurrentPoint();
//		}
//	}
	@Override
	protected void cutLoss() {
		
			if (Global.getNoOfContracts() > 0){
				if (Global.balance + Global.getCurrentPoint() <= -30){
			
//					Global.addLog("Balance: " + (Global.balance + Global.getCurrentPoint()));
			closeContract("CutLoss " + className);
			shutdown = true;
				}
				}else if (Global.getNoOfContracts() < 0){
					if (Global.balance - Global.getCurrentPoint() <= -30){
//						Global.addLog("Balance: " + (Global.balance - Global.getCurrentPoint()));
						closeContract("CutLoss " + className);
						shutdown = true;
					}
				}

		
	}
	
	

	boolean isUpperEnd(){
		double difference = getTimeBase().getHL(60).getTempHigh() - getTimeBase().getHL(60).getTempLow(); 
		
		return Global.getCurrentPoint() > Global.getDayLow() + difference * 0.8;
	}
	
	boolean isLowerEnd(){
		double difference = getTimeBase().getHL(60).getTempHigh() - getTimeBase().getHL(60).getTempLow();
		
		return Global.getCurrentPoint() < Global.getDayLow() + difference * 0.2;
	}
	
	
//	double getCutLossPt() {
//		return 50;
//	}
//
//	double getStopEarnPt() {
//		return 15;
//	}


//	protected void updateCutLoss() {
//
//		if (getCutLossPt() < cutLossPt)
//			cutLossPt = getCutLossPt();
//
////		 if (getStopEarnPt() < stopEarnPt)
////		 stopEarnPt = getStopEarnPt();
//
//		if (Global.getNoOfContracts() > 0) {
//			// if (Global.getCurrentPoint() - tempCutLoss > cutLossPt) {
//			// tempCutLoss = Global.getCurrentPoint() - cutLossPt;
//			// System.out.println("CurrentPt: " + Global.getCurrentPoint());
//			// System.out.println("cutLossPt: " + cutLossPt);
//			// System.out.println("TempCutLoss: " + tempCutLoss);
//			// }
//
//			if (buyingPoint - cutLossPt > tempCutLoss)
//				tempCutLoss = buyingPoint - cutLossPt;
//
//			 if (Global.getCurrentPoint() + stopEarnPt < tempStopEarn) {
//			 tempStopEarn = Global.getCurrentPoint() + stopEarnPt;
//			 System.out.println("TempStopEarn: " + tempStopEarn);
//			 }
//
//		} else if (Global.getNoOfContracts() < 0) {
//			// if (tempCutLoss - Global.getCurrentPoint() > cutLossPt) {
//			// tempCutLoss = Global.getCurrentPoint() + cutLossPt;
//			// System.out.println("CurrentPt: " + Global.getCurrentPoint());
//			// System.out.println("cutLossPt: " + cutLossPt);
//			// System.out.println("TempCutLoss: " + tempCutLoss);
//			// }
//
//			if (buyingPoint + cutLossPt < tempCutLoss)
//				tempCutLoss = buyingPoint + cutLossPt;
//
//			 if (Global.getCurrentPoint() - stopEarnPt > tempStopEarn) {
//			 tempStopEarn = Global.getCurrentPoint() - stopEarnPt;
//			 System.out.println("TempStopEarn: " + tempStopEarn);
//			 }
//		}
//	}
	
//	@Override
//	protected boolean reachGreatProfitPt() {
//		if (Global.getNoOfContracts() > 0)
//			return Global.getCurrentPoint() > getTimeBase().getMA(20);
//		else if (Global.getNoOfContracts() < 0)
//			return Global.getCurrentPoint() < getTimeBase().getMA(20);
//		return false;
//	}

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
	// if (Global.getNoOfContracts() == 0) { // 可能俾其他rule close左，或Trend
	// // truned，甘即係轉左勢，走
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

	@Override
	public TimeBase getTimeBase() {

		return StockDataController.getShortTB();
	}

//	@Override
//	protected
//	boolean reachGreatProfitPt() {
//
//		return true;
//	}
	
//	@Override
//	void updateStopEarn() {
//
//		float ma = getTimeBase().getMA(10);
//
//		if (Global.getNoOfContracts() > 0) {
//
//			if (getTimeBase().getRSI() > 65){
//				super.updateStopEarn();
//				return;
//			}
//			
////			if (ma > tempCutLoss && Global.getCurrentPoint() > ma)
////				tempCutLoss = ma;
//			if (getTimeBase().getMainUpRail().getRail() > tempCutLoss)
//				tempCutLoss = getTimeBase().getMainUpRail().getRail();
//
//		} else if (Global.getNoOfContracts() < 0) {
//
//			if (getTimeBase().getRSI() < 35){
//				super.updateStopEarn();
//				return;
//			}
//			
////			if (ma < tempCutLoss && Global.getCurrentPoint() < ma)
////				tempCutLoss = ma;
//			if (getTimeBase().getMainDownRail().getRail() != 0
//					&& getTimeBase().getMainDownRail().getRail() < tempCutLoss)
//				tempCutLoss = getTimeBase().getMainDownRail().getRail();
//		}
//	}
	
//	protected boolean reachGreatProfitPt() {
//
//		return true;
//	}
	
//	@Override
//	void updateStopEarn() {
//
//		float ma10 = getTimeBase().getMA(10);
//
//		if (Global.getNoOfContracts() > 0) {
//
//			if (ma10 > tempCutLoss && Global.getCurrentPoint() > ma10)
//				tempCutLoss = ma10;
//			if (getTimeBase().getMainUpRail().getRail() > tempCutLoss)
//				tempCutLoss = getTimeBase().getMainUpRail().getRail();
//
//		} else if (Global.getNoOfContracts() < 0) {
//
//			if (ma10 < tempCutLoss && Global.getCurrentPoint() < ma10)
//				tempCutLoss = ma10;
//			if (getTimeBase().getMainDownRail().getRail() != 0
//					&& getTimeBase().getMainDownRail().getRail() < tempCutLoss)
//				tempCutLoss = getTimeBase().getMainDownRail().getRail();
//		}
//	}
	
	double getCutLossPt() {
		return 10;
	}

	double getStopEarnPt() {
		return 50;
	}
		
	protected void updateCutLoss() {

		if (Global.getNoOfContracts() > 0) {

			tempCutLoss = buyingPoint - getCutLossPt();
		} else if (Global.getNoOfContracts() < 0) {

			tempCutLoss = buyingPoint + getCutLossPt();

		}
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
	
//	@Override
//	boolean trendReversed2() {
//		double slope = 0;
//
//		if (Global.getNoOfContracts() > 0)
//			slope = StockDataController.getSec10TB().getMainDownRail()
//					.getSlope();
//
//		if (Global.getNoOfContracts() < 0)
//			slope = StockDataController.getSec10TB().getMainUpRail().getSlope();
//
//		return slope > 5 && slope != 100;
//	}
	
//	void updateStopEarn() {
//
//		if (Global.getNoOfContracts() > 0) {
//
//			if (getTimeBase().getSupport() > tempCutLoss) {
//				tempCutLoss = getTimeBase().getSupport();
//
//			}
//
//		} else if (Global.getNoOfContracts() < 0) {
//
//			if (getTimeBase().getResist() < tempCutLoss) {
//				tempCutLoss = getTimeBase().getResist();
//
//			}
//		}
//
//	}
}
