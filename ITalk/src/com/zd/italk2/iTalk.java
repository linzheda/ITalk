package com.zd.italk2;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class iTalk {

	protected Shell shlItalk;
	private Table table;
	private Text text;
	private Text text_1;
	private Text text_2;
	private Text text_3;

	private Button button_2;

	private TaskResult tr;
	public static int EXITPORT=12345;

	private HashSet<InetAddress> hashSet=new HashSet<InetAddress>();

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			iTalk window = new iTalk();
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
		Thread t=new Thread(new Task(new MyNotify(){
			@Override
			public void notifyResult(Object obj) {
				tr=(TaskResult) obj;
				Display.getDefault().asyncExec(new Runnable(){
					@Override
					public void run() {
						text.append( tr.inetAddress+"对我说：\r\n"+ tr.content+"\r\n"+new Date()+"\r\n");
						text.append("===============================\r\n");			
						hashSet.add(tr.inetAddress);
						table.removeAll();
						for(InetAddress a:hashSet){
							TableItem ti=new TableItem(table,SWT.NONE);
							ti.setText(new String[]{a.getHostAddress()});
						}						
						if(button_2.getSelection()==true){
							String ip=tr.inetAddress.getHostAddress();
							String content=UUID.randomUUID().toString();
							byte [] bs=content.getBytes();
							int length=bs.length;
							DatagramSocket ds=null;

							try {
								InetSocketAddress addr=new InetSocketAddress(ip,Task.receivePort);
								DatagramPacket dp=new DatagramPacket(bs,length,addr);
								ds=new DatagramSocket(6789);
								ds.send(dp);
							} catch (Exception e) {
								e.printStackTrace();
							}finally{
								ds.close();
							}
						}
					}
				});
			}
		}));
		t.setDaemon(true);
		t.start();
		//监听退出
		Thread t2=new Thread(new  ReceiveExitMsgTask(new MyNotify(){

			@Override
			public void notifyResult(Object obj) {
				tr=(TaskResult) obj;
				InetAddress ia=tr.inetAddress;
				hashSet.remove(ia);
				Display.getDefault().asyncExec(new Runnable(){

					@Override
					public void run() {
						table.removeAll();
						for(InetAddress a:hashSet){
							TableItem ti=new TableItem(table,SWT.NONE);
							ti.setText(new String[]{a.getHostAddress()} );
						}
					}
				});
			}
		}));
		
		t2.setDaemon(true);
		t2.start();
		
		
		
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
		shlItalk.setSize(946, 630);
		shlItalk.setText("爱聊天");
		shlItalk.setLayout(new FillLayout(SWT.HORIZONTAL));

		SashForm sashForm = new SashForm(shlItalk, SWT.VERTICAL);

		Composite composite = new Composite(sashForm, SWT.NONE);

		Label lblip = new Label(composite, SWT.NONE);
		lblip.setBounds(27, 30, 48, 17);
		lblip.setText("对方Ip：");

		text_2 = new Text(composite, SWT.BORDER);
		text_2.setBounds(83, 30, 125, 23);

		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setBounds(292, 30, 91, 17);
		label_2.setText("我的发送端口：");

		text_3 = new Text(composite, SWT.BORDER);
		text_3.setText("6978");
		text_3.setBounds(389, 27, 73, 23);
		
		Button button_3 = new Button(composite, SWT.NONE);
	
		button_3.setBounds(649, 20, 80, 27);
		button_3.setText("退出系统");

		Composite composite_1 = new Composite(sashForm, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));

		SashForm sashForm_1 = new SashForm(composite_1, SWT.NONE);

		Composite composite_3 = new Composite(sashForm_1, SWT.NONE);
		composite_3.setLayout(new FillLayout(SWT.HORIZONTAL));

		Group group = new Group(composite_3, SWT.NONE);
		group.setText("历史记录");
		group.setLayout(new FillLayout(SWT.HORIZONTAL));

		text = new Text(group, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);

		Composite composite_4 = new Composite(sashForm_1, SWT.NONE);
		composite_4.setLayout(new FillLayout(SWT.HORIZONTAL));

		Group group_1 = new Group(composite_4, SWT.NONE);
		group_1.setText("在线用户列表");
		group_1.setLayout(new FillLayout(SWT.HORIZONTAL));

		table = new Table(group_1, SWT.BORDER | SWT.FULL_SELECTION);

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tblclmnip = new TableColumn(table, SWT.NONE);
		tblclmnip.setWidth(100);
		tblclmnip.setText("在线用户IP");
		sashForm_1.setWeights(new int[] {1, 1});

		Composite composite_2 = new Composite(sashForm, SWT.NONE);

		Label label = new Label(composite_2, SWT.NONE);
		label.setBounds(24, 10, 36, 17);
		label.setText("内容：");

		text_1 = new Text(composite_2, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		text_1.setBounds(61, 10, 443, 82);

		Button button = new Button(composite_2, SWT.NONE);

		button.setBounds(568, 65, 80, 27);
		button.setText("发送");

		Group group_2 = new Group(composite_2, SWT.NONE);
		group_2.setText("选项");
		group_2.setBounds(510, 10, 339, 49);

		final Button button_1 = new Button(group_2, SWT.CHECK);
		button_1.setBounds(60, 22, 45, 17);
		button_1.setText("群发");

		button_2 = new Button(group_2, SWT.CHECK);
		button_2.setBounds(131, 22, 69, 17);
		button_2.setText("智能应答");
		sashForm.setWeights(new int[] {62, 370, 88});

		//表格
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] tis=table.getSelection();
				if(tis==null||tis.length<=0){
					return;
				}
				TableItem ti=tis[0];
				String ip=ti.getText();
				text_2.setText(ip);
			}
		});
		//点击发送
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				DatagramSocket ds=null;

				String s=text_1.getText();//内容

				int myport=Integer.parseInt(text_3.getText());//我的发送端口
				byte[] bs=s.getBytes();
				int length=bs.length;
				if(button_1.getSelection()==true){
					for(InetAddress a:hashSet){
						String ip=a.getHostAddress();
						DatagramPacket dp;
						try {
							InetSocketAddress addr=new InetSocketAddress(ip,Task.receivePort);
							dp = new DatagramPacket(bs,length,Task.receivePort);
							ds=new DatagramSocket(myport);
							ds.send(dp);
						} catch (Exception e1) {
							e1.printStackTrace();
						}finally{
							ds.close();
						}

					}
				}else{
					String ip=text_2.getText();//对方ip
					try {
						InetSocketAddress addr=new InetSocketAddress(ip,Task.receivePort);
						DatagramPacket dp=new DatagramPacket(bs,length,addr);
						ds=new DatagramSocket(myport);
						ds.send(dp);
					} catch (Exception e1) {
						e1.printStackTrace();
					}finally{
						ds.close();
					}
				}

			}
		});
		//点击退出
		button_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean flag= MessageDialog.openConfirm(shlItalk,"退出系统","你要退出聊天吗"); 
				if(flag==true){
					int myport=Integer.parseInt(text_3.getText());
					byte[] bs="bye".getBytes();
					int length=bs.length;
					for(InetAddress a:hashSet){
						String ip=a.getHostAddress();
						DatagramPacket dp;
						DatagramSocket ds=null;
						try {
							InetSocketAddress addr=new InetSocketAddress(ip,iTalk.EXITPORT);
							dp = new DatagramPacket(bs,length,addr);
							ds=new DatagramSocket(myport);
							ds.send(dp);
						} catch (Exception e1) {
							e1.printStackTrace();
						}finally{
							ds.close();
						}

					}
					
					
					System.exit(0);
				}
				
			}
		});

	}
}
