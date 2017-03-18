package icegalaxy.net;

public class Global {

	public static synchronized boolean isTradeTime() {
		return isTradeTime;
	}

	public static synchronized void setTradeTime(boolean isTradeTime) {
		Global.isTradeTime = isTradeTime;
	}

	public static synchronized boolean hasLoggedIn() {
		return hasLoggedIn;
	}

	public static synchronized void setLoggedIn(boolean hasLoggedIn) {
		Global.hasLoggedIn = hasLoggedIn;
	}

	public static synchronized boolean isRunning() {
		return isRunning;
	}

	public static synchronized void setRunning(boolean isRunning) {
		Global.isRunning = isRunning;
	}

	public static synchronized float getCurrentPoint() {
		return currentPoint;
	}

	public static synchronized void setCurrentPoint(float currentPoint) {
		Global.currentPoint = currentPoint;
	}

	public static synchronized float getDayHigh() {
		return dayHigh;
	}

	public static synchronized void setDayHigh(float dayHigh) {
		Global.dayHigh = dayHigh;
	}

	public static synchronized float getDayLow() {
		return dayLow;
	}

	public static synchronized void setDayLow(float dayLow) {
		Global.dayLow = dayLow;
	}

	public static synchronized float getTempHigh() {
		return tempHigh;
	}

	public static synchronized void setTempHigh(float tempHigh) {
		Global.tempHigh = tempHigh;
	}

	public static synchronized float getTempLow() {
		return tempLow;
	}

	public static synchronized void setTempLow(float tempLow) {
		Global.tempLow = tempLow;
	}

	public static synchronized float getCutLost() {
		return cutLost;
	}

	public static synchronized void setCutLost(float cutLost) {
		Global.cutLost = cutLost;
	}

	public static synchronized boolean isOrderTime() {
		return isOrderTime;
	}

	public static synchronized void setOrderTime(boolean isOrderTime) {
		Global.isOrderTime = isOrderTime;
	}

	public static synchronized boolean isRuleOneTime() {
		return isRuleOneTime;
	}

	public static synchronized void setRuleOneTime(boolean isRuleOneTime) {
		Global.isRuleOneTime = isRuleOneTime;
	}

	public static synchronized void addLog(String msg) {
		msg = Global.getTableName() +" " + AutoTradeDB.getTime() .substring(1,9) + "	" + msg + "\r\n";
		System.out.println(msg);
		Global.log.append(msg);
		// DB.stringtoFile(Global.log.toString(),
		// "D:\\My Dropbox\\Programming\\Stock\\logAnalyse.txt");
	}


	public static synchronized void clearLog() {

		Global.log = new StringBuffer("");
	}

	public static synchronized String getLog() {
		return Global.log.toString();
	}

	public static synchronized boolean isForceSellTime() {
		return isForceSellTime;
	}

	public static synchronized void setForceSellTime(boolean isForceSellTime) {
		Global.isForceSellTime = isForceSellTime;
	}

	public static synchronized boolean isQuotePowerTime() {
		return isQuotePowerTime;
	}

	public static synchronized void setQuotePowerTime(boolean isQuotePowerTime) {
		Global.isQuotePowerTime = isQuotePowerTime;
	}

	public static synchronized float getCurrentBid() {
		return currentBid;
	}

	public static synchronized void setCurrentBid(float currentBid) {
		Global.currentBid = currentBid;
	}

	public static synchronized float getCurrentAsk() {
		return currentAsk;
	}

	public static synchronized void setCurrentAsk(float currentAsk) {
		Global.currentAsk = currentAsk;
	}

	public static synchronized boolean backHandLong() {
		return backHandLong;
	}

	public static synchronized void setBackHandLong(boolean backHandLong) {
		Global.backHandLong = backHandLong;
	}

	public static synchronized boolean backHandShort() {
		return backHandShort;
	}

	public static synchronized void setBackHandShort(boolean backHandShort) {
		Global.backHandShort = backHandShort;
	}

