package icegalaxy.net;

import java.util.ArrayList;

public class TI {

	public static float getMA(ArrayList<Float> points, int noOfPeriods) {

		if (points.size() < noOfPeriods) // check�U�������ƭp�A�p�G�����|�X-1
			return -1;

		float total = 0;

		for (int i = (points.size() - 1); i >= (points.size() - noOfPeriods); i--) {
			total += points.get(i);
		}
		return total / noOfPeriods;

	}

	public static float getEMA(ArrayList<Float> al, int noOfPeriods) {

//		float firstMA = 0;
		float ema = 0;

		if (al.size() <= noOfPeriods)
			return -1;

		float smoothingConstant = (float) 2 / (noOfPeriods + 1);

//		if (noOfPeriods == al.size()) {
//			firstMA = getMA(al, noOfPeriods);
//			return firstMA;
//		} else {

//			ema = firstMA;
		
		//should us the SMA to act the first EMA
		ema = getMA(al, noOfPeriods);

			//calculated from the first period to the latest
			for (int i = noOfPeriods + 1; i < al.size(); i++) {
				ema = (al.get(i) - ema) * smoothingConstant + ema;
			}
			return ema;
//		}
	}

	public static float getMA(ArrayList<Float> points, int noOfPeriods, int previosPeriods) {

		if (points.size() < noOfPeriods + previosPeriods) // check�U�������ƭp�A�p�G�����|�X-1
			return -1;

		float total = 0;

		for (int i = (points.size() - 1 - previosPeriods); i >= (points.size()
				- noOfPeriods - previosPeriods); i--) {
			total += points.get(i);
		}
		float f = total / noOfPeriods;
		// System.out.println("Current MA20: " + f);
		return f;
	}

}
