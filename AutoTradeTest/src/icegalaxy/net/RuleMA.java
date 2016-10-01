package icegalaxy.net;

public class RuleMA extends Rules {

	private double refPt;
	private float bufferPt = 5;
	private int maRetain = 15;
	private double fluctuation = 100;
	private double rsiFluctuation = 40;
	private double profit = 50;
	
	private double cutLoss;
	
	protected double referencePoint;

	private boolean tempHL;

	public RuleMA(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule) {
		super(wan1, wan2, globalRunRule);
		
		// TODO Auto-generated constructor stub
	}

	public void setBufferPt(float f) {
		bufferPt = f;
	}

	public void openContract() {
		
		if (shutdown)
			return;

		if (getTimeBase().getMA(20) == -1)
			return;

		if (getTimeBase().getMA(10) - bufferPt < getTimeBase().getMA(20)) {

			// if (getTimeBase().getMARetain() < 5)
			// return;

			while (getTimeBase().getMA(10) - bufferPt < getTimeBase().getMA(20) // 穿五點先算穿
					// 呢句加左係唔同的，加左難買d
			) {
				if (!Global.isOrderTime())
					return;
				wanPrevious.middleWaiter(wanNext);
			}

			// while (!getTimeBase().isQuantityRising()) {
			//
			// if (!Global.isOrderTime() || getTimeBase().getMA(10) <
			// getTimeBase().getMA(20))
			// return;
			//
			// wanPrevious.middleWaiter(wanNext);
			// }

			if (!Global.isOrderTime()
//					|| Global.getCurrentPoint() < getTimeBase().getMA(20)
					|| getTimeBase().getRSI() > 70
//					|| Global.getCurrentPoint() < getTimeBase().getResist()
//					|| getTimeBase().getAGAL(1) < getTimeBase().getAGAL(60) * 0.9// getALAG超緊要
//					|| getTimeBase().getAGAL(1) > getTimeBase().getAGAL(60) * 1.4
					// || getAGAL() < 6.5
//					|| !getTimeBase().isMARising(10, 1)
//					|| !getTimeBase().isMARising(5, 1)
//					|| getTimeBase().isMADropping(1, 1)
//					|| getTimeBase().isMADropping(20, 1)
//					|| !getTimeBase().isQuantityRising()
					// || !isGreatQuantiy()
					// || getTimeBase().getQuatityByPeriods(5) < 1000
					// || getTimeBase().getHL(5).getTempLow() !=
					// getTimeBase().getHL(30).getTempLow()
//					|| getTimeBase().getHL(60).getFluctuation() < fluctuation
//					|| getTimeBase().getHL(60).getRsiFluctuation() > rsiFluctuation
					) {
				return;
			}

			shortContract();
			cutLoss = getTimeBase().getStandD();
			referencePoint = buyingPoint;
			System.out.println("Buying Pt: " + buyingPoint);

		} else if (getTimeBase().getMA(10) + bufferPt > getTimeBase().getMA(20)) {

			// if (getTimeBase().getMARetain() < 5)
			// return;

			while (getTimeBase().getMA(10) + bufferPt > getTimeBase().getMA(20)
					) {
				if (!Global.isOrderTime())
					return;
				wanPrevious.middleWaiter(wanNext);
			}

			// while (!getTimeBase().isQuantityRising()) {
			//
			// if (!Global.isOrderTime() ||
			// getTimeBase().getMA(10) > getTimeBase().getMA(20))
			//
			// return;
			//
			// wanPrevious.middleWaiter(wanNext);
			// }

			if (!Global.isOrderTime()
//					|| Global.getCurrentPoint() > getTimeBase().getMA(20)
					|| getTimeBase().getRSI() < 30
//					|| Global.getCurrentPoint() > getTimeBase().getSupport()
//					|| getTimeBase().getAGAL(1) < getTimeBase().getAGAL(60) * 0.9
//					|| getTimeBase().getAGAL(1) > getTimeBase().getAGAL(60) * 1.4
					// || getAGAL() < 6.5
//					|| !getTimeBase().isMADropping(10, 1)
//					|| !getTimeBase().isMADropping(5, 1)
//					|| getTimeBase().isMARising(1, 1)
//					|| getTimeBase().isMARising(20, 1)
//					|| !getTimeBase().isQuantityRising()
					// || !isGreatQuantiy()
					// || getTimeBase().getQuatityByPeriods(5) < 1000
					// || getTimeBase().getHL(5).getTempHigh() !=
					// getTimeBase().getHL(30).getTempHigh()
//					|| getTimeBase().getHL(60).getFluctuation() < fluctuation
//					|| getTimeBase().getHL(60).getRsiFluctuation() > rsiFluctuation
			// // rsi
			) {
				return;
			}

			// while (StockDataController.getSec10TB().isMARising(1)) {
			// if (getTimeBase().getMA(10) > getTimeBase().getMA(20))
			// return;
			//
			// wanPrevious.middleWaiter(wanNext);
			// }
			//
			//
			// if (!Global.isOrderTime()
			// || getTimeBase().isMARising(20)
			// || Global.getCurrentPoint() < refPt
			// )
			// return;

			longContract();
			cutLoss = getTimeBase().getStandD();
			referencePoint = buyingPoint;
			System.out.println("Buying Pt: " + buyingPoint);
		}

	}
	
