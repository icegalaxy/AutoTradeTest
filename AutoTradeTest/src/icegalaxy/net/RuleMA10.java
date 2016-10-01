package icegalaxy.net;

public class RuleMA10 extends Rules {

	private TimeBase timeBase;
	
	public RuleMA10(WaitAndNotify wan1, WaitAndNotify wan2,
			boolean globalRunRule, TimeBase tb) {
		super(wan1, wan2, globalRunRule);
		
		timeBase = tb;
	}


	@Override
	public TimeBase getTimeBase(){
		return timeBase;
	}

	@Override
	public void openContract() {
		
		
		if (getTimeBase().getMA(1) < getTimeBase().getMA(10)) {
						
			while (getTimeBase().getMA(1) < getTimeBase().getMA(10)) { // 穿五點先算穿
				wanPrevious.middleWaiter(wanNext);
			}

			wanPrevious.middleWaiter(wanNext);

			if (!Global.isOrderTime()
					|| getTimeBase().getRSI() > 70
					|| !getTimeBase().isQuantityRising()
			){
				System.out.println("Give up to Long");
				return;
			}

			longContract();

		} else {

			while (getTimeBase().getMA(1) > getTimeBase().getMA(10)) { // 穿五點先算穿
				wanPrevious.middleWaiter(wanNext);
			}

			wanPrevious.middleWaiter(wanNext);

			if (!Global.isOrderTime()
					|| getTimeBase().getRSI() < 30
					|| !getTimeBase().isQuantityRising()
			)
				return;
			
			shortContract();

		}

	}
	


	
	
	public void setName(String s){
		className = s;
	}

	@Override
	boolean trendReversed() {
		// TODO Auto-generated method stub
		return false;
	}

}
