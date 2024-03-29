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

            while (getTimeBase().getRSI() < lowerRSI || getTimeBase().getRSI() > upperRSI)
                wanPrevious.middleWaiter(wanNext);

            shutdown = false;

        }

        if (getTimeBase().getRSI() < lowerRSI) {
        	
//        	refPt = Global.getCurrentPoint();
//
//        	while (Global.getCurrentPoint() < refPt + 10){
//        		
//        		if(refPt > Global.getCurrentPoint())
//        			refPt =  Global.getCurrentPoint();
//        		
//        		wanPrevious.middleWaiter(wanNext);
//        	}
//
//
//            longContract();
        	
        	shortContract();


        } else
        if (getTimeBase().getRSI() > upperRSI) {

//        	refPt = Global.getCurrentPoint();
//
//        	while (Global.getCurrentPoint() > refPt - 10){
//        		
//        		if(refPt < Global.getCurrentPoint())
//        			refPt =  Global.getCurrentPoint();
//        		
//        		wanPrevious.middleWaiter(wanNext);
//        	}
//
//            shortContract();

        	longContract();
        	
        }

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

        if (StockDataController.getShortTB().getMainDownRail().getSlope() != 100)
            slope = StockDataController.getShortTB().getMainDownRail()
                    .getSlope();
        if (getTimeBase().getMainUpRail().getSlope() != 100)
            longSlope = getTimeBase().getMainUpRail().getSlope();

        return slope > longSlope * 2
                && StockDataController.getShortTB().getMainDownRail().slopeRetained > 2;
    }

    boolean isRising() {

        double slope = 0;
        double longSlope = 0;

        if (StockDataController.getShortTB().getMainUpRail().getSlope() != 100)
            slope = StockDataController.getShortTB().getMainUpRail().getSlope();

        if (getTimeBase().getMainDownRail().getSlope() != 100)
            longSlope = getTimeBase().getMainDownRail().getSlope();

        return slope > longSlope * 2
                && StockDataController.getShortTB().getMainUpRail().slopeRetained > 2;

    }

    double getCutLossPt() {
        return 20;
    }

    double getStopEarnPt() {
//        return Global.getDayHigh() - Global.getDayLow(); //應該只會計買果一刻的diff (i.e. escaped 30 70後)  因為進入Close Contract個Loop之後，StopEarn只會減少，不會增多
//        return getTimeBase().getHL(120).getFluctuation() / 2;
    	return 30;
    }



    @Override
    public TimeBase getTimeBase() {
        return StockDataController.getShortTB();
    }

    

    void updateStopEarn() {

		if (StockDataController.getLongTB().getEMA(5) == -1)
			super.updateStopEarn();

		else {

			if (Global.getNoOfContracts() > 0) {

				if (StockDataController.getLongTB().getEMA(5) < refEMA)
					tempCutLoss = 99999;

				refEMA = StockDataController.getLongTB().getEMA(5);

			} else if (Global.getNoOfContracts() < 0) {

				if (StockDataController.getLongTB().getEMA(5) > refEMA)
					tempCutLoss = 0;

				refEMA = StockDataController.getLongTB().getEMA(5);
			}
		}

	}

    
}