	double getCutLossPt() {
		// if (Global.getNoOfContracts() > 0)
		// return maxDrop()/4;
		// else if (Global.getNoOfContracts() < 0)
		// return maxRise()/4;

		// if (getTimeBase().getRSI() > 70 || getTimeBase().getRSI() < 30)
		// return 100;

		if (cutLoss < 10)
			return 10;
		else
			return cutLoss;
	}

	double getStopEarnPt() {
		return cutLoss;
	}
	
//	@Override
//	protected void cutLoss() {
//		
//			if (Global.getNoOfContracts() > 0){
//				if (Global.balance + Global.getCurrentPoint() <= -20){
//			
////					Global.addLog("Balance: " + (Global.balance + Global.getCurrentPoint()));
//			closeContract("CutLoss " + className);
//			shutdown = true;
//				}
//				}else if (Global.getNoOfContracts() < 0){
//					if (Global.balance - Global.getCurrentPoint() <= -20){
////						Global.addLog("Balance: " + (Global.balance - Global.getCurrentPoint()));
//						closeContract("CutLoss " + className);
//						shutdown = true;
//					}
//				}
//
//		
//	}

	// @Override
	// public void closeContract() {
	//
	// if (Global.getNoOfContracts() > 0)
	// tempCutLoss = buyingPoint - 30;
	// else if (Global.getNoOfContracts() < 0)
	// tempCutLoss = buyingPoint + 30;
	//
	// if (Global.getNoOfContracts() > 0) {
	// while (hasContract) {
	// if (getTimeBase().getMainUpRail().getRail() > tempCutLoss) {
	// tempCutLoss = (float) getTimeBase().getMainUpRail()
	// .getRail();
	// System.out.println("TempCutLoss: " + tempCutLoss);
	// }
	//
	// if (getTimeBase().getMA(10) > tempCutLoss)
	// tempCutLoss = getTimeBase().getMA(10);
	//
	// stopEarn();
	// if (Global.isForceSellTime())
	// closeContract("Force Sell");
	// wanPrevious.middleWaiter(wanNext);
	// }
	// } else if (Global.getNoOfContracts() < 0) {
	// while (hasContract) {
	// if (getTimeBase().getMainDownRail().getRail() != 0
	// && getTimeBase().getMainDownRail().getRail() < tempCutLoss) {
	// tempCutLoss = (float) getTimeBase().getMainDownRail()
	// .getRail();
	// System.out.println("TempCutLoss: " + tempCutLoss);
	// }
	//
	// if (getTimeBase().getMA(10) < tempCutLoss)
	// tempCutLoss = getTimeBase().getMA(10);
	//
	// stopEarn();
	// if (Global.isForceSellTime())
	// closeContract("Force Sell");
	// wanPrevious.middleWaiter(wanNext);
	// }
	// }
	//
	// }

