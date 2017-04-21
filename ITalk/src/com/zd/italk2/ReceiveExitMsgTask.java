package com.zd.italk2;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ReceiveExitMsgTask implements Runnable{
	private DatagramPacket dp;//package
	private DatagramSocket ds;//socket
	private byte[] buf;//接受用的字节数组
	public final static int receivePort=10002;//接受用的端口
	private MyNotify myNotify;
	
	public ReceiveExitMsgTask(MyNotify myNotify) {
		this.myNotify = myNotify;
	}

	@Override
	public void run() {
		buf=new byte[1024];
		dp=new DatagramPacket(buf,buf.length);

		try {
			ds=new DatagramSocket(iTalk.EXITPORT);
			while(true){
				ds.receive(dp);
				String s=new String(buf,0,dp.getLength());
				if(s.equals("bye")){
					InetAddress address=dp.getAddress();
					if(myNotify!=null){
						TaskResult tr=new TaskResult();
						tr.content=s;
						tr.inetAddress=address;
						myNotify.notifyResult(tr);
					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}



	

}
