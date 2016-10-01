package icegalaxy.net;

public abstract class LocalRules implements Runnable {

	protected boolean hasContract;
	protected WaitAndNotify wanPrevious;
	protected WaitAndNotify wanNext;
	protected double tempCutLoss;
	protected double tempStopEarn;
	protected float refPt;
	protected double buyingPoint;
	private boolean globalRunRule;
	protected String className;
	double stopEarnPt;
	double cutLossPt;
	
	static boolean longed = false;
	static boolean shorted = false;
	
	protected double earnAdjustment = 0;
	protected double lossAdjustment = 0;
	
	double highestPtReached;
	double lowestPtReached;
	
	int morningOpen = 100000;
	int morningClose = 114500;
	int noonOpen = 134500;
	int noonClose = 154500;
	
	int holdingCount = 0;
	
	boolean shutdown;
	boolean testing;

	private final double CUTLOSS_FACTOR = 5.0F;
	private final double STOPEARN_FACTOR = 5.0F;

	boolean usingMA20;
	boolean usingMA10;
	boolean usingMA5;
	
	boolean shutDownLong;
	boolean shutDownShort;
	
	private static float balance; // holding contracts 的 balance

	// 可以揀Run唔Run
	public LocalRules(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule) {
		this.wanPrevious = wan1;
		this.wanNext = wan2;
		this.globalRunRule = globalRunRule;
		this.className = this.getClass().getSimpleName();

	}

	// 一定run
	public LocalRules(WaitAndNotify wan1, WaitAndNotify wan2) {
		this.wanPrevious = wan1;
		this.wanNext = wan2;
		this.globalRunRule = true;
		this.className = this.getClass().getSimpleName();

	}
	
	public void setOrderTime(int morningOpen,int morningClose, int noonOpen, int noonClose){
		this.morningOpen = morningOpen;
		this.morningClose = morningClose;
		this.noonOpen = noonOpen;
		this.noonClose = noonClose;
	}
	
	public boolean isOrderTime(){
		
		int time = TimePeriodDecider.getTime();
		
		//System.out.println("Check: " + time + morningOpen + morningClose + noonOpen + noonClose);
		
		if (time > morningOpen && time < morningClose)
			return true;
		else if (time > noonOpen && time < noonClose)
			return true;
		else
			return false;
	}
	
	public double getCurrentProfit(){
		
		if (longed)
			return Global.getCurrentPoint() - buyingPoint;
		if (shorted)
			return buyingPoint - Global.getCurrentPoint();
		
		return 0;
		
	}

	@Override
	public void run() {

		if (!globalRunRule) {
			while (Global.isRunning()) {
				wanPrevious.middleWaiter(wanNext);
			}
		} else {

			Global.addLog(className + " Start");

			while (Global.isRunning()) {

				usingMA20 = true;
				usingMA10 = true;
				usingMA5 = true;

				if (hasContract) {
					closeContract();
				} else 
//					if (isOrderTime()) 
					{
					openContract();
				}

				wanPrevious.middleWaiter(wanNext);
			}
		}
	}

	protected boolean reachGreatProfitPt() {

		if (getStopEarnPt() < stopEarnPt)
			stopEarnPt = getStopEarnPt();

		if (longed)
			return Global.getCurrentBid() - stopEarnPt > buyingPoint; // it's
		// moving
		else if (shorted)
			return Global.getCurrentAsk() + stopEarnPt < buyingPoint;
		else
			return false;
	}

