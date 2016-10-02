package icegalaxy.net;


public abstract class Rules implements Runnable {

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
	
	protected double earnAdjustment = 0;
	protected double lossAdjustment = 0;
	
	double highestPtReached;
	double lowestPtReached;
	
	int morningOpen = 100000;
	int morningClose = 114500;
	int noonOpen = 134500;
	int noonClose = 154500;
	
	int holdingCount = 0;
	
	int lossTimes;
	
	boolean shutdown;
	boolean testing;

	private final double CUTLOSS_FACTOR = 5.0F;
	private final double STOPEARN_FACTOR = 5.0F;

	boolean usingMA20;
	boolean usingMA10;
	boolean usingMA5;
	
	boolean shutDownLong;
	boolean shutDownShort;
	
	private static float balance; // holding contracts �� balance

	// �i�H�zRun��Run
	public Rules(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule) {
		this.wanPrevious = wan1;
		this.wanNext = wan2;
		this.globalRunRule = globalRunRule;
		this.className = this.getClass().getSimpleName();

	}

	// �@�wrun
	public Rules(WaitAndNotify wan1, WaitAndNotify wan2) {
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
	
	public boolean isMornintTime(){
		
		int time = TimePeriodDecider.getTime();
		
		//System.out.println("Check: " + time + morningOpen + morningClose + noonOpen + noonClose);
		
		if (time > morningOpen && time < morningClose)
			return true;
		else return false;
	}
	
	public boolean isAfternoonTime(){
		
		int time = TimePeriodDecider.getTime();
		
		//System.out.println("Check: " + time + morningOpen + morningClose + noonOpen + noonClose);
		
		if (time > noonOpen && time < noonClose)
			return true;
		else
			return false;
	}
	
	public double getCurrentProfit(){
		
		if (Global.getNoOfContracts() > 0)
			return Global.getCurrentPoint() - buyingPoint;
		if (Global.getNoOfContracts() < 0)
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

		if (Global.getNoOfContracts() > 0)
			return Global.getCurrentBid() - stopEarnPt > buyingPoint; // it's
		// moving
		else if (Global.getNoOfContracts() < 0)
			return Global.getCurrentAsk() + stopEarnPt < buyingPoint;
		else
			return false;
	}

	protected void updateCutLoss() {

		if (getCutLossPt() < cutLossPt)
			cutLossPt = getCutLossPt();

		if (Global.getNoOfContracts() > 0) {
		
			if (buyingPoint - cutLossPt > tempCutLoss)
				tempCutLoss = buyingPoint - cutLossPt;

		

		} else if (Global.getNoOfContracts() < 0) {
			

			if (buyingPoint + cutLossPt < tempCutLoss)
				tempCutLoss = buyingPoint + cutLossPt;

			
		}
	}

	protected void cutLoss() {
		if (Global.getNoOfContracts() > 0
				&& Global.getCurrentPoint() < tempCutLoss){
			closeContract(className + ": CutLoss, short @ " + Global.getCurrentBid());
			shutdown = true;
		}
		else if (Global.getNoOfContracts() < 0
				&& Global.getCurrentPoint() > tempCutLoss){
			closeContract(className + ": CutLoss, long @ " +  Global.getCurrentAsk());
			shutdown = true;
		}
	}

	void stopEarn() {
		if (Global.getNoOfContracts() > 0 && StockDataController.getShortTB().getLatestCandle().getClose() < tempCutLoss){
			
			if(Global.getCurrentPoint() < buyingPoint){
				cutLoss();
				return;
			}
			
			closeContract(className + ": StopEarn, short @ " + Global.getCurrentBid());
			if (lossTimes > 0)
				lossTimes--;
				
		}
		else if (Global.getNoOfContracts() < 0 && StockDataController.getShortTB().getLatestCandle().getClose() > tempCutLoss){
			

			if(Global.getCurrentPoint() > buyingPoint){
				cutLoss();
				return;
			}
			
			closeContract(className + ": StopEarn, long @ " + Global.getCurrentAsk());
			if (lossTimes > 0)
				lossTimes--;
		}
	}

	public void closeContract(String msg) {

		boolean b = Sikuli.closeContract();
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
		Global.addLog("");
		Global.addLog("Current Balance: " + Global.balance + " points");
		Global.addLog("------------------------------");
		Global.addLog("");
		if (b)
			hasContract = false;
	}

	public void closeContract() {
		
		highestPtReached = 0;
		lowestPtReached = 99999;

		if (Global.getNoOfContracts() > 0) {
			tempCutLoss = buyingPoint - getCutLossPt();
			tempStopEarn = buyingPoint + getStopEarnPt();
		} else if (Global.getNoOfContracts() < 0) {
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

			if (Global.getNoOfContracts() == 0) { // �i��ڨ�Lrule close���A��Trend
				// truned�A�̧Y�Y�४�աA��
				hasContract = false;
				break;
			}

			if (!hasContract)
				break;

			wanPrevious.middleWaiter(wanNext);
		}

		if (Global.getNoOfContracts() == 0) {
			hasContract = false;
			return;
		}

		if (!hasContract)
			return;

		// updateCutLoss();
		refPt = Global.getCurrentPoint();

		Global.addLog(className + ": Secure Profit @ " + Global.getCurrentPoint());
	
	
		
		while (hasContract) {

			if (Global.getNoOfContracts() == 0) {
				hasContract = false;
				break;
			}
			
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

			updateCutLoss();
			cutLoss();
			
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
		if (Global.getNoOfContracts() > 0) {
			if (getTimeBase().getMA(10) < getTimeBase().getMA(20)) {
				closeContract(className + ": MA reversed");
				return true;
			}
		} else if (Global.getNoOfContracts() < 0) {
			if (getTimeBase().getMA(10) < getTimeBase().getMA(20)) {
				closeContract(className + ": MA reversed");
				return true;
			}
		}
		return false;
	}

	private void checkRSI() {
		if (Global.getNoOfContracts() > 0) {
			if (getTimeBase().getRSI() < 30 && getTimeBase().isQuantityRising()) {
				closeContract(className + ": Close Contract, RSI < 30");
			}
		} else if (Global.getNoOfContracts() < 0) {
			if (getTimeBase().getRSI() > 70 && getTimeBase().isQuantityRising()) {
				closeContract(className + ": Close Contract, RSI > 70");
			}
		}

	}

	private void checkDayHighLow() {
		if (Global.getNoOfContracts() > 0) {
			if (Global.getCurrentPoint() <= Global.getDayLow()) {
				closeContract(className + ": Close Contract, Day Low");
			}
		} else if (Global.getNoOfContracts() < 0) {
			if (Global.getCurrentPoint() >= Global.getDayHigh()) {
				closeContract(className + ": Close Contract, Day High");
			}
		}

	}

	protected float getAGAL() {

		StockDataController.getShortTB().getRSI(); // ���[�O�y����AGAL�Y���|����
		return (StockDataController.getShortTB().getAG() + StockDataController
				.getShortTB().getAL()); // �Y���O�׫Y���Y�n�εfShort
		// Period
		// ALAG��Ĺ��
	}

	public void shortContract() {
		boolean b = Sikuli.shortContract(1);
		if (!b)
			return;
		hasContract = true;
		Global.addLog(className + ": Short @ "  + Global.getCurrentBid());
		
//		if (Global.getNoOfContracts() == 0)
			buyingPoint = Global.getCurrentBid();
		balance += Global.getCurrentBid();
	}
	
	public void shortContract(int i) {
		boolean b = Sikuli.shortContract(i);
		if (!b)
			return;
		hasContract = true;
		Global.addLog(className + ": Short");
//		if (Global.getNoOfContracts() == 0)
			buyingPoint = Global.getCurrentBid();
		balance += Global.getCurrentBid() * i;
	}
	
	public void shortSecondContract() {
		boolean b = Sikuli.shortContract(1);
		if (!b)
			return;
		hasContract = true;
		Global.addLog(className + ": Short");
		
		balance += Global.getCurrentBid();
	}
	

	public void longContract() {
		boolean b = Sikuli.longContract(1);
		if (!b)
			return;
		hasContract = true;
		Global.addLog(className + ": Long @ " + Global.getCurrentAsk());
//		if (Global.getNoOfContracts() == 0)
			buyingPoint = Global.getCurrentAsk();
		balance -= Global.getCurrentAsk();
	}
	
	public void longContract(int i) {
		boolean b = Sikuli.longContract(i);
		if (!b)
			return;
		hasContract = true;
		Global.addLog(className + ": Long");
//		if (Global.getNoOfContracts() == 0)
			buyingPoint = Global.getCurrentAsk();
		balance -= Global.getCurrentAsk() * i;
	}
	
	public void longSecondContract() {
		boolean b = Sikuli.longContract(1);
		if (!b)
			return;
		hasContract = true;
		Global.addLog(className + ": Long");
		balance -= Global.getCurrentAsk();
	}

	public abstract void openContract();

	void updateStopEarn() {
		
		if (Global.getNoOfContracts() > 0) {

			try{
			if (StockDataController.getShortTB().getLatestCandle().getLow() > tempCutLoss) 
				tempCutLoss = StockDataController.getShortTB().getLatestCandle().getLow();
			}catch (Exception e){
				e.printStackTrace();
				tempCutLoss = Global.getCurrentPoint() - 5;
			}
			

		} else if (Global.getNoOfContracts() < 0) {

			try{
			if (StockDataController.getShortTB().getLatestCandle().getHigh() < tempCutLoss) 
				tempCutLoss = StockDataController.getShortTB().getLatestCandle().getHigh();
			}catch (Exception e){
				e.printStackTrace();
				tempCutLoss = Global.getCurrentPoint() + 5;
			}
			
		}

	}
	
	double getProfit(){
		if(Global.getNoOfContracts() > 0)
			return Global.getCurrentPoint() - buyingPoint;
		else
			return buyingPoint - Global.getCurrentPoint();
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
		if (Global.getNoOfContracts() > 0)
			return balance + Global.getCurrentPoint()
					* Global.getNoOfContracts();
		else if (Global.getNoOfContracts() < 0)
			return balance + Global.getCurrentPoint()
					* Global.getNoOfContracts();
		else {
			balance = 0;
			return balance;
		}
	}

	public static synchronized void setBalance(float balance) {
		Rules.balance = balance;
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
//		if (Global.getNoOfContracts() > 0){
//			if (StockDataController.getSec10TB().getMainDownRail()
//					.getSlope() != 100)
//			slope = StockDataController.getSec10TB().getMainDownRail()
//					.getSlope();
//			
//			if (getTimeBase().getMainUpRail().getSlope() != 100)
//				longSlope = getTimeBase().getMainUpRail().getSlope();
//			
//		}
//		if (Global.getNoOfContracts() < 0){
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
		
		for (int i=0; i<Setting.quantity.length; i++){ //for ����20 DAY����l
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
