package icegalaxy.net;

public class ruleMACD extends Rules {

	private double fluctuation = 100;
	private float refPt = 0;
	
	public ruleMACD(WaitAndNotify wan1, WaitAndNotify wan2,
			boolean globalRunRule) {
		super(wan1, wan2, globalRunRule);
		// TODO Auto-generated constructor stub
	}

	public void openContract() {

//		Global.addLog("MACD: " + getTimeBase().getMACD());
		
		if (isUpTrend()) {

			refPt = Global.getCurrentPoint();
			
//			Global.addLog("MACD: " + getTimeBase().getMACD());
			
			while (true 
//					&& refPt+20 > Global.getCurrentPoint()
					){
				
//				if (Global.getCurrentPoint() >= refPt)
//					refPt = Global.getCurrentPoint();
				
				if (!Global.isOrderTime()
						|| isDropping())
					break;
				
				wanPrevious.middleWaiter(wanNext);
			}

			while (!getTimeBase().isOutsideDay()){
							
				if (!Global.isOrderTime())
					break;
				
				if (!isDropping())
					return;
				
				wanPrevious.middleWaiter(wanNext);
			}

//			Global.addLog("MACD2: " + getTimeBase().getMACD());
			
			
			if (!Global.isOrderTime()
					|| !isUpTrend()	
//					|| isRising()
//					|| Global.getCurrentPoint() > refPt
					)
				return;

			
			longContract();

		} else if (isDownTrend()) {

			refPt = Global.getCurrentPoint();
			
//			Global.addLog("MACD: " + getTimeBase().getMACD());
			
			
			while (true
//					&&refPt-20 < Global.getCurrentPoint()
					){
				
//				if (Global.getCurrentPoint() <= refPt)
//					refPt = Global.getCurrentPoint();
				
				if (!Global.isOrderTime()
						|| isRising())
					break;
				
				wanPrevious.middleWaiter(wanNext);
			}

			while (!getTimeBase().isOutsideDay()){
							
				if (!Global.isOrderTime())
					break;
				
				if (!isRising())
					return;
				
				wanPrevious.middleWaiter(wanNext);
			}
			
//			Global.addLog("MACD2: " + getTimeBase().getMACD());
			

			if (!Global.isOrderTime()
					|| !isDownTrend()
//					|| isDropping()
//					|| Global.getCurrentPoint() < refPt
					)
				return;
		
			shortContract();

		}

	}
	
	
//	boolean isUpTrend(){
//		return getTimeBase().getMACDHistogram() > 0 && getTimeBase().getMACD() > 0;
//	}
//	
//	boolean isDownTrend(){
//		return getTimeBase().getMACDHistogram() < 0 && getTimeBase().getMACD() < 0;
//	}

	boolean isRising(){
		return getTimeBase().getMA(5) > getTimeBase().getMA(10);
	}
	
	boolean isDropping(){
		return getTimeBase().getMA(5) < getTimeBase().getMA(10);
	}
	
	
	double getCutLossPt() {
		return 50;
	}

	double getStopEarnPt() {
		return 20;
	}
	
//	@Override
//	void updateStopEarn() {
//
//		if (Global.getNoOfContracts() > 0) {
//			
//			if (getTimeBase().getRSI() > 70){
//				usingMA20 = false;
//				usingMA10 = false;
////				usingMA5 = false;
//			}
//			
//
//			if (getTimeBase().getMA(20) > tempCutLoss && usingMA20) {
//				if (getTimeBase().getMA(10) > tempCutLoss)
//					tempCutLoss = getTimeBase().getMA(20);
//
//			} else if (getTimeBase().getMA(10) > tempCutLoss && usingMA10) {
//				if (getTimeBase().getMA(10) > tempCutLoss) {
//					tempCutLoss = getTimeBase().getMA(10);
//					usingMA20 = false;
//				}
//
//			} else if (getTimeBase().getMA(5) > tempCutLoss && usingMA5) {
//				if (getTimeBase().getMA(10) > tempCutLoss) {
//					tempCutLoss = getTimeBase().getMA(5);
//					usingMA20 = false;
//					usingMA10 = false;
//				}
//
//			} else if (getTimeBase().getMA(1) > tempCutLoss) {
//				tempCutLoss = getTimeBase().getMA(1);
//				usingMA20 = false;
//				usingMA10 = false;
//				usingMA5 = false;
//			}
//
//		} else if (Global.getNoOfContracts() < 0) {
//			
//			if (getTimeBase().getRSI() < 30){
//				usingMA20 = false;
//				usingMA10 = false;
////				usingMA5 = false;
//			}
//
//			if (getTimeBase().getMA(20) < tempCutLoss && usingMA20) {
//				if (getTimeBase().getMA(10) < tempCutLoss)
//					tempCutLoss = getTimeBase().getMA(20);
//
//			} else if (getTimeBase().getMA(10) < tempCutLoss && usingMA10) {
//				if (getTimeBase().getMA(10) < tempCutLoss) {
//					tempCutLoss = getTimeBase().getMA(10);
//					usingMA20 = false;
//				}
//			} else if (getTimeBase().getMA(5) < tempCutLoss && usingMA5) {
//				if (getTimeBase().getMA(10) < tempCutLoss) {
//					tempCutLoss = getTimeBase().getMA(5);
//					usingMA20 = false;
//					usingMA10 = false;
//				}
//
//			} else if (getTimeBase().getMA(1) < tempCutLoss){
//				tempCutLoss = getTimeBase().getMA(1);
//					usingMA20 = false;
//					usingMA10 = false;
//					usingMA5 = false;
//				}
//		}
//
//	}
	
	@Override
	boolean trendReversed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TimeBase getTimeBase() {
		return StockDataController.getM15TB();
	}



}