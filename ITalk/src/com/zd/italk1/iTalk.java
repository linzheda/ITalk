package com.zd.italk1;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FillLayout;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashSet;

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

public class iTalk  implements Runnable{

	protected Shell shlItalk;
	private Table table;
	private Text text;
	private Text text_1;
	private Text text_2;
	private Text text_3;
	
	
	
	private DatagramPacket dp;//package
	private byte[] buf;//接受用的字节数组
	private int receivePort=10002;//接受用的端口

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
		Thread t=new Thread(iTalk.this);
		t.setDaemon(true);
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
		shlItalk.setSize(924, 565);
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
		
		button.setBounds(510, 46, 80, 27);
		button.setText("发送");
		sashForm.setWeights(new int[] {66, 274, 82});
		
		///
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] tis=table.getSelection();
				if(tis==null||tis.length<=0){
					return;
				}
				TableItem ti=tis[0];
				text_2.setText(ti.toString());
			}
		});
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DatagramSocket ds=null;
				try {
					String s=text_1.getText();//内容
					String ip=text_2.getText();//对方ip
					//int port=Integer.parseInt(text_1.getText());//对方的端口
					int myport=Integer.parseInt(text_3.getText());//我的发送端口
					DatagramPacket dp=new DatagramPacket(s.getBytes(),s.getBytes().length,new InetSocketAddress(ip,receivePort));
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
	private HashSet<InetAddress> hash=new HashSet<InetAddress>();
	
	
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
						
						InetAddress address=dp.getAddress();
						
						text.append( address+"对我说：\r\n"+  s+"\r\n"+new Date()+"\r\n");
						text.append("===============================\r\n");
						
						hash.add(address);
						
						table.removeAll();
						for(InetAddress a:hash){
							TableItem ti=new TableItem(table,SWT.NONE);
							ti.setText(new String[]{a.getHostAddress()});
						}
						
						
					}

				});

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
