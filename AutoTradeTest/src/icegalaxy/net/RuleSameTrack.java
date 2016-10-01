package icegalaxy.net;

public class RuleSameTrack extends Rules {
	
	double buffer = 10;
	private double rsiFluctuation = 40;

	public RuleSameTrack(WaitAndNotify wan1, WaitAndNotify wan2,
			boolean globalRunRule) {
		super(wan1, wan2, globalRunRule);
		// TODO Auto-generated constructor stub
	}




	@Override
	public TimeBase getTimeBase() {
		return StockDataController.getShortTB();
	}

	@Override
	public void openContract() {
		
		if (shutdown)
			return;
		
		if (getTimeBase().isMARising(10,1) 
				&& getTimeBase().isMARising(20,1)
//				&& getTimeBase().getMA(10) > getTimeBase().getMA(20)
//				&& Global.getCurrentPoint() < getTimeBase().getMA(10) + buffer
//				&& getTimeBase().getMainUpRail().getRail() != 0
				&& Global.isOrderTime()
				&& getAGAL() > 6.5
				&& getTimeBase().isQuantityRising()
				&& getTimeBase().getHL(60).getRsiFluctuation() < rsiFluctuation )
			longContract();
		else if (getTimeBase().isMADropping(10,1) 
				&& getTimeBase().isMADropping(20,1) 
//				&& getTimeBase().getMA(10) < getTimeBase().getMA(20)
//				&& Global.getCurrentPoint() > getTimeBase().getMA(10) - buffer
//				&& getTimeBase().getMainDownRail().getRail() != 0
				&& Global.isOrderTime()
				&& getAGAL() > 6.5
				&& getTimeBase().isQuantityRising()
				&& getTimeBase().getHL(60).getRsiFluctuation() < rsiFluctuation)
			shortContract();

		
		wanPrevious.middleWaiter(wanNext);
	}


	@Override
	boolean trendReversed() {
		// TODO Auto-generated method stub
		return false;
	}
	
//	@Override
//	public void closeContract() {
//
//		if (Global.getNoOfContracts() > 0)
//			tempCutLoss = buyingPoint - 30;
//		else if (Global.getNoOfContracts() < 0)
//			tempCutLoss = buyingPoint + 30;
//
//		if (Global.getNoOfContracts() > 0) {
//			while (hasContract) {
//				if (getTimeBase().getMainUpRail().getRail() > tempCutLoss) {
//					tempCutLoss = (float) getTimeBase().getMainUpRail()
//							.getRail();
//					System.out.println("TempCutLoss: " + tempCutLoss);
//				}
//				
//				if (getTimeBase().getMA(20) > tempCutLoss)
//					tempCutLoss = getTimeBase().getMA(20);
//				
//				stopEarn();
//				if (Global.isForceSellTime())
//					closeContract("Force Sell");
//				wanPrevious.middleWaiter(wanNext);
//			}
//		} else if (Global.getNoOfContracts() < 0) {
//			while (hasContract) {
//				if (getTimeBase().getMainDownRail().getRail() != 0
//						&& getTimeBase().getMainDownRail().getRail() < tempCutLoss) {
//					tempCutLoss = (float) getTimeBase().getMainDownRail()
//							.getRail();
//					System.out.println("TempCutLoss: " + tempCutLoss);
//				}
//				
//				if (getTimeBase().getMA(20) < tempCutLoss)
//					tempCutLoss = getTimeBase().getMA(20);
//				
//				stopEarn();
//				if (Global.isForceSellTime())
//					closeContract("Force Sell");
//				wanPrevious.middleWaiter(wanNext);
//			}
//		}
//
//	}


}
