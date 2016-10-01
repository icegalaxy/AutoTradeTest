package icegalaxy.net;

public class RuleSync extends Rules {

	public RuleSync(WaitAndNotify wan1, WaitAndNotify wan2,
			boolean globalRunRule) {
		super(wan1, wan2, globalRunRule);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void openContract() {
		
		if (Global.getNoOfContracts() == 3 || Global.getNoOfContracts() == -3){  //防止其他rule買左三張後跌番落黎,ruleSync照買
			while(Global.getNoOfContracts() > 1 || Global.getNoOfContracts() < -1){
				if (!Global.isOrderTime())
					return;
				wanPrevious.middleWaiter(wanNext);
			}
		}
		

		if (Global.getNoOfContracts() == 2){
		
			System.out.println("Holding Balance: " + getBalance());
			
			wanPrevious.middleWaiter(wanNext); //因為20110805Auto Trade果邊亂買，試下等一秒
			
			while (getBalance() < 0){
				if(Global.getNoOfContracts() != 2)
					return;
				wanPrevious.middleWaiter(wanNext);
			}
			
			if (!Global.isOrderTime())
				return;
			longContract();
			System.out.println("Holding balance: " + getBalance());

		} if (Global.getNoOfContracts() == -2){
			
			System.out.println("Holding Balance: " + getBalance());
			
			wanPrevious.middleWaiter(wanNext); 

			while (getBalance() < 0){
				if(Global.getNoOfContracts() != 2)
					return;
				wanPrevious.middleWaiter(wanNext);
			}
			if (!Global.isOrderTime())
				return;
			shortContract();	
			System.out.println("Holding balance: " + getBalance());

		}

	}

//	@Override
//	public void closeContract() {
//		
//		while (Global.getNoOfContracts() < -1 || Global.getNoOfContracts() > 1){
//			wanPrevious.middleWaiter(wanNext);
//		}
//		
//		if (Global.getNoOfContracts() == 0){
//			Global.addLog("RuleSync: Contract Closed by other Threads");
//			hasContract = false;
//			return;
//		}
//		
//		closeContract("RuleSync: Close Contract");
//	}
	


	@Override
	public TimeBase getTimeBase() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	boolean trendReversed() {
		// TODO Auto-generated method stub
		return false;
	}


}

	