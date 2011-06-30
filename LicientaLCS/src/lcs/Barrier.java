package lcs;

public class Barrier {
	static int noThreadsMax;
	static int noThreadCurrent;
	private Environment env;
	
	public Barrier(Environment env) {
		this.env = env;
	}
	
	public void setNumThreads(int nThreads) {
		noThreadsMax = nThreads;
		System.out.println("Barrier has " + nThreads);
	}
	
	public synchronized void enterBarrier() throws InterruptedException {
		noThreadCurrent ++;
		if (noThreadCurrent == noThreadsMax) {
			noThreadCurrent = 0;
			env.sendChange();
			System.out.println("------------------");
			notifyAll();
		} else {
			wait();
		}
	}
	
	public synchronized void decThreadNum() {
		noThreadsMax--;
		if (noThreadCurrent == noThreadsMax) {
			noThreadCurrent = 0;
			notifyAll();
		}
	}

}
