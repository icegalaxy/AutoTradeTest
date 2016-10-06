package icegalaxy.net;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class TimePeriodDecider implements Runnable {

	private static boolean isOpeningTime;
	public final int dayOpen = 91459; 
	public final int noonClose = 120001;
	public final int noonOpen = 125959;
	public final int dayClose = 163001;
	public final int nightOpen = 171500;
	public final int nightClose = 234500;

	public final int morningOrderStart = 100000;
	public final int morningOrderStop = 114500;
	public final int afternoonOrderStart = 134500;
	public final int afternoonOrderStop = 154500;
	public final int nightOrderStart = 173000;
	public final int nightOrderStop = 231500; 

	public final int forceSell = 162500;
	public final int forceSell2 = 234000;
	private boolean noonClosed;
	private boolean noonOpened;
	private boolean dayClosed;
	private boolean nightOpened;

	public final int quotePowerStart = 94510;
	public final int quotePowerNoonStart = 142500;
	
	public final int DayHighLowTime1 = 100000;
	public final int DayHighLowTime2 = 140000;

	public boolean done = false;

	private WaitAndNotify wanPrevious;
	private WaitAndNotify wanNext;

	public TimePeriodDecider(WaitAndNotify wan1, WaitAndNotify wan2) {
		this.wanPrevious = wan1;
		this.wanNext = wan2;
	}

	@Override
	public void run() {

		System.out.println("Program Started");

		while (Global.isRunning()) {
			
			if (getTime() >= dayOpen && getTime() <= noonClose)
				Global.setTradeTime(true);
			else if (getTime() > noonClose && getTime() < noonOpen){
				if (!noonClosed){
					Global.addLog("Noon Close");
					noonClosed = true;
				}
				Global.setTradeTime(false);
			}
			else if (getTime() >= noonOpen && getTime() <= dayClose){
				if (!noonOpened){
					Global.addLog("Noon Opened");
					Global.setNoonOpened(true);
					noonOpened = true;
				}
				Global.setTradeTime(true);
				if (getTime() >= forceSell)
					Global.setForceSellTime(true);
				
			}else if (getTime() > dayClose && getTime() < nightOpen){
				if (!dayClosed){
					Global.addLog("Day Close");
					dayClosed = true;
				}
				Global.setTradeTime(false);
				if (Global.isForceSellTime())
					Global.setForceSellTime(false);
			}else if (getTime() >= nightOpen){
				if (!nightOpened){
					Global.addLog("Night Opened");
					nightOpened = true;
				}
				Global.setTradeTime(true);
			}
			
			if (getTime() >= forceSell2)
				Global.setForceSellTime(true);
			
			//these should be no use, need to check
			if (getTime() >= morningOrderStart && getTime() <= morningOrderStop)
				Global.setOrderTime(true);
			else if (getTime() > morningOrderStop && getTime() < afternoonOrderStart)
				Global.setOrderTime(false);
			else if (getTime() >= afternoonOrderStart && getTime() < afternoonOrderStop)
				Global.setOrderTime(true);
			else if (getTime() > afternoonOrderStop)
				Global.setOrderTime(false);

			if (!Global.isRunning()) {
				Global.setTradeTime(false);
				Global.setOrderTime(false);
			}

			wanPrevious.middleWaiter(wanNext);

		}

		// Global.setTradeTime(false); //�U�ȥ|�ɫ�~�|���O��
		// Global.setQuotePowerTime(false);
		// Global.setOrderTime(false);
		// Global.setRunning(false);
		// //Global.setForceSellTime(false);
		//		
		//		
		System.out.println("Program Ended");
	}
	
	public static boolean isOpeningTime(){
		return isOpeningTime;
	}

	public static int getTime() {

		String time = AutoTradeDB.getTime();
		// System.out.println(time);

		Integer a = new Integer(time.replaceAll(":", "").substring(1, 7));
		// System.out.println(a);
		int i = a;
		return i;
	}

}
