package icegalaxy.net;

import java.util.ArrayList;

import org.jfree.chart.renderer.xy.CandlestickRenderer;

import com.sun.javafx.binding.LongConstant;

//Use the OPEN Line

public class RuleTest2 extends Rules {

	int candleSize = 0;
	double refHLPoint;
	boolean lowFluc = true;
	boolean shutDownUp;
	boolean shutDownDown;
	double highestPt = 0;
	double lowestPt = 99999;
	boolean shortContract;
	boolean longContract;
	boolean shutdownthis;

	double refPt = 0;

	boolean reachLittleProfit;

	boolean isOpenPeriod = true;
	boolean tradeTimesReseted = false;

	double openPt = Global.getPreviousClose() + Global.getGap();
	private int lossTimes = 0;

	double refEMA;

	public RuleTest2(WaitAndNotify wan1, WaitAndNotify wan2, boolean globalRunRule) {
		super(wan1, wan2, globalRunRule);

		setOrderTime(91500, 114500, 131500, 160000);
		// testing = true;
		// TODO Auto-generated constructor stub
		Global.addLog("Open Point: " + openPt);

	}

	@Override
	public void openContract() {

		// checkLowFluc();

		if (shutdown) {
			lossTimes++;
			shutdown = false;

		}

		if (!isOrderTime() || getTimeBase().getEMA(6) == -1)
			return;

		if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6) && getTimeBase().isEMARising(5, 1))
			longContract();
		else if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6)  && getTimeBase().isEMADropping(5, 1))
			shortContract();

	}

	void updateStopEarn() {

		if (Global.getNoOfContracts() > 0) {
			if (getTimeBase().getEMA(5) < getTimeBase().getEMA(6))
				tempCutLoss = 99999;
		} else if (Global.getNoOfContracts() < 0)
			if (getTimeBase().getEMA(5) > getTimeBase().getEMA(6))
				tempCutLoss = 0;

		if (getTimeBase().getCandles().size() > candleSize) {

			System.out.println("EMA5:" + getTimeBase().getEMA(5));
			System.out.println("EMA6:" + getTimeBase().getEMA(6));
			System.out.println("");
			candleSize++;
		}
	}

	@Override
	public TimeBase getTimeBase() {

		return StockDataController.getLongTB();
	}

	double getCutLossPt() {

		return 100;

	}

	double getStopEarnPt() {

		return -100;
	}

}