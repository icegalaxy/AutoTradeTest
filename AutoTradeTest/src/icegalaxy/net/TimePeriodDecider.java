package icegalaxy.net;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimePeriodDecider implements Runnable {

	private static boolean isOpeningTime;
	public final int dayOpen = 91530; //�]��quotePower�n0945�����A�����e�榡�Y���P���A�Ӯa�n�I�R�J�P��X���A�ҥH�n���榡�@��
	public final int noonClose = 120030; //�]�����\get���줤�ȳ̫�@��data
	public final int noonOpen = 125930;
	public final int dayClose = 161500;

	//public final int morningOrderStart = 101500; // �}�l�R��
	public final int morningOrderStart = 100000; // �}�l�R��
	//public final int morningOrderStop = 121500; // �Q�G�I�T������R
	public final int morningOrderStop = 114500;
	public final int afternoonOrderStart = 134500; // �Ӯa���ȭ�reset RSI
//	public final int afternoonOrderStart = 134430; // �Ӯa���ȭ�reset RSI
	public final int afternoonOrderStop = 154500; // 

	public final int forceSell1 = 115900; // �����泥�ɶ��A���QKeep�L�U��or�L�]
	public final int forceSell2 = 161000; // ����R���P���f�ɶ��������Ӧ��̤֤Q�����A�H���@�R�Y��

	public final int ruleOneStart = 100500; // �Q�I�s�����}�l�R�A�]�����e�����}��
	public final int ruleOneStop = 103000;

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

			// Global.setRunning(true);
//			if (!Global.isForceSellTime())
//				System.out.println(new Integer(getTime()).toString());
			
			if (getTime() >= dayOpen && getTime() <= morningOrderStart)
				isOpeningTime = true;
			else if  (getTime() > morningOrderStart && getTime() <= noonOpen)
				isOpeningTime = false;
			else if (getTime() >= noonOpen && getTime() <= afternoonOrderStart)
				isOpeningTime = true;
			else
				isOpeningTime = false;
			
			if (getTime() >= dayOpen && getTime() <= DayHighLowTime1)
				Global.setDayHighLowTime(true);
			else if (getTime() > DayHighLowTime1 && getTime() < noonOpen)
				Global.setDayHighLowTime(false);
			else if (getTime() >= noonOpen && getTime() <= DayHighLowTime2)
				Global.setDayHighLowTime(true);
			else
				Global.setDayHighLowTime(false);
			

			if (getTime() >= dayOpen && getTime() <= noonClose)
				Global.setTradeTime(true);
			else if (getTime() > noonClose && getTime() < noonOpen)
				Global.setTradeTime(false);
			else if (getTime() >= noonOpen)
				Global.setTradeTime(true);

			if (getTime() >= morningOrderStart && getTime() <= morningOrderStop)
				Global.setOrderTime(true);
			else if (getTime() > morningOrderStop
					&& getTime() < afternoonOrderStart)
				Global.setOrderTime(false);
			else if (getTime() >= afternoonOrderStart
					&& getTime() < afternoonOrderStop)
				Global.setOrderTime(true);
			else if (getTime() > afternoonOrderStop)
				Global.setOrderTime(false);

			if (getTime() >= ruleOneStart && getTime() <= ruleOneStop)
				Global.setRuleOneTime(true);
			else
				Global.setRuleOneTime(false);

//			 decide���f�ɶ�
//			if (getTime() >= forceSell1 && getTime() <= noonClose)
//				Global.setForceSellTime(true);
////				Global.setForceSellTime(false);
//			else if (getTime() > noonClose && getTime() < forceSell2)
//				Global.setForceSellTime(false);
//			else 
				if (getTime() >= forceSell2 && getTime() < dayClose)
				Global.setForceSellTime(true);

			if (getTime() >= quotePowerStart && getTime() <= noonClose)
				Global.setQuotePowerTime(true);
			else if (getTime() > noonClose && getTime() < quotePowerNoonStart)
				Global.setQuotePowerTime(false);
			else if (getTime() >= quotePowerNoonStart)
				Global.setQuotePowerTime(true);

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

		Integer a = new Integer(time.replaceAll(":", "").substring(9, 15));
		// System.out.println(a);
		int i = a;
		return i;
	}

}
