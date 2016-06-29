package com.neusoft.saca.snap.file.app.file.controller.file.operation;

public class CountingThread extends Thread {
	
	public void run(){  
        System.out.println();  
        System.out.println("SubThread " + this + " begin"); //this显示本对象信息，[Thread-0,5,main]依次是线程名，优先级，该线程的父线程名字  
        for(int i=0; i<8; i++)  
            System.out.println(this.getName()+".i" + (i+1)+"\t");  
        System.out.println();  
        System.out.println("SubThread " + this+ " end");  
    }

}