	// public static synchronized boolean ma5Rising() {
	// return ma5Rising;
	// }
	//
	// public static synchronized void setMa5Rising(boolean ma5Rising) {
	// Global.ma5Rising = ma5Rising;
	// }

	public static synchronized int getNoOfContracts() {
		return noOfContracts;
	}

	public static synchronized void setNoOfContracts(int noOfContracts) {
		Global.noOfContracts = noOfContracts;
	}

	public static synchronized float getCurrentDeal() {
		return currentDeal;
	}

	public static synchronized void setCurrentDeal(float currentDeal) {
		Global.currentDeal = currentDeal;
	}

	public static synchronized float getGreatProfit() {
		return greatProfit;
	}

	public static synchronized void setGreatProfit(float greatProfit) {
		Global.greatProfit = greatProfit;
	}

	public static synchronized boolean isSideWay() {
		return isSideWay;
	}

	public static synchronized void setSideWayTrue() {
		Global.isSideWay = true;
		Global.isUpTrend = false;
		Global.isDownTrend = false;
	}

	public static synchronized boolean isUpTrend() {
		return isUpTrend;
	}

	public static synchronized void setUpTrendTrue() {
		Global.isSideWay = false;
		Global.isUpTrend = true;
		Global.isDownTrend = false;
	}

	public static synchronized boolean isDownTrend() {
		return isDownTrend;
	}

	public static synchronized void setDownTrendTrue() {
		Global.isSideWay = false;
		Global.isUpTrend = false;
		Global.isDownTrend = true;
	}

	public static synchronized boolean isLowFluctuation() {
		return isLowFluctuation;
	}

	public static synchronized void setLowFluctuation(boolean isLowFluctuation) {
		Global.isLowFluctuation = isLowFluctuation;
	}

	public static synchronized boolean isDayHighLowTime() {
		return isDayHighLowTime;
	}

	public static synchronized void setDayHighLowTime(boolean isDayHighLowTime) {
		Global.isDayHighLowTime = isDayHighLowTime;
	}

	public static synchronized double getChange() {
		return currentPoint - previousClose;
	}

	public static synchronized double getGap() {
		return gap;
	}

	public static synchronized void setGap(double gap) {
		Global.gap = gap;
	}

	public static synchronized double getPreviousClose() {
		return previousClose;
	}

	public static synchronized void setPreviousClose(double previousClose) {
		Global.previousClose = previousClose;
	}

	public static synchronized boolean getStartRSI2() {
		return startRSI2;
	}

	public static synchronized void setStartRSI2(boolean startRSI2) {
		Global.startRSI2 = startRSI2;
	}

	private static boolean backHandLong = false;
	private static boolean backHandShort = false;

	private static boolean isOrderTime = false; // �ڷ|��������ɶ��Ae.g.���]�e�Q���������R
	private static boolean isTradeTime = false; // �i����ɶ�
	private static boolean isRuleOneTime = false; // ���ѱЪ�rule
	private static boolean isForceSellTime = false; // �������f
	private static boolean isQuotePowerTime = false;

	private static boolean isSideWay;
	private static boolean isUpTrend;
	private static boolean isDownTrend;
	private static boolean isLowFluctuation;

	private static boolean hasLoggedIn;
	private static boolean isRunning;

	private static float currentBid;
	private static float currentAsk;
	private static float currentDeal;

	private static int noOfContracts = 0;
	private static float cutLost;
	private static float dayHigh = 0;
	private static float dayLow = 99999;
	private static float tempHigh;
	private static float tempLow;
	private static float currentPoint; // �O�ӫY����

	private static float greatProfit;

	public static StringBuffer log = new StringBuffer("");

	public static float totalBalance;
	public static boolean analysingAll;

	public static boolean runRuleMA;
	public static boolean runRuleMA2;
	public static boolean runRSI;
	public static boolean runRuleWaveTheory;

