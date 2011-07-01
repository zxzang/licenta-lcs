package lcs;

import java.util.Scanner;

public class Barrier {
	static int noThreadsMax;
	static int noThreadCurrent;
	private Environment env;
	static int noStepsTotal = 0;
	static int noStepsInter = 0;
	
	/* Debug */
	Scanner sc = new Scanner(System.in);
	/* ----- */
	
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
			noStepsTotal++;
			noStepsInter++;
			System.out.println("------------------");
			while(!sc.nextLine().equals(""));
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
