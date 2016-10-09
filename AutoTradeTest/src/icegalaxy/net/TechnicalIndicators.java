package icegalaxy.net;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ListIterator;

public class TechnicalIndicators {

//	public TechnicalIndicators(String table) {
//
//		ResultSet rs;
//
	float[] ema;
//		try {
//			rs = DB.stmt.executeQuery("Select Point FROM " + table);
//
//			while (rs.next()) {
//				this.close.add(rs.getFloat("Point"));
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//
//	}

	public TechnicalIndicators(ArrayList<Float> close) {
		this.close = close;
		
		//max 240periods
		ema = new float[240];
		
	}

	// current RSI,�Ӯa����A�ҥH�ntake currentPoint as para
	public float getRSI(float currentPoint, int noOfPeriods) {
		RSI rsi = new RSI(close, noOfPeriods);
		return rsi.getRSI(currentPoint);
	}

	// previous RSI, ������
	public float getRSI(int noOfPeriods) {
		RSI rsi = new RSI(close, noOfPeriods);
		return rsi.getRSI();
	}


	//Only Get Current MA
	public float getMovingAverage(int noOfPeriods) {

		if (close.size() < noOfPeriods) // check�U�������ƭp�A�p�G�����|�X-1
			return -1;

		float total = 0;

		for (int i = (close.size() - 1); i >= (close.size() - noOfPeriods); i--) {
			total += close.get(i);
		}
		float f = total / noOfPeriods;
		// System.out.println("Current MA20: " + f);
		return f;
	}
	
	//Can get Previos MA
	public float getMovingAverage(int noOfPeriods, int previosPeriods) {

		
		if (close.size() < noOfPeriods + previosPeriods) // check�U�������ƭp�A�p�G�����|�X-1
			return -1;

		float total = 0;

		for (int i = (close.size() - 1-previosPeriods); i >= (close.size() - noOfPeriods-previosPeriods); i--) {
			total += close.get(i);
		}
		float f = total / noOfPeriods;
		// System.out.println("Current MA20: " + f);
		return f;
	}

	public float getEMA(int noOfPeriods, int previosPeriods) {

		float ema = 0;

		if (close.size() < noOfPeriods)
			return -1;

		float smoothingConstant = (float) 2 / (noOfPeriods + 1);

		if (noOfPeriods == close.size()) {
			return getfirstMA(noOfPeriods);
		} else {

			ema = getfirstMA(noOfPeriods);

			for (int i = noOfPeriods; i < close.size() - previosPeriods; i++) {

				ema = (close.get(i) - ema) * smoothingConstant + ema;

			}
			return ema;
		}
	}
	

	// ma8 of lows
		public float getSupport(ArrayList<Candle> candles, int periods) {
			float total = 0;

			if (candles.size() < periods)
				return 0;
			
			for (int i = (candles.size() - 1); i >= (candles.size() - periods); i--) {
				total += candles.get(i).getLow();
			}
			float f = total / periods;
			// System.out.println("Current MA20: " + f);
			return f;
		}
		
		// ma10 of highs
		public float getResist(ArrayList<Candle> candles, int periods) {
			float total = 0;

			if (candles.size() < periods)
				return 99999;
			
			for (int i = (candles.size() - 1); i >= (candles.size() - periods); i--) {
				total += candles.get(i).getHigh();
			}
			float f = total / periods;
			// System.out.println("Current MA20: " + f);
			return f;
		}
	
		public float getEMA(int noOfPeriods) {

			float ema = 0;

			if (close.size() < noOfPeriods)
				return -1;

			float smoothingConstant = (float) 2 / (noOfPeriods + 1);

			if (noOfPeriods == close.size()) {
				return getfirstMA(noOfPeriods);
			} else {

				ema = getfirstMA(noOfPeriods);

				for (int i = noOfPeriods; i < close.size(); i++) {

					ema = (close.get(i) - ema) * smoothingConstant + ema;

				}
				return ema;
			}
		}
	
	public float getCurrentEMA(int noOfPeriods) {

		float ema = 0;

		if (close.size() < noOfPeriods)
			return -1;

		float smoothingConstant = (float) 2 / (noOfPeriods + 1);

		if (noOfPeriods == close.size()) {
			return getfirstMA(noOfPeriods);
		} else {

			ema = getfirstMA(noOfPeriods);

			for (int i = noOfPeriods; i < close.size(); i++) {

				ema = (close.get(i) - ema) * smoothingConstant + ema;

			}
			return (Global.getCurrentPoint() - ema) * smoothingConstant + ema;
		}

	}
		
//		public float getEMA(int noOfPeriods) {
//			return ema[noOfPeriods];
//		}
	
	public void calculateEMA(int noOfPeriods) {

		float currentEMA = 0;

		if (ema[noOfPeriods] == 0 || close.size() == 0)
			ema[noOfPeriods] = -1;

		float smoothingConstant = (float) 2 / (noOfPeriods + 1);

//		if (noOfPeriods == close.size()) {
//			return getfirstMA(noOfPeriods);
//		} else {

			//close.get(i) starts from 0, so i=noOfPeriods means added 1
		
		currentEMA = (close.get(close.size()-1) - ema[noOfPeriods]) * smoothingConstant + ema[noOfPeriods];

//		Global.addLog("Close: " + (close.get(close.size()-1)));
		
			ema[noOfPeriods] = currentEMA;
//		}
	}

	private float getfirstMA(int noOfPeriods) {

		float sum = 0;

		if (close.size() < noOfPeriods)
			return -1;
		else {
			for (int i = 0; i < noOfPeriods; i++) {
				sum = sum + close.get(i);
			}
			return sum / noOfPeriods;
		}

	}

	// period�Y�Y��ma�X�h�����u
	public float getStandardDeviation(int period) {

		if (close.size() < period)
			return -1;
		float theSumPart = 0;
		float average = getMovingAverage(period);
		for (int i = 1; i < period; i++) {
			theSumPart = theSumPart
					+ (float) Math
							.pow(close.get(close.size() - i) - average, 2);
		}

		// System.out.println("sqrt: " + (period) );

		return (float) Math.sqrt((1.0 / period) * theSumPart); // �]��int ��
		// int�|�Xint,�O�׷|�X�s,�ҥH��1.0�����o
	}

	public float getMACD() {

		float macd;
		int currentCloseSize = close.size();

		if (getEMA(12) == -1.0 || getEMA(26) == -1.0)
			return 0;
		else {
			macd = getEMA(12) - getEMA(26);
			if (currentCloseSize != this.closeSize) {
				macdArray.add(macd);
				this.closeSize = currentCloseSize;
			}
			return macd;
		}
	}

	public float getMACDSignalLine() {

		if (macdArray.size() < 9)
			return 0;
		else {
			TechnicalIndicators tiMACD = new TechnicalIndicators(macdArray);
			return tiMACD.getEMA(9);
		}
	}

	public float getMACDHistogram() {

		if (macdArray.size() < 9)
			return 0;
		else
			return getMACD() - getMACDSignalLine();
	}

	int closeSize = 0;

	ArrayList<Float> close = new ArrayList<Float>();
	public ArrayList<Float> macdArray = new ArrayList<Float>();
}
