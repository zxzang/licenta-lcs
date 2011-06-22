package lcs;

public class Barrier {
	static int noThreadsMax;
	static int noThreadCurrent;
	
	public Barrier(){
		
	}
	public Barrier(int nThreads){
		noThreadsMax = nThreads;		
	}
	
	public void setNumThreads(int nThreads){
		noThreadsMax = nThreads;
		System.out.println("Barrier has " + nThreads);
	}
	
	public synchronized void enterBarrier() throws InterruptedException{
		noThreadCurrent ++;
		if (noThreadCurrent == noThreadsMax){			
			noThreadCurrent = 0;
			System.out.println("------------------");
			notifyAll();
		}
		else{			
			wait();
		}
	}
	
	public synchronized void decThreadNum(){
		noThreadsMax--;
		if (noThreadCurrent == noThreadsMax){
			noThreadCurrent = 0;
			notifyAll();			
		}
	}	

}