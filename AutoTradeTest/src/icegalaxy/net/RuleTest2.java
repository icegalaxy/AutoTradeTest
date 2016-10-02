package icegalaxy.net;



//Use the OPEN Line

public class RuleTest2 extends Rules {

	private int lossTimes;
	// private double refEMA;
	private boolean tradeTimesReseted;
	

	public RuleTest2(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule) {
		 super(wan1, wan2, globalRunRule);
		 setOrderTime(92000, 113000, 130500, 160000);
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

		//use 1min TB will have more profit sometime, but will lose so many times when ranging.
		
			if(getTimeBase().getEMA(5) > getTimeBase().getEMA(6) + 2
					&& Global.getCurrentPoint() > getTimeBase().getEMA(5)
//					&& getTimeBase().isEMARising(5, 1)
//					&& StockDataController.getShortTB().getEMA(5) > StockDataController.getShortTB().getEMA(6)
					){
				
				//wait for a better position
				Global.addLog(className + ": waiting for a better position");
				
				while(Global.getCurrentPoint() > getTimeBase().getEMA(5)){
					wanPrevious.middleWaiter(wanNext);
					
//				
//					if(!getTimeBase().isEMARising(5, 1)){
//						Global.addLog(className + ": wrong trend");
//						return;
//					}
					
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
//					&& getTimeBase().isEMADropping(5, 1)
//					&&StockDataController.getShortTB().getEMA(5) < StockDataController.getShortTB().getEMA(6)
					){
				
				//wait for a better position
				Global.addLog(className + ": waiting for a better position");
				
				while(Global.getCurrentPoint() < getTimeBase().getEMA(5)){
					wanPrevious.middleWaiter(wanNext);
//					
//					if(!getTimeBase().isEMADropping(5, 1)){
//						Global.addLog(className + ": wrong trend");
//						return;
//					}
//					
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

		//use 1min TB will have more profit sometime, but will lose so many times when ranging.
		
		if (Global.getNoOfContracts() > 0) {
			if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6) - lossTimes) {
				tempCutLoss = 99999;
				Global.addLog(className + " StopEarn: EMA5 < EMA6");
			}
		} else if (Global.getNoOfContracts() < 0) {
			if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6) + lossTimes) {
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