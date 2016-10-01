package icegalaxy.net;

public class RuleEMA extends Rules {
	
	private double refPt;
	private float bufferPt = 5;
	private int maRetain = 15;
	private double fluctuation = 100;
	private double rsiFluctuation = 40;
	private double profit = 15;

	public RuleEMA(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule) {
		super(wan1, wan2, globalRunRule);
		// TODO Auto-generated constructor stub
	}

	public void setBufferPt(float f) {
		bufferPt = f;
	}
	
	
	@Override
	boolean trendReversed() {
		if (Global.getNoOfContracts() > 0){
			return getTimeBase().isMADropping(20,1);
		}else if (Global.getNoOfContracts() < 0)
			return getTimeBase().isMARising(20,1);
		return false;
	}

	@Override
	void updateStopEarn() {

		float ma10 = getTimeBase().getMA(10);

		if (Global.getNoOfContracts() > 0) {

			if (ma10 > tempCutLoss && Global.getCurrentPoint() > ma10)
				tempCutLoss = ma10;
			if (getTimeBase().getMainUpRail().getRail() > tempCutLoss)
				tempCutLoss = getTimeBase().getMainUpRail().getRail();

		} else if (Global.getNoOfContracts() < 0) {

			if (ma10 < tempCutLoss && Global.getCurrentPoint() < ma10)
				tempCutLoss = ma10;
			if (getTimeBase().getMainDownRail().getRail() != 0
					&& getTimeBase().getMainDownRail().getRail() < tempCutLoss)
				tempCutLoss = getTimeBase().getMainDownRail().getRail();
		}
	}
	
	@Override
	public void openContract() {

		if (getTimeBase().getEMA(20) == -1)
			return;


		if (getTimeBase().getEMA(10) < getTimeBase().getEMA(20)) {

			while (getTimeBase().getEMA(10) - bufferPt < getTimeBase().getEMA(20) // 穿五點先算穿
					&& Global.isOrderTime() && getTimeBase().getRSI() < 70 // 呢句加左係唔同的，加左難買d
			) {

				wanPrevious.middleWaiter(wanNext);
			}

			if (!Global.isOrderTime()
					|| Global.getCurrentPoint() < getTimeBase().getMA(20)
					|| getTimeBase().getRSI() > 70
					|| getAGAL() < 6.5 // getALAG超緊要
					|| getTimeBase().getLatestCandle().getHigh() == getTimeBase().getHL(60).getTempHigh()
					|| getTimeBase().isMADropping(20,1)
					|| !getTimeBase().isMARising(10,1)
					|| !getTimeBase().isQuantityRising()
					|| getTimeBase().getHL(60).getRsiFluctuation() > rsiFluctuation

			)

			{

				return;
			}

			longContract();

		} else {

			while (getTimeBase().getEMA(10) + bufferPt > getTimeBase().getEMA(20)
					&& Global.isOrderTime() && getTimeBase().getRSI() > 30) {

				wanPrevious.middleWaiter(wanNext);
			}


			if (!Global.isOrderTime()
					|| Global.getCurrentPoint() > getTimeBase().getEMA(20)
					|| getTimeBase().getRSI() < 30
					|| getTimeBase().getLatestCandle().getLow() == getTimeBase().getHL(60).getTempLow()
//					|| getTimeBase().getLatestCandle().getRsi() == getTimeBase().getTempRsiLow()
					|| getAGAL() < 6.5
					// || !getTimeBase().isMADropping(1)
					// || getTimeBase().getMA(5) > getTimeBase().getMA(20)
					// || Global.isLowFluctuation()
					|| getTimeBase().isMARising(20,1)
					|| !getTimeBase().isMADropping(10,1)
					|| !getTimeBase().isQuantityRising()
//					|| getTimeBase().getTempRsiHigh() < 70
					// || getTimeBase().getMainDownRail().getRail() == 0
//					|| getTimeBase().getFluctuation() < fluctuation
					|| getTimeBase().getHL(60).getRsiFluctuation() > rsiFluctuation //rsi Flu越大表示上下波到大,越細即越單邊走
		
			) {
				return;
			}

			shortContract();
		}

	}

	@Override
	public TimeBase getTimeBase() {
		return StockDataController.getShortTB();
	}

}
