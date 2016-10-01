package icegalaxy.net;

public abstract class Reasons {

	private StringBuffer log = new StringBuffer();
	
	public abstract boolean getReasons();

	public String getLog() {
		return log.toString();
	}
	
	

}
