package org.yasya;

public class YThread  extends Thread{
	public String netDataFileName;
	public String feederDataFileName;

	public YThread(String netDataFileName, String feederDataFileName){
		this.netDataFileName = netDataFileName;
		this.feederDataFileName = feederDataFileName;
	}

	public void run() {
		System.out.printf("thread %s %s is running... \n", netDataFileName, feederDataFileName);
		try {
			App.play(netDataFileName, feederDataFileName);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.printf("thread %s %s is finished. \n", netDataFileName, feederDataFileName);
	}
}
