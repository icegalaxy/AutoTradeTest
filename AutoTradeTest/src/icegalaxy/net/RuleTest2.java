package icegalaxy.net;


//Use the OPEN Line

public class RuleTest2 extends Rules {

	private int lossTimes;
	// private double refEMA;
	private boolean tradeTimesReseted;
	private boolean firstCorner = true;
	

	public RuleTest2(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule) {
		 super(wan1, wan2, globalRunRule);
		 setOrderTime(91600, 113000, 130500, 160000);
		// wait for EMA6, that's why 0945
	}

	public void openContract() {

		if (shutdown) {
			lossTimes++;
			shutdown = false;
		}
		
		// Reset the lossCount at afternoon because P.High P.Low is so important
//				if (isAfternoonTime() && !tradeTimesReseted) {
//					lossTimes = 0;
//					tradeTimesReseted = true;
//				}
		

		if (!isOrderTime() 
				|| Global.getNoOfContracts() != 0
				|| Global.getpHigh() == 0)
			return;
		
	if (firstCorner){
			
			if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6)) {
				// wait for a better position
				Global.addLog(className + ": waiting for the first corner");

				while (getTimeBase().getEMA(5) > getTimeBase().getEMA(6) - 2) 
					wanPrevious.middleWaiter(wanNext);
					
				firstCorner = false;
				
				Global.addLog(className + ": waiting for a pull back");

				while (Global.getCurrentPoint() > getTimeBase().getEMA(5)) 
					wanPrevious.middleWaiter(wanNext);

				
			
				
					if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6) - 2) {
						Global.addLog(className + ": trend changed");
						return;
					
				}

				longContract();
			} else if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6)) {

				
				Global.addLog(className + ": waiting for the first corner");

				while (getTimeBase().getEMA(5) < getTimeBase().getEMA(6) + 2) 
					wanPrevious.middleWaiter(wanNext);
				
				firstCorner = false;
				
				// wait for a better position
				Global.addLog(className + ": waiting for a pull back");

				while (Global.getCurrentPoint() < getTimeBase().getEMA(5)) 
					wanPrevious.middleWaiter(wanNext);

					if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6) + 2 ) {
						Global.addLog(className + ": trend changed");
						return;
					}
				

				shortContract();
			}
			
			
		}
		
			if(getTimeBase().getEMA(5) > getTimeBase().getEMA(6) + 2
					&& Global.getCurrentPoint() > getTimeBase().getEMA(5)
//					&& Math.abs(getTimeBase().getEMA(5) - getTimeBase().getEMA(6)) < 10
//					&& getTimeBase().isEMARising(5, 1)
//					&& StockDataController.getShortTB().getEMA(5) > StockDataController.getShortTB().getEMA(6)
					){
				
				//wait for a better position
				Global.addLog(className + ": waiting for a better position");
				refPt = Global.getCurrentPoint();
				
				while(Global.getCurrentPoint() > getTimeBase().getEMA(5)){
					wanPrevious.middleWaiter(wanNext);
					
					
//					Global.addLog("Current Pt: " + Global.getCurrentPoint() + " / EMA: " + getTimeBase().getCurrentEMA(5) );
//				
//					if(!getTimeBase().isEMARising(5, 1)){
//						Global.addLog(className + ": wrong trend");
//						return;
//					}
					
//					if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6) + 5
//							&& Global.getCurrentPoint() < StockDataController.getShortTB().getEMA(5))
//						break;
					
					if (Global.getCurrentPoint() > refPt + 50){
						Global.addLog(className + ": too far away"); 
						firstCorner = true;
						return;
					}
					
//					difference becomes small may mean changing trend
					if ( getTimeBase().getEMA(5) < getTimeBase().getEMA(6) + 2){
						Global.addLog(className + ": trend change");
						return;
					}
				}
							
				longContract();
			}
			else if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6) - 2
					&& Global.getCurrentPoint() < getTimeBase().getEMA(5)
