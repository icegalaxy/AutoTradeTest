package icegalaxy.net;

import org.jfree.base.config.HierarchicalConfiguration;

import sun.security.action.GetLongAction;

public class RuleRSI extends Rules {

    int noOfCutLoss = 0;

    protected double referencePoint;

    double lowerRSI = 30;
    double upperRSI = 70;

    private float refRSI;

	private float refEMA;

	private int lossTimes;

    public RuleRSI(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule) {
        super(wan1, wan2, globalRunRule);

        // TODO Auto-generated constructor stub
    }

    @Override
    public void openContract() {
    	
		if (shutdown) {
			lossTimes++;
			shutdown = false;

		}

        refRSI = 50;

        if (!Global.isOrderTime()
//        		|| lossTimes >= 2
//                || noOfCutLoss >= 3
        // || Global.getDayHigh() - Global.getDayLow() > 100
                )
            return;

        if (shutdown){

//            while (getTimeBase().getRSI() < lowerRSI || getTimeBase().getRSI() > upperRSI)
//                wanPrevious.middleWaiter(wanNext);
//
//            shutdown = false;

        }

        if (getTimeBase().getRSI() < lowerRSI && GetData.getEma5().getEMA() < GetData.getEma25().getEMA()) {
        	
        	while(GetData.getEma5().getEMA() < GetData.getEma25().getEMA())
        	{
        		 wanPrevious.middleWaiter(wanNext);
        		 
        		 if (!isOrderTime())
        			 return;
        	}

            longContract();
        	
        


        } else
        if (getTimeBase().getRSI() > upperRSI && GetData.getEma5().getEMA() < GetData.getEma25().getEMA()) {

        	while(GetData.getEma5().getEMA() > GetData.getEma25().getEMA())
        	{
        		 wanPrevious.middleWaiter(wanNext);
        		 
        		 if (!isOrderTime())
        			 return;
        	}

            shortContract();

        }

        wanPrevious.middleWaiter(wanNext);
//         wait to escape 70 30 zone
//        while (getTimeBase().getRSI() < lowerRSI || getTimeBase().getRSI() > upperRSI)
//            wanPrevious.middleWaiter(wanNext);
    }



    private boolean isSmallFluctutaion() {
        return getTimeBase().getHL(60).getFluctuation() < 100;
    }

    boolean isDropping() {

        double slope = 0;
        double longSlope = 0;

        if (GetData.getShortTB().getMainDownRail().getSlope() != 100)
            slope = GetData.getShortTB().getMainDownRail()
                    .getSlope();
        if (getTimeBase().getMainUpRail().getSlope() != 100)
            longSlope = getTimeBase().getMainUpRail().getSlope();

        return slope > longSlope * 2
                && GetData.getShortTB().getMainDownRail().slopeRetained > 2;
    }

    boolean isRising() {

        double slope = 0;
        double longSlope = 0;

        if (GetData.getShortTB().getMainUpRail().getSlope() != 100)
            slope = GetData.getShortTB().getMainUpRail().getSlope();

        if (getTimeBase().getMainDownRail().getSlope() != 100)
            longSlope = getTimeBase().getMainDownRail().getSlope();

        return slope > longSlope * 2
                && GetData.getShortTB().getMainUpRail().slopeRetained > 2;

    }

    double getCutLossPt() {
        return 30;
    }

    double getStopEarnPt() {
//        return Global.getDayHigh() - Global.getDayLow(); //應該只會計買果一刻的diff (i.e. escaped 30 70後)  因為進入Close Contract個Loop之後，StopEarn只會減少，不會增多
//        return getTimeBase().getHL(120).getFluctuation() / 2;
    	return 30;
    }

    @Override
	boolean trendReversed()
	{

		if (Global.getNoOfContracts() > 0)
		{

//			if (getProfit() > 30)
				return GetData.getEma5().getEMA() < GetData.getEma25().getEMA();
//			else
//				return GetData.getEma5().getEMA() < GetData.getEma5().getPreviousEMA(1);

		} else
		{
//			if (getProfit() > 30)
				return GetData.getEma5().getEMA() > GetData.getEma25().getEMA();
//			else
//				return GetData.getEma5().getEMA() > GetData.getEma5().getPreviousEMA(1);
		}
	}


    @Override
    public TimeBase getTimeBase() {
        return GetData.getShortTB();
    }

    

    void updateStopEarn() {

    	double ema5;
		double ema6;

		// if (Math.abs(getTimeBase().getEMA(5) - getTimeBase().getEMA(6)) <
		// 10){
		ema5 = GetData.getShortTB().getLatestCandle().getClose();
		ema6 = GetData.getEma25().getEMA();

		if (Global.getNoOfContracts() > 0)
		{

			if (isUpTrend2() && getProfit() > 50)
			{
				ema5 = GetData.getEma5().getEMA();
				ema6 = GetData.getEma50().getEMA();
			}

			if (buyingPoint > tempCutLoss && getProfit() > 50)
			{
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint + 5;
			}

			if (ema5 < ema6)
			{
				tempCutLoss = 99999;
				Global.addLog(className + " StopEarn: EMA5 x MA20");
				
			}
		} else if (Global.getNoOfContracts() < 0)
		{

			if (isDownTrend2() && getProfit() > 50)
			{
				ema5 = GetData.getEma5().getEMA();
				ema6 = GetData.getEma50().getEMA();
			}

			if (buyingPoint < tempCutLoss && getProfit() > 50)
			{
				Global.addLog("Free trade");
				tempCutLoss = buyingPoint - 5;

			}

			if (ema5 > ema6)
			{
				tempCutLoss = 0;
				Global.addLog(className + " StopEarn: EMA5 x MA20");
			
			}
		}

	}

    
}