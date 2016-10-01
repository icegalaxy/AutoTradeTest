package icegalaxy.net;

public class ThreadController {

	private int noOfWaitingThreads = 0;
	private int noOfThreads;

	public synchronized void mainThread() {

		notifyAll();
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public synchronized void waitAndNotify() {
		if (noOfWaitingThreads == noOfThreads)
			notifyAll();
		else {
			noOfWaitingThreads++;
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void noOfWaitingThreads(int i) {
		noOfThreads = i;
	}
}
