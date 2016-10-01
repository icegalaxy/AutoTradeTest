package icegalaxy.net;

public class ReasonsNotBuying {
	
	public boolean getLongReasons(Reasons r){
		boolean b = r.getReasons();
		if (b){
			Global.addLog(r.getLog());
			return true;
		}else
			return false;
	}
	
	public boolean getShortReasons(Reasons r){
		boolean b = r.getReasons();
		if (b){
			Global.addLog(r.getLog());
			return true;
		}else
			return false;
	}

}
