package icegalaxy.net;

public class LoginThread implements Runnable {


	public boolean done = false;
	private WaitAndNotify wanPrevious;
	private WaitAndNotify wanNext;
	
	public LoginThread(WaitAndNotify wan1,WaitAndNotify wan2){
		this.wanPrevious = wan1;
		this.wanNext = wan2;
	}

	@Override
	public void run() {

		

		boolean didLogin = false;
		//String log = ""; //�]��Tactics Analyse ���~log.txt

		while (Global.isRunning()) {
			while (Global.isTradeTime()) {
				if (!didLogin) {
					Sikuli.login();
					didLogin = true;
				}

//				if (!log.equals(Global.getLog())) {
//					DB.stringtoFile(Global.getLog(), "log.txt");
//					log = Global.getLog();
//				}
				wanPrevious.endWaiter(); // �O�׭��~�ݧ֡A�ݦ�login�����|�R����
			}

			wanPrevious.endWaiter();
		}

	}

	
}