	public static float balance = 0;
	public static boolean runRSI5;
	public static boolean ruleSync;
	public static boolean runRuleMACD;
	public static int noOfTrades;

	public static int maxContracts = 4;
	public static boolean isDayHighLowTime;

	public static double gap;
	public static double previousClose;

	public static boolean isTrendingMrt;
	public static boolean isSidewayMrt;

	public static boolean startRSI2;

	public static boolean isRsi2working;

	public static boolean isRsi2working() {
		return isRsi2working;
	}

	public static void setRsi2working(boolean isRsi2working) {
		Global.isRsi2working = isRsi2working;
	}

	
	public static synchronized double getpOpen() {
		return pOpen;
	}

	public static synchronized void setpOpen(double pOpen) {
		Global.pOpen = pOpen;
	}

	public static synchronized double getpHigh() {
		return pHigh;
	}

	public static synchronized void setpHigh(double pHigh) {
		Global.pHigh = pHigh;
	}

	public static synchronized double getpLow() {
		return pLow;
	}

	public static synchronized void setpLow(double pLow) {
		Global.pLow = pLow;
	}

	public static synchronized double getpClose() {
		return pClose;
	}

	public static synchronized void setpClose(double pClose) {
		Global.pClose = pClose;
	}

	public static synchronized double getpFluc() {
		return pFluc;
	}

	public static synchronized void setpFluc(double pFluc) {
		Global.pFluc = pFluc;
	}
	
	
	
	public static synchronized double getAOH() {
		return AOH;
	}

	public static synchronized void setAOH(double aOH) {
		AOH = aOH;
	}

	public static synchronized double getAOL() {
		return AOL;
	}

	public static synchronized void setAOL(double aOL) {
		AOL = aOL;
	}

	public static synchronized double getOpen() {
		return open;
	}

	public static synchronized void setOpen(double open) {
		Global.open = open;
	}
	
	
	

	public static synchronized boolean isOpenDown() {
		return OpenDown;
	}

	public static synchronized void setOpenDown(boolean pOpenDown) {
		Global.OpenDown = pOpenDown;
	}
	
	


	public static synchronized Chasing getChasing()
	{
		return chasing;
	}

	public static synchronized void setChasing(Chasing chasing)
	{
		Global.chasing = chasing;
	}

	
	
	public static synchronized boolean isRapidRise()
	{
		return rapidRise;
	}

	public static synchronized void setRapidRise(boolean rapidRise)
	{
		Global.rapidRise = rapidRise;
	}

	public static synchronized boolean isRapidDrop()
	{
		return rapidDrop;
	}

	public static synchronized void setRapidDrop(boolean rapidDrop)
	{
		Global.rapidDrop = rapidDrop;
	}
	
	

	public static boolean isHugeRise()
	{
		return hugeRise;
	}

	public static void setHugeRise(boolean hugeRise)
	{
		Global.hugeRise = hugeRise;
	}

	public static boolean isHugeDrop()
	{
		return hugeDrop;
	}

	public static void setHugeDrop(boolean hugeDrop)
	{
		Global.hugeDrop = hugeDrop;
	}

	static Chasing chasing;

	static double pOpen = 0;
	static boolean OpenDown;
	static double pHigh = 0;
	static double pLow = 0;
	static double pClose = 0;
	static double pFluc;
	static double AOH = 0;
	static double AOL = 0;
	static double open = 0;
	
	static boolean rapidRise;
	static boolean rapidDrop;
	
	static boolean hugeRise;
	static boolean hugeDrop;

	static String tableName;;
	
	public static void setTableName(String s) {
		tableName = s;
		
	}
	
	public static String getTableName(){
		return tableName;
	}

	public static void setNoonOpened(boolean b) {
		noonOpened = b;
		
	}
	
	public static boolean getNoonOpened(){
		return noonOpened;
	}

	static boolean noonOpened;
	
}



