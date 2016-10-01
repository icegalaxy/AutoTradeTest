package icegalaxy.net;

import java.util.ArrayList;

public class RuleMACupcake extends Rules {

	private TimeBase timeBase;

	public RuleMACupcake(WaitAndNotify wan1, WaitAndNotify wan2,
			boolean globalRunRule, TimeBase tb) {
		super(wan1, wan2, globalRunRule);

		timeBase = tb;
	}


	@Override
	public TimeBase getTimeBase() {
		return timeBase;
	}

	@Override
	public void openContract() {

		int noOfPeriods = 4;
		boolean longContract = true;
		boolean shortContract = true;

		ArrayList<Float> ma10 = getTimeBase().getMAList(10, noOfPeriods);
		ArrayList<Float> ma20 = getTimeBase().getMAList(20, noOfPeriods);

		if (getTimeBase().getMA(20) == -1)
			return; //未夠料
		
		for (int i = 1; i < noOfPeriods; i++) { // loop少一次，因為五period只可比較四次
			if (ma10.get(i) - ma20.get(i) <= ma10.get(i - 1) - ma20.get(i - 1))
				longContract = false;
		}
		
		for (int i = 1; i < noOfPeriods; i++) { // loop少一次，因為五period只可比較四次
			if (ma10.get(i) - ma20.get(i) >= ma10.get(i - 1) - ma20.get(i - 1))
				shortContract = false;
		}
		
		if (longContract){
			
			if (!Global.isTradeTime() 
					|| getTimeBase().getRSI() > 70
					|| !getTimeBase().isMARising(1,3)
					|| !getTimeBase().isQuantityRising(3)
			)
				return;
			System.out.println(ma10);
			System.out.println(ma20);
			longContract();	
			
		}else if (shortContract){
			
			if (!Global.isTradeTime() 
					|| getTimeBase().getRSI() < 30
					|| !getTimeBase().isMADropping(1,noOfPeriods)
					|| !getTimeBase().isQuantityRising(3)
			)
				return;
			System.out.println("MA10" + ma10);
			System.out.println("MA20" + ma20);
			shortContract();		
		}
	}

	@Override
	boolean trendReversed() {
		// TODO Auto-generated method stub
		return false;
	}

}
