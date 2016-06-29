package com.neusoft.saca.snap.file;

public class TimeShower {

	private long time;
	
	public TimeShower(long time) {
		this.time = time;
	}
	
	public void show() {
		long localTime = System.currentTimeMillis();
		System.out.println("the time elapse:" + (localTime - time));
		this.time = localTime;
	}
}