//					&& Math.abs(getTimeBase().getEMA(5) - getTimeBase().getEMA(6)) < 10
//					&& getTimeBase().isEMADropping(5, 1)
//					&&StockDataController.getShortTB().getEMA(5) < StockDataController.getShortTB().getEMA(6)
					){
				
				//wait for a better position
				Global.addLog(className + ": waiting for a better position");
				refPt = Global.getCurrentPoint();
				
				while(Global.getCurrentPoint() < getTimeBase().getEMA(5)){
					wanPrevious.middleWaiter(wanNext);
//					
					
//					if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6) - 5
//							&& Global.getCurrentPoint() > StockDataController.getShortTB().getEMA(5))
//						break;
					
//					if(!getTimeBase().isEMADropping(5, 1)){
//						Global.addLog(className + ": wrong trend");
//						return;
//					}
//					
					if (Global.getCurrentPoint() < refPt - 50){
						Global.addLog(className + ": too far away"); 
						firstCorner = true;
						return;
					}
					
					if ( getTimeBase().getEMA(5) > getTimeBase().getEMA(6) - 2){
						Global.addLog(className + ": trend change");
						return;
					}
					
				}
				
				
				
				shortContract();
			}
		
	}

	//use 1min instead of 5min
	void updateStopEarn() {

		
		double ema5;
		double ema6;
		int difference;
		
		if (getProfit() > 100)
			difference = 0;
		else
			difference = 2;
		
//		if (Math.abs(getTimeBase().getEMA(5) - getTimeBase().getEMA(6)) < 10){
			ema5 = getTimeBase().getEMA(5);
			ema6 = getTimeBase().getEMA(6);
//		}else{
//			ema5 = StockDataController.getShortTB().getEMA(5);
//			ema6 = StockDataController.getShortTB().getEMA(6);			
//		}
		//use 1min TB will have more profit sometime, but will lose so many times when ranging.
	
		if (Global.getNoOfContracts() > 0) {
			if (ema5 < ema6 - lossTimes) {
				tempCutLoss = 99999;
				Global.addLog(className + " StopEarn: EMA5 < EMA6");
			}
		} else if (Global.getNoOfContracts() < 0) {
			if (ema5 > ema6  + lossTimes) {
				tempCutLoss = 0;
				Global.addLog(className + " StopEarn: EMA5 > EMA6");

			}
		}

	}
	
	//use 1min instead of 5min
	double getCutLossPt() {
		
		//One time lost 100 at first trade >_< 20160929
//		if (Global.getNoOfContracts() > 0){
//			if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6))
//				return 1;
//			else
//				return 30;
//		}else{
//			if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6))
//				return 1;
//			else
//				return 30;
//		}
		
		return 30;

	}
	
	@Override
	protected void cutLoss() {
		

		if (Global.getNoOfContracts() > 0 && Global.getCurrentPoint() < tempCutLoss) {
			closeContract(className + ": CutLoss, short @ " + Global.getCurrentBid());
			shutdown = true;
			
			//wait for it to clam down
			
//			if (Global.getCurrentPoint() < getTimeBase().getEMA(6)){
//				Global.addLog(className + ": waiting for it to calm down");
//			}
			
//			while (Global.getCurrentPoint() < getTimeBase().getEMA(6))
//				wanPrevious.middleWaiter(wanNext);
			
		} else if (Global.getNoOfContracts() < 0 && Global.getCurrentPoint()  > tempCutLoss) {
			closeContract(className + ": CutLoss, long @ " + Global.getCurrentAsk());
			shutdown = true;
			
//			if (Global.getCurrentPoint() > getTimeBase().getEMA(6)){
//				Global.addLog(className + ": waiting for it to calm down");
//			}
//			
//			while (Global.getCurrentPoint() > getTimeBase().getEMA(6))
//				wanPrevious.middleWaiter(wanNext);
		}
	}

	double getStopEarnPt() {
		return -100;
	}

	@Override
	public TimeBase getTimeBase() {
		return StockDataController.getLongTB();
	}

}