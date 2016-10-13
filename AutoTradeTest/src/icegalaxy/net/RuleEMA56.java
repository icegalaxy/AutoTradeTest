package icegalaxy.net;

import org.jfree.data.time.TimePeriod;

//Use the OPEN Line

public class RuleEMA56 extends Rules {

	private int lossTimes;
	// private double refEMA;
	private boolean firstCorner = true;
	private double cutLoss;

	public RuleEMA56(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule) {
		super(wan1, wan2, globalRunRule);
		setOrderTime(93000, 113000, 130500, 160000, 230000, 230000);
		// wait for EMA6, that's why 0945
	}

	public void openContract() {

		if (shutdown) {
			lossTimes++;
//			firstCorner = true;
			shutdown = false;
		}
		
		while (lossTimes > 0 && TimePeriodDecider.getTime() < 100000)
			wanPrevious.middleWaiter(wanNext);

		// Reset the lossCount at afternoon because P.High P.Low is so important
		// if (isAfternoonTime() && !tradeTimesReseted) {
		// lossTimes = 0;
		// tradeTimesReseted = true;
		// }

		if (!isOrderTime() || Global.getNoOfContracts() != 0 || Global.getpHigh() == 0
				|| lossTimes >= 3)
			return;

		if (firstCorner)
			firstCorner();

		if (hasContract)
			return;

		if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6) + 2
				&& Global.getCurrentPoint() > getTimeBase().getEMA(5)
		// && Math.abs(getTimeBase().getEMA(5) - getTimeBase().getEMA(6)) < 10
		// && getTimeBase().isEMARising(5, 1)
		// && StockDataController.getShortTB().getEMA(5) >
		// StockDataController.getShortTB().getEMA(6)
		) {

			// wait for a better position
			Global.addLog(className + ": waiting for a better position");
			refPt = Global.getCurrentPoint();

			while (Global.getCurrentPoint() > getTimeBase().getEMA(5) + 5) {
				wanPrevious.middleWaiter(wanNext);

				// Global.addLog("Current Pt: " + Global.getCurrentPoint() + " /
				// EMA: " + getTimeBase().getEMA(5) );
				//
				// if(!getTimeBase().isEMARising(5, 1)){
				// Global.addLog(className + ": wrong trend");
				// return;
				// }

				// if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6) + 5
				// && Global.getCurrentPoint() <
				// StockDataController.getShortTB().getEMA(5))
				// break;

//				if (Global.getCurrentPoint() > refPt + 50) {
//					Global.addLog(className + ": too far away");
//					firstCorner = true;
//					return;
//				}

				// difference becomes small may mean changing trend
				if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6)) {
					Global.addLog(className + ": trend change");
					return;
				}
			}

			Global.addLog(className + ": waiting for a second corner");
			refPt = Global.getCurrentPoint();

			while (Global.getCurrentPoint() < StockDataController.getShortTB().getLatestCandle().getHigh()){
				wanPrevious.middleWaiter(wanNext);
				
				if (Global.getCurrentPoint() < refPt)
					refPt = Global.getCurrentPoint();		
				
			}
			
			longContract();
			cutLoss = Math.abs(Global.getCurrentPoint() - refPt);
			Global.addLog("CutLossPt: " + cutLoss);
		} else if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6) - 2
				&& Global.getCurrentPoint() < getTimeBase().getEMA(5)
		// && Math.abs(getTimeBase().getEMA(5) - getTimeBase().getEMA(6)) < 10
		// && getTimeBase().isEMADropping(5, 1)
		// &&StockDataController.getShortTB().getEMA(5) <
		// StockDataController.getShortTB().getEMA(6)
		) {

			// wait for a better position
			Global.addLog(className + ": waiting for a better position");
			refPt = Global.getCurrentPoint();

			while (Global.getCurrentPoint() < getTimeBase().getEMA(5) - 5) {
				wanPrevious.middleWaiter(wanNext);
				//

				// if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6) - 5
				// && Global.getCurrentPoint() >
				// StockDataController.getShortTB().getEMA(5))
				// break;

				// if(!getTimeBase().isEMADropping(5, 1)){
				// Global.addLog(className + ": wrong trend");
				// return;
				// }
				//
//				if (Global.getCurrentPoint() < refPt - 50) {
//					Global.addLog(className + ": too far away");
//					firstCorner = true;
//					return;
//				}

				if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6)) {
					Global.addLog(className + ": trend change");
					return;
				}

			}

			Global.addLog(className + ": waiting for a second corner");
			refPt = Global.getCurrentPoint();
			
			while (Global.getCurrentPoint() > StockDataController.getShortTB().getLatestCandle().getLow()){
				wanPrevious.middleWaiter(wanNext);
				
				if (Global.getCurrentPoint() > refPt)
					refPt = Global.getCurrentPoint();		
			}
	
			shortContract();
			cutLoss = Math.abs(Global.getCurrentPoint() - refPt);
			Global.addLog("CutLossPt: " + cutLoss);
		}

	}

	private void firstCorner() {

		if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6)) {
			// wait for a better position
			Global.addLog(className + ": waiting for the first corner");

			while (getTimeBase().getEMA(5) > getTimeBase().getEMA(6))
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
	
			firstCorner = false;
			shortContract();
			cutLoss = Math.abs(Global.getCurrentPoint() - refPt);
			Global.addLog("CutLossPt: " + cutLoss);
			
		} else if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6)) {

			Global.addLog(className + ": waiting for the first corner");

			while (getTimeBase().getEMA(5) < getTimeBase().getEMA(6))
				wanPrevious.middleWaiter(wanNext);

			firstCorner = false;

			Global.addLog(className + ": waiting for a pull back");
			
			refPt = Global.getCurrentPoint();

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
			cutLoss = Math.abs(Global.getCurrentPoint() - refPt);
			Global.addLog("CutLossPt: " + cutLoss);
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
	
		if (lossTimes > 2){
			
			 ema5 = StockDataController.getShortTB().getEMA(5);
			 ema6 = StockDataController.getShortTB().getEMA(6);
			
		}else {
		// 10){
		ema5 = getTimeBase().getEMA(5);
		ema6 = getTimeBase().getEMA(6);
		}
		// }else{
		// ema5 = StockDataController.getShortTB().getEMA(5);
		// ema6 = StockDataController.getShortTB().getEMA(6);
		// }
		// use 1min TB will have more profit sometime, but will lose so many
		// times when ranging.

		if (Global.getNoOfContracts() > 0) {
			
//			if (buyingPoint > tempCutLoss){
//				
//				if (getProfit() > 50)
//					tempCutLoss = buyingPoint;
//			}
//			
			
			if (ema5 < ema6 - lossTimes) {
				tempCutLoss = 99999;
				Global.addLog(className + " StopEarn: EMA5 < EMA6");
			}
		} else if (Global.getNoOfContracts() < 0) {
			
//			if (buyingPoint < tempCutLoss){
//				
//				if (getProfit() > 50)
//					tempCutLoss = buyingPoint;
//			}
			
			
			if (ema5 > ema6 + lossTimes) {
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

	double getStopEarnPt() {
		return 30;
	}

	@Override
	public TimeBase getTimeBase() {
		return StockDataController.getLongTB();
	}

}