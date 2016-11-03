package icegalaxy.net;

public class Chasing
{

	double refHL = 0;
	boolean chaseUp;
	boolean chaseDown;
	
	public Chasing (double refHL){
		this.refHL = refHL;
	}
	
	

	public double getRefHL()
	{
		return refHL;
	}



	public void setRefHL(double refHL)
	{
		this.refHL = refHL;
	}



	public boolean chaseUp()
	{
		return chaseUp;
	}

	public void setChaseUp(boolean chaseUp)
	{
		this.chaseUp = chaseUp;
	}

	public boolean chaseDown()
	{
		return chaseDown;
	}

	public void setChaseDown(boolean chaseDown)
	{
		this.chaseDown = chaseDown;
	}
	
	
	
}
