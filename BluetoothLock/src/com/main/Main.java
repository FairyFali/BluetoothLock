package com.main;
import java.awt.Image;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
 
 
public class Main
{
	// GUI���
	static private JFrame jframe;
	// ��ʾʱ��Ŀؼ�
	static private JLabel displayArea;
	static private JLabel timeLabel;
	static private String time;
	static private String DEFAULT_TIME_FORMAT = "HH:mm:ss";
	static private int ONE_SECOND = 1000;
	/**
	 * ��õ�ǰ�û���ֽ��·��
	 * @return
	 */
	private static String getPath()
	{
		File[] fileArr = new File("C:\\Users\\������\\AppData\\Roaming\\Microsoft\\Windows\\Themes\\CachedFiles")
				.listFiles();// win10�µ��û����õı�ֽ
		File maxSize = fileArr[0];
		for (File Temp : fileArr)
		{
			if (Temp.length() > maxSize.length())
				maxSize = Temp;
		}
		return maxSize.getPath();// ��������ļ���·���������������
	}
	/**
	* �ڲ���
	* ��ȡ��ǰϵͳ��̬ʱ��
	* Copyright: Copyright (c) 2019 flwang
	* 
	* @ClassName: Main.java
	* @Description: ����Ĺ�������
	*
	* @version: v1.0.0
	* @author: ������
	* @date: 2019��1��8�� ����1:45:00
	 */
	protected static class JLabelTimerTask extends TimerTask {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(DEFAULT_TIME_FORMAT);

		@Override
		public void run() {
			time = dateFormatter.format(Calendar.getInstance().getTime());
			// ��ʾʱ��Ŀؼ���ʾʱ��
			displayArea.setText(time);
		}
	}
	
	/**
	 * ������������
	 */
	private static void startScreenSaver(){
		// ��ǰ��Ļ�Ĵ�С
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// ��ʼ��GUI���
		jframe = new JFrame();
		// ���ò���
		jframe.setLayout(null);
		// �ޱ߿�
		jframe.setUndecorated(true);
		
		Rectangle bounds = new Rectangle(screenSize);
		jframe.setBounds(bounds);
		Image image = new ImageIcon(getPath()).getImage();
		// ���JPanel����JPanel�����ͼƬ
		JPanel jp = new BackgroundPanel(image);
		jp.setLayout(null);
		jp.setBounds(jframe.getBounds());
		// ���������ͼƬ���
		jframe.add(jp);
		
		// ��ʾʱ��
		Timer tmr = new Timer();
		timeLabel = new JLabel("CurrentTime: ");
		displayArea = new JLabel();
		tmr.scheduleAtFixedRate(new JLabelTimerTask(),new Date(), ONE_SECOND);
		timeLabel.setBounds(0,50,230,30);
		timeLabel.setForeground(Color.white);
		timeLabel.setFont(new java.awt.Font("Dialog", 1, 25));
		displayArea.setBounds(300,50,200,30);
		displayArea.setFont(new java.awt.Font("Dialog", 1, 35));
		displayArea.setForeground(Color.white);
		jp.add(timeLabel);
		jp.add(displayArea);
 
		// �����˳���ť
		JButton exit = new JButton("�˳�");
		exit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
//				System.exit(0);
				jframe.setVisible(false);
			}
		});
		exit.setBounds(0, 0, 100, 30);
		// �˳���ťĬ�Ͽɼ�
		exit.setVisible(true);
		jp.add(exit);

		// ��������
		JPasswordField jtf = new JPasswordField();
		jtf.setBounds((screenSize.width / 2) - 100, screenSize.height - 100, 200, 30);
		jtf.addKeyListener(new KeyListener()
		{
 
			@Override
			public void keyTyped(KeyEvent e){
			}
 
			@Override
			public void keyReleased(KeyEvent e){
			}
 
			@Override
			public void keyPressed(KeyEvent e)// �簴��ָ���ļ���������������
			{
				if (17 == e.getKeyCode() || 18 == e.getKeyCode() || 110 == e.getKeyCode() || 115 == e.getKeyCode()
						|| 192 == e.getKeyCode())
				{
					try
					{
//						Runtime.getRuntime().exec("shutdown /s /t " + 0);
						Runtime.getRuntime().exec("RunDll32.exe user32.dll,LockWorkStation");
					} catch (IOException e1)
					{
						e1.printStackTrace();
					}
				}
			}
		});
		jp.add(jtf);
		// ������ť
		JButton lockButton = new JButton("����");
		lockButton.addActionListener(new ActionListener()
		{
			boolean lockFlag = false;
			String password = null;
 
			public void actionPerformed(ActionEvent arg0)
			{
				if (true == lockFlag)
				{
					String input = new String(jtf.getPassword());
					
					if (input.equals(password))
					{
						lockFlag = false;
						lockButton.setText("����");
						jtf.setText("");
						exit.setVisible(true);
					} else
					{
						JOptionPane.showMessageDialog(jtf, "�������", "��ʾ", 0);
						jtf.setText("");
					}
				} else {
					String input = new String(jtf.getPassword());
					if (false == input.equals(""))
					{
						password = input;
						lockFlag = true;
						jtf.setText("");
						lockButton.setText("����");
						exit.setVisible(false);
					} else
					{
						JOptionPane.showMessageDialog(jtf, "����������", "��ʾ", 1);
					}
				}
			}
		});
		lockButton.setBounds((screenSize.width / 2) - 50, screenSize.height - 50, 100, 30);
		jp.add(lockButton);
 
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setVisible(true);
	}
	
	/**
	 *  �˳���������
	 */
	private static void endScreenerSaver(){
		jframe.setVisible(false);
	}

	/**
	 * ������
	 * @param args
	 */
	public static void main(String[] args){
		// ����һ���̼߳��������ľ���
		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				// �������������socket
				ServerSocket ss = null;
				Socket s = null;
				try {
					ss = new ServerSocket(16666);
					System.out.println("�ȴ�����......");
					// ����socket����
					s = ss.accept();
					System.out.println("a client connect!");
					String str = null;
					BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
					PrintWriter pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
					str = br.readLine();
					while (!"bye".equals(str)) {
						if("bye".equals(str)){break;};
						System.out.println("�������룺" + str);
						// mΪ��λ
						double distance = Double.valueOf(str);
						// ���С��10cm�����������������������
						if (distance < 0.1) {
							// ��������
							endScreenerSaver();
						} else {
							// ��ʼ����
							startScreenSaver();
						}
//							pw.println(str.toUpperCase());
//							pw.flush();
						s = ss.accept();
						System.out.println("a client connect!");
						br = new BufferedReader(new InputStreamReader(s.getInputStream()));
						pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
						str = br.readLine();
					}
					System.out.println("���������bye!");
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						s.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
				
		});
		System.out.println("�����̼߳�����ʼ����...");
		t.start();
	}
}