	protected void updateCutLoss() {

		if (getCutLossPt() < cutLossPt)
			cutLossPt = getCutLossPt();
		
		//每秒加0.016，每分中加1.幾
		earnAdjustment = earnAdjustment + 0.02;
		lossAdjustment = lossAdjustment + 0.02;

		// if (getStopEarnPt() < stopEarnPt)
		// stopEarnPt = getStopEarnPt();

		if (longed) {
			// if (Global.getCurrentPoint() - tempCutLoss > cutLossPt) {
			// tempCutLoss = Global.getCurrentPoint() - cutLossPt;
			// System.out.println("CurrentPt: " + Global.getCurrentPoint());
			// System.out.println("cutLossPt: " + cutLossPt);
			// System.out.println("TempCutLoss: " + tempCutLoss);
			// }
			
			if (Global.getCurrentPoint() - buyingPoint > lossAdjustment)
				lossAdjustment = Global.getCurrentPoint() - buyingPoint;
			
			if (buyingPoint - Global.getCurrentPoint() > earnAdjustment)
				earnAdjustment = buyingPoint - Global.getCurrentPoint();

			if (buyingPoint - cutLossPt > tempCutLoss)
				tempCutLoss = buyingPoint - cutLossPt;
			
			earnAdjustment = earnAdjustment - 0.01;
			lossAdjustment = lossAdjustment - 0.01;

			// if (Global.getCurrentPoint() + stopEarnPt < tempStopEarn) {
			// tempStopEarn = Global.getCurrentPoint() + stopEarnPt;
			// System.out.println("TempStopEarn: " + tempStopEarn);
			// }

		} else if (shorted) {
			// if (tempCutLoss - Global.getCurrentPoint() > cutLossPt) {
			// tempCutLoss = Global.getCurrentPoint() + cutLossPt;
			// System.out.println("CurrentPt: " + Global.getCurrentPoint());
			// System.out.println("cutLossPt: " + cutLossPt);
			// System.out.println("TempCutLoss: " + tempCutLoss);
			// }

			if (buyingPoint + cutLossPt < tempCutLoss)
				tempCutLoss = buyingPoint + cutLossPt;

			if (buyingPoint - Global.getCurrentPoint() > lossAdjustment)
				lossAdjustment = buyingPoint - Global.getCurrentPoint();
			
			if (Global.getCurrentPoint() - buyingPoint > earnAdjustment)
				earnAdjustment = Global.getCurrentPoint() - buyingPoint;
			
			// if (Global.getCurrentPoint() - stopEarnPt > tempStopEarn) {
			// tempStopEarn = Global.getCurrentPoint() - stopEarnPt;
			// System.out.println("TempStopEarn: " + tempStopEarn);
			// }
		}
	}

	protected void cutLoss() {
		if (longed
				&& Global.getCurrentPoint() < tempCutLoss){
			closeContract("CutLoss " + className);
			shutdown = true;
		}
		else if (shorted
				&& Global.getCurrentPoint() > tempCutLoss){
			closeContract("CutLoss " + className);
			shutdown = true;
		}
	}

	void stopEarn() {
		if (longed
				&& Global.getCurrentPoint() < tempCutLoss)
			closeContract("StopEarn " + className);
		else if (shorted
				&& Global.getCurrentPoint() > tempCutLoss)
			closeContract("StopEarn " + className);
	}

	public void closeContract(String msg) {
		
		boolean b = false;
		
		if (longed){
			b = LocalSikuli.shortContract(1);
			longed = false;
			Global.setStartRSI2(false);
		}
		else if (shorted){
			b = LocalSikuli.longContract(1);
			shorted = false;
			Global.setStartRSI2(false);
		}

//		boolean b = LocalSikuli.closeContract();
		earnAdjustment = 0;
		lossAdjustment = 0;
		holdingCount = 0;
		
		if (testing)
		{
			Global.balance = 0;
			Global.noOfTrades = 0;
			testing = false;
		}
		Global.addLog(msg);
		if (b)
			hasContract = false;
		
		
	}

	public void closeContract() {
		
		highestPtReached = 0;
		lowestPtReached = 99999;

		if (longed) {
			tempCutLoss = buyingPoint - getCutLossPt();
			tempStopEarn = buyingPoint + getStopEarnPt();
		} else if (shorted) {
			tempCutLoss = buyingPoint + getCutLossPt();
			tempStopEarn = buyingPoint - getStopEarnPt();
		}
		
		stopEarnPt = getStopEarnPt();
		cutLossPt = 300;

		while (!reachGreatProfitPt()) {
			
			holdingCount++;

			updateCutLoss();
			cutLoss();
			
			if (Global.getCurrentPoint() > highestPtReached)
				highestPtReached = Global.getCurrentPoint();
			else if (Global.getCurrentPoint() < lowestPtReached)
				lowestPtReached = Global.getCurrentPoint();

			// checkRSI();
			// checkDayHighLow();
			if (trendReversed()) {
				closeContract(className + ": Trend Reversed");
				return;
			}

			if (trendUnstable()) {
				closeContract(className + ": Trend Unstable");
				return;
			}
			// if (maReversed())
			// return;

			if (Global.isForceSellTime()) {
				closeContract("Force Sell");
				return;
			}

			if (Global.getNoOfContracts() == 0) { // 可能俾其他rule close左，或Trend
				// truned，甘即係轉左勢，走
				hasContract = false;
				break;
			}

			if (!hasContract)
				break;

			wanPrevious.middleWaiter(wanNext);
		}

//		if (Global.getNoOfContracts() == 0) {
//			hasContract = false;
//			return;
//		}

		if (!hasContract)
			return;

		// updateCutLoss();
		refPt = Global.getCurrentPoint();

		Global.addLog("Stop earning at: " + Global.getCurrentPoint());
		
		while (hasContract) {

//			if (Global.getNoOfContracts() == 0) {
//				hasContract = false;
//				break;
//			}
			
			if (Global.getCurrentPoint() > highestPtReached)
				highestPtReached = Global.getCurrentPoint();
			else if (Global.getCurrentPoint() < lowestPtReached)
				lowestPtReached = Global.getCurrentPoint();


			if (trendReversed2())
				closeContract(className + ": TrendReversed2");

			if (Global.isForceSellTime()) {
				closeContract("Force Sell");
				return;
			}

			updateStopEarn();

			stopEarn();

			// System.out.println("Temp Stop Earn" + tempCutLoss);

			wanPrevious.middleWaiter(wanNext);
		}
	}

