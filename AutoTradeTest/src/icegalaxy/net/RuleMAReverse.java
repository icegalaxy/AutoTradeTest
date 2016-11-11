package icegalaxy.net;

public class RuleMAReverse extends Rules {

	private float bufferPt = 5;
	private int maRetain = 15;
	private double fluctuation = 100;

	public RuleMAReverse(WaitAndNotify wan1, WaitAndNotify wan2,
			boolean globalRunRule) {
		super(wan1, wan2, globalRunRule);
		// TODO Auto-generated constructor stub
	}

	public void setBufferPt(float f) {
		bufferPt = f;
	}

	public void openContract() {

		if (getTimeBase().getMA(20) == -1)
			return;

		if (getTimeBase().getMARetain() > maRetain)
			return;

		if (getTimeBase().getMA(10) < getTimeBase().getMA(20)) {

			while (getTimeBase().getMA(10) - bufferPt < getTimeBase().getMA(20) // �來�I�����
					&& Global.isOrderTime()) {

				wanPrevious.middleWaiter(wanNext);
			}

			// wanPrevious.middleWaiter(wanNext); //
			// ���@��A��Getdata��set�nGlobal�Aڻ�U�|���|�nd

			if (!Global.isOrderTime()
			// || Global.getCurrentPoint() < getTimeBase().getMA(20)
			// || getTimeBase().getRSI() > 70
			// || getAGAL() < 6.5 // getALAG�W��n
					// || !getTimeBase().isMARising(20)
					// || !getTimeBase().isMARising(1)
					// || getTimeBase().getMA(5) < getTimeBase().getMA(20)
					// || Global.isLowFluctuation()
					// || !getTimeBase().isMARising(10)
					|| getTimeBase().isQuantityRising()
					// || getTimeBase().getMainUpRail().getRail() == 0
					|| getTimeBase().getHL(60).getFluctuation() > fluctuation
			// || getBalance() < 0
			// || !shouldRise()

			// || Global.getMacdHistogram() < 1
			)// ����ڻ�f���ma20rising���X��n�A�iڻ��j�աA�קK�R���n�h��

			{
				// Global.addLog("RuleMA, Give up Long");
				// System.out.println("MA10: " + getTimeBase().getMA(10));
				// System.out.println("MA20: " + getTimeBase().getMA(20));
				return;
			}

			// if (getBalance() < 0) {
			// while (true) {
			// if (Global.getNoOfContracts() ==0
			// || getTimeBase().getMA(10) - bufferPt < getTimeBase().getMA(20)
			// || !Global.isOrderTime())
			// return;
			//
			// if (getBalance() >= 0)
			// break;
			// wanPrevious.middleWaiter(wanNext);
			// }
			// }

			System.out.println("MA10: " + getTimeBase().getMA(10));
			System.out.println("MA20: " + getTimeBase().getMA(20));
			// Global.addLog("EMA5 Quantity: " +
			// getTimeBase().getMA5Quantity());
			// Global.addLog("Average Quantity: " +
			// getTimeBase().getAverageQuantity());
			longContract();

		} else {

			while (getTimeBase().getMA(10) + bufferPt > getTimeBase().getMA(20)
					&& Global.isOrderTime()) {

				wanPrevious.middleWaiter(wanNext);
			}

			// wanPrevious.middleWaiter(wanNext);

			if (!Global.isOrderTime()
			// || Global.getCurrentPoint() > getTimeBase().getMA(20)
			// || getTimeBase().getRSI() < 30
					// || !getTimeBase().isMADropping(20)
					// || getAGAL() < 6.5
					// || !getTimeBase().isMADropping(1)
					// || getTimeBase().getMA(5) > getTimeBase().getMA(20)
					// || Global.isLowFluctuation()
					// || !getTimeBase().isMADropping(10)
					|| getTimeBase().isQuantityRising()
					// || getTimeBase().getMainDownRail().getRail() == 0
					|| getTimeBase().getHL(60).getFluctuation() > fluctuation
			// || getBalance() < 0
			// || !shouldDrop()

			// || Global.getMacdHistogram() > -1
			) {
				// Global.addLog("RuleMA, Give up Short");
				// System.out.println("MA10: " + getTimeBase().getMA(10));
				// System.out.println("MA20: " + getTimeBase().getMA(20));
				return;
			}

			System.out.println("MA10: " + getTimeBase().getMA(10));
			System.out.println("MA20: " + getTimeBase().getMA(20));
			// Global.addLog("EMA5 Quantity: " +
			// getTimeBase().getMA5Quantity());
			// Global.addLog("Average Quantity: " +
			// getTimeBase().getAverageQuantity());
			shortContract();
		}

	}

	protected void updateCutLoss() {
		if (Global.getNoOfContracts() > 0)
			tempCutLoss = buyingPoint - 40;

		else if (Global.getNoOfContracts() < 0)
			tempCutLoss = buyingPoint + 40;

	}

//	public void closeContract() {
//
//		if (Global.getNoOfContracts() > 0) {
//			tempCutLoss = buyingPoint - 40;
//			tempStopEarn = buyingPoint + getStopEarnPt();
//		} else if (Global.getNoOfContracts() < 0) {
//			tempCutLoss = buyingPoint + 40;
//			tempStopEarn = buyingPoint - getStopEarnPt();
//		}
//
//		stopEarnPt = getStopEarnPt();
//		cutLossPt = getCutLossPt();
//
//		while (!reachGreatProfitPt()) {
//
//			updateCutLoss();
//			cutLoss();
//
//			// checkRSI();
//			// checkDayHighLow();
//			// checkTrend();
//
//			// if (maReversed())
//			// return;
//
//			if (Global.isForceSellTime()) {
//				closeContract("Force Sell");
//				return;
//			}
//
//			if (Global.getNoOfContracts() == 0) { // �i��ڨ�Lrule close���A��Trend
//				// truned�A�̧Y�Y�४�աA��
//				hasContract = false;
//				break;
//			}
//
//			if (!hasContract)
//				break;
//
//			wanPrevious.middleWaiter(wanNext);
//		}
//
//		if (Global.getNoOfContracts() == 0) {
//			hasContract = false;
//			return;
//		}
//
//		if (!hasContract)
//			return;
//
//		// updateCutLoss();
//		refPt = Global.getCurrentPoint();
//
//		while (hasContract) {
//
//			if (Global.getNoOfContracts() == 0) {
//				hasContract = false;
//				break;
//			}
//
//			if (Global.isForceSellTime()) {
//				closeContract("Force Sell");
//				return;
//			}
//
//			updateStopEarn();
//
//			stopEarn();
//
//			// System.out.println("Temp Stop Earn" + tempCutLoss);
//
//			wanPrevious.middleWaiter(wanNext);
//		}
//	}

	double getCutLossPt() {
		return 40;
	}

	double getStopEarnPt() {
		return 40;
	}


	@Override
	public TimeBase getTimeBase() {
		return GetData.getShortTB();
	}

	@Override
	boolean trendReversed() {
		// TODO Auto-generated method stub
		return false;
	}

	// @Override
	// public void checkTrend() {
	// if (Global.isQuantityDroppingShort())
	// closeContract("RuleMA: Trend truned");
	// }

}