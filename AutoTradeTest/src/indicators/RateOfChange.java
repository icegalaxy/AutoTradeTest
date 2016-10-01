package indicators;

import java.util.ArrayList;

import icegalaxy.net.Candle;
import icegalaxy.net.NotEnoughPeriodException;

public class RateOfChange {

	ArrayList<Candle> candles;
	
	public RateOfChange(ArrayList<Candle> candles){
		this.candles = candles;
	}
	
	public double getRateOfChange(int periods) throws NotEnoughPeriodException{
		
		double total = 0;
		
		if (periods > candles.size() - 1) //�]���n�h�@��period
			throw new NotEnoughPeriodException();
		
		for (int i=candles.size()-1; i>candles.size()-periods-1; i--){
			

			double difference = candles.get(i).getClose() - candles.get(i-1).getClose();
			
			if (difference >= 0)
				total += difference;
			else
				total -= difference;
			
		}
		
		return total/periods;
	}
	
}