	boolean trendReversed2() {
		return false;
	}

	boolean trendUnstable() {
		return false;
	}


	private boolean maReversed() {
		if (longed) {
			if (getTimeBase().getMA(10) < getTimeBase().getMA(20)) {
				closeContract(className + ": MA reversed");
				return true;
			}
		} else if (shorted) {
			if (getTimeBase().getMA(10) < getTimeBase().getMA(20)) {
				closeContract(className + ": MA reversed");
				return true;
			}
		}
		return false;
	}

	private void checkRSI() {
		if (longed) {
			if (getTimeBase().getRSI() < 30 && getTimeBase().isQuantityRising()) {
				closeContract(className + ": Close Contract, RSI < 30");
			}
		} else if (shorted) {
			if (getTimeBase().getRSI() > 70 && getTimeBase().isQuantityRising()) {
				closeContract(className + ": Close Contract, RSI > 70");
			}
		}

	}

	private void checkDayHighLow() {
		if (longed) {
			if (Global.getCurrentPoint() <= Global.getDayLow()) {
				closeContract(className + ": Close Contract, Day Low");
			}
		} else if (shorted) {
			if (Global.getCurrentPoint() >= Global.getDayHigh()) {
				closeContract(className + ": Close Contract, Day High");
			}
		}

	}

	protected float getAGAL() {

		StockDataController.getShortTB().getRSI(); // 唔加呢句的話AGAL係唔會郁的
		return (StockDataController.getShortTB().getAG() + StockDataController
				.getShortTB().getAL()); // 即做呢度係都係要用番Short
		// Period
		// ALAG先贏到
	}

	public void shortContract() {
		boolean b = LocalSikuli.shortContract(1);
		if (!b)
			return;
		hasContract = true;
		Global.addLog(className + ": Short");
		
//		if (Global.getNoOfContracts() == 0)
			buyingPoint = Global.getCurrentBid();
		balance += Global.getCurrentBid();
	}
	
	public void shortContract(int i) {
		boolean b = LocalSikuli.shortContract(i);
		if (!b)
			return;
		hasContract = true;
		Global.addLog(className + ": Short");
//		if (Global.getNoOfContracts() == 0)
			buyingPoint = Global.getCurrentBid();
		balance += Global.getCurrentBid() * i;
	}
	
	public void shortSecondContract() {
		boolean b = LocalSikuli.shortContract(1);
		if (!b)
			return;
		hasContract = true;
		Global.addLog(className + ": Short");
		
		balance += Global.getCurrentBid();
	}
	

	public void longContract() {
		boolean b = LocalSikuli.longContract(1);
		if (!b)
			return;
		hasContract = true;
		Global.addLog(className + ": Long");
//		if (Global.getNoOfContracts() == 0)
			buyingPoint = Global.getCurrentAsk();
		balance -= Global.getCurrentAsk();
	}
	
	public void longContract(int i) {
		boolean b = LocalSikuli.longContract(i);
		if (!b)
			return;
		hasContract = true;
		Global.addLog(className + ": Long");
//		if (Global.getNoOfContracts() == 0)
			buyingPoint = Global.getCurrentAsk();
		balance -= Global.getCurrentAsk() * i;
	}
	