	// protected void updateCutLoss() {
	// if (Global.getNoOfContracts() > 0) {
	// if (getTimeBase().getLatestCandle().getLow() - tempCutLoss > cutLossPt) {
	// tempCutLoss = (float) (getTimeBase().getLatestCandle().getLow() -
	// cutLossPt);
	// System.out.println("TempCutLoss: " + tempCutLoss);
	// }
	//
	// } else if (Global.getNoOfContracts() < 0) {
	// if (tempCutLoss - getTimeBase().getLatestCandle().getLow() > cutLossPt) {
	// tempCutLoss = (float) (getTimeBase().getLatestCandle().getLow() +
	// cutLossPt);
	// System.out.println("TempCutLoss: " + tempCutLoss);
	// }
	// }
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
	// tempCutLoss = (float) getTimeBase().getMainUpRail().getRail();
	//
	// } else if (Global.getNoOfContracts() < 0) {
	//
	// if (ma10 < tempCutLoss && Global.getCurrentPoint() > ma10)
	// tempCutLoss = ma10;
	// if (getTimeBase().getMainDownRail().getRail() != 0
	// && getTimeBase().getMainDownRail().getRail() < tempCutLoss)
	// tempCutLoss = (float) getTimeBase().getMainDownRail().getRail();
	// }
	// }

//	@Override
//	boolean trendUnstable() {
//		if (Global.getNoOfContracts() > 0) {
//			return getTimeBase().isMADropping(10)
//					&& Global.getCurrentPoint() > buyingPoint;
//		} else if (Global.getNoOfContracts() < 0)
//			return getTimeBase().isMARising(10)
//					&& Global.getCurrentPoint() < buyingPoint;
//		return false;
//	}

//	@Override
//	boolean trendReversed2() {
//		double slope = 0;
//		double longSlope = 0;
//
//		if (Global.getNoOfContracts() > 0) {
//			if (StockDataController.getSec10TB().getMainDownRail().getSlope() != 100)
//				slope = StockDataController.getSec10TB().getMainDownRail()
//						.getSlope();
//
//			if (getTimeBase().getMainUpRail().getSlope() != 100)
//				longSlope = getTimeBase().getMainUpRail().getSlope();
//
//		}
//		if (Global.getNoOfContracts() < 0) {
//
//			if (StockDataController.getSec10TB().getMainUpRail().getSlope() != 100)
//				slope = StockDataController.getSec10TB().getMainUpRail()
//						.getSlope();
//
//			if (getTimeBase().getMainDownRail().getSlope() != 100)
//				longSlope = getTimeBase().getMainDownRail().getSlope();
//		}
//		return slope > longSlope * 2;
//	}

//	@Override
//	boolean trendReversed() {
//		if (Global.getNoOfContracts() > 0) {
//			return Global.getCurrentPoint() < getTimeBase().getSupport() - 10;
//		} else if (Global.getNoOfContracts() < 0)
//			return Global.getCurrentPoint() > getTimeBase().getResist() + 10;
//		return false;
//	}
	
//	@Override
//	boolean trendReversed() {
//		if (Global.getNoOfContracts() > 0) {
//			return getTimeBase().getMA(10) - bufferPt < getTimeBase().getMA(20);
//		} else if (Global.getNoOfContracts() < 0)
//			return getTimeBase().getMA(10) + bufferPt > getTimeBase().getMA(20);
//		return false;
//	}

	@Override
	public TimeBase getTimeBase() {
		return StockDataController.getShortTB();
	}

	// @Override
	// protected boolean reachGreatProfitPt() {
	//
	// if (getStopEarnPt() < stopEarnPt)
	// stopEarnPt = getStopEarnPt();
	//
	// if (Global.getNoOfContracts() > 0) {
	// if (Global.getCurrentPoint() - stopEarnPt > buyingPoint)
	// return true;
	//
	// if (Global.getCurrentPoint() > getTimeBase().getHL(15)
	// .getTempHigh() && tempHL) {
	// tempHL = false;
	// return true;
	// }
	// return false;
	// } else if (Global.getNoOfContracts() < 0) {
	// if (Global.getCurrentPoint() + stopEarnPt < buyingPoint)
	// return true;
	//
	// if (Global.getCurrentPoint() < getTimeBase().getHL(15).getTempLow()
	// && tempHL) {
	// tempHL = false;
	// return true;
	// }
	//
	// return false;
	// }
	// return false;
	// }

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

	
//	protected boolean reachGreatProfitPt() {
//
////		if (getStopEarnPt() < stopEarnPt)
////			stopEarnPt = getStopEarnPt();
//
//		if (Global.getNoOfContracts() > 0){
//			
//			if (Global.getCurrentPoint() < referencePoint)
//				referencePoint = Global.getCurrentPoint();
//			
//			return Global.getCurrentPoint() - 50 > referencePoint; 
//		}
//		
//		else if (Global.getNoOfContracts() < 0){
//			
//			if (Global.getCurrentPoint() > referencePoint)
//				referencePoint = Global.getCurrentPoint();
//			
//			return Global.getCurrentPoint() + 50 < referencePoint;
//			
//		}
//		else
//			return false;
//	}
}