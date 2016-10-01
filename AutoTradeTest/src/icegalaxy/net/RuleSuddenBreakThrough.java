package icegalaxy.net;

public class RuleSuddenBreakThrough extends Rules {

	// 唔應該睇成交，因為成交每一百先郁

	private double fluctuation = 100;

	public RuleSuddenBreakThrough(WaitAndNotify wan1, WaitAndNotify wan2,
			boolean globalRunRule) {
		super(wan1, wan2, globalRunRule);
	}

	public RuleSuddenBreakThrough(WaitAndNotify wan1, WaitAndNotify wan2) {
		super(wan1, wan2);
		// TODO Auto-generated constructor stub
	}

	@Override
	public TimeBase getTimeBase() {
		return StockDataController.getShortTB();
	}

	@Override
	public void openContract() {
		
		if(!Global.isOrderTime()
				|| shutdown)
			return;

		if (isSideWay()) {

			if (!isSideWay()) {

				if (Global.getCurrentPoint() > getTimeBase().getHL(30)
						.getTempLow() + 15)
					longContract();
				else if (Global.getCurrentPoint() < getTimeBase().getHL(30)
						.getTempLow() - 15)
					shortContract();

			}

		}

		wanPrevious.middleWaiter(wanNext);
	}

	private boolean isSideWay() {
		return getTimeBase().getHL(30).getFluctuation() < 50;
		// && !maRising(5)
		// && !maDropping(5)
		// && !emaRising(20) && !emaDropping(20)
		// && !emaRising(10) && !emaDropping(10)
		
	}

	private boolean isSamllFluctutaion() {
		return getTimeBase().getHL(60).getFluctuation() < 100;
	}

	boolean maRising(int period) {

		float points = getAGAL() / 10;

		return getTimeBase().isMARising(period, 1);
	}

	boolean maDropping(int period) {
		return getTimeBase().isMADropping(period, 1);
	}

	boolean isLargeAverageHL() {
		try {
			return getTimeBase().getAverageHL(15) > 10;
		} catch (NotEnoughPeriodException e) {
			return false;
		}
	}

	// @Override
	// public void closeContract() {
	//
	// tempCutLoss = getTimeBase().getMA(1);
	//
	// while (hasContract && !Global.isForceSellTime()) {
	//
	// //updateCutLoss();
	//
	// if (Global.getNoOfContracts() > 0){
	// if (getTimeBase().getMA(1) < tempCutLoss)
	// closeContract(className + ": Close Contract");
	// else tempCutLoss = getTimeBase().getMA(1);
	// }else if (Global.getNoOfContracts() < 0){
	// if (getTimeBase().getMA(1) > tempCutLoss)
	// closeContract(className + ": Close Contract");
	// else tempCutLoss = getTimeBase().getMA(1);
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
	// wanPrevious.middleWaiter(wanNext);
	// }
	//
	// closeContract(className + ": Force Sell");
	// }

	// @Override
	// protected void updateCutLoss() {
	//
	// if (Global.getNoOfContracts() > 0) {
	// if (getTimeBase().getMA(1) > tempCutLoss) {
	//
	// tempCutLoss = getTimeBase().getMA(1);
	// System.out.println("TempCutLoss: " + getTimeBase().getMA(1));
	// }
	//
	// } else if (Global.getNoOfContracts() < 0) {
	// if (getTimeBase().getMA(1) < tempCutLoss) {
	// tempCutLoss = getTimeBase().getMA(1);
	// System.out.println("TempCutLoss: " + getTimeBase().getMA(1));
	// }
	// }
	// }

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

//	@Override
//	boolean trendReversed() {
//
//		// if (isSamllFluctutaion())
//		// return false;
//		double slope = 0;
//
//		if (Global.getNoOfContracts() > 0) {
//			return maDropping(20)
//					&& Global.getCurrentPoint() < buyingPoint - 20;
//		} else if (Global.getNoOfContracts() < 0)
//			return maRising(20) && Global.getCurrentPoint() > buyingPoint + 20;
//		return false;
//	}

	// @Override
	// boolean trendReversed2() {
	// if (Global.getNoOfContracts() > 0) {
	// return getTimeBase().isMADropping(10);
	// } else if (Global.getNoOfContracts() < 0)
	// return getTimeBase().isMARising(10);
	// return false;
	// }

//	@Override
//	void updateStopEarn() {
//
//		float ma = getTimeBase().getMA(10);
//
//		if (Global.getNoOfContracts() > 0) {
//
//			if (ma > tempCutLoss && Global.getCurrentPoint() > ma)
//				tempCutLoss = ma;
//			if (getTimeBase().getMainUpRail().getRail() > tempCutLoss)
//				tempCutLoss = getTimeBase().getMainUpRail().getRail();
//
//		} else if (Global.getNoOfContracts() < 0) {
//
//			if (ma < tempCutLoss && Global.getCurrentPoint() < ma)
//				tempCutLoss = ma;
//			if (getTimeBase().getMainDownRail().getRail() != 0
//					&& getTimeBase().getMainDownRail().getRail() < tempCutLoss)
//				tempCutLoss = getTimeBase().getMainDownRail().getRail();
//		}
//	}

	@Override
	protected void cutLoss() {
		
			if (Global.getNoOfContracts() > 0){
				if (Global.balance + Global.getCurrentPoint() <= -20){
			
//					Global.addLog("Balance: " + (Global.balance + Global.getCurrentPoint()));
			closeContract("CutLoss " + className);
			shutdown = true;
				}
				}else if (Global.getNoOfContracts() < 0){
					if (Global.balance - Global.getCurrentPoint() <= -20){
//						Global.addLog("Balance: " + (Global.balance - Global.getCurrentPoint()));
						closeContract("CutLoss " + className);
						shutdown = true;
					}
				}

		
	}
	
}
