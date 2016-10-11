package icegalaxy.net;

//Use the OPEN Line

public class RulePHigh extends Rules {

	private int lossTimes;
	// private double refEMA;
	private boolean firstCorner = true;
	private double cutLoss;

	public RulePHigh(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule) {
		super(wan1, wan2, globalRunRule);
		setOrderTime(93000, 113000, 130500, 160000, 172000, 231500);
		// wait for EMA6, that's why 0945
	}

	public void openContract() {

		if (shutdown) {
			lossTimes++;
			shutdown = false;
		}
		
		if (!isOrderTime() || Global.getNoOfContracts() != 0
				)
			return;

		if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6)
				&& Global.getCurrentPoint() > Global.getpHigh() + 5){
			
			
			// pull back
			while (Global.getCurrentPoint() > Global.getpHigh() + 5){
				wanPrevious.middleWaiter(wanNext);
			}
			
			Global.addLog(className + ": waiting for a pull back");

			while (Global.getCurrentPoint() > StockDataController.getShortTB().getLatestCandle().getLow()){
				wanPrevious.middleWaiter(wanNext);			
			}
			
			refPt = Global.getCurrentPoint();
			Global.addLog(className + ": waiting for a second corner");
			
			while (Global.getCurrentPoint() < StockDataController.getShortTB().getLatestCandle().getHigh()){
				wanPrevious.middleWaiter(wanNext);
				if (Global.getCurrentPoint() < refPt)
					refPt = Global.getCurrentPoint();		
				
			}
			
			longContract();
			cutLoss = Math.abs(buyingPoint - refPt);
			
		}else if (isDownTrend()
				&& Global.getCurrentPoint() < Global.getpHigh() - 5
				&& getTimeBase().getRSI() < 70){
			
			while (Global.getCurrentPoint() < Global.getpHigh() - 5)
				wanPrevious.middleWaiter(wanNext);
			
			Global.addLog(className + ": waiting for a pull back");

			while (Global.getCurrentPoint() < StockDataController.getShortTB().getLatestCandle().getHigh()){
				wanPrevious.middleWaiter(wanNext);
			}
				refPt = Global.getCurrentPoint();	
				
			Global.addLog(className + ": waiting for a second corner");
			
			while (Global.getCurrentPoint() > StockDataController.getShortTB().getLatestCandle().getLow()){
				wanPrevious.middleWaiter(wanNext);
				
				if (Global.getCurrentPoint() > refPt)
					refPt = Global.getCurrentPoint();		
			}
			
			shortContract();
			cutLoss = Math.abs(buyingPoint - refPt);
		}
		
		
		
		
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
		ema5 = getTimeBase().getEMA(5);
		ema6 = getTimeBase().getEMA(6);
		// }else{
		// ema5 = StockDataController.getShortTB().getEMA(5);
		// ema6 = StockDataController.getShortTB().getEMA(6);
		// }
		// use 1min TB will have more profit sometime, but will lose so many
		// times when ranging.

		if (Global.getNoOfContracts() > 0) {
			
			if (buyingPoint > tempCutLoss && getProfit() > 30){
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint;
			}
			
			
			if (ema5 < ema6) {
				tempCutLoss = 99999;
				Global.addLog(className + " StopEarn: EMA5 < EMA6");
			}
		} else if (Global.getNoOfContracts() < 0) {
			
			if (buyingPoint < tempCutLoss && getProfit() > 30){
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint;
			}
			
			
			if (ema5 > ema6) {
				tempCutLoss = 0;
				Global.addLog(className + " StopEarn: EMA5 > EMA6");

			}
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

		return cutLoss;

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

	double getStopEarnPt() {
		if (Global.getNoOfContracts() > 0){
			if (getTimeBase().getEMA(5) >  getTimeBase().getEMA(6))
				return -100;
		}else if (Global.getNoOfContracts() < 0){
			if (getTimeBase().getEMA(5) <  getTimeBase().getEMA(6))
				return -100;
		}
		
		return 100;
	}

	@Override
	public TimeBase getTimeBase() {
		return StockDataController.getLongTB();
	}

}