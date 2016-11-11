package icegalaxy.net;

public class EMA
{
	private double latestEMA;
	private int period;

	public EMA(double previousDayEMA, int period)
	{
		this.latestEMA = previousDayEMA;
		this.period = period;
	}

	public void setlatestEMA(double currentPt)
	{
		latestEMA = getEMA(currentPt);
	}

	public double getEMA(double currentPt)
	{

		double smoothingConstant = (double) 2 / (period + 1);

		return (currentPt - latestEMA) * smoothingConstant + latestEMA;

	}

}
