package com.zd.italk;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class UtpTestITalk implements Runnable{

	protected Shell shlItalk;
	private Text text;
	private Text text_1;
	private Text text_2;
	private Text text_3;
	private Text text_4;
	private int port;
	
	
	
	private DatagramPacket dp;//package
	private byte[] buf;//接受用的字节数组
	private int receivePort=10002;//接受用的端口

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			UtpTestITalk window = new UtpTestITalk();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		Thread t=new Thread(UtpTestITalk.this);
		t.start();
		shlItalk.open();
		shlItalk.layout();
		while (!shlItalk.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlItalk = new Shell();
		shlItalk.setSize(862, 479);
		shlItalk.setText("iTalk");



		Label lblip = new Label(shlItalk, SWT.NONE);
		lblip.setBounds(72, 46, 61, 17);
		lblip.setText("对方的ip：");

		text = new Text(shlItalk, SWT.BORDER);
		text.setText("192.168.14.100");
		text.setBounds(139, 43, 132, 23);

		Label label = new Label(shlItalk, SWT.NONE);
		label.setBounds(293, 46, 61, 17);
		label.setText("对方端口：");

		text_1 = new Text(shlItalk, SWT.BORDER);
		text_1.setText("10001");
		text_1.setBounds(360, 43, 73, 23);

		Label label_1 = new Label(shlItalk, SWT.NONE);
		label_1.setBounds(439, 49, 61, 17);
		label_1.setText("发送端口：");

		text_2 = new Text(shlItalk, SWT.BORDER);
		text_2.setText("7890");
		text_2.setBounds(506, 43, 73, 23);

		Label label_2 = new Label(shlItalk, SWT.NONE);
		label_2.setBounds(72, 137, 61, 17);
		label_2.setText("历史记录：");

		text_3 = new Text(shlItalk, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL | SWT.PASSWORD | SWT.CANCEL | SWT.MULTI);
		text_3.setBounds(139, 106, 440, 96);

		Label label_3 = new Label(shlItalk, SWT.NONE);
		label_3.setBounds(97, 250, 36, 17);
		label_3.setText("内容：");

		text_4 = new Text(shlItalk, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		text_4.setBounds(139, 230, 440, 74);

		Button button = new Button(shlItalk, SWT.NONE);

		button.setBounds(283, 310, 80, 27);
		button.setText("发送");

		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DatagramSocket ds=null;
				try {
					String s=text_4.getText();//内容
					String ip=text.getText();//对方ip
					port=Integer.parseInt(text_1.getText());//对方的端口
					int myport=Integer.parseInt(text_2.getText());//我的发送端口
					DatagramPacket dp=new DatagramPacket(s.getBytes(),s.getBytes().length,new InetSocketAddress(ip,port));
					ds=new DatagramSocket(myport);
					ds.send(dp);

				} catch (Exception e1) {
					e1.printStackTrace();
				}finally{
					ds.close();
				}

			}
		});


	}

	@Override
	public void run() {
		buf=new byte[1024];
		dp=new DatagramPacket(buf,buf.length);

		try {
			final DatagramSocket ds=new DatagramSocket(receivePort);
			while(true){
				ds.receive(dp);
				Display.getDefault().asyncExec(new Runnable(){
					@Override
					public void run() {
						String s=new String(buf,0,dp.getLength());
						text_3.append( dp.getAddress()+"对我说：\r\n"+  s+"\r\n"+new Date()+"\r\n");
						text_3.append("===============================\r\n");
					}

				});

			}
		} catch (Exception e) {
			e.printStackTrace();
		}



	}

}
