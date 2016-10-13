package icegalaxy.net;

//Use the OPEN Line

public class RuleTest2 extends Rules {

	private int lossTimes;
	// private double refEMA;
	private boolean firstCorner = false;
	private double cutLoss;

	public RuleTest2(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule) {
		super(wan1, wan2, globalRunRule);
		setOrderTime(93000, 100000, 160000, 160000, 231500, 231500);
		// wait for EMA6, that's why 0945
	}

	public void openContract() {

		if (shutdown) {
			lossTimes++;
			firstCorner = true;
			shutdown = false;
		}
		
		if (!isOrderTime() || Global.getNoOfContracts() != 0
				|| lossTimes >= 2)
			return;
		
//		if (firstCorner)
//			firstCorner();
//		
//		if (hasContract)
//			return;
			

		while (Global.getCurrentPoint() < Global.getAOL() -5 && Global.getCurrentPoint() > Global.getAOH() + 5)
			wanPrevious.middleWaiter(wanNext);
		
		while (Global.getCurrentPoint() > Global.getAOL() -10 && Global.getCurrentPoint() < Global.getAOH() + 10)
			wanPrevious.middleWaiter(wanNext);
		
		if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6)
				&& Global.getCurrentPoint() > Global.getAOH() + 10) 
			longContract();
		
		else 	if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6)
				&& Global.getCurrentPoint() < Global.getAOL() - 10) 
			shortContract();
			

		
		
//			while (StockDataController.getShortTB().getEMA(5) > StockDataController.getShortTB().getEMA(6))
//				wanPrevious.middleWaiter(wanNext);
//				
//			while (Global.getCurrentPoint() < getTimeBase().getLatestCandle().getHigh())
//				wanPrevious.middleWaiter(wanNext);
//			
//			if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6))
//				return;
//				
//				longContract();
//			
//		}
//			else if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6)
//					&& isDownTrend()){
//						
//				while (StockDataController.getShortTB().getEMA(5) < StockDataController.getShortTB().getEMA(6))
//					wanPrevious.middleWaiter(wanNext);
//					
//				while (Global.getCurrentPoint() > getTimeBase().getLatestCandle().getLow())
//					wanPrevious.middleWaiter(wanNext);
//				
//							shortContract();
//					}
	

	}

	// use 1min instead of 5min
	void updateStopEarn() {

		double ema5;
		double ema6;
		int difference;

		if (getProfit() > 100)
			difference = 0;
		else
			difference = 2;

		// if (Math.abs(getTimeBase().getEMA(5) - getTimeBase().getEMA(6)) <
		// 10){
//		ema5 = getTimeBase().getEMA(5);
//		ema6 = getTimeBase().getEMA(6);
		// }else{
		 ema5 = StockDataController.getShortTB().getEMA(5);
		 ema6 = StockDataController.getShortTB().getEMA(6);
		// }
		// use 1min TB will have more profit sometime, but will lose so many
		// times when ranging.

		if (Global.getNoOfContracts() > 0) {

//			if (buyingPoint > tempCutLoss && getProfit() > 50) {
//				Global.addLog("Free trade");
//				tempCutLoss = buyingPoint;
//			}
			if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6))
				tempCutLoss = 99999;
			
			
//			if (getProfit() > 50 - lossTimes * 10) {
//				tempCutLoss = 99999;
//				Global.addLog(className + " StopEarn: EMA5 < EMA6");
//			}
		} else if (Global.getNoOfContracts() < 0) {

//			if (buyingPoint < tempCutLoss && getProfit() > 50) {
//				Global.addLog("Free trade");
//				tempCutLoss = buyingPoint;
//			}
			if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6))
				tempCutLoss = 0;

//			if (getProfit() > 50 -  lossTimes * 10) {
//				tempCutLoss = 0;
//				Global.addLog(className + " StopEarn: EMA5 > EMA6");
//
//			}
		}

	}

	// use 1min instead of 5min
	double getCutLossPt() {

		// One time lost 100 at first trade >_< 20160929
		// if (Global.getNoOfContracts() > 0){
		// if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6))
		// return 1;
		// else
		// return 30;
		// }else{
		// if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6))
		// return 1;
		// else
		// return 30;
		// }

		return 15;

	}

	@Override
	protected void cutLoss() {

		if (Global.getNoOfContracts() > 0 && Global.getCurrentPoint() < tempCutLoss) {
			//
			// while (Global.getCurrentPoint() <
			// StockDataController.getShortTB().getEMA(5)){
			// wanPrevious.middleWaiter(wanNext);
			// if (getProfit() < -30)
			// break;
			// }
			//

			closeContract(className + ": CutLoss, short @ " + Global.getCurrentBid());
			shutdown = true;

			// wait for it to clam down

			// if (Global.getCurrentPoint() < getTimeBase().getEMA(6)){
			// Global.addLog(className + ": waiting for it to calm down");
			// }

			// while (Global.getCurrentPoint() < getTimeBase().getEMA(6))
			// wanPrevious.middleWaiter(wanNext);

		} else if (Global.getNoOfContracts() < 0 && Global.getCurrentPoint() > tempCutLoss) {
			//
			//
			// while (Global.getCurrentPoint() >
			// StockDataController.getShortTB().getEMA(5)){
			// wanPrevious.middleWaiter(wanNext);
			// if (getProfit() < -30)
			// break;
			// }

			closeContract(className + ": CutLoss, long @ " + Global.getCurrentAsk());
			shutdown = true;

			// if (Global.getCurrentPoint() > getTimeBase().getEMA(6)){
			// Global.addLog(className + ": waiting for it to calm down");
			// }
			//
			// while (Global.getCurrentPoint() > getTimeBase().getEMA(6))
			// wanPrevious.middleWaiter(wanNext);
		}
	}

	private void firstCorner() {

		if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6)) {
			// wait for a better position
			Global.addLog(className + ": waiting for the first corner");

			while (getTimeBase().getEMA(5) > getTimeBase().getEMA(6))
				wanPrevious.middleWaiter(wanNext);

			firstCorner = false;

		} else if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6)) {

			Global.addLog(className + ": waiting for the first corner");

			while (getTimeBase().getEMA(5) < getTimeBase().getEMA(6))
				wanPrevious.middleWaiter(wanNext);

			firstCorner = false;
		}
	}

	double getStopEarnPt() {
		if (Global.getNoOfContracts() > 0) {
			if (StockDataController.getShortTB().getEMA(5) > StockDataController.getShortTB().getEMA(6))
				return -100;
			return 30;
		} else if (Global.getNoOfContracts() < 0) {
			if (StockDataController.getShortTB().getEMA(5) < StockDataController.getShortTB().getEMA(6))
				return -100;
			return 30;
		}

		return -100;
	}

	@Override
	public TimeBase getTimeBase() {
		return StockDataController.getLongTB();
	}

}