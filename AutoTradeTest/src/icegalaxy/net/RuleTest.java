package icegalaxy.net;

public class RuleTest extends Rules {

	private int lossTimes;

	public RuleTest(WaitAndNotify wan1, WaitAndNotify wan2,
			boolean globalRunRule) {
		super(wan1, wan2, globalRunRule);
		setOrderTime(91500, 114500, 131500, 16000);

	}

	public void openContract() {

		if (shutdown) {
			lossTimes++;
			shutdown = false;
		}

//		System.out.println("EMA 5: " + getTimeBase().getEMA(10));

		// Reset the lossCount at afternoon because P.High P.Low is so important
//		if (isAfternoonTime() && !tradeTimesReseted) {
//			lossTimes = 0;
//			tradeTimesReseted = true;
//		}

		if (!isOrderTime() 
				|| lossTimes >= 2 
				|| Global.getNoOfContracts() != 0
//				|| !Global.isOpenDown()
				)
			return;

		if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6) + 2
				&& Global.getCurrentPoint() > getTimeBase().getEMA(5)
				){
			
			Global.addLog(className + ": Wait for a pull back");
			
			while (Global.getCurrentPoint() > getTimeBase().getEMA(5)){
				wanPrevious.middleWaiter(wanNext);
			}
			
			longContract();
			
		}else if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6) - 2
				&& Global.getCurrentPoint() < getTimeBase().getEMA(5)){
			
			Global.addLog(className + ": Wait for a pull back");
			
			while (Global.getCurrentPoint() < getTimeBase().getEMA(5)){
				wanPrevious.middleWaiter(wanNext);
			}
			
			shortContract();
			
		}
		
		
		

	}

	void updateStopEarn() {

		if (Global.getNoOfContracts() > 0) {

			tempCutLoss = 99999;

		} else if (Global.getNoOfContracts() < 0) {

			tempCutLoss = 0;

		}

	}
	
//	@Override
//	boolean trendReversed(){
//		if(Global.getNoOfContracts() > 0)
//			return getTimeBase().getEMA(5) <  getTimeBase().getEMA(10) + 5;
//		else if (Global.getNoOfContracts() < 0)
//			return getTimeBase().getEMA(5) > getTimeBase().getEMA(10) - 5;
//			
//		return false;
//		
//		
//	}

	double getCutLossPt() {
		return 15;
	}

	double getStopEarnPt() {
		return 15;
	}

	@Override
	public TimeBase getTimeBase() {
		// TODO Auto-generated method stub
		return StockDataController.getLongTB();
	}

}