	public void longSecondContract() {
		boolean b = LocalSikuli.longContract(1);
		if (!b)
			return;
		hasContract = true;
		Global.addLog(className + ": Long");
		balance -= Global.getCurrentAsk();
	}

	public abstract void openContract();

	void updateStopEarn() {

		if (longed) {

			if (getTimeBase().getLatestCandle().getLow() > tempCutLoss) {
				tempCutLoss = getTimeBase().getLatestCandle().getLow();

			}

		} else if (shorted) {

			if (getTimeBase().getLatestCandle().getHigh() < tempCutLoss) {
				tempCutLoss = getTimeBase().getLatestCandle().getHigh();

			}
		}

	}

	double getCutLossPt() {
		return getAGAL() * CUTLOSS_FACTOR;
		// return StockDataController.getShortTB().getHL15().getFluctuation() /
		// CUTLOSS_FACTOR;
	}

	double getStopEarnPt() {
		return getAGAL() * STOPEARN_FACTOR;
		// return StockDataController.getShortTB().getHL15().getFluctuation() /
		// STOPEARN_FACTOR;
	}

	public void setName(String s) {
		className = s;
	}

	public static synchronized float getBalance() {
		if (longed)
			return balance + Global.getCurrentPoint()
					* Global.getNoOfContracts();
		else if (shorted)
			return balance + Global.getCurrentPoint()
					* Global.getNoOfContracts();
		else {
			balance = 0;
			return balance;
		}
	}

	public static synchronized void setBalance(float balance) {
		LocalRules.balance = balance;
	}

	public abstract TimeBase getTimeBase();

	boolean maRising(int period) {
		return getTimeBase().isMARising(period, 1);
	}

	boolean maDropping(int period) {
		return getTimeBase().isMADropping(period, 1);
	}

	boolean emaRising(int period) {
		return getTimeBase().isEMARising(period, 1);
	}

	boolean emaDropping(int period) {
		return getTimeBase().isEMADropping(period, 1);
	}
	
	boolean trendReversed(){
		
//		double slope = 0;
//		double longSlope = 0;
//		
//		if (longed){
//			if (StockDataController.getSec10TB().getMainDownRail()
//					.getSlope() != 100)
//			slope = StockDataController.getSec10TB().getMainDownRail()
//					.getSlope();
//			
//			if (getTimeBase().getMainUpRail().getSlope() != 100)
//				longSlope = getTimeBase().getMainUpRail().getSlope();
//			
//		}
//		if (shorted){
//			
//			if (StockDataController.getSec10TB().getMainUpRail().getSlope() != 100)
//				slope = StockDataController.getSec10TB().getMainUpRail().getSlope();
//			
//			if (getTimeBase().getMainDownRail().getSlope() != 100)
//				longSlope = getTimeBase().getMainDownRail().getSlope();
//		}
//		return slope > 5 && slope > longSlope * 2;
		
		return false;
	}
	
	public boolean isGreatQuantiy(){
		
		double currentQ;
		double averageQ = 0;
		int maxIndex = 0;
		
		currentQ = getTimeBase().getQuatityByPeriods(Setting.quantityPeriods[getQuantitiesIndex()])
//				- getTimeBase().getQuatityByPeriods(Setting.quantityPeriods[getQuantitiesIndex()-1])
				;
		
		for (int i=0; i<Setting.quantity.length; i++){ //for 唔夠20 DAY的日子
			if (maxIndex < Setting.quantity[0][i] + 1)
				maxIndex = (int) Setting.quantity[0][i] + 1;
		}
		
		
		for (int i=0; i<maxIndex; i++){
			averageQ += Setting.quantity[getQuantitiesIndex()][i];
		}
		
		averageQ = averageQ / maxIndex;
		
		return currentQ > averageQ;
		
	}
	

	
	public int getQuantitiesIndex() {

		int periods[] = {0, 93100, 101600, 104600, 111600, 114600, 134600, 141600, 144600, 151600, 153100};
		
		int time = AutoTradeDB.getTimeInt();

		for (int i=periods.length-1; i>0; i--){			
			if (time > periods[i])
				return i;	
		}
		
//		if (time > 153100)
//			return 6;
//		else if (time > 150100)
//			return 5;
//		else if (time > 1430)
//			return 4;
//		else if (time > 111600)
//			return 3;
//		else if (time > 101600)
//			return 2;
//		else if (time > 93000)
//			return 1;
		
		return -1;

	}
	
}
