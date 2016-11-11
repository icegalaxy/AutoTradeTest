package icegalaxy.net;

public class RuleSupportAndResist extends Rules {

	public RuleSupportAndResist(WaitAndNotify wan1, WaitAndNotify wan2,
			boolean globalRunRule) {
		super(wan1, wan2, globalRunRule);
		// TODO Auto-generated constructor stub
	}

	public RuleSupportAndResist(WaitAndNotify wan1, WaitAndNotify wan2) {
		super(wan1, wan2);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void openContract() {
		
		if(!Global.isOrderTime()
				|| shutdown)
			return;

		if (getTimeBase().zone.getTopOfResist() != 0) {
			
			while (Global.getCurrentPoint() < getTimeBase().zone
					.getBottomOfResist()) {

				if (!Global.isOrderTime())
					return;

				wanPrevious.middleWaiter(wanNext);
			}
			

			while (Global.getCurrentPoint() < getTimeBase().zone
					.getTopOfResist()
					&& Global.getCurrentPoint() > getTimeBase().zone
							.getBottomOfResist()) {

				if (!Global.isOrderTime())
					return;

				wanPrevious.middleWaiter(wanNext);
			}

			if (Global.getCurrentPoint() > getTimeBase().zone.getTopOfResist())
				longContract();
			else 
				if (Global.getCurrentPoint() < getTimeBase().zone
					.getBottomOfResist())
				shortContract();
			return;

		}

		if (getTimeBase().zone.getTopOfSupport() != 0) {

			while (Global.getCurrentPoint() > getTimeBase().zone
					.getTopOfSupport()) {

				if (!Global.isOrderTime())
					return;

				wanPrevious.middleWaiter(wanNext);
			}
			
			
			while (Global.getCurrentPoint() < getTimeBase().zone
					.getTopOfSupport()
					&& Global.getCurrentPoint() > getTimeBase().zone
							.getBottomOfSupport()) {

				if (!Global.isOrderTime())
					return;

				wanPrevious.middleWaiter(wanNext);
			}

			if (Global.getCurrentPoint() > getTimeBase().zone.getTopOfSupport())
				longContract();
			else
				if (Global.getCurrentPoint() < getTimeBase().zone
					.getBottomOfSupport())
				shortContract();
			return;

		}

		wanPrevious.middleWaiter(wanNext);
	}

	@Override
	public TimeBase getTimeBase() {
		return GetData.getShortTB();
	}
	
	@Override
	protected void cutLoss() {
		
			if (Global.getNoOfContracts() > 0){
				if (Global.balance + Global.getCurrentPoint() <= -20){
			
//					Global.addLog("Balance: " + (Global.balance + Global.getCurrentPoint()));
			closeContract("CutLoss " + className);
			shutdown = true;
				}
				}else if (Global.getNoOfContracts() < 0){
					if (Global.balance - Global.getCurrentPoint() <= -20){
//						Global.addLog("Balance: " + (Global.balance - Global.getCurrentPoint()));
						closeContract("CutLoss " + className);
						shutdown = true;
					}
				}

		
	}

}
