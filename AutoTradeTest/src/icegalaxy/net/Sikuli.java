package icegalaxy.net;



//Global.setHoldingStock(true)��Global.setHoldingStock(false)���Y�סA�]���׫ץΨ�
//�R���esetTrue�A�椧�����setFalse�A�H���R�h��
public class Sikuli {



	public static void login() {
		Global.addLog("Login" + " " +Global.isForceSellTime());
	}

	public static void resetWindow() {
		
	}

	public static synchronized boolean longContract(int noOfContracts) {

//		if(Global.hasLongContract()){
//			Global.addLog("Rule Error: Double Long. Action Cancelled!");
//			return false;
//		}
		
		if(Global.getNoOfContracts() >= Global.maxContracts){			
			//Global.addLog("Error: max Contracts reached. Action Cancelled!");
			return false;
			
		}
		
//		Global.addLog("Long at " + Global.getCurrentAsk() + " x " + noOfContracts + "no(s) of Contracts " + Global.isForceSellTime());
	
		Global.balance -= Global.getCurrentAsk() * noOfContracts;
		Global.noOfTrades += 1 * noOfContracts;

		Global.setNoOfContracts(Global.getNoOfContracts() + noOfContracts);
		
//		Global.addLog("DayHigh: " + Global.getDayHigh() + "; DayLow: " + Global.getDayLow() + "; Diff: " + (Global.getDayHigh() - Global.getDayLow()));
		
		
		if (Global.getNoOfContracts() == 0){
//			Global.addLog("Current Balance: " + Global.balance + " points");
			Rules.setBalance(0);
		}
		
		return true;
	}

	public static synchronized boolean shortContract(int noOfContracts) {

//		if(Global.hasShortContract()){
//			Global.addLog("Rule Error: Double Short. Action Cancelled!");
//			return false;
//		}
		
		if(Global.getNoOfContracts() <= Global.maxContracts * -1){
			
			//Global.addLog("Error: max Contracts reached. Action Cancelled!");
			return false;
			
		}
		
		
		
//		Global.addLog("Short at " + Global.getCurrentBid()  + " x " + noOfContracts + "no(s) of Contracts " +  Global.isForceSellTime());
		
		Global.balance += Global.getCurrentBid() * noOfContracts;
		Global.noOfTrades += 1 * noOfContracts;
		
		Global.setNoOfContracts(Global.getNoOfContracts() - 1 * noOfContracts);
		
//		Global.addLog("DayHigh: " + Global.getDayHigh() + "; DayLow: " + Global.getDayLow() + "; Diff: " + (Global.getDayHigh() - Global.getDayLow()));
		
		if (Global.getNoOfContracts() == 0){
//			Global.addLog("Current Balance: " + Global.balance + " points");
			Rules.setBalance(0);
		}
		
		return true;
	}

	public static synchronized boolean closeContract() {

		if (Global.getNoOfContracts() > 0) {

			boolean b = Sikuli.shortContract(Global.getNoOfContracts());
			if (!b) {
				Global.addLog("Fail to close, reset Window");
				return false;
			}

		} else if (Global.getNoOfContracts() < 0) {

			boolean b = Sikuli.longContract(Global.getNoOfContracts() * -1);
			if (!b) {
				Global.addLog("Fail to close, reset Window");
				return false;
			}
		} else {
			Global.addLog("Error: No Contract to close");
			return false;
		}
		return true;
	}
}
