package icegalaxy.net;

public class WaitAndNotify {

		

	public synchronized void endWaiter() {

		notifyAll();
		try {			
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	public synchronized void middleWaiter(WaitAndNotify wanOfNextThread) {

		wanOfNextThread.endWaiter();
		notifyAll();
		try {	
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	public synchronized void waitOnly() {
		
		try {	
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	boolean tpdDone = false;
	boolean loginDone = false;
	boolean rsi3Done = false;
	boolean tiThreadDone = false;
	boolean sdcDone = false;
	int counter = 0;
	int counter2;
	int noOfThreads;
	Runnable thread;
}
